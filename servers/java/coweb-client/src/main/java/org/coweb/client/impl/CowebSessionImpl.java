package org.coweb.client.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.cometd.bayeux.Bayeux;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
import org.cometd.bayeux.Message.Mutable;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSession.Extension;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.ClientSessionChannelListener;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.client.BayeuxClient;
import org.cometd.client.BayeuxClient.State;
import org.cometd.client.ext.AckExtension;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.cometd.common.JSONContext.Client;
import org.coweb.client.ICowebSession;
import org.coweb.client.impl.dtos.AdminResponse;
import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CowebSessionImpl implements ICowebSession {
	private static final Logger logger = LoggerFactory
			.getLogger(CowebSessionImpl.class);
	
	public IStateCallback stateCallback;
	public ISyncCallback syncCallback;

	public void init(final AdminResponse adminResponse, final String token) throws Exception {
		// Prepare the HTTP transport
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		ClientTransport httpTransport = new LongPollingTransport(null,
				httpClient);

		// Configure the BayeuxClient, with the websocket transport listed
		// before the http transport
		final BayeuxClient client = new BayeuxClient("http://localhost:9999/cometd",
				httpTransport);
		client.addExtension(new AckExtension());
		client.addExtension(new Extension() {

			@Override
			public boolean rcv(ClientSession session, Mutable message) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean rcvMeta(ClientSession session, Mutable message) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean send(ClientSession session, Mutable message) {
				logger.debug("send");
				return processCustom(adminResponse, message);
			}

			@Override
			public boolean sendMeta(ClientSession session, Mutable message) {
				logger.debug("sendMeta");
				return processCustom(adminResponse, message);
			}

			private boolean processCustom(final AdminResponse adminResponse,
					Mutable message) {
				HashMap<String, Object> hashMap = new HashMap<String,Object>();
				hashMap.put("sessionid", adminResponse.getSessionid());
				hashMap.put("updaterType", "default");
				message.getExt(true).put("coweb", hashMap);
				
				hashMap = new HashMap<String, Object>();
				hashMap.put("token", token);
				message.getExt(true).put("userDefined", hashMap);
				// TODO Auto-generated method stub
				return true;
			}
			
		});
		client.handshake();
		boolean handshaken = client.waitFor(1000, State.CONNECTED);
		if (handshaken) {
			System.out.println("HANDSHAKE SUCCESSFUL");
			subscribeChannel(client, "/session/"+adminResponse.getSessionid()+"/roster/*");
			subscribeChannel(client, "/session/sync/*");
			subscribeChannel(client, "/session/"+adminResponse.getSessionid()+"/sync/*");
			subscribeChannel(client, "/service/session/join/*");
		}else {
			logger.debug("HANDSHAKE NOT SUCCESSFUL");
		}
	}

	private void subscribeChannel(BayeuxClient client, final String channelName) {
		ClientSessionChannel channel = client.getChannel(channelName);
		channel.subscribe(new MessageListener() {

			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				System.out.println("############### got message on channel"+message.getChannel());
				System.out.println("############### message data: "+message.getData());
				if (message.getChannel().endsWith("state")) {
					handleStateMessage(message);
//					Object[] data = (Object[]) message.getData();
//					Object[] value = (Object[]) ((Map<String, Object>) data[0]).get("value");
//					System.out.println(Arrays.deepToString(value));
				}else if (message.getChannel().endsWith("/sync/app")) {
					handleSyncMessage(message);
				}
				logger.debug("On channel {}: {}", channelName, message);
			}

			private void handleSyncMessage(Message message) {
				if (syncCallback!=null) {
					syncCallback.syncReceived(message);
				}
			}

			private void handleStateMessage(Message message) {
				Object data = message.getData();
				if (data instanceof Object[]) {
					for (Object o : (Object[])data) {
						if (o instanceof HashMap) {
							System.out.println("!!!!!!!!!!!!!!! message contained map: "+o);
							HashMap m = (HashMap) o;
							String topic = (String) m.get("topic");
							if (!topic.equals("coweb.engine.state")) {
								if (stateCallback!=null) {
									stateCallback.stateReceived(topic, (HashMap<String, Object>) m.get("value"));
								}
							}
						}else {
							System.out.println("NOOOOOOO, o was "+o);
						}
					}
				}else {
					System.out.println("NOOOOOOO data was "+data);
				}
			}
		});
	}
	
	public void setStateCallback(IStateCallback stateCallback) {
		this.stateCallback = stateCallback;
	}
	
	public void setSyncCallback(ISyncCallback syncCallback) {
		this.syncCallback = syncCallback;
	}
}

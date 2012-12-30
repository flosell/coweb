package org.coweb.client.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.cometd.bayeux.Bayeux;
import org.cometd.bayeux.Channel;
import org.cometd.bayeux.Message;
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
import org.coweb.client.impl.dtos.AdminRequest;
import org.coweb.client.impl.dtos.AdminResponse;
import org.eclipse.jetty.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CowebSessionImpl implements ICowebSession {
	static final Logger logger = LoggerFactory
			.getLogger(CowebSessionImpl.class);

	public IStateCallback stateCallback;
	public ISyncCallback syncCallback;

	private String host;

	private String adminPath;

	Map<String, Object> userDefined;

	/**
	 * 
	 * @param host
	 *            the servers host-address, e.g. http://localhost:8080
	 * @param adminPath
	 *            the path to the adminServlet e.g. /admin
	 */
	public CowebSessionImpl(String host, String adminPath) {
		this.host = host;
		this.adminPath = adminPath;
	}

	public void connect(String key, Map<String, Object> userDefined) throws Exception {
		this.userDefined = userDefined;
		AdminResponse resp = sendAdminRequest(key);
		init(resp);
	}

	private AdminResponse sendAdminRequest(String key) throws JsonGenerationException, JsonMappingException, IOException {
			ObjectMapper om = new ObjectMapper(); // TODO: remove this dependency?
			String req = om.writeValueAsString(new AdminRequest(key, true,
					false, host, ""));

			logger.debug("req: {}", req);
			String resp = post(req, host+adminPath);
			logger.debug("admin resp: {}", resp);

			AdminResponse adminResponse = om.readValue(resp,
					AdminResponse.class);
			return adminResponse;
	}

	private void init(final AdminResponse adminResponse)
			throws Exception {
		// Prepare the HTTP transport
		HttpClient httpClient = new HttpClient();
		httpClient.start();
		ClientTransport httpTransport = new LongPollingTransport(null,
				httpClient);

		// Configure the BayeuxClient, with the websocket transport listed
		// before the http transport
		String cometUrl = host+adminResponse.getSessionurl();
		final BayeuxClient client = new BayeuxClient(
				cometUrl, httpTransport);
		client.addExtension(new AckExtension());
		client.addExtension(new CowebClientExtension(this, adminResponse));
		client.handshake();
		boolean handshaken = client.waitFor(1000, State.CONNECTED);
		if (handshaken) {
			System.out.println("HANDSHAKE SUCCESSFUL");
			subscribeChannel(client, "/session/" + adminResponse.getSessionid()
					+ "/roster/*");
			subscribeChannel(client, "/session/sync/*");
			subscribeChannel(client, "/session/" + adminResponse.getSessionid()
					+ "/sync/*");
			subscribeChannel(client, "/service/session/join/*");
		} else {
			logger.debug("HANDSHAKE NOT SUCCESSFUL");
		}
	}

	private void subscribeChannel(BayeuxClient client, final String channelName) {
		ClientSessionChannel channel = client.getChannel(channelName);
		channel.subscribe(new MessageListener() {

			@Override
			public void onMessage(ClientSessionChannel channel, Message message) {
				System.out.println("############### got message on channel"
						+ message.getChannel());
				System.out.println("############### message data: "
						+ message.getData());
				if (message.getChannel().endsWith("state")) {
					handleStateMessage(message);
				} else if (message.getChannel().endsWith("/sync/app")) {
					handleSyncMessage(message);
				}
				logger.debug("On channel {}: {}", channelName, message);
			}

			private void handleSyncMessage(Message message) {
				if (syncCallback != null) {
					syncCallback.syncReceived(message);
				}
			}

			private void handleStateMessage(Message message) {
				Object data = message.getData();
				if (data instanceof Object[]) {
					for (Object o : (Object[]) data) {
						if (o instanceof HashMap) {
							System.out
									.println("!!!!!!!!!!!!!!! message contained map: "
											+ o);
							HashMap m = (HashMap) o;
							String topic = (String) m.get("topic");
							if (!topic.equals("coweb.engine.state")) {
								if (stateCallback != null) {
									stateCallback.stateReceived(topic,
											(HashMap<String, Object>) m
													.get("value"));
								}
							}
						} else {
							System.out.println("NOOOOOOO, o was " + o);
						}
					}
				} else {
					System.out.println("NOOOOOOO data was " + data);
				}
			}
		});
	}

	public static String post(String inputJson, String theUrl)
			throws ClientProtocolException, IOException {
		logger.debug("posting to {}", theUrl); // TODO use jetty-client instead of apache?
		return Request.Post(theUrl)
				.bodyString(inputJson, ContentType.APPLICATION_JSON)
				.connectTimeout(10000).socketTimeout(10000).execute()
				.returnContent().asString();
	}

	public void setStateCallback(IStateCallback stateCallback) {
		this.stateCallback = stateCallback;
	}

	public void setSyncCallback(ISyncCallback syncCallback) {
		this.syncCallback = syncCallback;
	}
}

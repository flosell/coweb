package org.coweb.client.impl.handlers;

import java.util.HashMap;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.coweb.client.impl.CowebSessionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionJoinHandler implements MessageListener{
	private static final Logger logger = LoggerFactory
			.getLogger(SessionJoinHandler.class);
	private CowebSessionImpl parent;

	public SessionJoinHandler(CowebSessionImpl cowebSessionImpl) {
		this.parent = cowebSessionImpl; // TODO: this is not too elegant
	}

	public void onMessage(ClientSessionChannel channel, Message message) {
		if (message.getChannel().endsWith("state")) { 
			handleState(message);
		}else if (message.getChannel().endsWith("siteid")) {
			handleSiteId(message);
		}else if (message.getChannel().endsWith("roster")) {
			handleRoster(message);
		}
	}

	private void handleState(Message message) {
		Object data = message.getData();
		// TODO: maybe dataAsMap is better?
		if (data instanceof Object[]) {
			for (Object o : (Object[]) data) {
				if (o instanceof HashMap) {
					HashMap<String,Object> m = (HashMap<String,Object>) o;
					String topic = (String) m.get("topic");
					if (!topic.equals("coweb.engine.state")) {
						if (parent.getStateCallback() != null) {
							parent.getStateCallback().stateReceived(topic,(HashMap<String, Object>) m.get("value"));
						}
					}
				} else {
					logger.error("Unknown data-element {}",o);
				}
			}
		} else {
			logger.error("unknown data-object {}",data);
		}
	}
	
	public void handleSiteId(Message message) {
		long siteId = (Long) message.getData();
		parent.setSiteId(siteId);
	}
	
	public void handleRoster(Message message) {
		// TODO
	}

	
}

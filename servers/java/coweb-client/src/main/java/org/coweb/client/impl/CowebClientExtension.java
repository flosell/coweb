package org.coweb.client.impl;

import java.util.HashMap;
import java.util.Map;

import org.cometd.bayeux.Message.Mutable;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSession.Extension;

final class CowebClientExtension implements Extension {
	private final String sessionId;
	private Map<String, Object> userDefined;

	public CowebClientExtension(String sessionId, Map<String, Object> userDefined) {
		this.sessionId = sessionId;
		this.userDefined = userDefined;
	}

	@Override
	public boolean rcv(ClientSession session, Mutable message) {
		return true;
	}

	@Override
	public boolean rcvMeta(ClientSession session, Mutable message) {
		return true;
	}

	@Override
	public boolean send(ClientSession session, Mutable message) {
		return processCustom(message);
	}

	@Override
	public boolean sendMeta(ClientSession session, Mutable message) {
		return processCustom(message);
	}

	private boolean processCustom(Mutable message) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("sessionid", sessionId);
		hashMap.put("updaterType", "default");
		message.getExt(true).put("coweb", hashMap);

		
		message.getExt(true).put("userDefined", this.userDefined);
		return true;
	}
}
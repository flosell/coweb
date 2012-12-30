package org.coweb.client.impl;

import java.util.HashMap;

import org.cometd.bayeux.Message.Mutable;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSession.Extension;
import org.coweb.client.impl.dtos.AdminResponse;

final class CowebClientExtension implements Extension {
	/**
	 * 
	 */
	private final CowebSessionImpl cowebSessionImpl;
	private final AdminResponse adminResponse;

	CowebClientExtension(CowebSessionImpl cowebSessionImpl, AdminResponse adminResponse) {
		this.cowebSessionImpl = cowebSessionImpl;
		this.adminResponse = adminResponse;
	}

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
		CowebSessionImpl.logger.debug("send");
		return processCustom(adminResponse, message);
	}

	@Override
	public boolean sendMeta(ClientSession session, Mutable message) {
		CowebSessionImpl.logger.debug("sendMeta");
		return processCustom(adminResponse, message);
	}

	private boolean processCustom(final AdminResponse adminResponse,
			Mutable message) {
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("sessionid", adminResponse.getSessionid());
		hashMap.put("updaterType", "default");
		message.getExt(true).put("coweb", hashMap);

		
		message.getExt(true).put("userDefined", this.cowebSessionImpl.userDefined);
		// TODO Auto-generated method stub
		return true;
	}
}
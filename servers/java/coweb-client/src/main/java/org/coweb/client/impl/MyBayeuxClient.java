package org.coweb.client.impl;

import org.cometd.bayeux.Message;
import org.cometd.client.BayeuxClient;
import org.cometd.client.transport.ClientTransport;

final class MyBayeuxClient extends BayeuxClient {
	/**
	 * 
	 */
	private final CowebSessionImpl cowebSessionImpl;

	MyBayeuxClient(CowebSessionImpl cowebSessionImpl, String url, ClientTransport transport) {
		super(url, transport);
		this.cowebSessionImpl = cowebSessionImpl;
	}
	
	// TODO: convert to static class

	@Override
	public void onFailure(Throwable x, Message[] messages) {
		if (this.cowebSessionImpl.getErrorHandler()!=null) {
			// TODO: probably not thread-safe
			this.cowebSessionImpl.getErrorHandler().onFailure(x, messages);
		}
	}
}
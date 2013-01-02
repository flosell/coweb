package org.coweb.client.impl;

import org.cometd.bayeux.Message;

public interface MessageErrorHandler {
	public void onFailure(Throwable error, Message[] m);
}

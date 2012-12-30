package org.coweb.client.impl;

import org.cometd.bayeux.Message;

public interface ISyncCallback {

	void syncReceived(Message message);

}

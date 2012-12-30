package org.coweb.client.impl.handlers;

import org.cometd.bayeux.Message;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.coweb.client.impl.CowebSessionImpl;
import org.coweb.client.impl.ISyncCallback;

public class SyncHandler implements MessageListener{
	
	private CowebSessionImpl parent;

	public SyncHandler(CowebSessionImpl cowebSessionImpl) {
		this.parent = cowebSessionImpl; // TODO: this is not too elegant
	}

	@Override
	public void onMessage(ClientSessionChannel channel, Message message) {
		if (message.getChannel().endsWith("app")) {
			handleSyncApp(message);
		} else if (message.getChannel().endsWith("engine")) {
			handleSyncEngine(message);
		}else {
			System.out.println("unknown: " + message.getChannel());
		}		
	}

	private void handleSyncApp(Message message) {
		// backup to make sure we have defined behavior even if someone
		// executes setNull() on parent
		// TODO: does this work? maybe it gets optimized away?
		ISyncCallback cb = parent.getSyncCallback();
		if (cb != null) {
			cb.syncReceived(message);
		}
	}
	
	private void handleSyncEngine(Message message) {
		// TODO
	}
}

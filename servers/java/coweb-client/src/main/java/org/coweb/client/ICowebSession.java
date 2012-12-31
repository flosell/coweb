package org.coweb.client;

import java.util.Map;

import org.coweb.client.impl.IStateCallback;
import org.coweb.client.impl.ISyncCallback;

public interface ICowebSession {

	void setStateCallback(IStateCallback stateCallback);

	void setSyncCallback(ISyncCallback iSyncCallback);

	void connect(String key, Map<String, Object> userDefined) throws Exception;

}

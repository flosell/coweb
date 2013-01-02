package org.coweb.client;

import java.io.IOException;
import java.util.Map;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.coweb.client.impl.IStateCallback;
import org.coweb.client.impl.ISyncCallback;
import org.coweb.client.impl.MessageErrorHandler;
import org.coweb.client.impl.SyncType;

public interface ICowebSession {

	void setStateCallback(IStateCallback stateCallback);

	void setSyncCallback(ISyncCallback iSyncCallback);

	void connect(String key, Map<String, Object> userDefined) throws Exception;

	void sendSync(String value, SyncType type, int position, String topic);

	void disconnect() throws Exception;

	void setErrorHandler(MessageErrorHandler errorHandler);
}

package org.coweb.client;

import org.coweb.client.impl.IStateCallback;
import org.coweb.client.impl.ISyncCallback;
import org.coweb.client.impl.dtos.AdminResponse;

public interface ICowebSession {

	void init(AdminResponse adminResponse, String token) throws Exception;

	void setStateCallback(IStateCallback stateCallback);

	void setSyncCallback(ISyncCallback iSyncCallback);

}

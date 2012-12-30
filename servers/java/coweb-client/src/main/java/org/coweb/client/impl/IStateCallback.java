package org.coweb.client.impl;

import java.util.HashMap;

public interface IStateCallback {
	void stateReceived(String topic,HashMap<String, Object> state);
}

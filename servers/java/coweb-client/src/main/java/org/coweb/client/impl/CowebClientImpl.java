package org.coweb.client.impl;

import java.util.Map;

import org.coweb.client.ICowebClient;
import org.coweb.client.ICowebSession;

public class CowebClientImpl implements ICowebClient {
	
	
	private String host;
	private String adminPath;

	public CowebClientImpl(String host, String adminPath) {
		this.host = host;
		this.adminPath = adminPath;
	}



	public ICowebSession initSession() {
		return new CowebSessionImpl(host,adminPath);
	}
	
	/**
	 * @deprecated Dont use this!, al logic is in initSession at the moment
	 * @param args
	 */
	@Deprecated()
	public void prepare(Map<String, String> args) {
		return; // FIXME: refactor interfaces: pull session preparation from session impl,...
	}

}

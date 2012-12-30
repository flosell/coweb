package org.coweb.client.impl.dtos;

public class AdminRequest {

	private String key;
	private boolean collab;
	private boolean cacheState; 
	private String requestURL;
	private String sessionName;
	
	public AdminRequest() {
	}
	
	public AdminRequest(String key, boolean collab, boolean cacheState,
			String requestURL, String sessionName) {
		super();
		this.key = key;
		this.collab = collab;
		this.cacheState = cacheState;
		this.requestURL = requestURL;
		this.sessionName = sessionName;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public boolean isCollab() {
		return collab;
	}
	public void setCollab(boolean collab) {
		this.collab = collab;
	}
	public boolean isCacheState() {
		return cacheState;
	}
	public void setCacheState(boolean cacheState) {
		this.cacheState = cacheState;
	}
	public String getRequestURL() {
		return requestURL;
	}
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}
	public String getSessionName() {
		return sessionName;
	}
	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
	}
	
	
	
}

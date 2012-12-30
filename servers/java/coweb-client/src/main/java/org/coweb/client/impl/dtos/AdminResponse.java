package org.coweb.client.impl.dtos;
public class AdminResponse {
	private boolean collab;
	private Object info;  
	private String key; 
	private String sessionid;
	private String sessionurl; 
	private String username;
	
	public AdminResponse() {
	}

	public boolean isCollab() {
		return collab;
	}

	public void setCollab(boolean collab) {
		this.collab = collab;
	}

	public Object getInfo() {
		return info;
	}
	
	public void setInfo(Object info) {
		this.info = info;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSessionid() {
		return sessionid;
	}

	public void setSessionid(String sessionid) {
		this.sessionid = sessionid;
	}

	public String getSessionurl() {
		return sessionurl;
	}

	public void setSessionurl(String sessionurl) {
		this.sessionurl = sessionurl;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	
	
	
}

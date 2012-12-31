package org.coweb.client.impl.dtos;

import java.util.HashMap;
import java.util.Map;

import org.coweb.client.impl.SyncType;

public class Sync {
	private String value; 
	private String type; 
	private int position;
	private String topic;
	
	public Sync() {
	}
	
	public Sync(String value, SyncType type, int position, String topic) {
		this.value = value; 
		this.type = type.toString().toLowerCase(); 
		this.position = position;
		this.topic = topic;
	}

	public Map<String,Object> asMap() {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("value", "\""+value+"\""); // TODO: why the double-quoting?
		result.put("type",type);
		result.put("position",position);
		result.put("topic", topic);
		
		return result;
	}
	

}

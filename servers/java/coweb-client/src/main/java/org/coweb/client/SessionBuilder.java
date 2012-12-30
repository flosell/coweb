package org.coweb.client;

public class SessionBuilder {
	
	private static String host = "http://localhost:9999";
	private static String adminPath = "/admin";
	
	public static ICowebClient getClient() {
		return new org.coweb.client.impl.CowebClientImpl(host,adminPath);
	}

	public static String getHost() {
		return host;
	}
	
	
	
	public static void setHost(String host) {
		SessionBuilder.host = host;
	}
	
	
	
	public static String getAdminPath() {
		return adminPath;
	}
	
	
	
	public static void setAdminPath(String adminPath) {
		SessionBuilder.adminPath = adminPath;
	}
}

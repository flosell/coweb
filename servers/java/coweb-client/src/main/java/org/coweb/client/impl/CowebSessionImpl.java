package org.coweb.client.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.client.BayeuxClient;
import org.cometd.client.BayeuxClient.State;
import org.cometd.client.ext.AckExtension;
import org.cometd.client.transport.ClientTransport;
import org.cometd.client.transport.LongPollingTransport;
import org.coweb.client.ICowebSession;
import org.coweb.client.impl.dtos.AdminRequest;
import org.coweb.client.impl.dtos.AdminResponse;
import org.coweb.client.impl.dtos.Sync;
import org.coweb.client.impl.handlers.SessionJoinHandler;
import org.coweb.client.impl.handlers.SyncHandler;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CowebSessionImpl implements ICowebSession {
	static final Logger logger = LoggerFactory
			.getLogger(CowebSessionImpl.class);

	private IStateCallback stateCallback;
	private ISyncCallback syncCallback;

	private String host;

	private String adminPath;

	private Map<String, Object> userDefined;

	private ObjectMapper om;

	private BayeuxClient client;

	private SyncHandler syncHandler;

	private SessionJoinHandler sjHandler;

	private ClientSessionChannel syncChannel;
	
	MessageErrorHandler errorHandler;

	private long siteId;

	private HttpClient httpClient;

	/**
	 * 
	 * @param host
	 *            the servers host-address, e.g. http://localhost:8080
	 * @param adminPath
	 *            the path to the adminServlet e.g. /admin
	 */
	public CowebSessionImpl(String host, String adminPath) {
		this.host = host;
		this.adminPath = adminPath;
		om = new ObjectMapper();
	}
	
	public void disconnect() throws Exception  {
		client.disconnect(1000);
		TimeUnit.SECONDS.sleep(5);
		httpClient.stop();			
	}

	public void connect(String key, Map<String, Object> userDefined)
			throws Exception {
		this.userDefined = userDefined;
		AdminResponse resp = sendAdminRequest(key);
		initBayeux(resp);
	}
	
	public synchronized void sendSync(String value, SyncType type, int position, String topic) {
		Sync data = new Sync(value,type,position,topic);
		syncChannel.publish(data.asMap());
	}

	private AdminResponse sendAdminRequest(String key)
			throws JsonGenerationException, JsonMappingException, IOException {
		
		String req = om.writeValueAsString(new AdminRequest(key, true, false,
				host, ""));

		logger.debug("req: {}", req);
		String resp = post(req, host + adminPath);
		logger.debug("admin resp: {}", resp);

		AdminResponse adminResponse = om.readValue(resp, AdminResponse.class);
		return adminResponse;
	}

	private void initBayeux(final AdminResponse adminResponse) throws Exception {
		// Prepare the HTTP transport
		httpClient = new HttpClient();
		httpClient.start();
		ClientTransport httpTransport = new LongPollingTransport(null,
				httpClient);
		

		// Configure the BayeuxClient, with the websocket transport listed
		// before the http transport
		String cometUrl = host + adminResponse.getSessionurl();
		client = new MyBayeuxClient(this, cometUrl, httpTransport);
		client.addExtension(new AckExtension());
		client.addExtension(new CowebClientExtension(adminResponse.getSessionid(), userDefined));
		client.handshake();

		syncHandler = new SyncHandler(this);
		sjHandler = new SessionJoinHandler(this);
		
		boolean handshaken = client.waitFor(10000, State.CONNECTED);
		if (handshaken) {
			System.out.println("HANDSHAKE SUCCESSFUL");
			client.getChannel(
					"/session/" + adminResponse.getSessionid() + "/roster/*")
					.subscribe(sjHandler);
			client.getChannel("/session/sync/*").subscribe(syncHandler);
			syncChannel = client.getChannel(
					"/session/" + adminResponse.getSessionid() + "/sync/app");
			 client.getChannel(
						"/session/" + adminResponse.getSessionid() + "/sync/*").subscribe(syncHandler);
			client.getChannel("/service/session/join/*").subscribe(sjHandler);
			
		} else {
			throw new IllegalStateException("Handshake failed");
		}
	}

	public static String post(String inputJson, String theUrl)
			throws ClientProtocolException, IOException {
		logger.debug("posting to {}", theUrl); // TODO use jetty-client instead
												// of apache?
		return Request.Post(theUrl)
				.bodyString(inputJson, ContentType.APPLICATION_JSON)
				.connectTimeout(10000).socketTimeout(10000).execute()
				.returnContent().asString();
	}

	public void setStateCallback(IStateCallback stateCallback) {
		this.stateCallback = stateCallback;
	}

	public void setSyncCallback(ISyncCallback syncCallback) {
		this.syncCallback = syncCallback;
	}

	public IStateCallback getStateCallback() {
		return stateCallback;
	}

	public ISyncCallback getSyncCallback() {
		return syncCallback;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	public long getSiteId() {
		return siteId;
	}
	
	public void setErrorHandler(MessageErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	public MessageErrorHandler getErrorHandler() {
		return errorHandler;
	}

}

package ttn;


import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.zoolu.util.Base64;

import it.unipr.netsec.nemo.http.HttpClient;
import it.unipr.netsec.nemo.http.HttpResponse;
import ttn.api.CreateApplicationRequest;
import ttn.api.EndDeviceEntityIdentifiers;
import ttn.api.EntityIdentifiers;
import ttn.api.JSON;
import ttn.api.StreamEventsRequest;



public class TtnAPI {
	
	public static boolean DEBUG= false;
	public static boolean TRACE= false;
	
	protected static void log(String message) {
		System.out.println("DEBUG: "+TtnAPI.class.getSimpleName()+": "+message);
	}

	private String apiUrl;
	private String user;
	private String apiKey;
	//private String email= null;

	
	/** Creates a new instance.
	 */
	public TtnAPI(String apiUrl, String user, String apiKey) {
		this.apiUrl= apiUrl;
		this.user= user;
		this.apiKey= apiKey;
	}
		
	/** Gets the list of applications. 
	 * @throws IOException
	 */
	public String applicationRegistryList() throws IOException {
		return request("GET","/users/"+user+"/applications",null);
	}
	
	/** Gets the list of devices. 
	 * @throws IOException
	 */
	public String endDeviceRegistryList(String applicationId) throws IOException {
		return request("GET","/applications/"+applicationId+"/devices",null);
	}
	
	/** Creates an application. 
	 * @throws IOException
	 */
	public String applicationRegistryCreate(String applicationId) throws IOException {
		CreateApplicationRequest msg= new CreateApplicationRequest();
		msg.application.ids.application_id= applicationId;
		return request("POST","/users/"+user+"/applications",JSON.toJson(msg));
	}
	
	/** Gets a stream of events for a given device.
	 * @throws IOException 
	 */
	public InputStream getEventStream(String appId, String devId) throws IOException {
		EndDeviceEntityIdentifiers devEntityIds= new EndDeviceEntityIdentifiers();
		devEntityIds.device_ids.device_id= devId;
		devEntityIds.device_ids.application_ids.application_id= appId;
		StreamEventsRequest msg= new StreamEventsRequest();
		msg.identifiers= new EntityIdentifiers[]{devEntityIds};
		if (DEBUG||TRACE) log("getEventStream(): "+JSON.toJson(msg));
		return requestStream("POST","/events",JSON.toJson(msg));
	}
	
	/* Schedules downlink data using HTTP Webhook.
	 * @throws IOException 
	 */
	public void scheduleDownlink(String appId, String webhookId, String devId, int fPort, byte[] data) throws IOException {
		String jsonMsg= "{\"downlinks\":[{ \"frm_payload\":\""+Base64.encode(data)+"\", \"f_port\":"+fPort+", \"priority\":\"NORMAL\" }]}";
		if (DEBUG||TRACE) log("scheduleDownlink(): "+jsonMsg);
		String url= "/as/applications/"+appId+"/webhooks/"+webhookId+"/devices/"+devId+"/down/push";
		request("POST",url,jsonMsg);
	}

	/** Sends an API request.
	 */
	public String request(String method, String reqPath, String jsonMessage) throws IOException {
		String reqUrl= apiUrl+reqPath;
		if (TRACE) log("request(): request: "+method+" "+reqUrl);
		if (TRACE) log("request(): request: body: "+jsonMessage);
		HashMap<String,String> hdr= new HashMap<>();
		hdr.put("Authorization","Bearer "+apiKey);
		HttpResponse resp;
		//if (jsonMessage!=null) resp= new HttpClient().request(method,reqUrl,hdr,"application/json",jsonMessage.getBytes());
		if (jsonMessage!=null) resp= new HttpClient().request(method,reqUrl,hdr,"application/json",jsonMessage.getBytes());
		else resp= new HttpClient().request(method,reqUrl,hdr,null,null);
		if (TRACE) log("request(): response: "+resp);
		if (resp.hasBody()) return new String(resp.getBody());
		else return null;
	}
	
	
	/** Sends an API request.
	 */
	public InputStream requestStream(String method, String reqPath, String jsonMessage) throws IOException {
		String reqUrl= apiUrl+reqPath;
		if (TRACE) log("requestStream(): request: "+method+" "+reqUrl);
		if (TRACE) log("requestStream(): request: body: "+jsonMessage);
		HashMap<String,String> hdr= new HashMap<>();
		hdr.put("Authorization","Bearer "+apiKey);
		hdr.put("Accept","text/event-stream");
		hdr.put("Connection","close");
		HttpResponse resp;
		//if (jsonMessage!=null) resp= new HttpClient().request(method,reqUrl,hdr,"application/json",jsonMessage.getBytes());
		if (jsonMessage!=null) resp= new HttpClient().request(method,reqUrl,hdr,"application/json",jsonMessage.getBytes());
		else resp= new HttpClient().request(method,reqUrl,hdr,null,null);
		if (TRACE) log("requestStream(): response: "+resp);
		if (TRACE) log("requestStream(): response: stream: "+resp.getInputStream());
		return resp.getInputStream();
	}


}

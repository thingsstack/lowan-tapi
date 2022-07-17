package allthingstalk;


import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import org.zoolu.util.json.JsonMember;
import org.zoolu.util.json.JsonNumber;
import org.zoolu.util.json.JsonObject;
import org.zoolu.util.json.JsonParser;
import org.zoolu.util.json.JsonString;
import org.zoolu.util.json.JsonValue;

import it.unipr.netsec.nemo.http.HttpClient;
import it.unipr.netsec.nemo.http.HttpResponse;


public final class AllthingstalkAPI {
	private AllthingstalkAPI() {}

	public static PrintStream LOGOUT= System.out;
		
	private static void log(String msg) {
		if (LOGOUT!=null) LOGOUT.println("DEBUG: "+AllthingstalkAPI.class.getSimpleName()+": "+msg);
	}

	
	public static final String ALLTHINGSTALCK_URL= "https://api.allthingstalk.io";
	
	
	public static void publishValue(String deviceId, String accessToken, String asset, String value) throws IOException {
		publish(deviceId,accessToken,asset,"{\"value\":\""+value+"\"}");
	}

	public static void publishValue(String deviceId, String accessToken, String asset, int value) throws IOException {
		publish(deviceId,accessToken,asset,"{\"value\":"+value+"}");
	}

	public static void publishValue(String deviceId, String accessToken, String asset, double value) throws IOException {
		publish(deviceId,accessToken,asset,"{\"value\":"+value+"}");
	}
	
	public static void publishValues(String deviceId, String accessToken, String json) throws IOException {
		JsonParser par= new JsonParser(json);
		JsonObject obj= par.parseObject();
		for (JsonMember member: obj.getMembers()) {
			JsonValue value= member.getValue();
			if (value instanceof JsonNumber) {
				JsonNumber number= (JsonNumber)value;
				if (number.isInteger()) publishValue(deviceId,accessToken,member.getName(),(int)number.getValue());
				else publishValue(deviceId,accessToken,member.getName(),number.getValue());
			}
			else
			if (value instanceof JsonString) {
				publishValue(deviceId,accessToken,member.getName(),((JsonString)value).getValue());
			}
		}
	}	

	public static void publishValue(String deviceId, String accessToken, String asset, Object value) throws IOException {
		if (value instanceof String) publish(deviceId,accessToken,asset,"{\"value\":\""+value.toString()+"\"}");
		else if (value instanceof Number) publish(deviceId,accessToken,asset,"{\"value\":"+value+"}");
		else throw new IOException("Type not supported: "+value.getClass().getSimpleName());
	}

	public static void publishValue(String accessToken, String assetId, Object value) throws IOException {
		if (value instanceof String) publish(accessToken,assetId,"{\"value\":\""+value.toString()+"\"}");
		else if (value instanceof Number) publish(accessToken,assetId,"{\"value\":"+value+"}");
		else throw new IOException("Type not supported: "+value.getClass().getSimpleName());
	}

	private static void publish(String deviceId, String accessToken, String asset, String json) throws IOException {
		String reqURL= ALLTHINGSTALCK_URL+"/device/"+deviceId+"/asset/"+asset+"/state";
		log("publish(): PUT "+reqURL+": "+json);
		HashMap<String,String> hdr=new HashMap<>();
		hdr.put("Authorization","Bearer "+accessToken);
		HttpResponse resp=new HttpClient().request("PUT",reqURL,hdr,"application/json",json.getBytes());
		log("publish(): resp: "+resp);
	}

	private static void publish(String accessToken, String assetId, String json) throws IOException {
		String reqURL= ALLTHINGSTALCK_URL+"/asset/"+assetId+"/state";
		log("publish(): PUT "+reqURL+": "+json);
		HashMap<String,String> hdr=new HashMap<>();
		hdr.put("Authorization","Bearer "+accessToken);
		HttpResponse resp=new HttpClient().request("PUT",reqURL,hdr,"application/json",json.getBytes());
		log("publish(): resp: "+resp);
	}

}

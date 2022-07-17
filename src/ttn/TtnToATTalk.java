package ttn;


import java.io.File;
import java.io.IOException;

import org.zoolu.util.Base64;
import org.zoolu.util.Flags;
import org.zoolu.util.LoggerLevel;
import org.zoolu.util.LoggerWriter;
import org.zoolu.util.SystemUtils;
import org.zoolu.util.json.JsonUtils;

import allthingstalk.AllthingstalkAPI;
import it.unipr.netsec.nemo.http.HttpRequestHandle;
import it.unipr.netsec.nemo.http.HttpRequestURL;
import it.unipr.netsec.nemo.http.HttpServer;
import it.unipr.netsec.thingsstack.lorawan.dragino.DraginoLHT65Payload;
import it.unipr.netsec.thingsstack.lorawan.dragino.DraginoLSE01Payload;


/** Gateway that receives data from TTN and upload it to AllThingsTalk.
 */
public class TtnToATTalk {

	/** Maximum payload storage size */
	static long MAX_PAYLOAD_STORAGE_SIZE=1000000L; // 1MB

	/** Maximum payload size */
	static long MAX_PAYLOAD_SIZE=10000L; // 10000B

	
	/** HTTP server */
	HttpServer server;
	
	/** Devices */
	TtnToATTalkDevice[] devices;
	
	
	/** Creates a new server.
	 * @param port server port
	 * @param devices array of tuples containing TTN device ID, AllThinksTalk device ID, and AllThinksTalk device token */
	public TtnToATTalk(int port, TtnToATTalkDevice[] devices) throws IOException {
		this.devices= devices;
		server=new HttpServer(port,this::processHttpRequest);
	}

	
	/** Creates a new server.
	 * @param port server port
	 * @param ttnDeviceId TTN device ID
	 * @param attDeviceId AllThinksTalk device ID
	 * @param attDeviceToken AllThinksTalk device token */
	public TtnToATTalk(int port, String ttnDeviceId, String attDeviceId, String attDeviceToken) throws IOException {
		devices= new TtnToATTalkDevice[1];	
		devices[0].ttnId= ttnDeviceId;
		devices[0].attId= attDeviceId;
		devices[0].attToken= attDeviceToken;
		server=new HttpServer(port,this::processHttpRequest);
	}

	
	private void processHttpRequest(HttpRequestHandle req_handle) {
		try {
			String method=req_handle.getMethod();
			//HttpRequestURL requestUrl=req_handle.getRequestURL();
			if (method.equals("POST")) {
				byte[] body=req_handle.getRequest().getBody();
				String json=new String(body);
				int beginIndex=json.indexOf("\"device_id\"");
				beginIndex+=13;
				int endIndex=json.indexOf('"',beginIndex);
				String ttnDeviceId= json.substring(beginIndex,endIndex);
				for (TtnToATTalkDevice device: devices) {
					if (ttnDeviceId.equals(device.ttnId)) {
						beginIndex=json.indexOf("frm_payload");
						beginIndex+=14;
						endIndex=json.indexOf('"',beginIndex);
						byte[] payload=Base64.decode(json.substring(beginIndex,endIndex));
						if (device.type.equals("LHT65")) {
							DraginoLHT65Payload lht65= new DraginoLHT65Payload(payload);
							double battery= lht65.getBatteryVoltage();
							double temperature= lht65.getTemperature();
							double humidity= lht65.getHumidity();
							//int extType= lht65.getExtType();
							//long extValue= lht65.getExternalValue();
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"battery",battery);
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"temperature",temperature);
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"humidity",humidity);
							//AllthingstalkAPI.publishValue(device.attId,device.attToken,"extValue",extValue);						
						}
						else
						if (device.type.equals("LSE01")) {
							DraginoLSE01Payload lse01= new DraginoLSE01Payload(payload);
							double battery= lse01.getBatteryVoltage();
							double temperature= lse01.getSoilTemperature();
							double moisture= lse01.getSoilMoisture();
							double conductivity= lse01.getSoilConductivity();
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"battery",battery);
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"temperature",temperature);
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"soil_moisture",moisture);
							AllthingstalkAPI.publishValue(device.attId,device.attToken,"conductivity",conductivity);
						}
					}								
				}
				req_handle.setResponseCode(200);
				return;
			}
			else{
				req_handle.setResponseCode(405);
				return;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		req_handle.setResponseCode(400);
	}
		
	
	public static void main(String[] args) throws IOException {
		Flags flags=new Flags(args);
		int port=flags.getInteger("-p",9092,"port","server port");
		String ttnId=flags.getString("-ttnid",null,"ID","TTN device ID");
		String deviceId=flags.getString("-id",null,"ID","ATT device ID");
		String deviceToken= flags.getString("-token",null,"token","ATT device token");
		String configFile= flags.getString("-f",null,"file","config file");
		boolean help=flags.getBoolean("-h","prints this message");
		boolean veryVerbose= flags.getBoolean("-vv","very verbose mode");
		boolean verbose=flags.getBoolean("-v","verbose mode");
		if (help) {
			System.out.println(flags.toUsageString(TtnToATTalk.class));
			return;
		}
		// else
		if (verbose||veryVerbose) {
			HttpServer.VERBOSE=true;
			SystemUtils.setDefaultLogger(new LoggerWriter(System.out,veryVerbose?LoggerLevel.DEBUG:LoggerLevel.INFO));
		}
		
		if (configFile!=null) {
			TtnToATTalkDevice[] devices= (TtnToATTalkDevice[])JsonUtils.fromJsonArrayFile(new File(configFile),TtnToATTalkDevice.class);
			System.out.println("devices: "+JsonUtils.toJson(devices).toString());
			new TtnToATTalk(port,devices);
		}
		else {
			new TtnToATTalk(port,ttnId,deviceId,deviceToken);			
		}
	}

}

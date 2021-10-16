package test;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.zoolu.util.Base64;
import org.zoolu.util.Bytes;
import org.zoolu.util.DateFormat;
import org.zoolu.util.Flags;

import it.unipr.netsec.thethingsnetwork.TtnAPI;
import it.unipr.netsec.thethingsnetwork.api.Applications;
import it.unipr.netsec.thethingsnetwork.api.EndDevices;
import it.unipr.netsec.thethingsnetwork.api.JSON;
import it.unipr.netsec.thethingsnetwork.api.Result;


public abstract class TtnTest {
	
	
	private static void log(String str) {
		System.out.println("OUT: "+str);
	}
	
	
	public static void main(String[] args) throws IOException {
		
		String apiUrl= "https://eu1.cloud.thethings.network/api/v3";
		
		Flags flags= new Flags(args);
		boolean VERBOSE= flags.getBoolean("-v","verbose mode");
		String user= flags.getString("-u",null,"user","TTN user");
		String apiKey= flags.getString("-k",null,"key","TTN API key e.g. 'NNSXS.XXX'");
		String applicationId= flags.getString("-a",null,"appid","Application ID");
		String deviceId= flags.getString("-d",null,"devid","Device ID");
		boolean help= flags.getBoolean("-h","prints this message");
		
		if (help) {
			System.out.println(flags.toUsageString(TtnTest.class));
			return;
		}

		if (VERBOSE) TtnAPI.DEBUG=true;
		TtnAPI ttn= new TtnAPI(apiUrl,user,apiKey);
		
		// get application list
		String appList= ttn.applicationRegistryList();
		log("application list: "+appList);

		// get device list of a given application
		if (applicationId==null) applicationId= JSON.fromJson(appList,Applications.class).applications[0].ids.application_id;
		String devList= ttn.endDeviceRegistryList(applicationId);
		log("device list: "+devList);

		// create new application (DOES NOT WORK)
		//log("create application: "+ttn.applicationRegistryCreate("new-app"));		
		
		// get events of the first device
		if (deviceId==null) deviceId= JSON.fromJson(devList,EndDevices.class).end_devices[0].ids.device_id;
		InputStream stream=ttn.getEventStream(applicationId,deviceId);
		if (stream!=null) {
			BufferedReader in=new BufferedReader(new InputStreamReader(stream));
			while (true) {
				String line= in.readLine();
				//if (VERBOSE) log("line: "+line);
				if (line.startsWith("{")) {
					if (VERBOSE) log("event: "+line);
					Result event= JSON.fromJson(line,Result.class);
					//if (VERBOSE) log("event: "+JSON.toJson(event));
					if (event.result.name.equals("as.up.data.forward")) {				
						String appId= event.result.identifiers[0].device_ids.application_ids.application_id;
						String devId= event.result.identifiers[0].device_ids.device_id;
						String devEUI= event.result.identifiers[0].device_ids.dev_eui;
						String payload= event.result.data.uplink_message.frm_payload;
						//log("payload: "+payload);					
						log("time: "+DateFormat.formatHHmmssSSS(new Date())+" payload: 0x"+Bytes.toHex(Base64.decode(payload)));					
					}
				}
			}
		}		
	}

}

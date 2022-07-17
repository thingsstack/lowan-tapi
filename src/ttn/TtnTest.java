package ttn;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Date;

import javax.crypto.Cipher;

import org.zoolu.util.Base64;
import org.zoolu.util.Bytes;
import org.zoolu.util.DateFormat;
import org.zoolu.util.Flags;

import it.unipr.netsec.thingsstack.lorawan.mac.AesCipher;
import jacob.CborDecoder;
import test.EncryptedService;
import ttn.TtnAPI;
import ttn.api.Applications;
import ttn.api.EndDevices;
import ttn.api.JSON;
import ttn.api.Result;


public final class TtnTest {
	private TtnTest() {}
	
	private static void log(String str) {
		System.out.println("OUT: "+str);
	}
	
	
	public static void main(String[] args) throws IOException, GeneralSecurityException {
		
		String apiUrl= "https://eu1.cloud.thethings.network/api/v3";
		
		Flags flags= new Flags(args);
		boolean TRACE= flags.getBoolean("-vvv","very very verbose mode");
		boolean DEBUG= flags.getBoolean("-vv","very verbose mode");
		boolean VERBOSE= flags.getBoolean("-v","verbose mode");
		String user= flags.getString("-u",null,"user","TTN user");
		String apiKey= flags.getString("-k",null,"key","TTN API key e.g. 'NNSXS.XXX'");
		String applicationId= flags.getString("-app",null,"appid","application ID");
		String deviceId= flags.getString("-dev",null,"devid","device ID");
		int fPort= flags.getInteger("-fp",-1,"num","FPort");
		String webhookId= flags.getString("-wh",null,"hookid","webhook ID");
		String dataKey=flags.getString("-datakey",null,"key","secret key for removing additional data encryption envelopment (experimental)");
		boolean cbor=flags.getBoolean("-cbor","decodes CBOR payload");
		String downlinkData= flags.getString("-dl",null,"data","schedules datalink data");
		boolean help= flags.getBoolean("-h","prints this message");
		
		if (help) {
			System.out.println(flags.toUsageString(TtnTest.class));
			return;
		}

		if (DEBUG || TRACE) {
			VERBOSE=true;
			TtnAPI.DEBUG=true;
			if (TRACE) TtnAPI.TRACE=true;
		}
		
		TtnAPI ttn= new TtnAPI(apiUrl,user,apiKey);
		
		// schedule downlink
		if (downlinkData!=null) {
			ttn.scheduleDownlink(applicationId,webhookId,deviceId,fPort,Bytes.fromHex(downlinkData));
			return;
		}
		// else
		
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
			Cipher decryption=null;
			while (true) {
				String line= in.readLine();
				//if (VERBOSE) log("line: "+line);
				if (line.startsWith("{")) {
					if (DEBUG) log("event: "+line);
					Result event= JSON.fromJson(line,Result.class);
					if (VERBOSE && !DEBUG && event.result.name.startsWith("as.")) log("event: "+line);
					//if (VERBOSE) log("event: "+JSON.toJson(event));
					if (event.result.name.equals("as.up.data.forward")) {
						String appId= event.result.identifiers[0].device_ids.application_ids.application_id;
						String devId= event.result.identifiers[0].device_ids.device_id;
						String devEUI= event.result.identifiers[0].device_ids.dev_eui;
						String payload= event.result.data.uplink_message.frm_payload;
						//log("payload: "+payload);
						byte[] data=Base64.decode(payload);
						log(DateFormat.formatHHmmssSSS(new Date())+": payload: 0x"+Bytes.toHex(data));					
						if (dataKey!=null) {
							if (decryption==null) decryption=AesCipher.getDecryptionInstance(Bytes.fromFormattedHex(dataKey));
							data=decryption.doFinal(data);
							log(DateFormat.formatHHmmssSSS(new Date())+": cleatext: 0x"+Bytes.toHex(data));
							data=EncryptedService.unpad(data);
							log(DateFormat.formatHHmmssSSS(new Date())+": cleatext: 0x"+Bytes.toHex(data));
							log(DateFormat.formatHHmmssSSS(new Date())+": cleatext: "+Bytes.toAscii(data));
						}
						if (cbor) {
							ByteArrayInputStream bais=new ByteArrayInputStream(data);
							CborDecoder decoder=new CborDecoder(bais);
							log(DateFormat.formatHHmmssSSS(new Date())+": cbor length: "+decoder.readMapLength());
							log(DateFormat.formatHHmmssSSS(new Date())+": name: "+decoder.readTextString());
							log(DateFormat.formatHHmmssSSS(new Date())+": value: "+decoder.readDouble());
						}
					}
				}
			}
		}	
	}

}

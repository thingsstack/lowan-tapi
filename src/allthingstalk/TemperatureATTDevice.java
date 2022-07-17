package allthingstalk;


import java.io.IOException;
import java.util.Date;

import org.zoolu.util.Flags;

import it.unipr.netsec.thingsstack.lorawan.device.service.DataUtils;


public final class TemperatureATTDevice {
	private TemperatureATTDevice() {}
	
	public static void main(String[] args) throws IOException {
		
		Flags flags= new Flags(args);
		String deviceId= flags.getString("-id",null,"id","device ID");
		String accessToken= flags.getString("-token",null,"token","access token");
		//String asset= flags.getString("-a","temperature","asset","asset name (default is 'temperature')");
		long time= flags.getInteger("-t",1200,"secs","transmission intertime in seconds (default is 1200 = 20min)");
		boolean help= flags.getBoolean("-h","prints this message");

		if (help) {
			System.out.println(flags.toUsageString(ATTDevice.class));
			return;
		}
		
		new ATTDevice<Double>(accessToken,deviceId,"temperature",time*1000,()->DataUtils.getTemperature(new Date()));
	}
}

package allthingstalk;


import java.io.IOException;
import java.util.function.Supplier;

import org.zoolu.util.Clock;
import org.zoolu.util.LoggerLevel;
import org.zoolu.util.SystemUtils;
import org.zoolu.util.Timer;


/** Device that periodically sends sensor values to the AllThingsTalk platform using REST API.
 */
public class ATTDevice<T> {
	
	String accessToken;
	String deviceId;
	String asset;
	long timeout;
	
	Supplier<T> supplier;


	public ATTDevice(String accessToken, String deviceId, String asset, long timeout, Supplier<T> supplier) throws IOException {
		this.deviceId= deviceId;
		this.accessToken= accessToken;
		this.asset= asset;
		this.timeout= timeout;
		this.supplier= supplier;
		processDataTimeout(null);
	}

	
	private void processDataTimeout(Timer t) {
		new Thread(this::sendData).start();
		Clock.getDefaultClock().newTimer(timeout,this::processDataTimeout).start();
	}

	
	private void sendData() {
		try {
			AllthingstalkAPI.publishValue(accessToken,deviceId,asset,supplier.get());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}

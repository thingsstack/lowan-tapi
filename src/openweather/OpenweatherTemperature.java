package openweather;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import it.unipr.netsec.thingsstack.lorawan.device.service.DataService;
import it.unipr.netsec.thingsstack.lorawan.device.service.DataUtils;
import jacob.CborEncoder;


/** Read-only service that provides temperature values taken from OpenWeather (openweathermap.org).
 */
public class OpenweatherTemperature implements DataService {
	
	public static enum Format { CBOR, JSON }
	
	public static String NAME="temperature";

	OpenweatherAPI api;	
	String city, countryCode;
	Format format;
	
	
	public OpenweatherTemperature(String[] params) {
		this(params[0],params[1],params[2],Format.CBOR);
	}

	public OpenweatherTemperature(String apikey, String city, String countryCode, Format format) {
		this.city=city;
		this.countryCode=countryCode;
		this.format=format;
		api=new OpenweatherAPI(apikey);
	}

	@Override
	public byte[] getData() {
		try {
			double value=api.getTemperature(city,countryCode);
			if (format==Format.CBOR) {
				ByteArrayOutputStream baos= new ByteArrayOutputStream();
				CborEncoder enc= new CborEncoder(baos);
				enc.writeMapStart(1);
				enc.writeTextString(NAME);
				enc.writeDouble(value);
				return baos.toByteArray();				
			}
			else
			if (format==Format.JSON) {
				return DataUtils.getJson(NAME,value).getBytes();
			}				
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setData(byte[] data) {
		// do nothing	
	}

}

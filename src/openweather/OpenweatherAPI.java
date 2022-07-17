package openweather;


import java.io.IOException;
import java.io.PrintStream;

import it.unipr.netsec.nemo.http.HttpClient;
import it.unipr.netsec.nemo.http.HttpResponse;


public class OpenweatherAPI {
	
	public static PrintStream LOGOUT= System.out;
		
	private static void log(String msg) {
		if (LOGOUT!=null) LOGOUT.println("DEBUG: "+OpenweatherAPI.class.getSimpleName()+": "+msg);
	}

	static String WEATHER_URL= "http://api.openweathermap.org/data/2.5/weather";
	static double KZERO= 273.15;
	
	String apikey;
	
	
	public OpenweatherAPI(String apikey) {
		this.apikey= apikey;
	}
	
	
	public Double getTemperature(String city, String contryCode) throws IOException {
		String reqURL= WEATHER_URL+"?q="+city+","+contryCode+"&appid="+apikey;
		log("getTemperature(): GET: "+reqURL);
		HttpResponse resp= new HttpClient().request("GET",reqURL,null,null,null);
		log("getTemperature(): resp: "+resp);
		if (resp.getStatusCode()==200) {
			String json= new String(resp.getBody());
			int begin= json.indexOf("\"temp\":");
			begin+= 7;
			int end= json.indexOf(',',begin);
			return Math.round((Double.parseDouble(json.substring(begin,end))-KZERO)*100)/100.0;
		}
		else return null;
	}
	
}

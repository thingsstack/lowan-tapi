package allthingstalk;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.zoolu.util.DateFormat;
import org.zoolu.util.Flags;


/** Device that sends scheduled sensor values taken from a {time,value} DB (file).
 *  Data is sent to the AllThingsTalk platform using REST API.
 */
public class FileATTDevice {
	
	public static PrintStream LOGOUT= System.out;
	
	private static void log(String msg) {
		if (LOGOUT!=null) LOGOUT.println(FileATTDevice.class.getSimpleName()+": "+msg);
	}
	
	
	public FileATTDevice(String accessToken, String deviceId, String file) throws IOException, InterruptedException {
		this(accessToken,deviceId,file,0,0,0,0,0,0);
	}

	public FileATTDevice(String accessToken, String deviceId, String file, int years, int months, int days, int hours, int minutes, int seconds) throws IOException, InterruptedException {
		BufferedReader in= new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String[] headers= in.readLine().split("\t");
		String[] assets= Arrays.copyOfRange(headers,1,headers.length);
		String line;
		long now= System.currentTimeMillis();
		while((line=in.readLine())!=null) {
			String[] values= line.split("\t");
			//log("line: "+Arrays.toString(values));
			Date date= DateFormat.parseYYYYMMDDThhmmss(values[0]);
			//date.setYear(date.getYear()+1);
			Calendar cal= DateFormat.toCalendar(date);
			cal.add(Calendar.YEAR,years);
			cal.add(Calendar.MONTH,months);
			cal.add(Calendar.DAY_OF_MONTH,days);
			cal.add(Calendar.HOUR_OF_DAY,hours);
			cal.add(Calendar.MINUTE,minutes);
			cal.add(Calendar.SECOND,seconds);
			date= cal.getTime();
			//log("time: "+date);			
			long time= date.getTime();
			//log(""+now+" "+time);	
			if (now<time) {
				log("next time: "+date);
				Thread.sleep(time-now);
				try {
					for (int i=0; i<assets.length; i++) {
						double value= Double.parseDouble(values[1+i]);
						log("send: "+assets[i]+": "+value);
						AllthingstalkAPI.publishValue(accessToken,deviceId,assets[i],value);					
					}
				}
				catch (IOException e) {
					e.printStackTrace();
				}
				now= System.currentTimeMillis();
			}
		}
		in.close();
	}
	
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		Flags flags= new Flags(args);
		String deviceId= flags.getString("-id",null,"id","device ID");
		String accessToken= flags.getString("-token",null,"token","access token");
		String file= flags.getString("-f",null,"file","input file with aassets in the first line");
		//String[] dateDiff= flags.getStringTuple("-datediff",3,null,"Ys Ms Ds","date difference, in terms of years, months, and days to add");
		//String[] timeDiff= flags.getStringTuple("-timediff",6,null,"Ys Ms Ds Hs Ms Ss","time difference, in terms of years, months, days, hours, minutes, and seconds to add");		
		boolean help= flags.getBoolean("-h","prints this message");
		int years=0, months=0, days=0, hours=0, mins=0, secs=0;
		String[] timeDiff= flags.getStringTuple("-add",2,null,"Y|M|D|H|h|m|s ","adds the given number of Years|Months|Days|hours|minutes|seconds");
		while (timeDiff!=null) {
			int val= Integer.parseInt(timeDiff[1]);
			switch (timeDiff[0]) {
				case "Y" : years= val; break;
				case "M" : months= val; break;
				case "D" : days= val; break;
				case "h" : hours= val; break;
				case "m" : mins= val; break;
				case "s" : secs= val; break;
			}
			timeDiff= flags.getStringTuple("-add",2,null,null,null);
		}

		if (help || file==null) {
			System.out.println(flags.toUsageString(FileATTDevice.class));
			return;
		}
		log("id: "+deviceId);
		log("token: "+accessToken);
		log("file: "+file);
		log("deviceId: "+deviceId);
		log("current time: "+new Date());
		log("add time: Y="+years+", M="+months+", D="+days+", h="+hours+", m="+mins+", s="+secs);
		//new FileATTDevice(accessToken,deviceId,file,1,0,-2);
		new FileATTDevice(accessToken,deviceId,file,years,months,days,hours,mins,secs);
	}
}

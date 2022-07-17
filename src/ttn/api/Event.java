package ttn.api;


public class Event {
	public String name;
	public String time;
	//public EntityIdentifiers[] identifiers;
	public EndDeviceEntityIdentifiers[] identifiers;
	public Data data= new Data();
	public String[] correlation_ids;
	public String origin;
	//public Map<String,byte[]> context;
	public Rights visibility= new Rights();
	public EventAuthentication authentication=new EventAuthentication();
	public String remote_ip;
	public String user_agent;
	public String unique_id;
}

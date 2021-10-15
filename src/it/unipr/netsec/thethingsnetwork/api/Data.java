package it.unipr.netsec.thethingsnetwork.api;


public class Data {
	public String type;
	public String raw_payload;
	public Payload payload= new Payload();
	public String dev_addr;
	public String selected_mac_version;
	public String net_id;
	//public Object downlink_settings;
	//public Object rx_delay;
	//public Object cf_list;
	public String[] correlation_ids; 
	public String received_at;
	public String consumed_airtime;
	public UplinkMessage uplink_message=new UplinkMessage();
}

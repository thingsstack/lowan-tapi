package it.unipr.netsec.thethingsnetwork.api;


public class Application {
	public ApplicationIdentifiers ids= new ApplicationIdentifiers();
	public String created_at;
	public String updated_at;
	public String deleted_at;
	public String name;
	public String description;
	public Object attributes;
	public String[] contact_info;
	public int dev_eui_counter;
}

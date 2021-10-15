package it.unipr.netsec.thethingsnetwork.api;


public abstract class JSON {

	public static String toJson(Object obj) {
		//return new com.google.gson.Gson().toJson(obj);
		return org.zoolu.util.json.JsonUtils.toJson(obj);
	}
	
	public static <T> T fromJson(String json, Class<T> classOfT) {
		//return new com.google.gson.Gson().fromJson(json,classOfT);
		return org.zoolu.util.json.JsonUtils.fromJson(json,classOfT);
	}

}

package ttn.api;


public class Payload {
	public Object m_hdr;
	public String mic;
	public JoinRequestPayload join_request_payload= new JoinRequestPayload();
}

package com.agilecrm.api;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public interface CallApi {

	public boolean startCall(String number);
	
	public boolean endCall(String callId); // cancel call + ignore call + 
	
	public boolean answerCall(String callId);
	
	public CallApi getObject();
	
	public void mute(String callId);
	
	public void unmute(String callId);
	
	public void sendDTMF(String digit, String callId);
	
	public JsonObject getLastCallDetail(String number, String callId);
	
	public JsonArray getCallLogs(String number);
	
	public CallApi startApp();
	
	public void sendVoicemail();
	
	public void runVoicemail();
	
	
}

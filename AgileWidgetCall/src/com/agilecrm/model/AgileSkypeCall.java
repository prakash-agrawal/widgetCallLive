package com.agilecrm.model;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.agilecrm.api.CallApi;
import com.agilecrm.exception.AgileApplicationException;
import com.agilecrm.main.InitialiseListener;
import com.skype.Call;
import com.skype.Call.DTMF;
import com.skype.Friend;
//import com.agilecrm.thread.SendCallCommand;
import com.skype.Skype;
import com.skype.SkypeClient;
import com.skype.SkypeException;
import com.skype.User;


public class AgileSkypeCall implements CallApi {

	private static AgileSkypeCall callInstance;
	public static Logger logger = Logger.getLogger(AgileSkypeCall.class);
	
	@Override
	public boolean startCall(String number) {
	try {
	
		Runtime.getRuntime().exec(new String[] {"rundll32", "url.dll,FileProtocolHandler", "callto:"+number}); 
		return true;
	
	} catch (Exception e) {
		logger.info("Exception inside startcall method of skype call - " + e.getMessage());
		return false;
	}
			
	}

	@Override
	public boolean endCall(String callId) {

		boolean flag = false;
		Call[] calls;
		try {
			calls = Skype.getAllActiveCalls();
			for(Call call : calls){
				if(call.getId().equals(callId)){
					flag = true;
						call.finish();
						break;
				}
			}
		} catch (SkypeException e) {
			logger.info("Exception inside endcall method of skype call - " + e.getMessage());
		}
		return flag;
	}

	@Override
	public CallApi getObject() {
		return callInstance;
	}

	@Override
	public CallApi startApp() {
		logger.info("starting initialization skype");
		try {
			InitialiseListener.start();
			if(callInstance == null){
				callInstance = new AgileSkypeCall();
			}
		} catch (AgileApplicationException e) {
			return null;
		}
		return callInstance;
		
		

	}

	@Override
	public boolean answerCall(String callId) {

		boolean flag = false;
		Call[] calls;
		try {
			calls = Skype.getAllActiveCalls();
			for(Call call : calls){
				if(call.getId().equals(callId)){
					flag = true;
						call.answer();
						break;
				}
			}
		} catch (SkypeException e) {
			logger.info("Exception inside answercall method of skype call - " + e.getMessage());
		}
		return flag;
	}

	@Override
	public void mute(String callId) {
		try {
			Call[] calls;
				calls = Skype.getAllActiveCalls();
				for(Call call : calls){
					if(call.getId().equals(callId)){
						logger.info("inside mute");
						call.setPortCaptureMic(-1000);
						call.clearFileCaptureMic();
						call.clearPortOutput();
						call.clearPortCaptureMic();
						break;
					}
				}
		} catch (SkypeException e) {
		}
		
	}

	@Override
	public void unmute(String callId) {
		try {
			Call[] calls;
				calls = Skype.getAllActiveCalls();
				for(Call call : calls){
					if(call.getId().equals(callId)){
							call.setPortCaptureMic(1000);
							break;
					}
				}
		} catch (SkypeException e) {
		}
		
	}

	@Override
	/**
	 * parameter passed is JsonObject with key - callId,direction,duration,displayName,startTime -in String format
	 * duration changed from int to String
	 * startTime changed from long to String
	 */
	public JSONObject getLastCallDetail(String number, String callId) {

		JSONObject json = new JSONObject();
	try {
		User user = Skype.getUser(number);
		if(null == user){
			return json;
		}
		Call[] lastCalls = user.getAllCalls();
		for(Call call : lastCalls){
			if(call.getId().equals(callId)){
				json.put("callId", call.getId());
				json.put("direction", call.getType().toString().split("_")[0].toLowerCase());
				json.put("duration", ((Integer)call.getDuration()).toString());
				json.put("displayName", call.getPartnerDisplayName());
				json.put("startTime", ((Long)call.getStartTime().getTime()));
				break;
			}
		}
	} catch (Exception e) {
	}
		return json;
	}

	@Override
	public JSONArray getCallLogs(String number) {

		JSONArray array = new JSONArray();
		try {
				User user = Skype.getUser(number);
				if(null == user){
					return array;
				}
			Call[] lastCalls = user.getAllCalls();
			int limit = 0 ;
			for(Call call : lastCalls){
				if(limit >= 20){
					break;
				}
					JSONObject obj = new JSONObject();
						obj.put("direction",call.getType().toString().split("_")[0].toLowerCase());
						obj.put("duration", ((Integer)call.getDuration()).toString());
						obj.put("startTime", ((Long)call.getStartTime().getTime()));
					//	obj.put("displayName", call.getPartnerDisplayName());
						array.put(obj);
						limit++;
			}
		} catch (Exception e) {
			return array;
		}
		
		return array;
	}

	
	@Override
	public void sendVoicemail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void runVoicemail() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendDTMF(String digit, String callId) {
		// TODO Auto-generated method stub
		Call[] calls;
		Integer i = 0;
		if(digit.equals("hash")){
			i = 10;
		}else if(digit.equals("star")){
			i=11;
		}else{
			i = Integer.valueOf(digit);
		}
		try {
			calls = Skype.getAllActiveCalls();
			for(Call call : calls){
				if(call.getId().equals(callId)){
						call.send(DTMF.values()[i]);
						break;
				}
			}
		} catch (SkypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package com.agilecrm.model;

import java.util.List;

import org.apache.log4j.Logger;

import com.agilecrm.api.CallApi;
import com.agilecrm.exception.AgileApplicationException;
import com.agilecrm.main.InitialiseListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skype.Call;
import com.skype.Call.DTMF;
//import com.agilecrm.thread.SendCallCommand;
import com.skype.Skype;
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
	public JsonObject getLastCallDetail(String number, String callId) {

		JsonObject json = new JsonObject();
	try {
		User user = Skype.getUser(number);
		if(null == user){
			return json;
		}
		Call[] lastCalls = user.getAllCalls();
		for(Call call : lastCalls){
			if(call.getId().equals(callId)){
				json.addProperty("callId", call.getId());
				json.addProperty("direction", call.getType().toString().split("_")[0].toLowerCase());
				json.addProperty("duration", ((Integer)call.getDuration()).toString());
				json.addProperty("displayName", call.getPartnerDisplayName());
				json.addProperty("startTime", ((Long)call.getStartTime().getTime()));
				break;
			}
		}
	} catch (Exception e) {
	}
		return json;
	}

	@Override
	public JsonArray getCallLogs(String number) {

		JsonArray array = new JsonArray();
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
					JsonObject obj = new JsonObject();
						obj.addProperty("direction",call.getType().toString().split("_")[0].toLowerCase());
						obj.addProperty("duration", ((Integer)call.getDuration()).toString());
						obj.addProperty("startTime", ((Long)call.getStartTime().getTime()));
					//	obj.put("displayName", call.getPartnerDisplayName());
						array.add(obj);
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
	
	/**
	 * This method will take the digit as extension and send it to the passed call having callid as parameter of callId one by one
	 * @param digit
	 * @param callId
	 */
	public void sendDTMF(List<String> digit, String callId) {
		// TODO Auto-generated method stub
		try {
			logger.info("extension to send to call is ----  " + callId + "  -------------  " + digit.toString());
			Call[] calls = Skype.getAllActiveCalls();
			Call activeCall = null;
				for(Call call : calls){
					if(call.getId().equals(callId)){
						activeCall = call;
							break;
					}
				}
			
			Integer i = 0;
				for(String dig : digit){
					if(dig.equals("h")){
						i = 10;
					}else if(dig.equals("s")){
						i=11;
					}else{
						i = Integer.valueOf(dig);
					}
						if(activeCall == null){
							logger.info("extension not send as the call to send extension is not active ---- > " );
							break;	
						}
					activeCall.send(DTMF.values()[i]);
					logger.info("extension sent ---- > " + i);
				}
		} catch (SkypeException e) {
			logger.info("exception in sending exxtension while connected --" + e.getMessage());
		}
	}
}

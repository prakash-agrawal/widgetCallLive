package com.agilecrm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.agilecrm.api.CallApi;
import com.agilecrm.bria.Message;
import com.agilecrm.bria.MessageBuilder;
import com.agilecrm.bria.data.AudioProperties;
import com.agilecrm.bria.data.Call;
import com.agilecrm.bria.enums.CallAction;
import com.agilecrm.bria.enums.CallType;
import com.agilecrm.bria.enums.DialType;
import com.agilecrm.exception.AgileApplicationException;
import com.agilecrm.main.BriaRemote;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Call.DTMF;

public class AgileBriaCall implements CallApi {

	public static BriaRemote briaRemote = new BriaRemote(); // initiate the Bria client
	public static Logger logger = Logger.getLogger(AgileBriaCall.class);
	
	private static AgileBriaCall callInstance;

	@Override
	public CallApi startApp() {
		try {
			logger.info("starting initialization Bria");
			briaRemote.startBria();
			if (callInstance == null) {
				callInstance = new AgileBriaCall();
			}
		} catch (AgileApplicationException e) {
			return null;
		}
		return callInstance;

	}

	@Override
	public boolean startCall(String number) {
		try {
			BriaRemote.lastState.put("AudioProperties",
					BriaRemote.briaModel.getAudioProperties());
			DialType dialType = DialType.AUDIO;
			Message callMessage = new MessageBuilder().call(dialType, number,
					"DisplayName").build();
			BriaRemote.apiPipe.writeMessage(callMessage);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean endCall(String callId) {
		boolean flag = false;
		try {
			List<Call> calls = BriaRemote.briaModel.getActiveCalls();
			if (calls.size() != 0) {
				for (int i = 0; i < calls.size(); i++) {
					Call call = calls.get(i);
					callId = call.getId();
					if (callId.equals(callId)) {
						flag = true;
						Message endMessage = new MessageBuilder().callAction(
								CallAction.END, callId).build();
						BriaRemote.apiPipe.writeMessage(endMessage);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception inside endcall method of bria call - " + e.getMessage());
		}
		return flag;
	}

	@Override
	public boolean answerCall(String callId) {

		boolean flag = false;
		try {
			List<Call> calls = BriaRemote.briaModel.getActiveCalls();
			if (calls.size() != 0) {
				for (int i = 0; i < calls.size(); i++) {
					Call call = calls.get(i);
					callId = call.getId();
					if (callId.equals(callId)) {
						flag = true;
						Message answerMessage = new MessageBuilder()
								.callAction(CallAction.ANSWER, callId).build();
						BriaRemote.apiPipe.writeMessage(answerMessage);
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.info("Exception inside answercall method of bria call - " + e.getMessage());
		}
		return flag;
	}

	@Override
	public CallApi getObject() {
		return callInstance;
	}

	@Override
	public void mute(String callId) {

		BriaRemote.lastState.put("AudioProperties",
				BriaRemote.briaModel.getAudioProperties());
		AudioProperties properties = new AudioProperties(true, true, false, 0,
				0);
		BriaRemote.briaModel.setAudioProperties(properties);
		Message audioMessage = new MessageBuilder().updateAudioProperties(
				properties).build();
		BriaRemote.apiPipe.writeMessage(audioMessage);

	}

	@Override
	public void unmute(String callId) {

		AudioProperties properties = (AudioProperties) BriaRemote.lastState
				.get("AudioProperties");
		BriaRemote.briaModel.setAudioProperties(properties);
		Message audioMessage = new MessageBuilder().updateAudioProperties(
				properties).build();
		BriaRemote.apiPipe.writeMessage(audioMessage);

	}

	@Override
	public JsonObject getLastCallDetail(String number, String callId) {

		Message callHistoryMessage = new MessageBuilder().callHistory(3,
				CallType.ALL).build();
		BriaRemote.apiPipe.writeMessage(callHistoryMessage);
		return null;

	}

	@Override
	public JsonArray getCallLogs(String number) {

		Message callHistoryMessage = new MessageBuilder().callHistory(100,
				CallType.ALL).build();
		BriaRemote.apiPipe.writeMessage(callHistoryMessage);
		return null;
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
		String i = "0";
		if(digit.equals("hash")){
			i = "#";
		}else if(digit.equals("star")){
			i= "*";
		}else{
			i = digit;
		}
		Message sendDTMF = new MessageBuilder().sendDTMF(i).build();
		BriaRemote.apiPipe.writeMessage(sendDTMF);
		logger.info("message is ------" + sendDTMF.toString() +"----"+ digit);

	}
	
	public void sendDTMF(List<String> digit, String callId) {
		// TODO Auto-generated method stub
		
		try {
			logger.info("extension to send to call is ----  " + callId + "  -------------  " + digit.toString());
			
			String i = "0";
			for(String dig : digit){
				if(dig.equals("h")){
					i = "#";
				}else if(dig.equals("s")){
					i= "*";
				}else{
					i = dig;
				}
				Message sendDTMF = new MessageBuilder().sendDTMF(i).build();
				BriaRemote.apiPipe.writeMessage(sendDTMF);
				logger.info("extension sent using bria---- > " + i);
			}
		} catch (Exception e) {
			logger.info("exception in sending exxtension while connected --" + e.getMessage());
		}
		
	}

}

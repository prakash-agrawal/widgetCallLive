package com.agilecrm.listener;

import org.apache.log4j.Logger;

import com.agilecrm.main.Globals;
import com.agilecrm.main.MainPage;
import com.agilecrm.model.AgileCall;
import com.agilecrm.model.AgileSkypeCall;
import com.agilecrm.thread.SendCommand;
import com.skype.Call;
import com.skype.Call.Status;
import com.skype.CallMonitorListener;
import com.skype.SkypeClient;
import com.skype.SkypeException;

public class AgileCallMonitorListener implements CallMonitorListener {
	
	public static Logger logger = Logger.getLogger(AgileCallMonitorListener.class);

	
	@Override
	public void callMonitor(Call call, Status status) throws SkypeException {
		
		try{
			if(!Globals.configuredWidget.get("Skype")){
				return;
			}
			String callDirection = call.getType().toString().split("_")[0].toLowerCase();		
			String callStatus;
			switch (status.toString().toUpperCase()) {
			case "RINGING":
			case "ROUTING":	
				callStatus = "connecting";
				break;
			case "INCOMING":
				callStatus = "ringing";
				break;
			case "INPROGRESS":
				callStatus = "connected";
				break;
			case "FINISHED" :
				callStatus = "ended";
				break;
			case "CANCELLED" :
				callStatus = "failed";
				break;
			case "REFUSED" :
				if(callDirection.equalsIgnoreCase("outgoing")){
					callStatus = "busy";
				}else{
					callStatus = "refused";
				}
				break;
			default:
				callStatus = status.toString().toLowerCase();
				break;
			}
			
			AgileCall callObj = new AgileCall();
			String clientType = MainPage.parameter.getCallClient();
			if(callDirection.equals("outgoing")){
				SkypeClient.hideSkypeWindow();
				
				if(!MainPage.parameter.getNumber().equals(call.getPartnerId()) || !clientType.equals("Skype")){
					logger.info("return from call monitor and command not send for call status and clientType" + callStatus + "---"+ clientType  );
					logger.info("Number" + MainPage.parameter.getNumber() + "and PartnerId" + call.getPartnerId());
					return;
				}
				callObj.setCallId(call.getId());
				callObj.setNumber(call.getPartnerId());
				callObj.setDisplayName(call.getPartnerDisplayName());
				callObj.setDirection("outgoing");
				callObj.setClient("Skype");
				
				MainPage.parameter.setCallClient("Skype");
				MainPage.parameter.setCallId(call.getId());
				MainPage.parameter.setNumber(call.getPartnerId());
				if(callStatus.equalsIgnoreCase("connected") && MainPage.hasExtension ){
					logger.info("11) seting extension is --- " + MainPage.hasExtension + "--" + call.getId() + "-----"+ MainPage.parameter.getExtension());
					AgileSkypeCall skypeCallObj = new AgileSkypeCall();
					skypeCallObj.sendDTMF(MainPage.parameter.getExtension(), call.getId());
					MainPage.hasExtension = false;
				}
				 
				Thread t = new SendCommand(callStatus, callObj);
				t.start();
				t.join();
				logger.info("3)command outgoing send to send object from skype is " + callStatus  );
				return;
				
			}else if(callDirection.equals("incoming")){
				SkypeClient.hideSkypeWindow();
				if(callStatus.equals("connecting") || callStatus.equals("ringing")){
					if(!clientType.equals("")){
						if(!clientType.equalsIgnoreCase("Skype")){
							logger.info("return from call monitor skype and command not send for call client"+  clientType);
							return;
						}
					}
					callObj.setCallId(call.getId());
					callObj.setNumber(call.getPartnerId());
					callObj.setDisplayName(call.getPartnerDisplayName());
					callObj.setDirection("incoming");
					callObj.setClient("Skype");
					MainPage.parameter.setCallClient("Skype");
					MainPage.parameter.setCallId(call.getId());
					MainPage.parameter.setNumber(call.getPartnerId());
					
					Thread t = new SendCommand("ringing",callObj);
					t.start();
					t.join();
					return;
				}else {
					if(!MainPage.parameter.getNumber().equals(call.getPartnerId()) || !clientType.equals("Skype")){
						return;
					}
					callObj.setCallId(call.getId());
					callObj.setNumber(call.getPartnerId());
					callObj.setDisplayName(call.getPartnerDisplayName());
					callObj.setDirection("incoming");
					callObj.setClient("Skype");
					
					MainPage.parameter.setCallClient("Skype");
					MainPage.parameter.setCallId(call.getId());
					MainPage.parameter.setNumber(call.getPartnerId());
					Thread t = new SendCommand(callStatus, callObj);
					t.start();
					t.join();
					return;
					
				}
			}

		}catch(Exception e){
			logger.info("Exception occured in skype listener -- " + e.getMessage());
		}
	}

}

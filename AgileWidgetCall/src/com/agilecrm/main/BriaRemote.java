/*******************************************************************************
 * (C) Copyright 2014 - CounterPath Corporation. All rights reserved.
 * 
 * THIS SOURCE CODE IS PROVIDED AS A SAMPLE WITH THE SOLE PURPOSE OF DEMONSTRATING A POSSIBLE
 * USE OF A COUNTERPATH API. IT IS NOT INTENDED AS A USABLE PRODUCT OR APPLICATION FOR ANY 
 * PARTICULAR PURPOSE OR TASK, WHETHER IT BE FOR COMMERCIAL OR PERSONAL USE.
 * 
 * COUNTERPATH DOES NOT REPRESENT OR WARRANT THAT ANY COUNTERPATH APIs OR SAMPLE CODE ARE FREE
 * OF INACCURACIES, ERRORS, BUGS, OR INTERRUPTIONS, OR ARE RELIABLE, ACCURATE, COMPLETE, OR 
 * OTHERWISE VALID.
 * 
 * THE COUNTERPATH APIs AND ASSOCIATED SAMPLE APPLICATIONS ARE PROVIDED "AS IS" WITH NO WARRANTY, 
 * EXPRESS OR IMPLIED, OF ANY KIND AND COUNTERPATH EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES AND 
 * CONDITIONS, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR 
 * A PARTICULAR PURPOSE, AVAILABLILTIY, SECURITY, TITLE AND/OR NON-INFRINGEMENT.  
 * 
 * YOUR USE OF COUNTERPATH APIS AND SAMPLE CODE IS AT YOUR OWN DISCRETION AND RISK, AND YOU WILL 
 * BE SOLELY RESPONSIBLE FOR ANY DAMAGE THAT RESULTS FROM THE USE OF ANY COUNTERPATH APIs OR
 * SAMPLE CODE INCLUDING, BUT NOT LIMITED TO, ANY DAMAGE TO YOUR COMPUTER SYSTEM OR LOSS OF DATA. 
 * 
 * COUNTERPATH DOES NOT PROVIDE ANY SUPPORT FOR THE SAMPLE APPLICATIONS.
 * 
 * TO OBTAIN A COPY OF THE OFFICIAL VERSION OF THE TERMS OF USE FOR COUNTERPATH APIs, PLEASE 
 * DOWNLOAD IT FROM THE WEB_SITE AT: http://www.counterpath.com/apitou
 ******************************************************************************/
package com.agilecrm.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.agilecrm.bria.BriaApiPipe;
import com.agilecrm.bria.Message;
import com.agilecrm.bria.MessageBodyParser;
import com.agilecrm.bria.MessageBuilder;
import com.agilecrm.bria.MessageHandler;
import com.agilecrm.bria.MessageProcessor;
import com.agilecrm.bria.data.AudioProperties;
import com.agilecrm.bria.data.Call;
import com.agilecrm.bria.data.CallHistoryEntry;
import com.agilecrm.bria.data.PhoneStatus;
import com.agilecrm.bria.enums.CallType;
import com.agilecrm.bria.enums.MessageBodyType;
import com.agilecrm.bria.enums.StatusType;
import com.agilecrm.exception.AgileApplicationException;
import com.agilecrm.exception.PipeNotAvailableException;
import com.agilecrm.model.AgileCall;
import com.agilecrm.model.BriaModel;
import com.agilecrm.thread.SendCommand;

public class BriaRemote {

	private MessageProcessor messageProcessor;
	public static  BriaApiPipe apiPipe;
	public static BriaModel briaModel = new BriaModel();
	public static Map<String, Object> lastState = new HashMap<String, Object>();
	public static Logger logger = Logger.getLogger(BriaRemote.class);
	
	


	/**
	 * Create the application.
	 * @throws AgileApplicationException 
	 */
	public void startBria() throws AgileApplicationException {
		try{
			logger.info("inside bria start");
			initializePipe();
			requestInitialStatuses();
		} catch (PipeNotAvailableException e){
			logger.info("bria is closed");
			throw new AgileApplicationException("bria-closed",e);
		}catch(Exception e){
			logger.info("cant run bria " + e.getMessage());
			throw new AgileApplicationException();
		}
		
	}

	private void requestInitialStatuses() {
		Message audioStatusMessage = new MessageBuilder().status(StatusType.AUDIO_PROPERTIES).build();
		apiPipe.writeMessage(audioStatusMessage);

		Message callStatusMessage = new MessageBuilder().status(StatusType.CALL).build();
		apiPipe.writeMessage(callStatusMessage);

		Message callHistoryMessage = new MessageBuilder().callHistory(100, CallType.ALL).build();
		apiPipe.writeMessage(callHistoryMessage);

		Message missedCallsStatusMessage = new MessageBuilder().status(StatusType.MISSED_CALL).build();
		apiPipe.writeMessage(missedCallsStatusMessage);
	}

	private void initializePipe(){
		apiPipe = new BriaApiPipe();
		apiPipe.startBriaApiPipe();
		
		this.messageProcessor = apiPipe.getMessageProcessor();
		this.messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_PHONE,
				new MessageHandler() {
					public void handle(Message message) {

						PhoneStatus status = MessageBodyParser.parsePhoneStatusResponse(message.getXmlDocument());

						briaModel.setPhoneStatus(status);

					}
				});

	
		
		this.messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_AUDIO_PROPERTIES,
				new MessageHandler() {

					public void handle(Message message) {
						Document body = message.getXmlDocument();
						AudioProperties properties = MessageBodyParser.parseAudioPropertiesStatusResponse(body);
						briaModel.setAudioProperties(properties);
					}
				});

		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_CALL, new MessageHandler() {
			public void handle(Message message) {
				if(!Globals.configuredWidget.get("Bria")){
					return;
				}
				Document body = message.getXmlDocument();
				List<Call> calls = MessageBodyParser.parseCallStatusResponse(body);
				briaModel.setActiveCalls(calls);

				if(!MainPage.parameter.getCallClient().equals("")){
					if(!MainPage.parameter.getCallClient().equals("Bria")){
						return;
					}
				}
				Call callCommand = null ;
				AgileCall callObj = new AgileCall();
				String callClient = MainPage.parameter.getCallClient();
				for(int i=0;i<calls.size();i++){
					Call call = calls.get(i);
					String number = call.getParticipants().get(0).getNumber().split("@")[0];
					logger.info("active call -- " + number);
					
					
					if(number.equals(MainPage.parameter.getNumber().split("@")[0]) && callClient.equals("Bria")){
						callCommand = call;
						break;
					}
				}
				
				if(null == callCommand){
					if(callClient.equals("Bria")){
							if(!SendCommand.lastSendCommand.equals("routing") || MainPage.parameter.getCommandIs().equals("ended")){
								if (SendCommand.lastSendCommand.equals("ended")) {
									return;
								}
								AgileCall call = new AgileCall();
								call.setClient("Bria");
								call.setCallId("");
								call.setDirection("");
								call.setDisplayName("");
								call.setNumber(MainPage.parameter.getNumber());
								Thread t = new SendCommand("ended",call);
								t.start();
								logger.info("3)call ended command send to send object from bria");
							}
					}else if(callClient.equals("")){
						if (SendCommand.lastSendCommand.equals("ringing")) {
							return;
						}
							if(calls.size() > 0){
								for(int i=0;i<calls.size();i++){
									if(calls.get(i).isRinging()){
										callObj.setCallId(calls.get(i).getId());
										callObj.setNumber(calls.get(i).getParticipants().get(0).getNumber());
										callObj.setDisplayName(calls.get(i).getParticipants().get(0).getDisplayName());
										callObj.setClient("Bria");
										MainPage.parameter.setCallId(calls.get(i).getId());
										MainPage.parameter.setNumber(calls.get(i).getParticipants().get(0).getNumber());
										MainPage.parameter.setCallClient("Bria");
										SendCommand.lastSendCommand = "routing";
										Thread t = new SendCommand("ringing",callObj);
										t.start();
										logger.info("3)call ringing command send to send object from bria");
										break;
									}
								}
							}
					} 
					
				}else {
						if(!callClient.equals("Bria")){
							return;
						}
						Call call = callCommand;
						callObj.setCallId(call.getId());
						callObj.setNumber(call.getParticipants().get(0).getNumber());
						callObj.setDisplayName(call.getParticipants().get(0).getDisplayName());
						callObj.setClient("Bria");
						
						MainPage.parameter.setCallId(call.getId());
						MainPage.parameter.setCallClient("Bria");
						MainPage.parameter.setCommandIs("");
						if(call.isConnecting()){
							if (SendCommand.lastSendCommand.equals("connecting")) {
								return;
							}
							Thread t = new SendCommand("connecting",callObj);
							t.start();
							logger.info("3)call connecting command send to send object from bria");
						}else if(call.isFailed()){
							if (SendCommand.lastSendCommand.equals("failed")) {
								return;
							}
							Thread t = new SendCommand("failed",callObj);
							t.start();
							logger.info("3)call failed command send to send object from bria");
						}
						else {
							if (SendCommand.lastSendCommand.equals("connected")) {
								return;
							}
							Thread t = new SendCommand("connected",callObj);
							t.start();
							logger.info("3)call connected command send to send object from bria");
						}

					}
			}
			
			
		});
		
		messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_CALL_HISTORY,
				new MessageHandler() {
					public void handle(Message message) {
						if(!Globals.configuredWidget.get("Bria")){
							return;
						}
						try {
							Thread.sleep(101);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						List<CallHistoryEntry> callHistoryEntries = MessageBodyParser
								.parseCallHistoryStatusResponse(message.getXmlDocument());
						briaModel.setCallHistoryEntries(callHistoryEntries);
						
						AgileCall call = new AgileCall();
						call.setClient("Bria");
							if(!SendCommand.lastSendCommand.equals("routing")){
								if(MainPage.parameter.getCommandIs().equals("getLastCallDetail")){
									Thread t = new SendCommand("lastCallDetail",call);
									t.start();
									logger.info("3)last call details command send to send object from bria");
								}else if(MainPage.parameter.getCommandIs().equals("getLogs")){
									Thread t = new SendCommand("logs",call);
									t.start();
									logger.info("3)logs send to send object from bria");
								}
							}
					}
				});

		messageProcessor.startProcessing();
	}


}

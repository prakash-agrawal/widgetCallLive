package com.agilecrm.thread;

import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.agilecrm.bria.Message;
import com.agilecrm.bria.MessageBuilder;
import com.agilecrm.bria.data.AudioProperties;
import com.agilecrm.bria.data.CallHistoryEntry;
import com.agilecrm.connection.Transmit;
import com.agilecrm.main.BriaRemote;
import com.agilecrm.main.MainPage;
import com.agilecrm.model.AgileCall;
import com.agilecrm.util.FrameUtil;

public class SendCommand extends Thread {
	public String command;
	public AgileCall call;
	public static String lastSendCommand = "";
	public static Logger logger = Logger.getLogger(SendCommand.class);

	public SendCommand(String command, AgileCall callCommand) {
		this.command = command;
		this.call = callCommand;

	}

	@Override
	public void run() {
		try {
			if (command.equals("missed")) {
				if (lastSendCommand.equals("missed")) {
					return;
				}
				lastSendCommand = "missed";
				JSONObject obj = createCommandObject(command);
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("routing")) {
				lastSendCommand = "routing";
				JSONObject obj = createCommandObject(command);
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("ringing")) {
				if (lastSendCommand.equals("ringing")) {
					return;
				}
				JSONObject obj = createCommandObject(command);
				lastSendCommand = "ringing";
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("connected")) {
				if (lastSendCommand.equals("connected")) {
					return;
				}
				JSONObject obj = createCommandObject(command);
				lastSendCommand = "connected";
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("connecting")) {
				if (lastSendCommand.equals("connecting")) {
					return;
				}
				JSONObject obj = createCommandObject(command);
				lastSendCommand = "connecting";
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("failed")) {
				if (lastSendCommand.equals("failed")) {
					return;
				}
				JSONObject obj = createCommandObject(command);
				lastSendCommand = "failed";
				Transmit.addToCurrentResponse(obj);
			} else if (command.equals("busy")) {
				if (lastSendCommand.equals("busy")) {
					return;
				}
				lastSendCommand = "busy";
				JSONObject obj = createCommandObject(command);
				Transmit.addToLogResponse(obj);
			}else if (command.equals("refused")) {
				if (lastSendCommand.equals("refused")) {
					return;
				}
				lastSendCommand = "refused";
				JSONObject obj = createCommandObject(command);
				Transmit.addToLogResponse(obj);
			}else if (command.equals("ended")) {
				if (lastSendCommand.equals("ended")) {
					return;
				}
				lastSendCommand = "ended";
				JSONObject obj = createCommandObject(command);
				Transmit.addToCurrentResponse(obj);
				checkForMute();
			} else if (command.equals("lastCallDetail")) {
				if (lastSendCommand.equals("lastCallDetail")) {
					return;
				}
				lastSendCommand = "lastCallDetail";
				JSONObject obj = createCommandObject(command);
				if (null == obj) {
					sendMessage(call.getClient(), "error");
					return;
				}
				Transmit.addToLogResponse(obj);
				MainPage.parameter.clearParameters();
			} else if (command.equals("logs")) {
				lastSendCommand = "logs";
				JSONObject obj = createArrayCommand(command);
				Transmit.addToLogResponse(obj);
			}
			
			FrameUtil.showLastCommand(command, 200, 180, 100, 30, Progress.frame);
		} catch (Exception e) {
			logger.info("in send command --> " + e.getMessage());
			return;
		}
	}

	private void checkForMute() {
		try{
			if (BriaRemote.lastState.get("AudioProperties") != null) {
				AudioProperties properties = (AudioProperties) BriaRemote.lastState
						.get("AudioProperties");
				BriaRemote.briaModel.setAudioProperties(properties);
				Message audioMessage = new MessageBuilder().updateAudioProperties(
						properties).build();
				BriaRemote.apiPipe.writeMessage(audioMessage);
			}
		}catch(Exception e){
		}
	}

	public JSONObject createCommandObject(String state) {

		JSONObject obj = new JSONObject();
		String displayName = "";
		String number = MainPage.parameter.getNumber().split("@")[0];
		String callId = "";
		Integer duration = 0;
		String direction = "incoming";
		try {
			String callClient = call.getClient();

			obj.put("callType", callClient);

			if (state.equals("lastCallDetail")) {

				if (callClient.equals("Bria")) {
					displayName = BriaRemote.briaModel.getCallHistoryEntries()
							.get(0).getDisplayName();
					String lastContactedNumber = MainPage.parameter.getNumber()
							.split("@")[0];
					logger.info("last contacted number for last contact detail"
							+ lastContactedNumber);
					for (int i = 0; i < 3; i++) {
						String[] temp = BriaRemote.briaModel
								.getCallHistoryEntries().get(i).getNumber()
								.split("@");
						logger.info(temp[0]);
						if (lastContactedNumber.equalsIgnoreCase(temp[0])) {
							direction = BriaRemote.briaModel
									.getCallHistoryEntries().get(i).getType()
									.toString();
							if (direction.equals("dialed")) {
								direction = "outgoing";
							} else if (direction.equals("received")) {
								direction = "Incoming";
							} else if (direction.equals("missed")) {
								direction = "Missed";
							}
							number = lastContactedNumber;
							duration = BriaRemote.briaModel
									.getCallHistoryEntries().get(i)
									.getDuration();
							break;
						}
					}

				}
				obj.put("displayName", displayName);
				obj.put("duration", duration);
				obj.put("direction", direction);
			} else {
				if (null != call) {
					//number = call.getNumber();
					callId = call.getCallId();
					displayName = call.getDisplayName();
					direction = call.getDirection();
				}
			}

			obj.put("state", state);
			obj.put("callId", callId);
			obj.put("number", number);

		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		return obj;
	}

	/**
	 * This methods create an array object with jsonobject as element This
	 * method return the call log for a particular number - number stored in
	 * MainPage.parameter.getNumber variable
	 * 
	 * @param command
	 * @return
	 */
	public JSONObject createArrayCommand(String state) {
		String direction;
		Integer duration;
		Long startTime;

		JSONObject json = new JSONObject();

		JSONArray array = new JSONArray();
		try {
			String callClient = call.getClient();
			json.put("callType", callClient);
			json.put("state", state);
			if (callClient.equals("Bria")) {
				List<CallHistoryEntry> callHistory = BriaRemote.briaModel
						.getCallHistoryEntries();
				int limit = 0;
				String logNumber = "";
				if(MainPage.parameter.getLogNumber().get(callClient) != null){
					logNumber = MainPage.parameter.getLogNumber().get(callClient).split("@")[0];
				}
				if (callHistory != null) {
					for (int i = 0; i < callHistory.size(); i++) {
						if (limit >= 15) {
							break;
						}
						String[] temp = BriaRemote.briaModel
								.getCallHistoryEntries().get(i).getNumber()
								.split("@");
						if (logNumber.equals(temp[0])) {
							limit++;
							direction = BriaRemote.briaModel
									.getCallHistoryEntries().get(i).getType()
									.toString();
							startTime = (BriaRemote.briaModel
									.getCallHistoryEntries().get(i)
									.getTimeInitiated());
							startTime = startTime * 1000; // it unix time in
															// msecond so mul by
															// 1000;
						} else {
							continue;
						}
						if (direction.equals("dialed")) {
							direction = "Outgoing";
						} else if (direction.equals("received")) {
							direction = "Incoming";
						} else if (direction.equals("missed")) {
							direction = "Missed";
						}
						duration = BriaRemote.briaModel.getCallHistoryEntries()
								.get(i).getDuration();
						JSONObject obj = new JSONObject();

						obj.put("direction", direction);
						obj.put("duration", duration.toString());
						obj.put("startTime", startTime);
						array.put(obj);
					}
				}
				json.put("number", logNumber);
			}
			json.put("data", array);
		} catch (Exception e) {
			logger.info(e.getMessage());
		}

		return json;
	}

	public static void sendMessage(String type, String state) {
		JSONObject obj = new JSONObject();
		try {
			obj.put("callType", type);
			obj.put("state", state);
		} catch (JSONException e1) {
			logger.info(e1.getMessage());
		}
		
		Transmit.addToCurrentResponse(obj);
		/*
		 * Transmit transmit= new Transmit(); transmit.start(obj);
		 */
	}
}

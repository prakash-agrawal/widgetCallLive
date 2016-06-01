package com.agilecrm.thread;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.agilecrm.api.CallApi;
import com.agilecrm.connection.Transmit;
import com.agilecrm.main.Globals;
import com.agilecrm.main.MainPage;
import com.agilecrm.model.AgileCall;
import com.agilecrm.util.FrameUtil;

public class RunCommand extends Thread {
	public String command;
	public AgileCall call;
	public static Logger logger = Logger.getLogger(RunCommand.class);

	public RunCommand() {

	}

	public RunCommand(String command) {

		this.command = command;
	}

	public RunCommand(String command, AgileCall call) {

		this.command = command;
		this.call = call;
	}

	@Override
	public void run() {
		logger.info("2)In run command after messge received--> " + command + "  and call client is " + call.getClient());
		CallApi callObj = Globals.globalCallObject.get(call.getClient());

		if (Globals.globalCallObject.get(call.getClient()) == null) {
			SendCommand.sendMessage(call.getClient(), "closed");

			if (command.equals("sendEmptyLogs")) {

				JSONObject json = new JSONObject();
				try {
					json.put("callType", call.getClient());
					json.put("state", "logs");
					json.put("data", "");
					json.put("number", call.getNumber().split("@")[0]);
				} catch (Exception e) {
					logger.info("in run command --> " + e.getMessage());
				}
				Transmit.addToLogResponse(json);
			}
			return;
		}

		if (command.equals("startCall")) {
			SendCommand.sendMessage(call.getClient(), "routing");
			callObj.startCall(call.getNumber());
			SendCommand.lastSendCommand = "routing";
		} else if (command.equals("endCall") || command.equals("ignoreCall")
				|| command.equals("cancelCall")) {
			boolean flag = callObj.endCall(call.getCallId());
			if (!flag) {
				SendCommand.sendMessage(call.getClient(), "error");
				MainPage.parameter.clearParameters();
			}
		} else if (command.equals("answerCall")) {
			boolean flag = callObj.answerCall(call.getCallId());
			if (!flag) {
				SendCommand.sendMessage(call.getClient(), "error");
				MainPage.parameter.clearParameters();
			}
		} else if (command.equals("mute")) {
			callObj.mute(call.getCallId());
		} else if (command.equals("unMute")) {
			callObj.unmute(call.getCallId());
		} else if (command.equals("sendDTMF")) {
			callObj.sendDTMF(call.getNumber(),call.getCallId());
		} else if (command.equals("getLastCallDetail")) {
			JSONObject json = callObj.getLastCallDetail(call.getNumber(),
					call.getCallId());
			if (json != null) {
				try {
					json.put("callType", call.getClient());
					json.put("state", "lastCallDetail");
				} catch (JSONException e) {
				}
				Transmit.addToLogResponse(json);
				MainPage.parameter.clearParameters();
			}

		} else if (command.equals("endCurrentCall")) {
			SendCommand.sendMessage(call.getClient(), "error");
			MainPage.parameter.clearParameters();
		} else if (command.equals("getLogs")) {
			JSONArray array = callObj.getCallLogs(call.getNumber());
			JSONObject json = new JSONObject();
			if (array != null) {
				try {
					json.put("callType", call.getClient());
					json.put("state", "logs");
					if (array.length() > 0) {
						json.put("data", array);
					} else {
						json.put("data", "");
					}
					json.put("number", call.getNumber().split("@")[0]);
				} catch (JSONException e) {
				}
				Transmit.addToLogResponse(json);
			}
		}
		FrameUtil.showLastCommand(command, 200, 180, 100, 30, Progress.frame);
	}
}

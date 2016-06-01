package com.agilecrm.model;

import java.util.HashMap;
import java.util.Map;

public class CommandParameters {

	private  String commandIs = ""; // what command is given by agile crm
	private String number = "";
	private String callId = "";
	private String callClient = "";
	public Map<String, String> logNumber = new HashMap<String, String>();
	

	public String getCommandIs() {
		return commandIs;
	}
	public void setCommandIs(String commandIs) {
		this.commandIs = commandIs;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getCallId() {
		return callId;
	}
	public void setCallId(String callId) {
		this.callId = callId;
	}
	public String getCallClient() {
		return callClient;
	}
	public void setCallClient(String callClient) {
		this.callClient = callClient;
	}
	
	public void clearParameters(){
		this.commandIs = "";
		this.number  = "";
		this.callId = "";
		this.callClient = "";
	}
	public Map<String, String> getLogNumber() {
		return logNumber;
	}
	public void setLogNumber(Map<String, String> logNumber) {
		this.logNumber = logNumber;
	}


}

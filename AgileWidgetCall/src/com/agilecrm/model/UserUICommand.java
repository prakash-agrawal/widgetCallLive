package com.agilecrm.model;

public class UserUICommand {

	public static final String startCall = "Dial";
	public static final String endCall = "End";
	public static final String answerCall = "Answer";
	public static final String mute = "Mute";
	public static final String unMute = "UnMute";
	public static final String sendDTMF = "Send DTMF";
	public static final String blank = "";
	public static final String routing = "Dialing";
	public static final String ringing = "Ringing";
	public static final String connecting = "Connecting";
	public static final String connected = "On Call";
	public static final String failed = "Failed";
	public static final String ended = "Ended";
	public static final String initialMessage = "  Please refresh website and wait for ready state.  ";
	public static final String notReady = "Not Ready";
	public static final String connectedToAgile = "Ready";
	
	public static String command(String command){
		if(command.equalsIgnoreCase("startCall")){
			return startCall;
		}else if(command.equalsIgnoreCase("endCall") || command.equalsIgnoreCase("ignoreCall") || command.equalsIgnoreCase("cancelCall")){
			return endCall;
		}else if(command.equalsIgnoreCase("answerCall")){
			return answerCall;
		}else if(command.equalsIgnoreCase("mute")){
			return mute;
		}else if(command.equalsIgnoreCase("unMute")){
			return unMute;
		}else if(command.equalsIgnoreCase("sendDTMF")){
			return sendDTMF;
		}else if(command.equalsIgnoreCase("getLastCallDetail") || command.equalsIgnoreCase("logs") ||command.equalsIgnoreCase("lastCallDetail") || command.equalsIgnoreCase("endCurrentCall") || command.equalsIgnoreCase("getLogs")){
			return blank;
		}else if(command.equalsIgnoreCase("routing")){
			return routing;
		}else if(command.equalsIgnoreCase("ringing") || command.equalsIgnoreCase("connecting")){
			return ringing;
		}else if(command.equalsIgnoreCase("failed")){
			return failed;
		}else if(command.equalsIgnoreCase("ended")){
			return ended;
		}else if(command.equalsIgnoreCase("connecting")){
			return connecting;
		}else if(command.equalsIgnoreCase("initialMessage")){
			return initialMessage;
		}else if(command.equalsIgnoreCase("connectedToAgile")){
			return connectedToAgile;
		}else if(command.equalsIgnoreCase("notReady")){
			return notReady;
		}
	return "";
	}
}

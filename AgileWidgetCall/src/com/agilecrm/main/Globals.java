package com.agilecrm.main;

import java.util.LinkedHashMap;
import java.util.Map;

import com.agilecrm.api.CallApi;

public final class Globals {

	
	//this will create object from call inside com.agilerm.mode.AgilecallObjectNameCall
	public static final String[] callObjectName = {"Bria","Skype"};
	
	// this will contain the created object
	public static Map<String, CallApi> globalCallObject = new LinkedHashMap<String, CallApi>();
	//public static CommandParameters globalCallparameter = new CommandParameters();
	public static Map<String,Boolean> configuredWidget = new LinkedHashMap<String, Boolean>();
	public static String a1 = "c3ViLWMtMTE4Zjg0ODItOTJjMy0xMWUyLTliNjktMTIzMTNmMDIyYzkw" ;
	public static String a2 = "cHViLWMtZTRjOGZkYzItNDBiMS00NDNkLThiYjAtMmE5YzhmYWNkMjc0";
	
	
}

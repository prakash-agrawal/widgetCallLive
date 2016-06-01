package com.agilecrm.listener;

import org.apache.log4j.Logger;

import com.agilecrm.exception.AgileApplicationException;
import com.skype.Skype;
import com.skype.SkypeException;

public class SkypeListener {
	public static boolean skypeIsRunning = false;
	public static Logger logger = Logger.getLogger(SkypeListener.class);
	
	public static void registerSkypeListeners() throws AgileApplicationException {
		
		try {
			logger.info("Skype is installed --" + Skype.isInstalled());
			if(!Skype.isInstalled()){
			throw new AgileApplicationException();
			}
			
			logger.info("Skype is running --" + Skype.isRunning());
			if(!Skype.isRunning()){
				 throw new AgileApplicationException();
			}
		
		} catch (SkypeException e) {
			 throw new AgileApplicationException("closed-skype",e);
		}
		
	}
}

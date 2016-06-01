package com.agilecrm.main;

import org.apache.log4j.Logger;

import com.agilecrm.exception.AgileApplicationException;
import com.agilecrm.listener.AgileCallMonitorListener;
import com.agilecrm.listener.SkypeListener;
import com.skype.Skype;
import com.skype.SkypeException;

public class InitialiseListener {

	private static Logger logger = Logger.getLogger(InitialiseListener.class);
	public static void start() throws AgileApplicationException {

		logger.info("initialization skype");
		initialiseSkype();
		logger.info("intialization finished skype");

	}

	private static void initialiseSkype() throws AgileApplicationException {

		try {
			SkypeListener.registerSkypeListeners();
			Skype.addCallMonitorListener(new AgileCallMonitorListener());
		} catch (SkypeException e) {
			logger.info("Listener --- skype listener not registered");
			throw new AgileApplicationException("listener-not-registered", e);
		}
	}

	public static boolean checkSkypeRunning() {
		try {
			if (Skype.isRunning()) {
				return true;
			}
		} catch (SkypeException e1) {
			logger.info("Listener --- skype not running");
			return false;
		}
		return false;
	}

}

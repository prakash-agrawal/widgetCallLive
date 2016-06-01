/**

 * 
 */
package com.agilecrm.thread;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.agilecrm.connection.Transmit;
import com.agilecrm.exception.ClientValidationException;
import com.agilecrm.exception.ConnectionException;
import com.agilecrm.main.MainPage;

/**
 * @author mantra
 *
 */
public class TransmitLogQueue extends Thread {

	private String dataToSend = "";
	public static Logger logger = Logger.getLogger(TransmitLogQueue.class);

	@Override
	public void run() {

		try {
			
			while (true) {
				JSONObject responseMessage = Transmit.logQueue.take();
				Transmit transmit = new Transmit();
				this.dataToSend = transmit.getData(responseMessage);
				String url = transmit
						.buildURL(MainPage.currentUser.getDomain(),"POST");
				if (url.equals("")) {
					logger.info("5) return and not publish because the url is not defined");
					return;
				}
				boolean res = transmit.establishConnection(url, this.dataToSend);
				logger.info("5) sending done is " + res + " and url -> " + this.dataToSend);
			}
		} catch (ConnectionException e) {
			logger.info(e.getMessage());
		} catch (ClientValidationException e) {
			logger.info(e.getMessage());
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		}

	}

}

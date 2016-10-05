/**
 * 
 */
package com.agilecrm.thread;

import org.apache.log4j.Logger;
import com.agilecrm.connection.Transmit;
import com.agilecrm.exception.ClientValidationException;
import com.agilecrm.main.MainPage;
import com.agilecrm.util.SenderUtil;
import com.google.gson.JsonObject;

/**
 * @author mantra
 *
 */
public class TransmitResponseQueue extends Thread {

	private JsonObject dataToSend;
	public static Logger logger = Logger.getLogger(TransmitResponseQueue.class);

	@Override
	public void run() {

		try {
			
			while (true) {
				JsonObject responseMessage = Transmit.responseQueue.take();
				Transmit transmit = new Transmit();
				this.dataToSend = transmit.getData(responseMessage);
				logger.info("5) data taken from response queue message  and sending to util is " + this.dataToSend);
				if(MainPage.currentUser.getId() != null){
					SenderUtil.publish(MainPage.currentUser.getId() + "_Channel", this.dataToSend);
				}else{
					logger.info("channel not declared so not sending the message :: " + this.dataToSend);
				}
				
				
			}
		}catch (ClientValidationException e) {
			logger.info(e.getMessage());
		} catch (InterruptedException e) {
			logger.info(e.getMessage());
		}catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

}

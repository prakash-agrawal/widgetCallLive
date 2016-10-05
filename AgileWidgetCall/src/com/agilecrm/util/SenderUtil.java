package com.agilecrm.util;

/**
 *This class is used to publish the message..
 * 
 * @author Prakash
 * 
 */


import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import com.agilecrm.main.Globals;
import com.google.gson.JsonObject;
import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.models.consumer.PNPublishResult;
import com.pubnub.api.models.consumer.PNStatus;


public class SenderUtil
{

	public static PubNub pubnub ;
	public static Logger logger = Logger.getLogger(SenderUtil.class);
    /**
     * Publishes message to the given channel.
     * 
     * @param channel
     *            - pubnub channel.
     * @param messageJSON
     *            - pubnub message.
     * @return pubnub response.
     */
	static{
		
		PNConfiguration pnConfiguration = new PNConfiguration();
		generateHashKey(pnConfiguration);
		pubnub = new PubNub(pnConfiguration);
		logger.info("sending message service initialised ...");
	}
	
    public static void publish(String channel, JsonObject messageJSON)
    {
	try
	{
		
		pubnub.publish()
	    .message(messageJSON.toString())
	    .channel(channel)
	    .async(new PNCallback<PNPublishResult>() {
	        @Override
	        public void onResponse(PNPublishResult result, PNStatus status) {
	            // handle publish result, status always present, result if successful
	            // status.isError to see if error happened
	        	logger.info("6) message send success ----------- and status is "+ status.getStatusCode());
	        	if(status.isError()){
	        		logger.error(result);
	        	}
	        	
	        }
	    });
		
		logger.info("6) channel is ----------" + channel + " -----message state publishing is" +messageJSON.get("state"));
	}
	catch (Exception e)
	{
	    logger.error("Exception occured in PubNub " + e.getMessage());
	    try{
	    pubnub.publish().message(messageJSON.toString()).channel(channel);
	    }catch(Exception ex){}
	}
    }
/**
 * 
 */
private static void generateHashKey(PNConfiguration pnConfiguration) {
	String a1Key = Globals.a1;
	String a2Key = Globals.a2;
	pnConfiguration.setSubscribeKey(new String(Base64.decodeBase64(a1Key)));
	pnConfiguration.setPublishKey(new String(Base64.decodeBase64(a2Key)));
}
	
}
/**
 * 
 */
package com.agilecrm.connection;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import com.agilecrm.exception.ClientValidationException;
import com.agilecrm.exception.ConnectionException;
import com.agilecrm.main.MainPage;
import com.agilecrm.thread.TransmitLogQueue;
import com.agilecrm.thread.TransmitResponseQueue;

/**
 * @author mantra
 *
 */
public class Transmit {

	public static BlockingQueue<JSONObject> responseQueue = new LinkedBlockingQueue<JSONObject>();
	public static LinkedBlockingQueue<JSONObject> logQueue = new LinkedBlockingQueue<JSONObject>();
	public static Logger logger = Logger.getLogger(Transmit.class);
	private int repeat = 0;
	private int timeout = 1500;
	
	public static void start() {

		try {

			Thread tmq = new TransmitResponseQueue();
			Thread tlq = new TransmitLogQueue();
			tmq.setPriority(Thread.MAX_PRIORITY);
			tlq.start();
			tmq.start();

		} catch (Exception e) {
			logger.info(e.getMessage());
		}

	}
	
	public String getData(Object message) throws ClientValidationException {

		String data = "";
		try {
			JSONObject dataObj = (JSONObject) message;
			if (null != MainPage.currentUser.getId()) {
				String userId = MainPage.currentUser.getId().toString();
				dataObj.put("userId", userId);
				dataObj.put("type", "call");
				data = dataObj.toString();
			}
		} catch (JSONException e) {
			throw new ClientValidationException(e);
		}
		return data;
	}
	
	public boolean establishConnection(String agileURL, String urlParameters)
			throws ConnectionException {
		try {
			byte[] postData = urlParameters.getBytes();

			URL url = new URL(agileURL);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection(Proxy.NO_PROXY);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"text/plain; charset=utf-8");
			connection.setRequestProperty("Content-Length",
					Integer.toString(postData.length));

			connection.setConnectTimeout(20000);

			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
				OutputStreamWriter wr = new OutputStreamWriter(
						connection.getOutputStream());
				wr.write(urlParameters);
				wr.flush();
				wr.close();
			int code = connection.getResponseCode();
			if (code >= 200 && code < 300) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			logger.info(e.getMessage());
			throw new ConnectionException(e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
			throw new ConnectionException(e.getMessage());
		}
	}
	
	public boolean establishGetConnection(String agileURL, String urlParameters)
			throws ConnectionException {
			int repeat = 0;
			boolean flag = false;
			
			while(!flag && repeat <= 3){
				try {	
					URL url = new URL(agileURL + "?prak=" + urlParameters);
					HttpURLConnection connection = (HttpURLConnection) url
							.openConnection(Proxy.NO_PROXY);
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(timeout);
					connection.setDoInput(true);
					connection.setDoOutput(false);
					connection.setUseCaches(false);
					connection.connect();
					int code = connection.getResponseCode();
					if (code >= 200 && code < 300) {
						flag = true;
					} else {
						logger.info("in get status code > 300  count is tried for --> " + repeat);
						repeat++;
						timeout = timeout+50;
					}
				}catch (Exception e){
						logger.info("Exception occured in establishGetConnection method count is tried for --> " + repeat +"--" + e.getMessage() );
						timeout = timeout+50;
						repeat++;
				}		
			}
		return flag;	
	}	
	
	public String buildURL(String domain, String method) throws ClientValidationException {

		String agileURL = "";
		
		
		if(method.equalsIgnoreCase("get")){
				agileURL 
				= "http://localhost:8888/GetInformUser";
			if (null != domain) {
				// for beta..
				//agileURL = "https://" + domain+ "-dot-sandbox-dot-agilecrmbeta.appspot.com/GetInformUser";
				// for version..
				//agileURL = "https://" + domain+ "-dot-24-7-dot-agile-crm-cloud.appspot.com/GetInformUser";
				// for live ....
				agileURL = "https://"+domain+".agilecrm.com/GetInformUser";
				// for development in local....
			}	
		}else{
				agileURL = "http://localhost:8888/InformUser";
			if (null != domain) {
				// for beta..
				//agileURL = "https://" + domain+ "-dot-sandbox-dot-agilecrmbeta.appspot.com/InformUser";
				// for version..
				//agileURL = "https://" + domain+ "-dot-24-7-dot-agile-crm-cloud.appspot.com/InformUser";
				// for live ....
				agileURL = "https://"+domain+".agilecrm.com/InformUser";
				// for development in local....
			}
		}
		return agileURL;
	}
	

	public static void addToCurrentResponse(JSONObject responseObj) {
		try {
			responseQueue.put(responseObj);
		} catch (InterruptedException e) {
			logger.info("4) Exception occured while adding in current response" + responseObj.toString());
			e.printStackTrace();
		}
		logger.info("4) " + responseObj.toString() + "added in queue to send..");
	}

	public static void addToLogResponse(JSONObject responseObj) {
		logQueue.add(responseObj);
		logger.info("4) " + responseObj.toString()+ "added in queue to send..");
	}

}

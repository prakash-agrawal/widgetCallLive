/*******************************************************************************
 * (C) Copyright 2014 - CounterPath Corporation. All rights reserved.
 * 
 * THIS SOURCE CODE IS PROVIDED AS A SAMPLE WITH THE SOLE PURPOSE OF DEMONSTRATING A POSSIBLE
 * USE OF A COUNTERPATH API. IT IS NOT INTENDED AS A USABLE PRODUCT OR APPLICATION FOR ANY 
 * PARTICULAR PURPOSE OR TASK, WHETHER IT BE FOR COMMERCIAL OR PERSONAL USE.
 * 
 * COUNTERPATH DOES NOT REPRESENT OR WARRANT THAT ANY COUNTERPATH APIs OR SAMPLE CODE ARE FREE
 * OF INACCURACIES, ERRORS, BUGS, OR INTERRUPTIONS, OR ARE RELIABLE, ACCURATE, COMPLETE, OR 
 * OTHERWISE VALID.
 * 
 * THE COUNTERPATH APIs AND ASSOCIATED SAMPLE APPLICATIONS ARE PROVIDED "AS IS" WITH NO WARRANTY, 
 * EXPRESS OR IMPLIED, OF ANY KIND AND COUNTERPATH EXPRESSLY DISCLAIMS ANY AND ALL WARRANTIES AND 
 * CONDITIONS, INCLUDING, BUT NOT LIMITED TO, ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR 
 * A PARTICULAR PURPOSE, AVAILABLILTIY, SECURITY, TITLE AND/OR NON-INFRINGEMENT.  
 * 
 * YOUR USE OF COUNTERPATH APIS AND SAMPLE CODE IS AT YOUR OWN DISCRETION AND RISK, AND YOU WILL 
 * BE SOLELY RESPONSIBLE FOR ANY DAMAGE THAT RESULTS FROM THE USE OF ANY COUNTERPATH APIs OR
 * SAMPLE CODE INCLUDING, BUT NOT LIMITED TO, ANY DAMAGE TO YOUR COMPUTER SYSTEM OR LOSS OF DATA. 
 * 
 * COUNTERPATH DOES NOT PROVIDE ANY SUPPORT FOR THE SAMPLE APPLICATIONS.
 * 
 * TO OBTAIN A COPY OF THE OFFICIAL VERSION OF THE TERMS OF USE FOR COUNTERPATH APIs, PLEASE 
 * DOWNLOAD IT FROM THE WEB_SITE AT: http://www.counterpath.com/apitou
 ******************************************************************************/
package com.agilecrm.bria;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;


class PipeWriter {
	
	private static final String CRLF = "\r\n";
	private RandomAccessFile pipeFile;
	private ExecutorService writingExecutor;
	private LinkedBlockingQueue<Message> messageQueue;
	public static Logger logger = Logger.getLogger(PipeWriter.class);

	/**
	 * 
	 * @param pipeFile
	 */
	public PipeWriter(RandomAccessFile pipeFile) {
		this.pipeFile = pipeFile;
		this.writingExecutor = Executors.newSingleThreadExecutor();
		this.messageQueue = new LinkedBlockingQueue<Message>();
		this.startWriteLoop();
		
	}
	
	public void addToWritingQueue(Message message) {
		this.messageQueue.add(message);
	}
	
	private void startWriteLoop() {
		
		Runnable writeRunnable = new Runnable() {

			public void run() {
				while (true) {
					try {
						Message message = messageQueue.take();
						writeMessageToPipe(message);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		this.writingExecutor.execute(writeRunnable);
	}
	
	private void writeMessageToPipe(Message message) {
				
		boolean messageSent = false;
		int count = 0 ;
		try {
			StringBuilder requestString = new StringBuilder();
			
			requestString.append(message.getStartLine());
			requestString.append(CRLF);
			
			Map<String, String> headers = message.getHeaders();
			
			// Need to set the Content-Length based on the body size
			String xmlBody = Utilities.xmlDocumentToString(message.getXmlDocument());
			byte[] bytes = xmlBody.getBytes(Utilities.UTF8_CHARSET);
			int contentLength = bytes.length;
			headers.put("Content-Length", contentLength + "");
			
			for (String key : headers.keySet()) {
				requestString.append(key);
				requestString.append(": ");
				requestString.append(headers.get(key));
				requestString.append(CRLF);
			}
			
			// Then write the body
			if (bytes.length > 0) {
				requestString.append(xmlBody);
			}
			
			byte[] bytesToWrite = requestString.toString().getBytes(Utilities.UTF8_CHARSET);
			pipeFile.write(bytesToWrite);
			//System.err.println("Sent message with transaction id:" + headers.get("Transaction-ID"));
			//System.err.println(new String(bytesToWrite));
			messageSent = true;
			count = 0;
			

		} catch (IOException e) {
			logger.info("Error writing message.");
		} finally {
			// Requeue the message if it wasn't successful
			if (!messageSent && count < 2) {
				count++;
				this.messageQueue.add(message);
			}
		}
	}
}

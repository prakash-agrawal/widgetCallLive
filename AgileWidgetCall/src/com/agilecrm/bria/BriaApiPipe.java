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

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.LinkedBlockingQueue;

import com.agilecrm.bria.enums.CallType;
import com.agilecrm.bria.enums.MessageBodyType;
import com.agilecrm.bria.enums.StatusType;
import com.agilecrm.exception.PipeNotAvailableException;

/**
 * <p>
 * Writes messages to a Bria named pipe and provides access to messages read
 * from the pipe. Users of this class can consume messages by registering
 * handlers to the {@link MessageProcessor} this class exposes. Usage examples:
 * </p>
 * 
 * <h2>Writing</h2>
 * <p>
 * The following code sends a logout message to the Bria client.
 * </p>
 * 
 * <pre>
 * {
 * 	&#064;code
 * 	BriaApiPipe pipe = new BriaApiPipe();
 * 	Message message = new MessageBuilder().logout.build();
 * 	pipe.writeMessage(message);
 * }
 * </pre>
 * 
 * <h2>Consuming a message</h2>
 * <p>
 * The following code consumes a phone status response sent by the Bria client.
 * </p>
 * 
 * <pre>
 * {@code
 * BriaApiPipe pipe = new BriaApiPipe();
 * this.messageProcessor.registerHandlerForMessageType(MessageBodyType.RESPONSE_STATUS_PHONE, new MessageHandler() {
 * 	public void handle(Message message) {
 * 		System.out.println(message);
 * 	}
 * });
 * </pre>
 * 
 * <p>
 * An application should only create a single {@code BriaApiPipe} during
 * execution.
 * </p>
 *
 * @see Message
 * @see MessageBuilder
 * @see MessageProcessor
 */
public class BriaApiPipe {

	private PipeWriter pipeWriter;
	private PipeReader pipeReader;
	private MessageProcessor messageProcessor;
	public static RandomAccessFile file;
	public static boolean isPipeInitialised = false;

	/**
	 * Creates a connection the the Bria named pipe.
	 * 
	 * @throws PipeNotAvailableException
	 *             if a Bria named pipe is not available
	 */
	public void startBriaApiPipe() {

		RandomAccessFile pipeFile = null;
		// try for x-ilte
		try {
			pipeFile = new RandomAccessFile("\\\\.\\pipe\\apixlite", "rw");
		} catch (FileNotFoundException e) {
		}
		// try for bria 4 and bria 3
		if (pipeFile == null) {
			try {
				pipeFile = new RandomAccessFile("\\\\.\\pipe\\apipipe", "rw");
			} catch (FileNotFoundException e) {
			}
		}
		if (pipeFile == null) {
			isPipeInitialised = false;
			throw new PipeNotAvailableException();
		}
		isPipeInitialised = true;
		file = pipeFile;
		LinkedBlockingQueue<Message> messageQueue = new LinkedBlockingQueue<Message>();
		this.pipeWriter = new PipeWriter(pipeFile);
		this.pipeReader = new PipeReader(pipeFile, messageQueue);
		this.messageProcessor = new MessageProcessor(messageQueue);
		registerDefaultPipeEventHandlers();

		this.pipeReader.startReadingPipe();
		isPipeInitialised = true;
	}

	public void writeMessage(Message message) {
		this.pipeWriter.addToWritingQueue(message);
	}

	public MessageProcessor getMessageProcessor() {
		return this.messageProcessor;
	}

	private void registerDefaultPipeEventHandlers() {

		final BriaApiPipe apiPipe = this;

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_AUDIO_SETTINGS_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.AUDIO_PROPERTIES).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_AUTHENTICATION_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder()
								.authenticationStatus().build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_CALL_HISTORY_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().callHistory(100,
								CallType.ALL).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_CALL_OPTION_CHANGE, new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.CALL_OPTIONS).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_CALL_STATUS_CHANGE, new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.CALL).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_MISSED_CALL_OCCURRED,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.MISSED_CALL).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_MWI_COUNT_CHANGE, new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.VOICEMAIL).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.EVENT_PHONE_STATUS_CHANGE,
				new MessageHandler() {
					public void handle(Message message) {
						Message request = new MessageBuilder().status(
								StatusType.PHONE).build();
						apiPipe.writeMessage(request);
					}
				});

		messageProcessor.registerHandlerForMessageType(
				MessageBodyType.BODYLESS, new MessageHandler() {
					public void handle(Message message) {

						if (message.getResponseCode() > 0
								&& message.getResponseCode() != 200) {
							System.err
									.println("Received an error message from the Bria client. You may wish to override a MessageBodyType.BODYLESS handler to handle any error responses. Message: ");
							System.err.println(Utilities
									.xmlDocumentToString(message
											.getXmlDocument()));
						}
					}
				});
	}

}

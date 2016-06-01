package com.agilecrm.listener;

import com.skype.ChatMessage;
import com.skype.ChatMessageListener;
import com.skype.SkypeException;

public class AgileChatMessageListener implements ChatMessageListener {
	
	public Object obj1 ;
	public Object obj2 ;

	public AgileChatMessageListener(){
		this.obj1 = new Object();
		this.obj2 = new Object(); 
	}
	
	@Override
	public void chatMessageReceived(ChatMessage message) throws SkypeException {
		synchronized (obj1) {
			System.out.println("chat message received");
			System.out.println(message.getId() + " conversation started by " + message.getSenderDisplayName()); 
			System.out.println("message = " + message.getContent());
		}
	}

	@Override
	public void chatMessageSent(ChatMessage message) throws SkypeException {
		synchronized (obj2) {
			System.out.println("chat message sent to");
			System.out.println(message.getId() + " message sent by " + message.getSender());
			System.out.println("message = " + message.getContent());
		}

	}

}

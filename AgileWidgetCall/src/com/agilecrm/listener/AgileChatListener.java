package com.agilecrm.listener;

import com.skype.ChatListener;
import com.skype.User;

public class AgileChatListener implements ChatListener {

	@Override
	public void userAdded(User guest) {
			/*System.out.println(guest.DISPLAYNAME + " added for chating");*/
	}

	@Override
	public void userLeft(User guest) {
		/*System.out.println(guest.DISPLAYNAME + " left from chating");*/
	}

}

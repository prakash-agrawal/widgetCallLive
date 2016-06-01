package com.agilecrm.util;

import java.awt.Color;
import java.awt.Image;
import java.net.URL;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.Border;

import org.apache.log4j.Logger;

import com.agilecrm.api.CallApi;
import com.agilecrm.image.ImageClass;
import com.agilecrm.main.Globals;
import com.agilecrm.model.UserUICommand;

public class FrameUtil {

	public static JLabel text ;
	public static JLabel message ;
	public static JLabel briaStatus;
	public static JLabel skypeStatus;
	public static Logger logger = Logger.getLogger(FrameUtil.class);
	
	public static void updateLogo(String name,int x,int y, int width, int height,JFrame desktopFrame){
		URL agileText = new ImageClass().getImageAsUrl(name);
		Image img = new ImageIcon(agileText).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		 JLabel progressText = new JLabel(new ImageIcon(img));
            progressText.setBounds(x,y,width,height);
            desktopFrame.add(progressText);
            desktopFrame.repaint();
            desktopFrame.revalidate();
	}

	public static void showLastCommand(String command,int x,int y, int width, int height,JFrame desktopFrame){
		if(text != null){
			desktopFrame.remove(text);
			desktopFrame.repaint();
		}
		String textCommand = UserUICommand.command(command);
		 JLabel progressText = new JLabel(textCommand);
         progressText.setBounds(x,y,width,height);
         desktopFrame.add(progressText);
         desktopFrame.repaint();
         text = progressText;
	}
	
	public static void showMessage(String command,int x,int y, int width, int height,JFrame desktopFrame){
		if(message != null){
			desktopFrame.remove(message);
			desktopFrame.repaint();
		}
		String textCommand = UserUICommand.command(command);
		 JLabel progressText = new JLabel(textCommand);
         progressText.setBounds(x,y,width,height);
         Border border = BorderFactory.createMatteBorder(1, 0, 0, 0, Color.lightGray);
         progressText.setBorder(border);
         progressText.setForeground(Color.gray);
         desktopFrame.add(progressText);
         desktopFrame.repaint();
         desktopFrame.revalidate();
         message = progressText;
	}
	
	public static void removeMessage(JFrame desktopFrame){
		if(message != null){
			desktopFrame.remove(message);
			desktopFrame.repaint();
		}
	}
	
	
	
	public static void updateStatus(JFrame desktopFrame){
		int x = 40;
		int y = 165;
		int width = 15;
		int height = 15;
		URL greentickUrl = new ImageClass().getImageAsUrl("greentick.png");
		URL redcrossUrl = new ImageClass().getImageAsUrl("redcross.png");
		Image img1 = new ImageIcon(greentickUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		Image img2 = new ImageIcon(redcrossUrl).getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		for(Entry<String, CallApi> entry : Globals.globalCallObject.entrySet()){
			JLabel status;
			if(entry.getValue() != null){
				status  = new JLabel(new ImageIcon(img1));
				status.setBounds(x,y,width,height);
		        
			}else{
				status = new JLabel(new ImageIcon(img2));
				status.setBounds(x,y,width,height);
			}
			if(entry.getKey().equals("Bria")){
				if(briaStatus != null){
					desktopFrame.remove(briaStatus);
					desktopFrame.repaint();
				}
				briaStatus = status;
			}else if(entry.getKey().equals("Skype")){
				if(skypeStatus != null){
					desktopFrame.remove(skypeStatus);
					desktopFrame.repaint();
				}
				skypeStatus = status;
			}
			desktopFrame.add(status);
	        desktopFrame.repaint();
	        logger.info("inside update status frame---------");
			x = x+35;
		}
		
		

	}
}

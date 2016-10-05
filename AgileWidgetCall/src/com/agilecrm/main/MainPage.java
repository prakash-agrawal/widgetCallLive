package com.agilecrm.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.agilecrm.api.CallApi;
import com.agilecrm.bria.BriaApiPipe;
import com.agilecrm.bria.MessageBuilder;
import com.agilecrm.connection.Transmit;
import com.agilecrm.image.ImageClass;
import com.agilecrm.model.AgileCall;
import com.agilecrm.model.AgileUser;
import com.agilecrm.model.CommandParameters;
import com.agilecrm.thread.Progress;
import com.agilecrm.thread.RunCommand;
import com.agilecrm.thread.SendCommand;
import com.agilecrm.util.FrameUtil;
import com.skype.Skype;
import com.skype.SkypeException;
import com.skype.Call.DTMF;

public class MainPage {

	public static AgileUser currentUser = new AgileUser(); // storing the
															// parameter in user
															// variable
	public static CommandParameters parameter = new CommandParameters();
	public static int count = 0; // count the progress of progress bar
	private static String userPath = System.getProperty("user.dir");
	public static boolean hasExtension = false;
	public static Logger logger;
	public static void main(String arr[]) {
				
/*		if(!SystemUtils.IS_OS_WINDOWS){
		          JOptionPane.showMessageDialog(null,
		  					" Sorry, this program can only be run on Windows OS","Error",0);
				System.exit(0);
		}*/
		
			System.setProperty("log", userPath);
			logger = Logger.getLogger(MainPage.class);
			logger.info(".......##$$$$$$$...........................");
			logger.info("Application initialised - path  " + userPath);
			logger.info("version 2.0 date 29/09/2016 6:00 pm");
			
			
			
		ServerSocket listener = null;
		//boolean updateUI = false;
		try {
			showUiToClient();
			count = 10;
			listener = new ServerSocket(33333);
			count = 20;
			InputStream success = new ImageClass()
					.getImageAsStream("success.png");
			count = 30;
			checkForExistingUserId(null);
			Transmit.start();
			SendCommand.sendMessage("Agile", "initializing");
			BufferedImage biSuccess = ImageIO.read(success);
			count = 50;
			startCallApps();
			count = 100;
			// this thread sends the response from the client to the agile
			// server.
			BufferedReader reader;
			String message = null;
			while (true) {

				Socket socket = listener.accept();
				try {
					
					reader = new BufferedReader(new InputStreamReader(
							socket.getInputStream()));
					String line = reader.readLine().trim();
					message = line.substring(line.indexOf('?') + 1,
							line.lastIndexOf('?'));
					logger.info("1)message received ------------------------" + message);
					// this writting image is just hack so that error does not
					// occur in application
					ImageIO.write(biSuccess, "png", socket.getOutputStream());
				} finally {
					socket.close();
				}
				setDataAndRun(message);
			}

		} catch (Exception e) {
			logger.info("In main " + e.getMessage());
			System.exit(0);
		} finally {
			try {
				listener.close();
			} catch (IOException e) {
				logger.info("In main " + e.getMessage());
				System.exit(0);
			}
		}
	}

	/**
	 * This will read the xml file and load user id to local parameter if the userid doesnot match to the existing user id
	 */
	private static void checkForExistingUserId(String userId) {
		
		
		// we will only read the userid if the currentuser id is null or doesnot match with the existing one
		if(userId != null && currentUser.getId()!=null && !userId.equalsIgnoreCase(currentUser.getId())){
			return;
		}
		
		File file = new File(userPath+File.separator+"log"+File.separator+"appuser.xml");
		if(!file.exists()){
			logger.info("user file doesnot exist to read from and returning");
			return;
		}
		String id = readIdFromXML(file);
		if(id != null || !id.equalsIgnoreCase("null") || !id.equalsIgnoreCase("")){
			currentUser.setId(id);
		}
	}

/**
 * we are reading user id from the xml file and send null if file not present
 */
	private static String readIdFromXML(File file) {
		String id = null;
		SAXBuilder saxBuilder = new SAXBuilder();
		try {
			org.jdom2.Document document = saxBuilder.build(file);
			System.out.println("Root element :" + document.getRootElement().getName());
			 org.jdom2.Element usersElement = document.getRootElement();
			id = usersElement.getChild("user").getChildText("id");
			logger.info("id found for user and id is ---> " + id);
		} catch (JDOMException | IOException e) {
			logger.info("exception occured in reading user data from xml -->" + e.getMessage() );
		}
		return id;
	}

	/**
	 * this is used to write the user id in xml file
	 * @param id
	 */
	private static void writeIdToXML(String id) {
		// check if the folder exist or not... If not then return from the function
		if(currentUser.getId() != null && currentUser.getId().equalsIgnoreCase(id)){
			return;
		}
		currentUser.setId(id);
		 Path path = Paths.get(userPath+File.separator +"log");
         if(Files.notExists(path,LinkOption.NOFOLLOW_LINKS)){
        	 logger.info("data not written to xml and returning in wrtiting user data as file doesnot exist");
        	return; 
         }
		//create xml document................
		DocumentBuilderFactory dbFactory =
		         DocumentBuilderFactory.newInstance();
		
		try {
			DocumentBuilder dBuilder = 
			        dbFactory.newDocumentBuilder();
			Document doc = dBuilder.newDocument();
			 // root element
	         Element rootElement = doc.createElement("users");
	         doc.appendChild(rootElement);

	         //child of root
	         Element user = doc.createElement("user");
	         rootElement.appendChild(user);
	         
	         // element inside user tag
	         Element userId = doc.createElement("id");
	         userId.appendChild(doc.createTextNode(id));
	         user.appendChild(userId);
	         
	         //write to xml file now.................
	         TransformerFactory transformerFactory = TransformerFactory.newInstance();
	         Transformer transformer = transformerFactory.newTransformer();
	         DOMSource source = new DOMSource(doc);
	        
	         File file = new File(userPath+File.separator+"log"+File.separator+"appuser.xml");
	         
	         if(!file.exists()){
	        	boolean bool = file.createNewFile();
	        	if(!bool){
	        		logger.info("error in creating xml file");
	        		return;
	        	}
	         }else{
	        	 logger.info("file already exists and writing users data to existing file");
	         }
	         
	         StreamResult result = new StreamResult(file);
	         transformer.transform(source, result);
	         
		} catch (Exception e) {
			logger.info("Error whiile writing id to users xml ---->" + e.getMessage());
		} 
	}

	private static void showUiToClient() {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {

				URL logo = new ImageClass().getImageAsUrl("logo.png");
				URL agileText = new ImageClass()
						.getImageAsUrl("agileImage.png");
				URL briaUrl = new ImageClass().getImageAsUrl("bria.png");
				URL skypeUrl = new ImageClass().getImageAsUrl("skype.png");

				JFrame desktopFrame = new JFrame();
				desktopFrame.setSize(new Dimension(300, 240));
				desktopFrame.setLayout(null);
				desktopFrame.setIconImage(new ImageIcon(logo).getImage());
				desktopFrame.setTitle("Agile CRM ");
				desktopFrame.getContentPane().setBackground(Color.white);
				desktopFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				// Showing Image ..............
				// Image img = new
				// ImageIcon(agileText).getImage().getScaledInstance(150, 100,
				// Image.SCALE_SMOOTH);
				JLabel picLabel = new JLabel(new ImageIcon(agileText));
				picLabel.setName("headerIcon");
				picLabel.setBounds(0, 0, 300, 100);
				desktopFrame.add(picLabel);
				// Showing image completed.........

				// Showing progress bar..............
				JProgressBar progressBar = new JProgressBar();
				progressBar.setValue(0);
				progressBar.setBorder(null);
				progressBar.setForeground(Color.decode("#5cb85c"));
				progressBar.setStringPainted(true);
				progressBar.setBounds(75, 110, 150, 30);
				desktopFrame.add(progressBar);

				// showing progress bar complete

				// Showing Image texxt..............

				JLabel progressText = new JLabel("Connecting ... ");
				progressText.setBounds(170, 175, 100, 30);
				progressText.setName("status");
				desktopFrame.add(progressText);
				// Showing image completed.........

				// showing Bria image
				Image img1 = new ImageIcon(briaUrl).getImage()
						.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
				JLabel briaIcon = new JLabel(new ImageIcon(img1));
				briaIcon.setBounds(5, 165, 50, 50);
				desktopFrame.add(briaIcon);
				// showig bria image completed

				// showing Skype image
				Image img2 = new ImageIcon(skypeUrl).getImage()
						.getScaledInstance(35, 35, Image.SCALE_SMOOTH);
				JLabel skypeIcon = new JLabel(new ImageIcon(img2));
				skypeIcon.setBounds(50, 165, 50, 50);
				desktopFrame.add(skypeIcon);
				// showig bria image completed

				Thread t = new Progress(progressBar, progressText, desktopFrame);

				desktopFrame.setLocationRelativeTo(null);
				desktopFrame.setVisible(true);
				desktopFrame.setResizable(false);
				// Showing progress bar completed.........
				try {
					t.start();
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private static void setDataAndRun(String message) {
		if (null == message) {
			return;
		}
		try {
			
			String[] data = message.split(";");
			String numberBefore = data[1].substring(data[1].indexOf("=") + 1);
			
			message = URLDecoder.decode(message, "UTF-8");
			//System.out.println(message);
			System.out.println(DTMF.values());
			data = message.split(";");
			System.out.println(new MessageBuilder().sendDTMF("123").build().toString());
			String command = data[0].substring(data[0].indexOf("=") + 1);
			String number = data[1].substring(data[1].indexOf("=") + 1);
			String callId = data[2].substring(data[2].indexOf("=") + 1);
			String domain = data[3].substring(data[3].indexOf("=") + 1);
			String id = data[4].substring(data[4].indexOf("=") + 1);
			String callClient = data[5].substring(data[5].indexOf("=") + 1);
			String extension = "";
			if(data.length >6 ){
				extension = data[6].substring(data[6].indexOf("=") + 1);
				if(command.equalsIgnoreCase("startCall") && extension != null && !extension.equals("")){
					hasExtension = true;
					List<String> extList = new ArrayList<String>();
					for(int i=0;i<extension.length();i++){
						extList.add(extension.substring(i, i+1));
					}
					parameter.setExtension(extList);
					
				}
			}
			

		      // Create a Pattern object to decode the phone number 
				  String pattern = "\\+{1}.*";
			      Pattern r = Pattern.compile(pattern);
			      Matcher m = r.matcher(numberBefore);
			      if (m.matches()) {
			    	  m = r.matcher(number);
			    	  if (!m.matches()) {
			    		  number = "+" + number.trim();
				      }
			      }
			      number = number.replaceAll("[\\s\\-\\(\\)]+","");
			      if(number.equals("")){
			    	  return;
			      }
			      
			currentUser.setDomain(domain);
			writeIdToXML(id);
			//currentUser.setId(id);

			if (command.equals("testConnection")) {
				//MainPage.parameter.clearParameters();
				String[] callObjectApps = Globals.callObjectName;
				for (String callObjectApp : callObjectApps) {
					Globals.configuredWidget.put(callObjectApp, true);
				}
				return;
			}else if(command.equals("notConfigured")){
				MainPage.parameter.clearParameters();
				Globals.configuredWidget.put(callClient, false);
				return;
			}else if(command.equals("busy")){
				//MainPage.parameter.clearParameters();
				return;
			}
			

			if (!checkAppStarted(callClient)
					|| Globals.globalCallObject.get(callClient) == null) {
				if (!startCallApp(callClient)) {  
					if (command.equals("getLogs")) {
						AgileCall call = new AgileCall();
						call.setCallId(callId);
						call.setClient(callClient);
						call.setNumber(number);
						Thread t = new RunCommand("sendEmptyLogs", call);
						t.start();
						t.join();
						return;
					}
					SendCommand.sendMessage(callClient, "closed");
					Globals.globalCallObject.put(callClient, null);
					FrameUtil.updateStatus(Progress.frame);
					return;
					}
			}

			parameter.setCallId(callId);
			if (!(command.equals("mute") || command.equals("unMute") || command
					.equals("sendDTMF") || command.equals("getLogs"))) {
				parameter.setCommandIs(command);
				parameter.setNumber(number);
				parameter.setCallClient(callClient);
			}else if(command.equals("getLogs")){
				parameter.setCommandIs("getLogs");
				parameter.logNumber.put(callClient, number);
			}

			AgileCall call = new AgileCall();
			call.setCallId(callId);
			call.setClient(callClient);
			call.setNumber(number);

			Thread t = new RunCommand(command, call);
			t.start();
			t.join();
		} catch (Exception e) {
			return;
		}
	}

	/**
	 * this will create an object with the name given in Globals Then it will
	 * run start method of the object If the sun is success it will put the
	 * object inside map with the key as name otherwise it will put null with
	 * the key as name
	 */
	private static void startCallApps() {

		String[] callObjectApps = Globals.callObjectName;
		for (String callObjectApp : callObjectApps) {
			Globals.configuredWidget.put(callObjectApp, true);
			if (Globals.globalCallObject.get(callObjectApp) != null) {
				continue;
			}
			String className = "com.agilecrm.model.Agile" + callObjectApp
					+ "Call";
			try {
				Class<?> clazz = Class.forName(className);
				Object obj = clazz.newInstance();
				CallApi call = (CallApi) obj;
				Globals.globalCallObject.put(callObjectApp, call.startApp());
			} catch (Exception e) {
				Globals.globalCallObject.put(callObjectApp, null);
			}
		}
	}

	/**
	 * this will create an object with the name given in Globals Then it will
	 * run start method of the object If the sun is success it will put the
	 * object inside map with the key as name otherwise it will put null with
	 * the key as name
	 */
	private static boolean startCallApp(String appName) {

		boolean flag = false;
		String className = "com.agilecrm.model.Agile" + appName + "Call";
		try {
			Class<?> clazz = Class.forName(className);
			Object obj = clazz.newInstance();
			CallApi call = (CallApi) obj;
			call = call.startApp();
			Globals.globalCallObject.put(appName, call);
			if (call != null) {
				flag = true;
			}

		} catch (Exception e) {
			Globals.globalCallObject.put(appName, null);
		}

		FrameUtil.updateStatus(Progress.frame);

		return flag;
	}

	/**
	 * list all the method to chec whether the app is started or not...
	 * 
	 * @param appName
	 * @return
	 */
	private static boolean checkAppStarted(String appName) {
		boolean flag = false;
		if (appName.equals("Bria")) {
			if (count < 10) {
				count++;
				try {
					if (BriaApiPipe.file.readBoolean()) {
						flag = true;
					} else {
						flag = false;
					}
				} catch (Exception e) {
					flag = false;
				}
			} else {
				if (BriaApiPipe.isPipeInitialised) {
					flag = true;
				}
			}
		} else if (appName.equals("Skype")) {
			try {
				if (Skype.isRunning()) {
					flag = true;
				}
			} catch (SkypeException e) {
			}
		}
		return flag;
	}
}
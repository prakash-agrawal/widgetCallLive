/**
 * 
 */
package com.agilecrm.thread;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import com.agilecrm.main.MainPage;
import com.agilecrm.util.FrameUtil;

/**
 * @author mantra
 *
 */
public class Progress extends Thread implements ActionListener {

	private JProgressBar progressBar;
	private JLabel progressText;
	public static JFrame frame;
	private JButton close;

	public Progress(JProgressBar bar, JFrame frm) {
		frame = frm;
		progressBar = bar;
	}

	public Progress(JProgressBar bar, JLabel text, JFrame desktopFrame) {
		frame = desktopFrame;
		progressBar = bar;
		progressText = text;
	}

	@Override
	public void run() {

		try {
			progressBar.setValue(0);
			Thread.sleep(500);

			int delay = 20;
			for (int i = 4; i <= 100; i = i + 2) {
				progressBar.setValue(i);
				if (i >= MainPage.count) {
					i = i - 1;
					delay = 100;
				} else {
					delay = 20;
				}
				if (i >= MainPage.count && MainPage.count == 50) {
					i = MainPage.count;
					delay = 20;
				}
				Thread.sleep(delay);
			}
			while(true){
				if (MainPage.count < 100) {
					Thread.sleep(3000);
				}else{
					break;
				}
			}
			
			MainPage.count = 0;
			// removing progres bar....
			frame.remove(progressBar);

			// adding close button.....
			close = new JButton("Disconnect");
			close.setBackground(Color.blue);
			close.setForeground(Color.white);
			close.setFont(new Font("Aerial", Font.BOLD, 14));
			close.setBounds(75, 110, 150, 30);
			frame.add(close);
			frame.remove(progressText);
			//progressText.setText("Connected ");

			//frame.setSize(new Dimension(300, 280));
			frame.repaint();
			frame.revalidate();
			close.setActionCommand("close");
			close.addActionListener(this);
			// frame.setState(Frame.ICONIFIED);

			FrameUtil.updateStatus(frame);
/*			FrameUtil.showMessage("initialMessage", 3, 215, 285, 30, frame);
			FrameUtil.showLastCommand("notReady", 200, 180, 80, 30, frame);*/

		} catch (InterruptedException e) {

		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getActionCommand().equals("close")) {
			System.exit(0);
		}

	}

}

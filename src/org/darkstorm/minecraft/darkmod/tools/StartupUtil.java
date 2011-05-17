package org.darkstorm.minecraft.darkmod.tools;

import java.io.File;
import java.util.*;

import javax.swing.*;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.ui.*;
import org.darkstorm.tools.io.StreamRedirectFactory;

public class StartupUtil {
	private String username, password, sessionID;

	public boolean handleLogin(String username, String password) {
		LoginUtil loginUtil = new LoginUtil();
		if(password != null) {
			loginUtil.login(username, password);
			if(!loginUtil.isLoggedIn())
				return false;
			this.username = loginUtil.getUsername();
			this.password = loginUtil.getPassword();
			sessionID = loginUtil.getSessionID();
			updateIfNecessary(loginUtil, null);
		} else {
			loginUtil.playOffline(username);
			this.username = username;
		}
		return true;
	}

	public LoginUI handleLoginWithUI() {
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		LoginUI loginUI = new LoginUI();
		ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ui.remove(ui.getCanvas());
		ui.add(loginUI, "Center");
		ui.pack();
		LoginUtil loginUtil = loginUI.getLoginUtil();
		while(!loginUtil.isLoggedIn() && !loginUtil.isPlayingOffline()) {
			loginUI.authenticate();
			try {
				Thread.sleep(500);
			} catch(InterruptedException exception) {
				exception.printStackTrace();
			}
		}
		username = loginUtil.getUsername();
		password = loginUtil.getPassword();
		sessionID = loginUtil.getSessionID();
		if(loginUtil.isLoggedIn() && loginUI.isCheckForUpdatesSelected())
			updateIfNecessary(loginUtil, loginUI);
		return loginUI;
	}

	public void handleUI() {
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		JButton screenshotButton = ui.getScreenshotButton();
		screenshotButton.setEnabled(true);
		if(!darkMod.isPlayingOffline()) {
			JMenuItem newSessionIDItem = ui.getNewSessionIDItem();
			newSessionIDItem.setEnabled(true);
		}
	}

	public void clearLoginUI(LoginUI loginUI) {
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		ui.remove(loginUI);
		ui.add(ui.getCanvas(), "Center");
		ui.pack();
		ui.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	}

	private void updateIfNecessary(LoginUtil loginUtil, LoginUI loginUI) {
		UpdateUtil updateUtil = new UpdateUtil(loginUtil.getLatestVersion(),
				username, loginUtil.getDownloadTicket(), loginUI);
		if(updateUtil.updateDarkMod())
			restart();
		updateUtil.updateMinecraft();
	}

	private void restart() {
		List<String> command = createCommand();
		startProcess(command);
	}

	private List<String> createCommand() {
		ArrayList<String> command = new ArrayList<String>();

		command.add("java");
		command.add("-Xmx1024M");
		File jar = Tools.getCurrentJar();
		command.add("-jar");
		String jarName = jar.getName();
		if(jarName.contains(" "))
			command.add("\"" + jar.getName() + "\"");
		else
			command.add(jarName);

		command.add("-u");
		command.add(username);

		if(password != null) {
			command.add("-p");
			command.add(password);
		}
		if(sessionID != null) {
			command.add("-s");
			command.add(sessionID);
		}
		return command;
	}

	private void startProcess(List<String> command) {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		try {
			if(ui != null) {
				ui.setVisible(false);
				ui.dispose();
			}
			Process process = processBuilder.start();
			StreamRedirectFactory.createInputToOutputRedirect(process
					.getInputStream(), System.out);
			StreamRedirectFactory.createInputToOutputRedirect(process
					.getErrorStream(), System.err);
			StreamRedirectFactory.createInputToOutputRedirect(System.in,
					process.getOutputStream());
			System.exit(process.waitFor());
		} catch(Exception exception) {
			exception.printStackTrace();
			System.exit(-1);
		}
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionID() {
		return sessionID;
	}
}

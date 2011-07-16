package org.darkstorm.minecraft.darkmod.tools;

import java.io.File;
import java.net.URL;

import javax.swing.JOptionPane;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.ui.LoginUI;
import org.darkstorm.tools.io.IOTools;

public class UpdateUtil {
	private String username, latestVersion, downloadTicket;
	private double latestDarkModVersion;
	private LoginUI loginUI;

	public UpdateUtil(String latestVersion, String username,
			String downloadTicket, LoginUI loginUI) {
		this.username = username;
		this.latestVersion = latestVersion;
		this.downloadTicket = downloadTicket;
		this.loginUI = loginUI;
	}

	public boolean updateDarkMod() {
		if(isDarkModUpdated())
			return installDarkModUpdate();
		return false;
	}

	private boolean isDarkModUpdated() {
		try {
			String versionInfo = new String(IOTools.download(new URL(
					"http://darkstorm652.webs.com/darkmod/version.txt")));
			String[] lines = versionInfo.split("<br/>");
			latestDarkModVersion = Double.valueOf(lines[0].trim());
			if(latestDarkModVersion > DarkMod.getVersion()) {
				return showConfirm("<html><center>An update is available!<br/>Would you like to update?<br/>New version: "
						+ latestDarkModVersion + "</center></html>");
			} else if(latestDarkModVersion < DarkMod.getVersion()) {
				return showConfirm("<html><center>A downgrade is available!<br/>Would you like to rollback?<br/>Previous version: "
						+ latestDarkModVersion + "</center></html>");
			} else
				return false;
		} catch(Exception exception) {
			exception.printStackTrace();
			showError("<html><center>Unable to check version:<br/>"
					+ exception.toString() + "</center></html>");
			return false;
		}
	}

	private boolean installDarkModUpdate() {
		try {
			File currentJar = Tools.getCurrentJar();
			File currentJarDir = currentJar.getParentFile();
			String newJarName = "DarkMod" + latestDarkModVersion + ".jar";
			for(int i = 1; new File(currentJarDir, newJarName).exists(); i++)
				newJarName = "DarkMod" + latestDarkModVersion + " (" + i
						+ ").jar";
			IOTools.download(new URL(
					"http://darkstorm652.webs.com/darkmod/DarkMod"
							+ latestDarkModVersion + ".jar"), new File(
					newJarName));
			currentJar.delete();
			showMessage("<html><center>DarkMod successfully updated!<br/>Restarting now.</center></html>");
			return true;
		} catch(Exception exception) {
			exception.printStackTrace();
			showError("<html><center>Unable to download new version:<br/>"
					+ exception + "</center></html>");
			return false;
		}
	}

	public boolean updateMinecraft() {
		if(isMinecraftUpdated())
			return installMinecraftUpdate();
		return false;
	}

	private boolean isMinecraftUpdated() {
		double latestVersion;
		try {
			latestVersion = Double.valueOf(this.latestVersion);
		} catch(NumberFormatException exception) {
			return false;
		}
		if(latestVersion > Tools.getMinecraftBuild())
			if(!checkVersionCompatability()) {
				showError("<html><center>An update to Minecraft is available but<br/>is not currently supported by DarkMod.<br/>Please wait patiently (but not long)<br/>for an update to DarkMod.</center></html>");
				return false;
			} else
				return showConfirm("<html><center>An update to Minecraft is available and is supported by DarkMod!<br/>Would you like to update?</center></html>");
		return false;
	}

	private boolean checkVersionCompatability() {
		String url = "http://darkstorm652.webs.com/darkmod/Hooks ("
				+ latestVersion + ").xml";
		return IOTools.testConnection(url);
	}

	private boolean installMinecraftUpdate() {
		try {
			String minecraftJarURLPath = "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user="
					+ username + "&ticket=" + downloadTicket;
			URL minecraftJarURL = new URL(minecraftJarURLPath);
			File minecraftJar = new File(Tools.getMinecraftDirectory(),
					"bin/minecraft.jar");
			IOTools.download(minecraftJarURL, minecraftJar);
			showMessage("Minecraft successfully updated!");
			return true;
		} catch(Exception exception) {
			exception.printStackTrace();
			showError("<html><center>Unable to update Minecraft:<br/>"
					+ exception + "</center></html>");
			return false;
		}
	}

	private void showMessage(String message) {
		if(loginUI != null) {
			loginUI.showMessage(message);
			return;
		}
		DarkMod darkMod = DarkMod.getInstance();
		JOptionPane.showMessageDialog(darkMod.getUI(), message, "",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private boolean showConfirm(String message) {
		if(loginUI != null)
			return loginUI.showConfirm(message);
		DarkMod darkMod = DarkMod.getInstance();
		return JOptionPane.showConfirmDialog(darkMod.getUI(), message,
				"Continue?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;
	}

	private void showError(String error) {
		if(loginUI != null) {
			loginUI.showMessage(error);
			return;
		}
		DarkMod darkMod = DarkMod.getInstance();
		JOptionPane.showMessageDialog(darkMod.getUI(), error, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
}

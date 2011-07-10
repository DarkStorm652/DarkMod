package org.darkstorm.minecraft.darkmod.tools;

import java.io.*;
import java.net.URLEncoder;
import java.util.Random;

import javax.crypto.*;
import javax.crypto.spec.*;

public class LoginUtil {
	private String username;
	private String password;
	private String sessionID;
	private String latestVersion;
	private String downloadTicket;

	private boolean loggedIn = false;
	private boolean playingOffline = false;

	private boolean rememberLogin = true;
	private boolean checkForUpdates = true;

	public String login(String username, String password) {
		try {
			String parameters = "user=" + URLEncoder.encode(username, "UTF-8")
					+ "&password=" + URLEncoder.encode(password, "UTF-8")
					+ "&version=" + 12;
			String result = Tools.post(
					"http://www.minecraft.net/game/getversion.jsp", parameters);
			if(loggedIn)
				return null;
			if(result == null) {
				return("Unable to connect");
			}
			if(!result.contains(":")) {
				if(result.trim().equals("Bad login"))
					return("Login failed");
				else if(result.trim().equals("Old version"))
					return("Outdated launcher");
				else
					return result;
			}
			String[] values = result.split(":");
			latestVersion = values[0];
			downloadTicket = values[1];
			this.username = values[2];
			this.password = password;
			sessionID = values[3];
			loggedIn = true;
		} catch(Exception exception) {
			exception.printStackTrace();
			return "Exception: " + exception.toString();
		}
		return null;
	}

	public void playOffline(String username) {
		this.username = username;
		playingOffline = true;
	}

	public boolean loadLogin() {
		DataInputStream inputStream = null;
		try {
			File lastLogin = new File(Tools.getWorkingDirectory(), "lastlogin");
			if(!lastLogin.exists())
				return false;
			Cipher cipher = getCipher(2, "passwordfile");
			if(cipher != null)
				inputStream = new DataInputStream(new CipherInputStream(
						new FileInputStream(lastLogin), cipher));
			else
				inputStream = new DataInputStream(
						new FileInputStream(lastLogin));
			username = inputStream.readUTF();
			password = inputStream.readUTF();
			try {

			} catch(Exception exception) {}
			rememberLogin = password.length() > 0;
			try {
				checkForUpdates = inputStream.readBoolean();
			} catch(Exception e) {}
			return true;
		} catch(Exception e) {} finally {
			try {
				inputStream.close();
			} catch(Exception exception) {}
		}
		return false;
	}

	public void saveLogin() {
		try {
			File lastLogin = new File(Tools.getWorkingDirectory(), "lastlogin");

			Cipher cipher = getCipher(1, "passwordfile");
			DataOutputStream dos;
			if(cipher != null)
				dos = new DataOutputStream(new CipherOutputStream(
						new FileOutputStream(lastLogin), cipher));
			else {
				dos = new DataOutputStream(new FileOutputStream(lastLogin));
			}
			dos.writeUTF(username);
			dos.writeUTF(password);
			dos.writeBoolean(checkForUpdates);
			dos.close();
		} catch(Exception e) {
			System.err.println("Unable to save login information");
		}
	}

	private Cipher getCipher(int mode, String password) throws Exception {
		Random random = new Random(43287234L);
		byte[] salt = new byte[8];
		random.nextBytes(salt);
		PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 5);

		SecretKey pbeKey = SecretKeyFactory.getInstance("PBEWithMD5AndDES")
				.generateSecret(new PBEKeySpec(password.toCharArray()));
		Cipher cipher = Cipher.getInstance("PBEWithMD5AndDES");
		cipher.init(mode, pbeKey, pbeParamSpec);
		return cipher;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSessionID() {
		return sessionID;
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public boolean shouldRememberLogin() {
		return rememberLogin;
	}

	public boolean shouldCheckForUpdates() {
		return checkForUpdates;
	}

	public void setCheckForUpdates(boolean checkForUpdates) {
		this.checkForUpdates = checkForUpdates;
	}

	public String getLatestVersion() {
		return latestVersion;
	}

	public String getDownloadTicket() {
		return downloadTicket;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isPlayingOffline() {
		return playingOffline;
	}
}

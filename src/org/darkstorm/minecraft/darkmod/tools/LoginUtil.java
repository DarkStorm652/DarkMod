package org.darkstorm.minecraft.darkmod.tools;

import java.net.URLEncoder;

public class LoginUtil {
	private String username;
	private String password;
	private String sessionID;
	private String latestVersion;
	private String downloadTicket;

	private boolean loggedIn;
	private boolean playingOffline;

	public void playOffline(String username) {
		this.username = username;
		playingOffline = true;
	}

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

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getSessionID() {
		return sessionID;
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

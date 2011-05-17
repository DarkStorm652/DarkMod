package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Session {
	public String getUsername();

	public void setUsername(String username);

	public String getSessionID();

	public void setSessionID(String sessionID);

	public String getMultiplayerPassword();

	public void setMultiplayerPassword(String multiplayerPassword);
}

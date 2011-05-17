package org.darkstorm.minecraft.darkmod;

import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.injection.InjectionHandler;
import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.tools.StartupUtil;
import org.darkstorm.minecraft.darkmod.ui.*;
import org.darkstorm.tools.exceptions.InstanceAlreadyExistsException;

public class DarkMod {
	private static DarkMod instance;
	private AccessHandler accessHandler;
	private ModHandler modHandler;
	private DarkModUI ui;

	private long minecraftVersion;

	private String username, password, sessionID;

	public DarkMod() {
		checkInstance();
		ui = new DarkModUI();
		StartupUtil handler = new StartupUtil();
		LoginUI loginUI = handler.handleLoginWithUI();
		username = handler.getUsername();
		password = handler.getPassword();
		sessionID = handler.getSessionID();
		handleAccess(loginUI);
		handler.clearLoginUI(loginUI);
		handler.handleUI();
		accessHandler.start();
		modHandler = new ModHandler(accessHandler);
	}

	public DarkMod(String username) {
		this(username, null);
	}

	public DarkMod(String username, String password) {
		checkInstance();
		StartupUtil handler = new StartupUtil();
		handler.handleLogin(username, password);
		this.username = handler.getUsername();
		this.password = handler.getPassword();
		sessionID = handler.getSessionID();
		ui = new DarkModUI();
		handler.handleUI();
		handleAccess(null);
		modHandler = new ModHandler(accessHandler);
	}

	public DarkMod(String username, String password, String sessionID) {
		checkInstance();
		this.username = username;
		this.password = password;
		this.sessionID = sessionID;
		ui = new DarkModUI();
		StartupUtil handler = new StartupUtil();
		handler.handleUI();
		handleAccess(null);
		modHandler = new ModHandler(accessHandler);
	}

	private void checkInstance() {
		if(instance != null)
			throw new InstanceAlreadyExistsException();
		instance = this;
	}

	private void handleAccess(LoginUI loginUI) {
		accessHandler = new InjectionHandler();
		accessHandler.load(loginUI);
		minecraftVersion = accessHandler.getMinecraftVersion();
	}

	public static DarkMod getInstance() {
		return instance;
	}

	public AccessHandler getAccessHandler() {
		return accessHandler;
	}

	public DarkModUI getUI() {
		return ui;
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

	public void setUsername(String username) {
		this.username = username;
		AccessHandler accessHandler = modHandler.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		Session session = minecraft.getSession();
		session.setUsername(username);
	}

	public void setPassword(String password) {
		this.password = password;
		AccessHandler accessHandler = modHandler.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		Session session = minecraft.getSession();
		session.setMultiplayerPassword(password);
	}

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
		AccessHandler accessHandler = modHandler.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		Session session = minecraft.getSession();
		session.setSessionID(sessionID);
	}

	public boolean isPlayingOffline() {
		return sessionID == null;
	}

	public ModHandler getModHandler() {
		return modHandler;
	}

	public long getMinecraftVersion() {
		return minecraftVersion;
	}

	public static double getVersion() {
		return 1.45;
	}
}

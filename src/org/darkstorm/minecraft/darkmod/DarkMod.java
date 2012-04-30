package org.darkstorm.minecraft.darkmod;

import java.lang.Thread.UncaughtExceptionHandler;

import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.access.injection.InjectionHandler;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.tools.StartupUtil;
import org.darkstorm.minecraft.darkmod.ui.*;
import org.darkstorm.tools.events.EventManager;
import org.darkstorm.tools.exceptions.InstanceAlreadyExistsException;
import org.darkstorm.tools.io.FileTools;
import org.darkstorm.tools.settings.SettingsHandler;

public class DarkMod {
	private static DarkMod instance;

	private SettingsHandler settingsHandler;
	private AccessHandler accessHandler;
	private EventManager eventManager;
	private ModHandler modHandler;
	private DarkModUI ui;

	private String username, password, sessionID;

	private long minecraftBuild;
	private String minecraftVersion;

	public DarkMod() {
		checkInstance();
		settingsHandler = new SettingsHandler(FileTools.DEFAULT_DIR
				+ "/.darkmod", "Settings.xml");
		eventManager = new EventManager();
		ExceptionHandler exceptionHandler = new ExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
		Thread.currentThread().setUncaughtExceptionHandler(exceptionHandler);
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
		Session session = accessHandler.getMinecraft().getSession();
		System.out.println(session.getUsername());
		System.out.println(session.getSessionID());
		System.out.println(session.getMultiplayerPassword());
	}

	public DarkMod(String username) {
		this(username, null);
	}

	public DarkMod(String username, String password) {
		checkInstance();
		eventManager = new EventManager();
		StartupUtil handler = new StartupUtil();
		handler.handleLoginWithoutUI(username, password);
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
		eventManager = new EventManager();
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
		minecraftBuild = accessHandler.getMinecraftBuild();
		minecraftVersion = accessHandler.getMinecraftVersion();
	}

	public static DarkMod getInstance() {
		return instance;
	}

	public SettingsHandler getSettingsHandler() {
		return settingsHandler;
	}

	public AccessHandler getAccessHandler() {
		return accessHandler;
	}

	public EventManager getEventManager() {
		return eventManager;
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

	public long getMinecraftBuild() {
		return minecraftBuild;
	}

	public String getMinecraftVersion() {
		return minecraftVersion;
	}

	public static double getVersion() {
		return 1.55;
	}

	private final class ExceptionHandler implements UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {
			MinecraftExtension mc = (MinecraftExtension) accessHandler
					.getMinecraft();
			System.out.println(mc.getClass().getName());
			// Method displayException = mc.getClass().getDeclaredMethod(",
			// parameterTypes)
		}
	}
}

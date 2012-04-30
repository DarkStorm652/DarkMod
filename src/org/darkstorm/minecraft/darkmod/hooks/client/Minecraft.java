package org.darkstorm.minecraft.darkmod.hooks.client;

import java.applet.Applet;
import java.awt.Canvas;
import java.io.File;
import java.util.Map;

public interface Minecraft extends Runnable {
	public void run();

	public World getWorld();

	public void setWorld(World world);

	public Canvas getCanvas();

	public Applet getApplet();

	public Player getPlayer();

	public Session getSession();

	public void setSession(Session session);

	public String getHomeURL();

	public void setHomeURL(String homeURL);

	public Map<Integer, Class<?>> getPacketList();

	public Map<String, Entity> getEntitiesByName();

	public Map<Integer, Entity> getEntitiesByID();

	public Item[] getItems();

	public float cos(float angle);

	public float sin(float angle);

	public String takeScreenshot(File directory, int width, int height);

	public GuiScreen getCurrentScreen();

	public void displayScreen(GuiScreen guiScreen);

	public PlayerController getPlayerController();

	public void setPlayerController(PlayerController playerController);

	public EntityTarget getPlayerTarget();

	public GuiIngame getGuiIngame();

	public Font getFont();

	public Font getStandardGalacticFont();

	public WorldType[] getWorldTypes();

	public WorldType getDefaultWorldType();

	public WorldType getFlatWorldType();

	public WorldType getDefault_1_1WorldType();
}

package org.darkstorm.minecraft.darkmod.mod;

import javax.swing.JOptionPane;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.hooks.client.Minecraft;
import org.darkstorm.minecraft.darkmod.mod.commands.CommandManager;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.tools.events.*;
import org.darkstorm.tools.loopsystem.*;

public abstract class Mod implements Loopable, EventListener {
	public static enum ModControl {
		NONE, ACTION, TOGGLE
	}

	protected ModHandler handler;
	protected Minecraft minecraft;
	protected AccessHandler accessHandler;
	protected CommandManager commandManager;
	protected EventManager eventManager;
	private LoopController controller;

	final void init(ModHandler handler) {
		this.handler = handler;
		accessHandler = handler.getAccessHandler();
		minecraft = accessHandler.getMinecraft();
		commandManager = handler.getCommandManager();
		eventManager = handler.getEventManager();
		LoopManager loopManager = handler.getLoopManager();
		controller = loopManager.addLoopable(this, getName());
	}

	public final void start() {
		if(controller.isAlive())
			return;
		onStart();
		controller.start();
		if(getControlOption() == ModControl.TOGGLE)
			handler.updateModMenu(this);
	}

	public final void stop() {
		if(!controller.isAlive())
			return;
		controller.stop();
		onStop();
		if(getControlOption() == ModControl.TOGGLE)
			handler.updateModMenu(this);
	}

	/**
	 * Where the mod should handle all calculations, actions, etc.
	 * 
	 * @return sleep time. See {@link Loopable} for return codes
	 */
	public abstract int loop() throws InterruptedException;

	/**
	 * @return Name of this mod, as viewed in the menu
	 */
	public abstract String getName();

	/**
	 * @return A short description of the mod that appears in a tooltip the
	 *         mod's menu item
	 */
	public abstract String getShortDescription();

	/**
	 * @return The full description of the mod, shown in the mod handler UI. May
	 *         include HTML.
	 */
	public String getFullDescription() {
		return "<html><b>No description available.</b></html>";
	}

	public abstract ModControl getControlOption();

	/**
	 * Whether or not to allow a user to open the mod's options UI.
	 * 
	 * @see Mod#showOptions()
	 * @return whether or not this mod has an options UI
	 */
	public abstract boolean hasOptions();

	/**
	 * Displays the mod's options UI. Override this method if hasOptions() is
	 * true.
	 * 
	 * @see Mod#hasOptions()
	 */
	public void showOptions() {
		DarkMod darkMod = DarkMod.getInstance();
		JOptionPane.showMessageDialog(darkMod.getUI(), "Mod \"" + getName()
				+ "\" has no configuration!");
	}

	/**
	 * Handle things on start here. You may register events and commands here.
	 * 
	 * @see Mod#stop()
	 */
	public void onStart() {
	}

	/**
	 * Handle things on stop here. You should always unregister events and
	 * commands registered on start here.
	 * 
	 * @see Mod#start()
	 */
	public void onStop() {
	}

	/**
	 * Override this if you have registered yourself for a listener via the
	 * EventManager
	 * 
	 * @see EventManager
	 */
	@Override
	public void onEvent(Event event) {
	}

	/**
	 * @return whether or not the mod is running
	 */
	public boolean isRunning() {
		return controller.isAlive();
	}

	protected void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch(InterruptedException exception) {}
	}

	// Temporary utility methods (I'm not sure why I put them here...)

	protected double getDistanceBetween(Location location1, Location location2) {
		double xResult = Math.pow(Math.max(location1.getX(), location2.getX())
				- Math.min(location1.getX(), location2.getX()), 2);
		double yResult = Math.pow(Math.max(location1.getY(), location2.getY())
				- Math.min(location1.getY(), location2.getY()), 2);
		double zResult = Math.pow(Math.max(location1.getZ(), location2.getZ())
				- Math.min(location1.getZ(), location2.getZ()), 2);
		return Math.sqrt(xResult + yResult + zResult);
	}

	protected void displayText(String text) {
		minecraft.getGuiIngame().displayTextInChat(text);
	}
}

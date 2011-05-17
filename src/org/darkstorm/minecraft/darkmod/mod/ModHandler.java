package org.darkstorm.minecraft.darkmod.mod;

import java.util.ArrayList;

import org.darkstorm.minecraft.darkmod.AccessHandler;
import org.darkstorm.minecraft.darkmod.mod.Mod.ModControl;
import org.darkstorm.minecraft.darkmod.mod.commands.CommandManager;
import org.darkstorm.minecraft.darkmod.ui.ModHandlerUI;
import org.darkstorm.tools.events.EventManager;
import org.darkstorm.tools.loopsystem.LoopManager;

public class ModHandler {
	private AccessHandler accessHandler;
	private EventManager eventManager;
	private LoopManager loopManager;
	private ModLoader modLoader;
	private ModMenuHandler menuHandler;
	private CommandManager commandManager;
	private ModHandlerUI ui;
	private ArrayList<Mod> mods;
	private Object modsLock = new Object();

	public ModHandler(AccessHandler accessHandler) {
		this.accessHandler = accessHandler;
		eventManager = new EventManager();
		loopManager = new LoopManager(new ThreadGroup("Mod Threads"));
		loopManager.stopAll();
		mods = new ArrayList<Mod>();
		modLoader = new ModLoader(this);
		ui = new ModHandlerUI(this);
		commandManager = new CommandManager(this);
		menuHandler = new ModMenuHandler(this);
		modLoader.reloadMods();
	}

	public void addMod(Mod mod) {
		synchronized(modsLock) {
			if(mods.contains(mod))
				throw new IllegalArgumentException("Mod already loaded!");
			if(mod.getControlOption() == null)
				throw new IllegalArgumentException(
						"Mod doesn\'t specify control");
			try {
				mod.init(this);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
			mods.add(mod);
			menuHandler.updateMod(mod);
			if(mod.getControlOption() == ModControl.NONE)
				mod.start();
		}
	}

	public void removeMod(Mod mod) {
		synchronized(modsLock) {
			if(!mods.contains(mod))
				throw new IllegalArgumentException("Mod not loaded!");
			if(mod.isRunning())
				mod.stop();
			mods.remove(mod);
			menuHandler.removeMod(mod);
		}
	}

	public void clearMods() {
		synchronized(modsLock) {
			Mod[] mods = this.mods.toArray(new Mod[this.mods.size()]);
			for(Mod mod : mods)
				removeMod(mod);
		}
	}

	public Mod[] getMods() {
		synchronized(modsLock) {
			return mods.toArray(new Mod[mods.size()]);
		}
	}

	public Mod startMod(String name) {
		Mod mod = getModByName(name);
		if(mod != null && !mod.isRunning())
			mod.start();
		return mod;
	}

	public Mod stopMod(String name) {
		Mod mod = getModByName(name);
		if(mod != null && mod.isRunning())
			mod.stop();
		return mod;
	}

	void updateModMenu(Mod mod) {
		menuHandler.updateMod(mod);
	}

	public Mod getModByName(String name) {
		synchronized(modsLock) {
			for(Mod mod : mods)
				if(name.equals(mod.getName()))
					return mod;
		}
		return null;
	}

	public void reloadMods() {
		modLoader.reloadMods();
	}

	public ModHandlerUI getUI() {
		return ui;
	}

	public void showUI() {
		ui.setVisible(true);
	}

	public AccessHandler getAccessHandler() {
		return accessHandler;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public LoopManager getLoopManager() {
		return loopManager;
	}
}

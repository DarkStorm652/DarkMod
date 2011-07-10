package org.darkstorm.minecraft.darkmod.tools;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.events.ShutdownEvent;
import org.darkstorm.tools.events.EventManager;

public class ShutdownEventSender extends Thread {
	private static boolean initialized = false;

	public static void init() {
		if(initialized)
			throw new UnsupportedOperationException(
					"Cannot reinitialize ShutdownEventSender");
		Runtime runtime = Runtime.getRuntime();
		runtime.addShutdownHook(new ShutdownEventSender());
	}

	private ShutdownEventSender() {
	}

	@Override
	public void run() {
		DarkMod darkMod = DarkMod.getInstance();
		EventManager eventManager = darkMod.getEventManager();
		eventManager.sendEvent(new ShutdownEvent());
	}
}

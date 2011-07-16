package org.darkstorm.minecraft.darkmod.hooks.client;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.mod.commands.CommandManager;
import org.darkstorm.tools.events.EventManager;

public class Callback {
	public static void onRender() {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(RenderEvent.class).length > 0)
			manager.sendEvent(new RenderEvent());
	}

	public static void onPlayerProcess(Player player, int status) {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(PlayerProcessEvent.class).length > 0)
			manager.sendEvent(new PlayerProcessEvent(player, status));
	}

	public static void onPacketSent(Packet packet) {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(PacketEvent.class).length > 0)
			manager.sendEvent(new PacketEvent(packet, PacketEvent.SENT));
	}

	public static void onPacketReceived(Packet packet) {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(PacketEvent.class).length > 0)
			manager.sendEvent(new PacketEvent(packet, PacketEvent.RECEIVED));
	}

	public static void onBlockDig(int status) {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(BlockDigEvent.class).length > 0)
			manager.sendEvent(new BlockDigEvent(status));
	}

	public static String handleText(String text) {
		DarkMod darkMod = DarkMod.getInstance();
		ModHandler modHandler = darkMod.getModHandler();
		CommandManager commandManager = modHandler.getCommandManager();
		return commandManager.handleText(text);
	}
}

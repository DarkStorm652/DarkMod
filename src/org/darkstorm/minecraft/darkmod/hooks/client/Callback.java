package org.darkstorm.minecraft.darkmod.hooks.client;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.mod.commands.CommandManager;
import org.darkstorm.tools.events.EventManager;

public class Callback {
	public static void onTick() {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(TickEvent.class).length > 0)
			manager.sendEvent(new TickEvent());
	}

	public static void onRender() {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(RenderEvent.class).length > 0)
			manager.sendEvent(new RenderEvent(RenderEvent.RENDER));
	}

	public static void onRenderEntitiesBegin() {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(RenderEvent.class).length > 0)
			manager.sendEvent(new RenderEvent(RenderEvent.RENDER_ENTITIES));
	}

	public static void onRenderEntitiesEnd() {
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		if(manager.getListeners(RenderEvent.class).length > 0)
			manager.sendEvent(new RenderEvent(RenderEvent.RENDER_ENTITIES_END));
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

	public static String onTextDisplayed(String text) {
		ChatEvent event = new ChatEvent(ChatEvent.DISPLAYED, text);
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		manager.sendEvent(event);
		text = event.getMessage();
		if(event.isCancelled())
			text = null;
		return text;
	}

	public static String handleText(String text) {
		DarkMod darkMod = DarkMod.getInstance();
		ModHandler modHandler = darkMod.getModHandler();
		CommandManager commandManager = modHandler.getCommandManager();
		text = commandManager.handleText(text);
		if(text == null)
			return null;
		ChatEvent event = new ChatEvent(ChatEvent.SENT, text);
		EventManager manager = DarkMod.getInstance().getModHandler()
				.getEventManager();
		manager.sendEvent(event);
		text = event.getMessage();
		if(event.isCancelled())
			text = null;
		return text;
	}
}

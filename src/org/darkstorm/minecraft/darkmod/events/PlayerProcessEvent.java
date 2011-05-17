package org.darkstorm.minecraft.darkmod.events;

import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.tools.events.Event;

public class PlayerProcessEvent extends Event {
	public static final int START = 0;
	public static final int PRE_SUPERCLASS_PROCESS = 1;
	public static final int FINISH = 2;

	public PlayerProcessEvent(Player player, int status) {
		super(player, status);
	}

	public Player getPlayer() {
		return (Player) info[0];
	}

	public int getStatus() {
		return (Integer) info[1];
	}
}

package org.darkstorm.minecraft.darkmod.events;

import org.darkstorm.tools.events.Event;

public class ChatEvent extends Event {
	public static final int SENT = 0, DISPLAYED = 1;
	private boolean cancelled = false;

	public ChatEvent(int status, String message) {
		super(status, message);
	}

	public int getStatus() {
		return (Integer) info[0];
	}

	public String getMessage() {
		return (String) info[1];
	}

	public void setMessage(String message) {
		info[1] = message;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}

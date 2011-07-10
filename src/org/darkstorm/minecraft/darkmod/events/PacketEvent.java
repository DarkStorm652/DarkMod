package org.darkstorm.minecraft.darkmod.events;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;
import org.darkstorm.tools.events.Event;

public class PacketEvent extends Event {
	public static final int SENT = 0;
	public static final int RECEIVED = 1;

	public PacketEvent(Packet packet, int status) {
		super(packet, status);
	}

	public Packet getPacket() {
		return (Packet) info[0];
	}

	public int getStatus() {
		return (Integer) info[1];
	}

}

package org.darkstorm.minecraft.darkmod.hooks.client;

import java.io.*;

public interface Packet {
	public void send(DataOutputStream dataOutputStream);

	public void receive(DataInputStream dataInputStream);
}

package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet105UpdateProgressBar extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public int getProgressBar();

	public void setProgressBar(int progressBar);

	public int getValue();

	public void setValue(int value);
}

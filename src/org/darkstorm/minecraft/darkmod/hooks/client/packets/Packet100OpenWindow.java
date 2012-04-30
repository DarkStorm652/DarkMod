package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet100OpenWindow extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public int getInventoryType();

	public void setInventoryType(int inventoryType);

	public String getWindowTitle();

	public void setWindowTitle(String windowTitle);

	public int getSlotCount();

	public void setSlotCount(int slotCount);
}

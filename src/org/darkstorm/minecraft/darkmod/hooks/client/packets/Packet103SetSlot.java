package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.*;

public interface Packet103SetSlot extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public int getSlot();

	public void setSlot(int slot);

	public InventoryItem getItem();

	public void setItem(InventoryItem item);
}

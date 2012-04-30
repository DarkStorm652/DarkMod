package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.*;

public interface Packet104WindowItems extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public InventoryItem[] getItems();

	public void setItems(InventoryItem[] items);
}

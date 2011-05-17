package org.darkstorm.minecraft.darkmod.hooks.client;

public interface InventoryItemSelectPacket extends Packet {
	public int getSelectedIndex();
}

package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.*;

public interface Packet102WindowClick extends Packet {
	public int getWindowID();

	public void setWindowID(int windowID);

	public int getSlot();

	public void setSlot(int slot);

	public int getButton();

	public void setButton(int button);

	public short getAction();

	public void setAction(short action);

	public InventoryItem getItem();

	public void setItem(InventoryItem item);

	public boolean isHoldingShift();

	public void setHoldingShift(boolean holdingShift);
}

package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Sign extends TileEntity {
	public String[] getSignText();

	public void setSignText(String[] text);

	public int getLineBeingEdited();

	public void setLineBeingEdited(int line);
}

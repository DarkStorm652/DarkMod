package org.darkstorm.minecraft.darkmod.hooks.client;

import java.util.List;

public interface GuiScreen {
	public List<GuiButton> getButtons();

	public GuiButton getSelectedButton();

	public void setSelectedButton(GuiButton button);

	public int getWidth();

	public void setWidth(int width);

	public int getHeight();

	public void setHeight(int height);
}

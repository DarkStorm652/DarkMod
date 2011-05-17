package org.darkstorm.minecraft.darkmod.hooks.client;

public interface GuiButton {
	public int getX();

	public void setX(int x);

	public int getY();

	public void setY(int y);

	public int getHeight();

	public void setHeight(int height);

	public int getWidth();

	public void setWidth(int width);

	public String getText();

	public void setText(String text);

	public int getID();

	public void setID(int id);

	public boolean isEnabled();

	public void setEnabled(boolean enabled);
}

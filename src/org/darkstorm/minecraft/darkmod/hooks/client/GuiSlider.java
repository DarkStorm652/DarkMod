package org.darkstorm.minecraft.darkmod.hooks.client;

public interface GuiSlider {
	public float getValue();

	public void setValue(float value);

	public boolean isDragging();

	public void setDragging(boolean dragging);
}

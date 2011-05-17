package org.darkstorm.minecraft.darkmod.hooks.client;

import java.nio.IntBuffer;

public interface Font {
	public int getTextureID();

	public int getListBase();

	public int[] getCharWidths();

	public IntBuffer getListIDBuffer();

	public int getStringWidth(String string);

	public void drawString(String string, int x, int y, int color);

	public void drawStringWithShadow(String string, int x, int y, int color);
}

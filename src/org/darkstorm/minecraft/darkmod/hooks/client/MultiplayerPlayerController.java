package org.darkstorm.minecraft.darkmod.hooks.client;

public interface MultiplayerPlayerController {
	public float getPercentComplete();

	public float getFloat1();

	public void setFloat1(float float1);

	public float getFloat2();

	public void setFloat2(float float2);

	public void setPercentComplete(float percentComplete);

	public void digBlockAt(int x, int y, int z, int face);

	public boolean isRemoving();

	public void setRemoving(boolean removing);

	public int getCurrentRemoveX();

	public void setCurrentRemoveX(int x);

	public int getCurrentRemoveY();

	public void setCurrentRemoveY(int y);

	public int getCurrentRemoveZ();

	public void setCurrentRemoveZ(int z);

	public int getSelectedItemIndex(int index);

	public void setSelectedItemIndex(int index);
}

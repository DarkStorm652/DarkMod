package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Arrow extends Entity {
	public int getXTile();

	public void setXTile(int xTile);

	public int getYTile();

	public void setYTile(int yTile);

	public int getZTile();

	public void setZTile(int zTile);

	public int getInTile();

	public void setInTile(int inTile);

	public int getInData();

	public void setInData(int inData);

	public boolean isInGround();

	public void setInGround(boolean inGround);

	public boolean isFiredByPlayer();

	public void setFiredByPlayer(boolean firedByPlayer);

	public int getShake();

	public void setShake(int shake);
}

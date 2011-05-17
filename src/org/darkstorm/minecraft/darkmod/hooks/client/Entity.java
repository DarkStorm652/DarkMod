package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Entity {
	public int getID();

	public void setID(int id);

	public double getX();

	public void setX(double x);

	public double getY();

	public void setY(double y);

	public double getZ();

	public void setZ(double z);

	public double getSpeedX();

	public void setSpeedX(double speedX);

	public double getSpeedY();

	public void setSpeedY(double speedY);

	public double getSpeedZ();

	public void setSpeedZ(double speedZ);

	public float getRotationX();

	public void setRotationX(float rotationX);

	public float getRotationY();

	public void setRotationY(float rotationY);

	public float getFallDistance();

	public void setFallDistance(float fallDistance);

	public int getOnFireTimer();

	public void setOnFireTimer(int onFireTimer);

	public int getBreathTimer();

	public void setBreathTimer(int breathTimer);

	public boolean isOnGround();

	public void setOnGround(boolean onGround);

	public void setPosition(double x, double y, double z);
}

package org.darkstorm.minecraft.darkmod.hooks.client.packets;

import org.darkstorm.minecraft.darkmod.hooks.client.Packet;

public interface Packet10PlayerPosition extends Packet {
	public double getX();

	public void setX(double x);

	public double getY();

	public void setY(double y);

	public double getZ();

	public void setZ(double z);

	public double getStance();

	public void setStance(double stance);

	public float getRotationX();

	public void setRotationX(float rotationX);

	public float getRotationY();

	public void setRotationY(float rotationY);

	public boolean isOnGround();

	public void setOnGround(boolean onGround);

	public boolean isMoving();

	public void setMoving(boolean moving);

	public boolean isRotating();

	public void setRotating(boolean rotating);
}

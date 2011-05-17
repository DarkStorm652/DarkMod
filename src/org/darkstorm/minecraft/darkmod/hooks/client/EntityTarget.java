package org.darkstorm.minecraft.darkmod.hooks.client;

public interface EntityTarget {
	public int getTargetX();

	public int getTargetY();

	public int getTargetZ();

	public int getTargetFace();

	public Vector3D getVector();

	public Entity getEntity();
}

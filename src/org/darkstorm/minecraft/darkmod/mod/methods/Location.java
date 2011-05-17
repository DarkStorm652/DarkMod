package org.darkstorm.minecraft.darkmod.mod.methods;

import org.darkstorm.minecraft.darkmod.mod.methods.interfaces.Locatable;

public class Location implements Locatable {
	private double x, y, z;

	public Location(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")";
	}

	@Override
	public Location getLocation() {
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Location))
			return false;
		Location location = (Location) obj;
		return x == location.getX() && y == location.getY()
				&& z == location.getZ();
	}
}

package org.darkstorm.minecraft.darkmod.hooks.client;

import java.io.*;

public interface Serializable {
	public void serialize(DataOutput out);

	public void deserialize(DataInput in);

	public byte getID();
}

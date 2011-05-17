package org.darkstorm.minecraft.darkmod.mod.methods;

import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.mod.methods.wrappers.Minecraft;

public class ModEnvironment {
	private ModHandler modHandler;
	private org.darkstorm.minecraft.darkmod.hooks.client.Minecraft minecraftAccessor;

	private Minecraft minecraft;

	public ModEnvironment(
			ModHandler modHandler,
			org.darkstorm.minecraft.darkmod.hooks.client.Minecraft minecraftAccessor) {
		this.modHandler = modHandler;
		this.minecraftAccessor = minecraftAccessor;
	}

	public Minecraft getMinecraft() {
		return minecraft;
	}

	public ModHandler getModHandler() {
		return modHandler;
	}

	public org.darkstorm.minecraft.darkmod.hooks.client.Minecraft getMinecraftAccessor() {
		return minecraftAccessor;
	}
}

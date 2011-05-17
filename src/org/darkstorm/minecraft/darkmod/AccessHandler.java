package org.darkstorm.minecraft.darkmod;

import org.darkstorm.minecraft.darkmod.hooks.client.Minecraft;
import org.darkstorm.minecraft.darkmod.ui.LoginUI;

public abstract class AccessHandler {
	public abstract void load(LoginUI loginUI);

	public abstract void start();

	public abstract Minecraft getMinecraft();

	public abstract ClassLoader getClassLoader();

	public abstract long getMinecraftVersion();
}

package org.darkstorm.minecraft.darkmod.mod.methods.wrappers;

import java.io.File;

import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.methods.ModEnvironment;

public class Minecraft extends Wrapper {
	private org.darkstorm.minecraft.darkmod.hooks.client.Minecraft accessor;

	public Minecraft(ModEnvironment env) {
		super(env);
		accessor = env.getMinecraftAccessor();
	}

	public Session getSession() {
		return accessor.getSession();
	}

	public World getWorld() {
		return accessor.getWorld();
	}

	public void render() {
		accessor.run();
	}

	public void setSession(Session session) {
		accessor.setSession(session);
	}

	public void setWorld(World world) {
		accessor.setWorld(world);
	}

	public String takeScreenshot(File directory, int width, int height) {
		return accessor.takeScreenshot(directory, width, height);
	}

}

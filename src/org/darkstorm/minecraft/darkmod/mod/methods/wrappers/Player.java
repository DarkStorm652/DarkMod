package org.darkstorm.minecraft.darkmod.mod.methods.wrappers;

import org.darkstorm.minecraft.darkmod.mod.methods.ModEnvironment;

public class Player extends Wrapper {
	@SuppressWarnings("unused")
	private org.darkstorm.minecraft.darkmod.hooks.client.Player accessor;

	public Player(ModEnvironment environment,
			org.darkstorm.minecraft.darkmod.hooks.client.Player accessor) {
		super(environment);
		this.accessor = accessor;
	}
}

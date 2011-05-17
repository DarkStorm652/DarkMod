package org.darkstorm.minecraft.darkmod.mod.methods;

import org.darkstorm.minecraft.darkmod.mod.methods.ModEnvironment;

public abstract class MethodProvider {
	protected ModEnvironment environment;

	public MethodProvider(ModEnvironment environment) {
		this.environment = environment;
	}
}

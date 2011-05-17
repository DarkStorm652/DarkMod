package org.darkstorm.minecraft.darkmod.hooks.client;


public interface MultiplayerWorld extends World {
	public NetworkHandler getNetworkHandler();

	public void setNetworkHandler(NetworkHandler networkHandler);
}

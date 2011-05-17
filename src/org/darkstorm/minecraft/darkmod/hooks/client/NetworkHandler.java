package org.darkstorm.minecraft.darkmod.hooks.client;

public interface NetworkHandler extends PacketHandler {
	public ConnectionHandler getConnectionHandler();

	public void setConnectionHandler(ConnectionHandler connectionHandler);

	public void sendPacket(Packet packet);
}

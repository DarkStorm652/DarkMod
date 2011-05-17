package org.darkstorm.minecraft.darkmod.hooks.client;

public interface GuiIngame {
	public void displayTextInChat(String text);

	public void translateAndDisplayTextInChat(String text);

	public String getNotification();

	public void setNotification(String notification);

	public int getNotificationTimeout();

	public void setNotificationTimeout(int notificationTimeout);
}

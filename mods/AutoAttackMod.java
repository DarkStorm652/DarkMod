import java.util.Vector;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.settings.*;
import org.lwjgl.input.*;

public class AutoAttackMod extends Mod implements CommandListener {
	private enum AttackMode {
		AUTO, SHORTCUT, OFF
	}

	private AttackMode attackMode = AttackMode.OFF;
	private Class<?> attackPacketClass;
	private Vector<String> friends;

	@Override
	public void onStart() {
		if(attackPacketClass == null)
			attackPacketClass = ClassRepository.getClassByName("a");
		friends = new Vector<String>();
		loadSettings();
		commandManager.registerListener(
				new Command("friend", "friend <add|remove|list>",
						"Modify friends for AutoAttackMod"), this);
		commandManager.registerListener(
				new Command("attackmode", "attackmode <auto|shortcut|off>",
						"Controls for AutoAttackMod"), this);
	}

	@Override
	public void onStop() {
		saveSettings();
		commandManager.unregisterListener("friend");
		commandManager.unregisterListener("attackmode");
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Auto Attack Mod";
	}

	@Override
	public String getShortDescription() {
		return "";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		try {
			if(minecraft.getWorld() == null
					|| !(minecraft.getWorld() instanceof MultiplayerWorld))
				return 500;
			if(attackMode == AttackMode.OFF
					|| (attackMode == AttackMode.SHORTCUT
							&& !Mouse.isButtonDown(2) && !Keyboard
							.isKeyDown(Keyboard.KEY_TAB)))
				return 100;
			Player player = minecraft.getPlayer();
			int playerID = player.getID();
			Location playerLocation = new Location(player.getX(),
					player.getY(), player.getZ());
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			label1: for(Entity entity : world.getEntities()) {
				if(entity.equals(player) || !(entity instanceof Animable))
					continue;
				Location entityLocation = new Location(entity.getX(), entity
						.getY(), entity.getZ());
				if(getDistanceBetween(playerLocation, entityLocation) > 5)
					continue;
				if(entity instanceof Humanoid) {
					Humanoid playerTarget = (Humanoid) entity;
					String playerName = playerTarget.getName();
					synchronized(friends) {
						for(String friend : friends)
							if(playerName.equalsIgnoreCase(friend))
								continue label1;
					}
				}
				int id = entity.getID();
				Packet attackPacket = (Packet) ReflectionUtil.instantiate(
						attackPacketClass, new Class[] { Integer.TYPE,
								Integer.TYPE, Integer.TYPE }, playerID, id, 1);
				networkHandler.sendPacket(attackPacket);
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return 100;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("friend") && parts.length > 1) {
			synchronized(friends) {
				if(parts[1].equalsIgnoreCase("add") && parts.length == 3) {
					if(!friends.contains(parts[2].toLowerCase())) {
						friends.add(parts[2].toLowerCase());
						displayText(ChatColor.GRAY + "Friend added: "
								+ ChatColor.GOLD + parts[2]);
					} else {
						displayText(ChatColor.GRAY + "Friend already added");
						return;
					}
				} else if(parts[1].equalsIgnoreCase("remove")
						&& parts.length == 3) {
					if(friends.remove(parts[2].toLowerCase()))
						displayText(ChatColor.GRAY + "Friend removed: "
								+ ChatColor.GOLD + parts[2]);
					else {
						displayText(ChatColor.GRAY + "Friend not found");
						return;
					}
				} else if(parts[1].equalsIgnoreCase("list")) {
					String friendsAppended = "";
					if(friends.size() > 0) {
						friendsAppended = friends.get(0);
						for(int i = 1; i < friends.size(); i++)
							friendsAppended += ", " + friends.get(i);
					}
					displayText(ChatColor.GRAY + "Friends: " + ChatColor.GOLD
							+ friendsAppended);
					return;
				} else
					return;
				saveSettings();
			}
		} else if(parts[0].equalsIgnoreCase("attackmode") && parts.length == 2) {
			if(parts[1].equalsIgnoreCase("auto")) {
				attackMode = AttackMode.AUTO;
				displayText(ChatColor.GRAY + "Attack mode set to "
						+ ChatColor.GOLD + "auto");
			} else if(parts[1].equalsIgnoreCase("shortcut")) {
				attackMode = AttackMode.SHORTCUT;
				displayText(ChatColor.GRAY + "Attack mode set to "
						+ ChatColor.GOLD + "shortcut");
			} else if(parts[1].equalsIgnoreCase("off")) {
				attackMode = AttackMode.OFF;
				displayText(ChatColor.GRAY + "Attack mode set to "
						+ ChatColor.GOLD + "off");
			} else
				displayText(ChatColor.GRAY + "Attack mode not recognized");
		}
	}

	private void loadSettings() {
		synchronized(friends) {
			friends.clear();
			DarkMod darkMod = DarkMod.getInstance();
			SettingsHandler settingsHandler = darkMod.getSettingsHandler();
			SettingVector settings = settingsHandler.getSettings();
			Setting rootSetting = settings.getSetting("AutoAttackMod");
			if(rootSetting != null) {
				SettingVector subSettings = rootSetting.getSubSettings();
				String friends = subSettings.getSettingValue("friends");
				if(friends != null)
					for(String friend : friends.split(","))
						this.friends.add(friend);
			}
		}
	}

	private void saveSettings() {
		synchronized(friends) {
			DarkMod darkMod = DarkMod.getInstance();
			SettingsHandler settingsHandler = darkMod.getSettingsHandler();
			SettingVector settings = settingsHandler.getSettings();
			Setting rootSetting = settings.getSetting("AutoAttackMod");
			if(rootSetting == null) {
				rootSetting = new Setting("AutoAttackMod", "");
				settings.add(rootSetting);
			}
			SettingVector subSettings = rootSetting.getSubSettings();
			Setting friendsSetting = subSettings.getSetting("friends");
			if(friendsSetting == null) {
				friendsSetting = new Setting("friends", "");
				subSettings.add(friendsSetting);
			}
			if(friends.size() > 0) {
				String friendsValue = friends.get(0);
				for(int i = 1; i < friends.size(); i++)
					friendsValue += "," + friends.get(i);
				friendsSetting.setValue(friendsValue);
			}
			settingsHandler.saveSettings();
		}
	}
}

import java.util.*;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.settings.*;
import org.lwjgl.input.*;

public class AutoAttackMod extends Mod implements CommandListener {
	private enum AttackMode {
		AUTO,
		NOCHEAT,
		SHORTCUT,
		OFF
	}

	public enum Team {
		RED(ChatColor.RED, "Red"),
		BLUE(ChatColor.INDIGO, "Blue"),
		ALL(null, "?") {
			@Override
			public boolean isOnTeam(String player) {
				return true;
			}
		},
		NONE(null, "X") {
			@Override
			public boolean isOnTeam(String player) {
				return false;
			}
		};

		private final ChatColor color;
		private final String name;

		private Team(ChatColor color, String name) {
			this.color = color;
			this.name = name;
		}

		public ChatColor getColor() {
			return color;
		}

		public String getName() {
			return name;
		}

		public boolean isOnTeam(Humanoid player) {
			return player != null && isOnTeam(player.getName());
		}

		public boolean isOnTeam(String player) {
			return player.startsWith(color.toString());
		}

		public static Team getTeam(String player) {
			if(!player.startsWith("\247"))
				return NONE;
			for(Team team : values())
				if(team.isOnTeam(player))
					return team;
			return NONE;
		}
	}

	private AttackMode attackMode = AttackMode.OFF;
	private Vector<String> friends;
	private Queue<Entity> attackQueue = new ArrayDeque<Entity>();
	private Team team = Team.NONE;
	private boolean hurtSelf = false;

	@Override
	public void onStart() {
		friends = new Vector<String>();
		loadSettings();
		commandManager.registerListener(
				new Command("friend", "friend <add|remove|list>",
						"Modify friends for AutoAttackMod"), this);
		commandManager.registerListener(new Command("attackmode",
				"attackmode <auto|knohax|shortcut|off|self>",
				"Controls for AutoAttackMod"), this);
		commandManager.registerListener(new Command("team",
				"team [blue|b|red|r|all|a]", "Sets attack mode team"), this);
	}

	@Override
	public void onStop() {
		saveSettings();
		commandManager.unregisterListeners(this);
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
				return 200;
			Player player = minecraft.getPlayer();
			int playerID = player.getID();
			Location playerLocation = new Location(player.getX(),
					player.getY(), player.getZ());
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			if(attackQueue.isEmpty()) {
				attackQueue.addAll(world.getEntities());
			}
			while(attackQueue.size() > 0) {
				Entity entity = null;
				label: while(team != Team.ALL && attackQueue.peek() != null) {
					entity = attackQueue.poll();
					Location entityLocation = new Location(entity.getX(),
							entity.getY(), entity.getZ());
					if(entity.equals(player)
							|| !(entity instanceof Animable)
							|| getDistanceBetween(playerLocation,
									entityLocation) > 5) {
						entity = null;
						continue;
					}
					if(entity instanceof Humanoid) {
						Humanoid playerTarget = (Humanoid) entity;
						String playerName = playerTarget.getName();
						synchronized(friends) {
							for(String friend : friends) {
								if(playerName.equalsIgnoreCase(friend)) {
									entity = null;
									continue label;
								}
							}
						}
						if(team != Team.NONE
								&& playerName.startsWith(team.getColor()
										.toString())) {
							entity = null;
							continue;
						}
					}
					break;
				}
				if(hurtSelf) {
					Packet19EntityAction actionPacket = (Packet19EntityAction) ReflectionUtil
							.instantiate(ClassRepository
									.getClassForInterface(Packet19EntityAction.class));
					actionPacket.setEntityID(playerID);
					actionPacket.setState(1);
					networkHandler.sendPacket(actionPacket);
					Packet7UseEntity attackPacket = (Packet7UseEntity) ReflectionUtil
							.instantiate(ClassRepository
									.getClassForInterface(Packet7UseEntity.class));
					attackPacket.setPlayerEntityID(playerID);
					attackPacket.setEntityID(playerID);
					attackPacket.setButton(1);
					networkHandler.sendPacket(attackPacket);
				}
				if(entity == null)
					return attackMode == AttackMode.NOCHEAT ? 100 : 50;

				int id = entity.getID();
				if(attackMode == AttackMode.NOCHEAT) {
					Packet12PlayerRotate rotatePacket = (Packet12PlayerRotate) ReflectionUtil
							.instantiate(ClassRepository
									.getClassForInterface(Packet12PlayerRotate.class));
					rotatePacket.setRotationX(getFacingRotationX(entity));
					rotatePacket.setRotationY(getFacingRotationY(entity));
					rotatePacket.setOnGround(player.isOnGround());
					Packet19EntityAction actionPacket = (Packet19EntityAction) ReflectionUtil
							.instantiate(ClassRepository
									.getClassForInterface(Packet19EntityAction.class));
					actionPacket.setEntityID(playerID);
					actionPacket.setState(1);
					networkHandler.sendPacket(actionPacket);
				}
				Packet7UseEntity attackPacket = (Packet7UseEntity) ReflectionUtil
						.instantiate(ClassRepository
								.getClassForInterface(Packet7UseEntity.class));
				attackPacket.setPlayerEntityID(playerID);
				attackPacket.setEntityID(id);
				attackPacket.setButton(1);
				networkHandler.sendPacket(attackPacket);
				if(attackMode == AttackMode.NOCHEAT)
					break;
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return attackMode == AttackMode.NOCHEAT ? 200 : 50;
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
			} else if(parts[1].equalsIgnoreCase("self")) {
				hurtSelf = !hurtSelf;
				displayText(ChatColor.GRAY + "Attacking self is now "
						+ ChatColor.GOLD + (hurtSelf ? "on" : "off"));
			} else
				displayText(ChatColor.GRAY + "Attack mode not recognized");
		} else if(parts[0].equalsIgnoreCase("team")) {
			if(parts.length == 2) {
				if(parts[1].toLowerCase().startsWith("b")) {
					team = Team.BLUE;
				} else if(parts[1].toLowerCase().startsWith("r")) {
					team = Team.RED;
				} else if(parts[1].toLowerCase().startsWith("a")) {
					team = Team.ALL;
				} else {
					displayText(ChatColor.GRAY + "Unknown team!");
					return;
				}
				displayText(ChatColor.GRAY + "Team set to " + team.getColor()
						+ team.toString().toLowerCase() + ChatColor.GRAY + ".");
			} else {
				team = Team.NONE;
				displayText(ChatColor.GRAY + "Team cleared.");
			}
		}
	}

	public float getFacingRotationX(Entity entity) {
		Player player = minecraft.getPlayer();
		double d = entity.getX() - player.getX();
		double d1 = entity.getZ() - player.getZ();
		return (float) ((Math.atan2(d1, d) * 180D) / 3.1415927410125732D) - 90F;
	}

	public float getFacingRotationY(Entity entity) {
		Player player = minecraft.getPlayer();
		double dis1 = entity.getY() + 1 - player.getY() + 1;
		double dis2 = Math.sqrt(Math.pow(entity.getX() - player.getX(), 2)
				+ Math.pow(entity.getZ() - player.getZ(), 2));
		return (float) ((Math.atan2(dis2, dis1) * 180D) / 3.1415927410125732D)
				- 80F
				- ((float) Math.pow(
						getDistanceTo(player, entity.getX(), entity.getY(),
								entity.getZ()) / 4, 2));
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

	public boolean isFriend(String name) {
		synchronized(friends) {
			return friends.contains(ChatColor.removeColors(name).toLowerCase());
		}
	}

	public Team getTeam() {
		return team;
	}
}

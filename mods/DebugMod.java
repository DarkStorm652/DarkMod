import java.lang.reflect.Field;
import java.util.Vector;

import org.darkstorm.minecraft.darkmod.events.RenderEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.Event;
import org.darkstorm.tools.strings.StringTools;
import org.lwjgl.opengl.GL11;

public class DebugMod extends Mod implements CommandListener {

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Debug Mod";
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
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public int loop() {
		return STOP;
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("debug",
				"/debug <id> [args...]", "asdf"), this);
		eventManager.addListener(RenderEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(RenderEvent.class, this);
		commandManager.unregisterListener("debug");
	}

	@Override
	@SuppressWarnings("unused")
	public void onCommand(String command) {
		try {
			String[] parts = command.split(" ");
			if(parts[0].equalsIgnoreCase("debug")) {
				if(parts.length == 1) {
					displayText(ChatColor.DARK_RED
							+ "/debug <user|target|spawn|reflection <field|method> <info>>");
				}
				if(parts[1].equalsIgnoreCase("user")) {
					Session session = minecraft.getSession();
					displayText(ChatColor.GRAY + "Name: "
							+ session.getUsername() + " Password: "
							+ session.getMultiplayerPassword() + " SessionID: "
							+ session.getSessionID());
				} else if(parts[1].equalsIgnoreCase("target")) {
					EntityTarget mop = minecraft.getPlayerTarget();
					if(mop != null)
						displayText(ChatColor.GRAY + "Target: ("
								+ mop.getTargetX() + ", " + mop.getTargetY()
								+ ", " + mop.getTargetZ() + ") "
								+ mop.getTargetFace());
					else
						displayText(ChatColor.GRAY + "No target");
				} else if(parts[1].equalsIgnoreCase("spawn")) {
					World world = minecraft.getWorld();
					WorldInfo worldInfo = world.getWorldInfo();
					displayText(ChatColor.GRAY + "Spawn location: ("
							+ worldInfo.getSpawnX() + ", "
							+ worldInfo.getSpawnY() + ", "
							+ worldInfo.getSpawnZ() + ")");
				} else if(parts[1].equalsIgnoreCase("reflection")) {
					if(parts[2].equalsIgnoreCase("field")) {
						String completePath = parts[3];
						String[] fieldParts = completePath.split("\\.");
						if(fieldParts.length < 2)
							return;
						String className = fieldParts[0];
						if(className.equalsIgnoreCase("minecraft"))
							className = "net.minecraft.client.Minecraft";
						Class<?> loadedClass;
						try {
							ClassLoader classLoader = accessHandler
									.getClassLoader();
							loadedClass = classLoader.loadClass(className);
						} catch(ClassNotFoundException exception1) {
							displayText(ChatColor.GRAY + "Class " + className
									+ " not found");
							return;
						}
						Object fieldValue = null;
						Class<?> fieldClass = loadedClass;
						if(fieldParts[0].equalsIgnoreCase("minecraft"))
							fieldValue = minecraft;
						for(int i = 1; i < fieldParts.length; i++) {
							if(fieldParts[i].equals("super")) {
								fieldClass = fieldClass.getSuperclass();
								continue;
							}
							Field field;
							try {
								field = fieldClass
										.getDeclaredField(fieldParts[i]);
							} catch(SecurityException exception) {
								displayText(ChatColor.GRAY
										+ "Security exception");
								exception.printStackTrace();
								return;
							} catch(NoSuchFieldException exception) {
								String path = fieldParts[0];
								for(int i2 = 1; i2 < i + 1; i2++)
									path += "." + fieldParts[i2];
								displayText(ChatColor.GRAY + "Field " + path
										+ " not found");
								return;
							}
							if(!field.isAccessible())
								field.setAccessible(true);
							try {
								fieldValue = field.get(fieldValue);
								if(fieldValue == null)
									break;
								fieldClass = fieldValue.getClass();
							} catch(IllegalArgumentException exception) {
								String path = fieldParts[0];
								for(int i2 = 1; i2 < i + 1; i2++)
									path += "." + fieldParts[i2];
								displayText(ChatColor.GRAY
										+ "Unable to access " + path);
								exception.printStackTrace();
							} catch(IllegalAccessException exception) {
								String path = fieldParts[0];
								for(int i2 = 1; i2 < i + 1; i2++)
									path += "." + fieldParts[i2];
								displayText(ChatColor.GRAY + "Field " + path
										+ " not accessible (shouldn\'t happen)");
								exception.printStackTrace();
							}
						}
						displayText(ChatColor.GRAY + fieldValue.toString());
					} else if(parts[2].equalsIgnoreCase("method")) {
						String className = parts[3];
						String methodName = parts[4];
						Vector<String> newParts = new Vector<String>();
						boolean quoted = false;
						int partsIndex = 0;
						for(int i = 5; i < parts.length; i++) {
							if(quoted && parts[i].length() == 0)
								newParts.set(partsIndex,
										newParts.get(partsIndex) + " ");
							else if(parts[i].startsWith("\"")) {
								if(quoted && parts[i].length() == 1) {
									newParts.set(partsIndex,
											newParts.get(partsIndex) + " ");
									quoted = false;
								} else if(!quoted) {
									newParts.add(parts[i].replaceFirst("\\\"",
											""));
									quoted = true;
									continue;
								}
							} else if(parts[i].endsWith("\"")) {
								newParts.set(partsIndex,
										newParts.get(partsIndex) + " "
												+ parts[i]);
								quoted = false;
							} else if(!quoted)
								newParts.add(parts[i]);
							if(!quoted)
								partsIndex++;
						}
						for(String part : newParts)
							System.out.println(part);
						if("".equals(""))
							return;
						int length = parts.length - 5;
						if(length % 2 != 0)
							return;
						String[] argTypeNames = new String[length / 2];

					}
				} else if(parts[1].equalsIgnoreCase("inventory")
						&& parts.length > 2 && StringTools.isInteger(parts[2])) {
					Player player = minecraft.getPlayer();
					if(player == null)
						return;
					Inventory inventory = player.getInventory();
					int index = Integer.parseInt(parts[2]);
					if(index < 0 || index > 35)
						return;
					displayText("Item: " + inventory.getItemAt(index));
				} else if(parts[1].equalsIgnoreCase("armor")
						&& parts.length > 2 && StringTools.isInteger(parts[2])) {
					Player player = minecraft.getPlayer();
					if(player == null)
						return;
					Inventory inventory = player.getInventory();
					int index = Integer.parseInt(parts[2]);
					if(index < 0 || index > 3)
						return;
					displayText("Armor: " + inventory.getArmorAt(index));
				}
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	@Override
	public void onEvent(Event event) {
		RenderEvent renderEvent = (RenderEvent) event;
		System.out.println("Rendering");
		if(renderEvent.getStatus() == RenderEvent.RENDER) {
			if("".equals(""))
				return;
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			minecraft.getFont().drawStringWithShadow("HELLO LOL", 30, 30,
					0xFFFFFFFF);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			return;
		} else if(renderEvent.getStatus() != RenderEvent.RENDER_ENTITIES)
			return;
		Player player = minecraft.getPlayer();
		World world = minecraft.getWorld();
		if(player == null || world == null)
			return;
		try {
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glLineWidth(3.0F);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(false);

			double size = 0.45;
			double ytSize = 0.35;
			GL11.glBegin(GL11.GL_LINES);
			GL11.glColor3f(0, 255, 0);
			double mX = player.getX();
			double mY = player.getY();
			double mZ = player.getZ();
			Location location = new Location(mX, mY, mZ);
			for(int x = 0; x < world.getPlayers().size(); x++) {
				Entity entity = world.getPlayers().get(x);
				double X = entity.getX();
				double Y = entity.getY();
				double Z = entity.getZ();
				if(getDistanceBetween(location, new Location(X, Y, Z)) > 25)
					continue;
				double dX = (mX - X);
				double dY = (mY - Y);
				double dZ = (mZ - Z);

				if(X != mX && Y != mY && Z != mZ) {
					GL11.glVertex3d(0, 0, 0);
					GL11.glVertex3d((-dX + size) - 0.5, (ytSize - dY) + 1.0,
							(-dZ - size) + 0.5);
				}
			}

			GL11.glEnd();
			GL11.glDepthMask(true);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

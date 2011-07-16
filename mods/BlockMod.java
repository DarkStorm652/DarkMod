import java.util.Vector;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.Location;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.*;
import org.darkstorm.tools.strings.StringTools;
import org.lwjgl.input.*;

public class BlockMod extends Mod implements EventListener, CommandListener {
	public Vector<Location> queuedLocations = new Vector<Location>();
	public Vector<Location> queuedSuperBreaker = new Vector<Location>();
	public Object lock = new Object();

	public int wandID = 280;
	public int wandType = -1;
	public static final int WAND_NONE = -1;
	public static final int WAND_SELECT = 0;
	public static final int WAND_SUPER = 1;
	public int superWandHitType = 0;
	public static final int SUPER_WAND_PER_CLICK = 0;
	public static final int SUPER_WAND_ON_HOLD = 1;

	public boolean allowRemoval = false;
	public boolean stopRemoving = false;
	public boolean buttonReleased = true;
	public Location location1;
	public Location location2;

	public int lastRemoveX = 0;
	public int lastRemoveY = 0;
	public int lastRemoveZ = 0;
	public int removeFailCounter = 0;

	public boolean tunnel;
	public int tunnelWidth;
	public int tunnelHeight;
	public int direction = -1;
	public static final int NORTH_SOUTH = 0;
	public static final int EAST_WEST = 1;
	public static final int UP_DOWN = 2;

	public int removalRate = 1;

	public Class<?> blockDigClass;
	public Class<?> blockPlaceClass;
	public Class<?> inventoryItemSelectClass;
	public Class<?> inventoryItemClass;
	public GuiScreen lastGuiScreen;

	public BlockMod() {
		blockDigClass = ClassRepository
				.getClassForInterface(BlockDigPacket.class);
		blockPlaceClass = ClassRepository
				.getClassForInterface(BlockPlacePacket.class);
		inventoryItemSelectClass = ClassRepository
				.getClassForInterface(InventoryItemSelectPacket.class);
		inventoryItemClass = ClassRepository
				.getClassForInterface(InventoryItem.class);
	}

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Block Mod";
	}

	@Override
	public String getShortDescription() {
		return "Makes/removes blocks";
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
	public void onStart() {
		allowRemoval = true;
		eventManager.addListener(RenderEvent.class, this);
		eventManager.addListener(BlockDigEvent.class, this);
		eventManager.addListener(PacketEvent.class, this);
		commandManager.registerListener(new Command("set", "/set <id>",
				"Sets the area to an ID. If the ID is 0, blocks are removed"),
				this);
		commandManager
				.registerListener(
						new Command(
								"tunnel",
								"/tunnel <direction:ns|ew|ud>",
								"Tunnels in a specified direction. ns for north/south, ew for east/west, ud for up/down"),
						this);
		commandManager.registerListener(new Command("walls", "/walls <id>",
				"Creates walls using blocks of the specified ID"), this);
		commandManager.registerListener(new Command("platform",
				"/platform <id> <direction>",
				"Creates a platform on the side facing the direction"), this);
		commandManager.registerListener(new Command("box", "/box <id>",
				"Creates a box using blocks of the specified ID."), this);
		commandManager.registerListener(new Command("expand",
				"/expand <amount> <direction>",
				"Expands the selected area in a direction by an amount"), this);
		commandManager.registerListener(new Command("contract",
				"/contract <amount> <direction>",
				"Contracts the selected area in a direction by an amount"),
				this);
		commandManager.registerListener(new Command("move",
				"/move <amount> <direction>",
				"Moves the selected area in a direction by an amount"), this);
		commandManager.registerListener(
				new Command("wand", "/wand <select|super [click|hold]>",
						"Use /wand for more help"), this);
		commandManager
				.registerListener(new Command("removalrate",
						"/removalrate <rate>",
						"Sets the removal per frame rate"), this);
		commandManager.registerListener(new Command("location1",
				"/location1 [set <x> <y> <z>]", "Used for selection point 1"),
				this);
		commandManager.registerListener(new Command("location2",
				"/location2 [set <x> <y> <z>]", "Used for selection point 2"),
				this);
		commandManager.registerListener(new Command("search",
				"/search <blockID>",
				"Search for blocks within the selected area"), this);
		commandManager.registerListener(
				new Command("superbreaker", "/superbreaker",
						"Uses McMMO super-breaker on selected blocks"), this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(RenderEvent.class, this);
		eventManager.removeListener(BlockDigEvent.class, this);
		eventManager.removeListener(PacketEvent.class, this);
		commandManager.unregisterListeners(this);
		allowRemoval = false;
		synchronized(lock) {
			queuedLocations.clear();
			queuedSuperBreaker.clear();
		}
	}

	@Override
	public void onCommand(String command) {
		try {
			if(minecraft.getWorld() != null) {
				String[] parts = command.split(" ");
				if(parts[0].equalsIgnoreCase("set") && parts.length == 2
						&& areLocationsValid()) {
					if(!StringTools.isInteger(parts[1]))
						return;

					int id = Integer.parseInt(parts[1]);
					if(id == 0) {
						removeBlocks();
						displayText(ChatColor.GRAY + "Removed blocks.");
					} else {
						addBlocks(id);
						displayText(ChatColor.GRAY + "Added blocks.");
					}
				} else if(parts[0].equalsIgnoreCase("search")
						&& parts.length == 2 && areLocationsValid()) {
					World world = minecraft.getWorld();
					if(!StringTools.isInteger(parts[1]) || world == null)
						return;
					int id = Integer.parseInt(parts[1]);
					int lowestX = min((int) location1.getX(), (int) location2
							.getX());
					int highestX = max((int) location1.getX(), (int) location2
							.getX());
					int lowestY = min((int) location1.getY(), (int) location2
							.getY());
					int highestY = max((int) location1.getY(), (int) location2
							.getY());
					int lowestZ = min((int) location1.getZ(), (int) location2
							.getZ());
					int highestZ = max((int) location1.getZ(), (int) location2
							.getZ());
					for(int xOffset = 0; xOffset < highestX - lowestX + 1; xOffset++) {
						for(int yOffset = 0; yOffset < highestY - lowestY + 1; yOffset++) {
							for(int zOffset = 0; zOffset < highestZ - lowestZ
									+ 1; zOffset++) {
								int x = lowestX + xOffset;
								int y = lowestY + yOffset;
								int z = lowestZ + zOffset;
								int blockID = world.getBlockIDAt(x, y, z);
								if(blockID == id)
									displayText(ChatColor.GRAY
											+ "Block found at "
											+ ChatColor.GOLD + "(" + x + ", "
											+ y + ", " + z + ")");
							}
						}
					}
				} else if(parts[0].equalsIgnoreCase("tunnel")
						&& areLocationsValid()) {
					if(parts.length == 2) {
						Player player = minecraft.getPlayer();
						int myX = (int) player.getX();
						int myY = (int) player.getY();
						int myZ = (int) player.getZ();
						String directionString = parts[1];
						if(directionString.equalsIgnoreCase("ns")
								|| directionString.equalsIgnoreCase("sn")) {
							tunnelWidth = getMaxZ() - getMinZ() + 1;
							tunnelHeight = getMaxY() - getMinY() + 1;
							location1 = new Location(myX, location1.getY(),
									location1.getZ());
							location2 = new Location(myX, location2.getY(),
									location2.getZ());
							direction = NORTH_SOUTH;
							displayText(ChatColor.GRAY + "Tunneling "
									+ ChatColor.GOLD + "north/south");
						} else if(directionString.equalsIgnoreCase("ew")
								|| directionString.equalsIgnoreCase("we")) {
							tunnelWidth = getMaxX() - getMinX();
							tunnelHeight = getMaxY() - getMinY();
							location1 = new Location(location1.getX(),
									location1.getY(), myZ);
							location2 = new Location(location2.getX(),
									location2.getY(), myZ);
							direction = EAST_WEST;
							displayText(ChatColor.GRAY + "Tunneling "
									+ ChatColor.GOLD + "east/west");
						} else if(directionString.equalsIgnoreCase("ud")
								|| directionString.equalsIgnoreCase("du")) {
							tunnelWidth = getMaxX() - getMinX();
							tunnelHeight = getMaxZ() - getMinZ();
							location1 = new Location(location1.getX(), myY,
									location1.getZ());
							location2 = new Location(location2.getX(), myY,
									location2.getZ());
							direction = UP_DOWN;
							displayText(ChatColor.GRAY + "Tunneling "
									+ ChatColor.GOLD + "up/down");
						} else
							return;
						tunnel = true;
					}
				} else if(parts[0].equalsIgnoreCase("expand")
						&& parts.length == 3 && areLocationsValid()) {
					if(!StringTools.isInteger(parts[1]))
						return;
					int value = Integer.parseInt(parts[1]);
					char direction = parts[2].charAt(0);
					Location location;
					switch(direction) {
					case 'n':
						location = location1.getX() == getMinX() ? location1
								: location2;
						location.setX(location.getX() - value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " north");
						break;
					case 's':
						location = location1.getX() == getMaxX() ? location1
								: location2;
						location.setX(location.getX() + value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " south");
						break;
					case 'e':
						location = location1.getZ() == getMinZ() ? location1
								: location2;
						location.setZ(location.getZ() - value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " east");
						break;
					case 'w':
						location = location1.getZ() == getMaxZ() ? location1
								: location2;
						location.setZ(location.getZ() + value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " west");
						break;
					case 'u':
						location = location1.getY() == getMaxY() ? location1
								: location2;
						location.setY(location.getY() + value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " up");
						break;
					case 'd':
						location = location1.getY() == getMinY() ? location1
								: location2;
						location.setY(location.getY() - value);
						displayText(ChatColor.GRAY + "Expanded "
								+ ChatColor.GOLD + value + " down");
						break;
					}
				} else if(parts[0].equalsIgnoreCase("contract")
						&& parts.length == 3 && areLocationsValid()) {
					if(!StringTools.isInteger(parts[1]))
						return;
					int value = Integer.parseInt(parts[1]);
					char direction = parts[2].charAt(0);
					Location location;
					switch(direction) {
					case 'n':
						location = location1.getX() == getMaxX() ? location1
								: location2;
						location.setX(location.getX() - value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " north");
						break;
					case 's':
						location = location1.getX() == getMinX() ? location1
								: location2;
						location.setX(location.getX() + value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " south");
						break;
					case 'e':
						location = location1.getZ() == getMaxZ() ? location1
								: location2;
						location.setZ(location.getZ() - value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " east");
						break;
					case 'w':
						location = location1.getZ() == getMinZ() ? location1
								: location2;
						location.setZ(location.getZ() + value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " west");
						break;
					case 'u':
						location = location1.getY() == getMinY() ? location1
								: location2;
						location.setY(location.getY() + value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " up");
						break;
					case 'd':
						location = location1.getY() == getMaxY() ? location1
								: location2;
						location.setY(location.getY() - value);
						displayText(ChatColor.GRAY + "Contracted "
								+ ChatColor.GOLD + value + " down");
						break;
					}
				} else if(parts[0].equalsIgnoreCase("move")
						&& parts.length == 3 && areLocationsValid()
						&& StringTools.isInteger(parts[1])) {
					int value = Integer.parseInt(parts[1]);
					char direction = parts[2].charAt(0);
					Location location;
					switch(direction) {
					case 'n':
						location = location1.getX() == getMinX() ? location1
								: location2;
						location.setX(location.getX() - value);
						location = location1.getX() == getMaxX() ? location1
								: location2;
						location.setX(location.getX() - value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " north");
						break;
					case 's':
						location = location1.getX() == getMaxX() ? location1
								: location2;
						location.setX(location.getX() + value);
						location = location1.getX() == getMinX() ? location1
								: location2;
						location.setX(location.getX() + value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " south");
						break;
					case 'e':
						location = location1.getZ() == getMinZ() ? location1
								: location2;
						location.setZ(location.getZ() - value);
						location = location1.getZ() == getMaxZ() ? location1
								: location2;
						location.setZ(location.getZ() - value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " east");
						break;
					case 'w':
						location = location1.getZ() == getMaxZ() ? location1
								: location2;
						location.setZ(location.getZ() + value);
						location = location1.getZ() == getMinZ() ? location1
								: location2;
						location.setZ(location.getZ() + value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " west");
						break;
					case 'u':
						location = location1.getY() == getMaxY() ? location1
								: location2;
						location.setY(location.getY() + value);
						location = location1.getY() == getMinY() ? location1
								: location2;
						location.setY(location.getY() + value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " up");
						break;
					case 'd':
						location = location1.getY() == getMinY() ? location1
								: location2;
						location.setY(location.getY() - value);
						location = location1.getY() == getMaxY() ? location1
								: location2;
						location.setY(location.getY() - value);
						displayText(ChatColor.GRAY + "Moved " + ChatColor.GOLD
								+ value + " down");
						break;
					}
				} else if(parts[0].equalsIgnoreCase("wand")) {
					if(parts.length == 1) {
						displayText(ChatColor.GRAY
								+ "/wand <set <id>|select|super [click|hold]>");
						displayText(ChatColor.GRAY + "   /wand set <id>");
						displayText(ChatColor.GRAY
								+ "      Sets the wand ID. Used to change wand tool.");
						displayText(ChatColor.GRAY + "   /wand select");
						displayText(ChatColor.GRAY
								+ "      Sets the wand type to selection.");
						displayText(ChatColor.GRAY
								+ "      Left click for location 1, right for 2");
						displayText(ChatColor.GRAY
								+ "   /wand super [click|hold]");
						displayText(ChatColor.GRAY
								+ "      Sets the wand type to super.");
						displayText(ChatColor.GRAY
								+ "      Click for removal per click, hold to hold and remove");
					} else if(parts.length == 2) {
						if(parts[1].equalsIgnoreCase("select")) {
							wandType = WAND_SELECT;
							displayText(ChatColor.GRAY + "Wand type is now "
									+ ChatColor.GOLD + "selection");
						} else if(parts[1].equalsIgnoreCase("super")) {
							wandType = WAND_SUPER;
							displayText(ChatColor.GRAY
									+ "Wand type is now "
									+ ChatColor.GOLD
									+ "super "
									+ (superWandHitType == SUPER_WAND_PER_CLICK ? "on click"
											: "per frame on hold"));
						} else if(parts[1].equalsIgnoreCase("off")) {
							wandType = WAND_NONE;
							displayText(ChatColor.GRAY + "Wand type is now "
									+ ChatColor.GOLD + "off");
						} else if(parts[1].equalsIgnoreCase("set")) {
							Player player = minecraft.getPlayer();
							Inventory inventory = player.getInventory();
							InventoryItem item = inventory.getSelectedItem();
							wandID = item.getID();
							displayText(ChatColor.GRAY + "Wand id is now "
									+ ChatColor.GOLD + wandID);
						}
					} else if(parts.length == 3) {
						if(parts[1].equalsIgnoreCase("super")) {
							wandType = WAND_SUPER;
							if(parts[2].equalsIgnoreCase("click")) {
								superWandHitType = SUPER_WAND_PER_CLICK;
								displayText(ChatColor.GRAY
										+ "Wand type is now " + ChatColor.GOLD
										+ "super on click");
							} else if(parts[2].equalsIgnoreCase("hold")) {
								superWandHitType = SUPER_WAND_ON_HOLD;
								displayText(ChatColor.GRAY
										+ "Wand type is now " + ChatColor.GOLD
										+ "super while held");
							}
						} else if(parts[1].equalsIgnoreCase("set")
								&& StringTools.isInteger(parts[1])) {
							wandID = Integer.parseInt(parts[1]);
							displayText(ChatColor.GRAY + "Wand id is now "
									+ ChatColor.GOLD + wandID);
						}
					}
				} else if(parts[0].equalsIgnoreCase("removalrate")
						&& parts.length == 2 && StringTools.isInteger(parts[1])) {
					int value = Integer.parseInt(parts[1]);
					if(value < 1)
						return;
					removalRate = value;
					displayText(ChatColor.GRAY
							+ "Wand removal rate per recurse is now "
							+ ChatColor.GOLD + value);
				} else if(parts[0].equalsIgnoreCase("walls")
						&& parts.length == 2 && StringTools.isInteger(parts[1])) {
					int id = Integer.parseInt(parts[1]);
					Player player = minecraft.getPlayer();
					Inventory inventory = player.getInventory();
					boolean multiplayer = player instanceof MultiplayerPlayer;
					if(multiplayer && inventory.getIndexOf(id) == -1) {
						displayText(ChatColor.GRAY
								+ "Inventory does not contain any "
								+ ChatColor.GOLD + +id);
						return;
					}
					int minX = getMinX();
					int minY = getMinY();
					int minZ = getMinZ();
					int maxX = getMaxX();
					int maxY = getMaxY();
					int maxZ = getMaxZ();
					int missing = 0;
					for(int y = minY; y <= maxY; y++) {
						for(int x = minX; x <= maxX; x++) {
							for(int z = minZ; z <= maxZ; z++) {
								if(x != minX && x != maxX && z != minZ
										&& z != maxZ)
									continue;
								if(multiplayer) {
									if(inventory.getIndexOf(id) != -1)
										addBlock(x, y, z, id);
									else
										missing++;
								} else
									addBlock(x, y, z, id);
							}
						}
					}
					if(missing == 0)
						displayText(ChatColor.GRAY + "Walls created.");
					else
						displayText(ChatColor.GOLD.toString() + missing
								+ ChatColor.GRAY + " more block"
								+ (missing == 1 ? "" : "s") + " required");
				} else if(parts[0].equalsIgnoreCase("platform")
						&& parts.length == 3 && StringTools.isInteger(parts[2])) {
					char direction = parts[1].charAt(0);
					int id = Integer.parseInt(parts[1]);
					Player player = minecraft.getPlayer();
					Inventory inventory = player.getInventory();
					boolean multiplayer = player instanceof MultiplayerPlayer;
					if(multiplayer && inventory.getIndexOf(id) == -1) {
						displayText(ChatColor.GRAY
								+ "Inventory does not contain any "
								+ ChatColor.GOLD + id);
						return;
					}
					int minX = getMinX();
					int minY = getMinY();
					int minZ = getMinZ();
					int maxX = getMaxX();
					int maxY = getMaxY();
					int maxZ = getMaxZ();
					int missing = 0;
					int x = minX, y = minY, z = minZ;
					switch(direction) {
					case 's':
						x = maxX;
					case 'n':
						for(; z <= maxZ; z++)
							for(; y <= maxY; y++)
								if(multiplayer) {
									if(inventory.getIndexOf(id) != -1)
										addBlock(x, y, z, id);
									else
										missing++;
								} else
									addBlock(x, y, z, id);
						break;
					case 'w':
						z = maxZ;
					case 'e':
						for(; x <= maxX; x++)
							for(; y <= maxY; y++)
								if(multiplayer) {
									if(inventory.getIndexOf(id) != -1)
										addBlock(x, y, z, id);
									else
										missing++;
								} else
									addBlock(x, y, z, id);
						break;
					case 'u':
						y = maxY;
					case 'd':
						for(; x <= maxX; x++)
							for(; z <= maxZ; z++)
								if(multiplayer) {
									if(inventory.getIndexOf(id) != -1)
										addBlock(x, y, z, id);
									else
										missing++;
								} else
									addBlock(x, y, z, id);
						break;
					default:
						return;
					}
					if(missing == 0)
						displayText(ChatColor.GRAY + "Platform created.");
					else
						displayText(ChatColor.GOLD.toString() + missing
								+ ChatColor.GRAY + " more block"
								+ (missing == 1 ? "" : "s") + " required");
				} else if(parts[0].equalsIgnoreCase("box")) {
					int id = Integer.parseInt(parts[1]);
					Player player = minecraft.getPlayer();
					Inventory inventory = player.getInventory();
					boolean multiplayer = player instanceof MultiplayerPlayer;
					if(multiplayer && inventory.getIndexOf(id) == -1) {
						displayText(ChatColor.GRAY
								+ "Inventory does not contain any "
								+ ChatColor.GOLD + id);
						return;
					}
					int minX = getMinX();
					int minY = getMinY();
					int minZ = getMinZ();
					int maxX = getMaxX();
					int maxY = getMaxY();
					int maxZ = getMaxZ();
					int missing = 0;
					for(int y = minY; y <= maxY; y++) {
						for(int x = minX; x <= maxX; x++) {
							for(int z = minZ; z <= maxZ; z++) {
								if(x != minX && x != maxX && z != minZ
										&& z != maxZ && y != minY && y != maxY)
									continue;
								if(multiplayer) {
									if(inventory.getIndexOf(id) != -1)
										addBlock(x, y, z, id);
									else
										missing++;
								} else
									addBlock(x, y, z, id);
							}
						}
					}
					if(missing == 0)
						displayText(ChatColor.GRAY + "Box created.");
					else
						displayText(ChatColor.GOLD.toString() + missing
								+ ChatColor.GRAY + " more block"
								+ (missing == 1 ? "" : "s") + " required");
				} else if(parts[0].equalsIgnoreCase("location1")) {
					if(parts.length == 1 && location1 != null) {
						int x = (int) location1.getX();
						int y = (int) location1.getY();
						int z = (int) location1.getZ();
						World world = minecraft.getWorld();
						if(world == null)
							return;
						int id = world.getBlockIDAt(x, y, z);
						displayText(ChatColor.GRAY + "Location 1: "
								+ ChatColor.GOLD + location1.toString()
								+ " (ID: " + id + ")");
					} else if(parts.length == 5
							&& parts[1].equalsIgnoreCase("set")) {
						if(!StringTools.isInteger(parts[2])
								|| !StringTools.isInteger(parts[3])
								|| !StringTools.isInteger(parts[4])) {
							displayText(ChatColor.GRAY + "Invalid coordinates.");
							return;
						}
						int x = Integer.valueOf(parts[2]);
						int y = Integer.valueOf(parts[3]);
						int z = Integer.valueOf(parts[4]);
						location1 = new Location(x, y, z);
						displayText(ChatColor.GRAY + "Location 1 set to "
								+ ChatColor.GOLD + location1.toString());
					}
				} else if(parts[0].equalsIgnoreCase("location2")) {
					if(parts.length == 1 && location2 != null) {
						int x = (int) location2.getX();
						int y = (int) location2.getY();
						int z = (int) location2.getZ();
						World world = minecraft.getWorld();
						if(world == null)
							return;
						int id = world.getBlockIDAt(x, y, z);
						displayText(ChatColor.GRAY + "Location 2: "
								+ ChatColor.GOLD + location2.toString()
								+ " (ID: " + id + ")");
					} else if(parts.length == 5
							&& parts[1].equalsIgnoreCase("set")) {
						if(!StringTools.isInteger(parts[2])
								|| !StringTools.isInteger(parts[3])
								|| !StringTools.isInteger(parts[4])) {
							displayText(ChatColor.GRAY + "Invalid coordinates.");
							return;
						}
						int x = Integer.valueOf(parts[2]);
						int y = Integer.valueOf(parts[3]);
						int z = Integer.valueOf(parts[4]);
						location2 = new Location(x, y, z);
						displayText(ChatColor.GRAY + "Location 2 set to "
								+ ChatColor.GOLD + location2.toString());
					}
				} else if(parts[0].equalsIgnoreCase("superbreaker")
						&& areLocationsValid()) {
					if(!(minecraft.getWorld() instanceof MultiplayerWorld)) {
						displayText(ChatColor.RED + "Multiplayer only!");
						return;
					}
					int lowestX = min((int) location1.getX(), (int) location2
							.getX());
					int highestX = max((int) location1.getX(), (int) location2
							.getX());
					int lowestY = min((int) location1.getY(), (int) location2
							.getY());
					int highestY = max((int) location1.getY(), (int) location2
							.getY());
					int lowestZ = min((int) location1.getZ(), (int) location2
							.getZ());
					int highestZ = max((int) location1.getZ(), (int) location2
							.getZ());

					synchronized(lock) {
						for(int xOffset = 0; xOffset < highestX - lowestX + 1; xOffset++) {
							for(int yOffset = highestY - lowestY; yOffset >= 0; yOffset--) {
								for(int zOffset = 0; zOffset < highestZ
										- lowestZ + 1; zOffset++) {
									int x = lowestX + xOffset;
									int y = lowestY + yOffset;
									int z = lowestZ + zOffset;
									Location location = new Location(x, y, z);
									if(!queuedSuperBreaker.contains(location))
										queuedSuperBreaker.add(location);
								}
							}
						}
					}
				} else if(!areLocationsValid())
					displayText(ChatColor.GRAY + "Locations not set.");
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public boolean canPlaceAt(int x, int y, int z) {
		World world = minecraft.getWorld();
		if(world != null) {
			int blockID = world.getBlockIDAt(x, y, z);
			if((blockID == 0 || (blockID >= 8 && blockID <= 11))
					&& hasAdjacentBlock(x, y, z))
				return true;
		}
		return false;
	}

	public boolean hasAdjacentBlock(int x, int y, int z) {
		World world = minecraft.getWorld();
		if(world != null) {
			if(world.getBlockIDAt(x + 1, y, z) != 0
					|| world.getBlockIDAt(x - 1, y, z) != 0
					|| world.getBlockIDAt(x, y + 1, z) != 0
					|| world.getBlockIDAt(x, y - 1, z) != 0
					|| world.getBlockIDAt(x, y, z + 1) != 0
					|| world.getBlockIDAt(x, y, z - 1) != 0)
				return true;
		}
		return false;
	}

	@Override
	public int loop() {
		return 500;
	}

	protected double getDistanceBetweenOptimized(Location location1,
			Location location2) {
		double x = Math.max(location1.getX(), location2.getX())
				- (Math.min(location1.getX(), location2.getX()));
		double y = Math.max(location1.getY(), location2.getY())
				- (Math.min(location1.getY(), location2.getY()));
		double z = Math.max(location1.getZ(), location2.getZ())
				- (Math.min(location1.getZ(), location2.getZ()));
		return x * x + y * y + z * z;
	}

	public int min(int a, int b) {
		return java.lang.Math.min(a, b);
	}

	public int max(int a, int b) {
		return java.lang.Math.max(a, b);
	}

	public void punchAt(int x, int y, int z) {
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			Packet packet = (Packet) ReflectionUtil.instantiate(blockDigClass,
					new Class<?>[] { Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, Integer.TYPE }, 0, x, y, z, 0);
			networkHandler.sendPacket(packet);
		}
	}

	public void removeAt(int x, int y, int z) {
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			Packet packet = (Packet) ReflectionUtil.instantiate(blockDigClass,
					new Class<?>[] { Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, Integer.TYPE }, 0, x, y, z, 0);
			networkHandler.sendPacket(packet);
			packet = (Packet) ReflectionUtil.instantiate(blockDigClass,
					new Class<?>[] { Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, Integer.TYPE }, 2, x, y, z, 0);
			networkHandler.sendPacket(packet);
			world.setBlockIDAt(x, y, z, 0);
		} else {
			World world = minecraft.getWorld();
			world.setBlockIDAt(x, y, z, 0);
		}
	}

	public boolean checkRemove(int x, int y, int z) {
		if(x != lastRemoveX || y != lastRemoveY || z != lastRemoveZ) {
			lastRemoveX = x;
			lastRemoveY = y;
			lastRemoveZ = z;
			removeFailCounter = 0;
		}
		if(!allowRemoval) {
			return true;
		}
		if(stopRemoving) {
			World world = minecraft.getWorld();
			if(world.getBlockIDAt(x, y, z) == 0) {
				return true;
			} else if(removeFailCounter > 2) {
				return true;
			} else {
				removeFailCounter++;
				stopRemoving = false;
				return false;
			}
		}
		return false;
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof RenderEvent) {
			try {
				Player player = minecraft.getPlayer();
				if(Mouse.isButtonDown(0)) {
					if(player != null) {
						Inventory inventory = player.getInventory();
						InventoryItem selectedItem = inventory
								.getSelectedItem();
						if(selectedItem != null) {
							int selectedID = selectedItem.getID();
							if(selectedID == wandID) {
								EntityTarget target = minecraft
										.getPlayerTarget();
								if(target != null)
									if(wandType == WAND_SELECT
											&& buttonReleased) {
										location1 = new Location(target
												.getTargetX(), target
												.getTargetY(), target
												.getTargetZ());
										displayText(ChatColor.GRAY
												+ "Location 1 set to "
												+ ChatColor.GOLD + location1);
									} else if(wandType == WAND_SUPER
											&& (superWandHitType == SUPER_WAND_ON_HOLD || (superWandHitType == SUPER_WAND_PER_CLICK && buttonReleased)))
										synchronized(lock) {
											World world = minecraft.getWorld();
											if(world instanceof MultiplayerWorld) {
												MultiplayerPlayerController controller = (MultiplayerPlayerController) minecraft
														.getPlayerController();
												controller.setRemoving(false);
												controller.digBlockAt(target
														.getTargetX(), target
														.getTargetY(), target
														.getTargetZ(), target
														.getTargetFace());
												controller
														.setPercentComplete(1.0F);
												controller.setRemoving(true);
												controller.digBlockAt(target
														.getTargetX(), target
														.getTargetY(), target
														.getTargetZ(), target
														.getTargetFace());
											} else
												world.setBlockIDAt(target
														.getTargetX(), target
														.getTargetY(), target
														.getTargetZ(), 0);
										}
							}
						}
					}
					buttonReleased = false;
				} else if(Mouse.isButtonDown(1)) {
					if(player != null) {
						Inventory inventory = player.getInventory();
						InventoryItem selectedItem = inventory
								.getSelectedItem();
						if(selectedItem != null) {
							int selectedID = selectedItem.getID();
							if(selectedID == wandID) {
								EntityTarget target = minecraft
										.getPlayerTarget();
								if(wandType == WAND_SELECT && buttonReleased
										&& target != null) {
									location2 = new Location(target
											.getTargetX(), target.getTargetY(),
											target.getTargetZ());
									displayText(ChatColor.GRAY
											+ "Location 2 set to "
											+ ChatColor.GOLD + location2);
								}
							}
						}
					}
					buttonReleased = false;
				} else if(!Mouse.isButtonDown(0) && !Mouse.isButtonDown(1)
						&& !buttonReleased)
					buttonReleased = true;

				if(Keyboard.isKeyDown(Keyboard.KEY_B)) {
					synchronized(lock) {
						queuedLocations.clear();
						queuedSuperBreaker.clear();
					}
					buttonReleased = true;
					stopRemoving = true;
					tunnel = false;
					direction = 0;
					tunnelWidth = -1;
					tunnelHeight = -1;
				} else if(player != null) {
					World world = minecraft.getWorld();

					int myX = (int) player.getX();
					int myY = (int) player.getY();
					int myZ = (int) player.getZ();
					Location myLoc = new Location(myX, myY, myZ);

					synchronized(lock) {
						if(tunnel) {
							int x = myX;
							int y = myY;
							int z = myZ;
							if(direction == -1)
								return;
							else if(direction == NORTH_SOUTH) {
								Location nextLoc = new Location(x, getMinY()
										+ (tunnelHeight / 2), getMinZ()
										+ (tunnelWidth / 2));
								double distance = getDistanceBetweenOptimized(
										myLoc, nextLoc);
								boolean[] trueFalse = { true, false };
								for(boolean on : trueFalse) {
									x = myX;
									while(distance < 36) {
										for(int yOffset = 0; yOffset < tunnelHeight; yOffset++) {
											y = getMinY() + yOffset;
											for(int zOffset = 0; zOffset < tunnelWidth; zOffset++) {
												z = getMinZ() + zOffset;
												int id = world.getBlockIDAt(x,
														y, z);
												if(id != 0 && id != 7
														&& id != 8 && id != 9
														&& id != 10 && id != 11) {
													Location removeLoc = new Location(
															x, y, z);
													if(!queuedLocations
															.contains(removeLoc))
														queuedLocations
																.add(removeLoc);
												}
											}
										}
										x += on ? 1 : -1;
										nextLoc = new Location(x, getMinY()
												+ (tunnelHeight / 2), getMinZ()
												+ (tunnelWidth / 2));
										distance = getDistanceBetweenOptimized(
												myLoc, nextLoc);
									}
								}
							} else if(direction == EAST_WEST) {
								Location nextLoc = new Location(getMinX()
										+ (tunnelHeight / 2), getMinY()
										+ (tunnelWidth / 2), z);
								double distance = getDistanceBetweenOptimized(
										myLoc, nextLoc);
								boolean[] trueFalse = { true, false };
								for(boolean on : trueFalse) {
									z = myZ;
									while(distance < 36) {
										for(int xOffset = 0; xOffset < tunnelHeight; xOffset++) {
											x = getMinX() + xOffset;
											for(int yOffset = 0; yOffset < tunnelWidth; yOffset++) {
												y = getMinY() + yOffset;
												int id = world.getBlockIDAt(x,
														y, z);
												if(id != 0 && id != 7
														&& id != 8 && id != 9
														&& id != 10 && id != 11
														&& id != 90) {
													Location removeLoc = new Location(
															x, y, z);
													if(!queuedLocations
															.contains(removeLoc))
														queuedLocations
																.add(removeLoc);
												}
											}
										}
										z += on ? 1 : -1;
										nextLoc = new Location(getMinX()
												+ (tunnelHeight / 2), getMinY()
												+ (tunnelWidth / 2), z);
										distance = getDistanceBetweenOptimized(
												myLoc, nextLoc);
									}
								}
							} else if(direction == UP_DOWN) {
								Location nextLoc = new Location(getMinX()
										+ (tunnelHeight / 2), y, getMinZ()
										+ (tunnelWidth / 2));
								double distance = getDistanceBetweenOptimized(
										myLoc, nextLoc);
								boolean[] trueFalse = { true, false };
								for(boolean on : trueFalse) {
									y = myY;
									while(distance < 36) {
										for(int xOffset = 0; xOffset < tunnelHeight; xOffset++) {
											x = getMinX() + xOffset;
											for(int zOffset = 0; zOffset < tunnelWidth; zOffset++) {
												z = getMinZ() + zOffset;
												int id = world.getBlockIDAt(x,
														y, z);
												if(id != 0 && id != 7
														&& id != 8 && id != 9
														&& id != 10 && id != 11
														&& id != 90) {
													Location removeLoc = new Location(
															x, y, z);
													if(!queuedLocations
															.contains(removeLoc))
														queuedLocations
																.add(removeLoc);
												}
											}
										}
										y += on ? 1 : -1;
										nextLoc = new Location(getMinX()
												+ (tunnelHeight / 2), y,
												getMinZ() + (tunnelWidth / 2));
										distance = getDistanceBetweenOptimized(
												myLoc, nextLoc);
									}
								}
							}
						}
					}

					synchronized(lock) {
						for(int i = 0; i < removalRate
								&& queuedLocations.size() > 0; i++) {
							System.out.println("Queued length: "
									+ queuedLocations.size());
							for(int loc = 0; loc < queuedLocations.size(); loc++) {
								Location location = queuedLocations.get(loc);
								final int x = (int) location.getX();
								final int y = (int) location.getY();
								final int z = (int) location.getZ();
								int id = world.getBlockIDAt(x, y, z);
								if(id != 0 && id != 7 && id != 8 && id != 9
										&& id != 10 && id != 11) {
									if(getDistanceBetweenOptimized(
											new Location(x, y, z),
											new Location(myX, myY, myZ)) < 36) {
										removeAt(x, y, z);
										if(checkRemove(x, y, z))
											queuedLocations.remove(location);
										removeFailCounter = 0;
										stopRemoving = false;
										break;
									}
								} else
									queuedLocations.remove(location);
							}
						}
						for(int i = 0; i < removalRate
								&& queuedSuperBreaker.size() > 0; i++) {
							System.out.println("Queued super-breaker length: "
									+ queuedLocations.size());
							for(int loc = 0; loc < queuedSuperBreaker.size(); loc++) {
								Location location = queuedSuperBreaker.get(loc);
								final int x = (int) location.getX();
								final int y = (int) location.getY();
								final int z = (int) location.getZ();
								int id = world.getBlockIDAt(x, y, z);
								if(id != 0 && id != 7 && id != 8 && id != 9
										&& id != 10 && id != 11) {
									if(getDistanceBetweenOptimized(
											new Location(x, y, z),
											new Location(myX, myY, myZ)) < 36) {
										punchAt(x, y, z);
										queuedSuperBreaker.remove(location);
										break;
									}
								} else
									queuedSuperBreaker.remove(location);
							}
						}
					}
				}
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		} else if(event instanceof BlockDigEvent) {
			BlockDigEvent blockDigEvent = (BlockDigEvent) event;
			if(blockDigEvent.getStatus() == BlockDigEvent.BLOCK_REMOVED)
				stopRemoving = true;
		} else if(event instanceof PacketEvent) {
			if(((PacketEvent) event).getPacket() instanceof BlockPlacePacket) {
				BlockPlacePacket packet = (BlockPlacePacket) ((PacketEvent) event)
						.getPacket();
				String debug = "Block Place: ";
				String[] fields = { "a", "b", "c", "d" };
				for(String field : fields) {
					try {
						debug += field
								+ ": "
								+ blockPlaceClass.getField(field)
										.getInt(packet) + " ";
					} catch(Throwable exception) {
						exception.printStackTrace();
					}
				}
				try {
					InventoryItem item = (InventoryItem) blockPlaceClass
							.getField("e").get(packet);
					if(item != null)
						debug += "Item(ID: " + item.getID() + "Stack: "
								+ item.getStackCount() + ")";
					else
						debug += "No Item";
				} catch(Throwable exception) {
					exception.printStackTrace();
				}
				System.out.println(debug);
			} else if(((PacketEvent) event).getPacket() instanceof BlockDigPacket) {
				BlockDigPacket packet = (BlockDigPacket) ((PacketEvent) event)
						.getPacket();
				System.out.println(packet.getStatus() + " : " + packet.getX()
						+ " : " + packet.getY() + " : " + packet.getZ() + " : "
						+ packet.getBlockID());
			}
		}
	}

	public int getMinX() {
		return (int) Math.min(location1.getX(), location2.getX());
	}

	public int getMinY() {
		return (int) Math.min(location1.getY(), location2.getY());
	}

	public int getMinZ() {
		return (int) Math.min(location1.getZ(), location2.getZ());
	}

	public int getMaxX() {
		return (int) Math.max(location1.getX(), location2.getX());
	}

	public int getMaxY() {
		return (int) Math.max(location1.getY(), location2.getY());
	}

	public int getMaxZ() {
		return (int) Math.max(location1.getZ(), location2.getZ());
	}

	public boolean areLocationsValid() {
		return location1 != null && location2 != null;
	}

	public void removeBlocks() {
		int lowestX = min((int) location1.getX(), (int) location2.getX());
		int highestX = max((int) location1.getX(), (int) location2.getX());
		int lowestY = min((int) location1.getY(), (int) location2.getY());
		int highestY = max((int) location1.getY(), (int) location2.getY());
		int lowestZ = min((int) location1.getZ(), (int) location2.getZ());
		int highestZ = max((int) location1.getZ(), (int) location2.getZ());

		synchronized(lock) {
			for(int xOffset = 0; xOffset < highestX - lowestX + 1; xOffset++) {
				for(int yOffset = highestY - lowestY; yOffset >= 0; yOffset--) {
					for(int zOffset = 0; zOffset < highestZ - lowestZ + 1; zOffset++) {
						int x = lowestX + xOffset;
						int y = lowestY + yOffset;
						int z = lowestZ + zOffset;
						Location location = new Location(x, y, z);
						World world = minecraft.getWorld();
						if(world instanceof MultiplayerWorld) {
							if(!queuedLocations.contains(location))
								queuedLocations.add(location);
						} else
							world.setBlockIDAt(x, y, z, 0);
					}
				}
			}
		}
	}

	public void addBlocks(int id) {
		int lowestX = min((int) location1.getX(), (int) location2.getX());
		int highestX = max((int) location1.getX(), (int) location2.getX());
		int lowestY = min((int) location1.getY(), (int) location2.getY());
		int highestY = max((int) location1.getY(), (int) location2.getY());
		int lowestZ = min((int) location1.getZ(), (int) location2.getZ());
		int highestZ = max((int) location1.getZ(), (int) location2.getZ());
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		int currentIndex = inventory.getSelectedIndex();
		for(int xOffset = 0; xOffset < highestX - lowestX + 1; xOffset++) {
			for(int yOffset = 0; yOffset < highestY - lowestY + 1; yOffset++) {
				for(int zOffset = 0; zOffset < highestZ - lowestZ + 1; zOffset++) {
					int x = lowestX + xOffset;
					int y = lowestY + yOffset;
					int z = lowestZ + zOffset;
					addBlock(x, y, z, id);
				}
			}
		}
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			InventoryItemSelectPacket selectPacket = (InventoryItemSelectPacket) ReflectionUtil
					.instantiate(inventoryItemSelectClass,
							new Class<?>[] { Integer.TYPE }, currentIndex);
			networkHandler.sendPacket(selectPacket);
		}
	}

	public void addBlock(int x, int y, int z, int id) {
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			Player player = minecraft.getPlayer();
			Inventory inventory = player.getInventory();
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler networkHandler = world.getNetworkHandler();
			int index = inventory.getIndexOf(id);
			if(!canPlaceAt(x, y, z) || index == -1)
				return;
			InventoryItemSelectPacket selectPacket = (InventoryItemSelectPacket) ReflectionUtil
					.instantiate(inventoryItemSelectClass,
							new Class<?>[] { Integer.TYPE }, index);
			networkHandler.sendPacket(selectPacket);
			InventoryItem item = inventory.getItemAt(index);
			Location adjactentBlock = getAdjactentBlock(x, y, z);
			int adjactentBlockFace = getAdjactentBlockFace(x, y, z);
			BlockPlacePacket placePacket = (BlockPlacePacket) ReflectionUtil
					.instantiate(blockPlaceClass, new Class<?>[] {
							Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, inventoryItemClass },
							(int) adjactentBlock.getX(), (int) adjactentBlock
									.getY(), (int) adjactentBlock.getZ(),
							adjactentBlockFace, item);
			networkHandler.sendPacket(placePacket);
			world.setBlockIDAt(x, y, z, id);
			if(item.getStackCount() < 2)
				inventory.setItemAt(index, null);
			else
				item.setStackCount(item.getStackCount() - 1);
		} else {
			World world = minecraft.getWorld();
			world.setBlockIDAt(x, y, z, id);
		}
	}

	public int getAdjactentBlockFace(int x, int y, int z) {
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		World world = minecraft.getWorld();
		if(world == null)
			return -1;
		if(world.getBlockIDAt(x + 1, y, z) != 0)
			return 4;
		else if(world.getBlockIDAt(x - 1, y, z) != 0)
			return 5;
		else if(world.getBlockIDAt(x, y, z + 1) != 0)
			return 2;
		else if(world.getBlockIDAt(x, y, z - 1) != 0)
			return 3;
		else if(world.getBlockIDAt(x, y + 1, z) != 0)
			return 0;
		else if(world.getBlockIDAt(x, y - 1, z) != 0)
			return 1;
		else
			return -1;
	}

	public Location getAdjactentBlock(int x, int y, int z) {
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		World world = minecraft.getWorld();
		if(world == null)
			return null;
		if(world.getBlockIDAt(x + 1, y, z) != 0)
			return new Location(x + 1, y, z);
		else if(world.getBlockIDAt(x - 1, y, z) != 0)
			return new Location(x - 1, y, z);
		else if(world.getBlockIDAt(x, y, z + 1) != 0)
			return new Location(x, y, z + 1);
		else if(world.getBlockIDAt(x, y, z - 1) != 0)
			return new Location(x, y, z - 1);
		else if(world.getBlockIDAt(x, y + 1, z) != 0)
			return new Location(x, y + 1, z);
		else if(world.getBlockIDAt(x, y - 1, z) != 0)
			return new Location(x, y - 1, z);
		else
			return null;
	}

	public boolean regionContains(Location location) {
		double x = location.getX(), y = location.getY(), z = location.getZ();
		return x >= getMinX() && x <= getMaxX() && y >= getMinY()
				&& y <= getMaxY() && z >= getMinZ() && z <= getMaxZ();
	}
}

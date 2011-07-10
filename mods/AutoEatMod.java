import java.lang.reflect.Field;

import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;
import org.darkstorm.tools.strings.StringTools;

public class AutoEatMod extends Mod implements CommandListener {
	private Class<?> blockPlaceClass;
	private Class<?> inventoryItemClass;
	private Class<?> interfaceActionClass;
	private Class<?> inventoryItemSelectClass;

	private int health = 8, foodID = 320;

	private int invalidCount = 4;
	private short lastInventoryAction = 0;
	private boolean lastActionSlotInvalid = false;

	public AutoEatMod() {
		blockPlaceClass = ClassRepository
				.getClassForInterface(BlockPlacePacket.class);
		inventoryItemClass = ClassRepository
				.getClassForInterface(InventoryItem.class);
		inventoryItemSelectClass = ClassRepository
				.getClassForInterface(InventoryItemSelectPacket.class);
		interfaceActionClass = ClassRepository.getClassByName("qs");
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("eat",
				"/eat <hp <health>|id <food id>>",
				"Use /eat hp <health> to set health, /eat id to set food id"),
				this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("eat");
	}

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "AutoEating Mod";
	}

	@Override
	public String getShortDescription() {
		return "Automatically eats when your health gets low";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		if(minecraft.getWorld() == null
				|| !(minecraft.getWorld() instanceof MultiplayerWorld))
			return 500;
		MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
		NetworkHandler networkHandler = world.getNetworkHandler();
		Player player = minecraft.getPlayer();
		int health = player.getHealth();
		if(health <= 20 - this.health) {
			Inventory inventory = player.getInventory();
			for(int i = 0; i < 36; i++) {
				InventoryItem item = inventory.getItemAt(i);
				if(item != null) {
					int id = item.getID();
					if(id == foodID) {
						if(i > 8) {
							int slotIndex = inventory.getIndexOfEmptySlot();
							if(slotIndex < 0 || slotIndex > 8)
								slotIndex = -1;
							boolean switchSpaces = slotIndex == -1;
							if(switchSpaces)
								slotIndex = 8;
							moveInventory(i, slotIndex, switchSpaces);
							eatFood(networkHandler, slotIndex, inventory
									.getItemAt(slotIndex));
							return 10;
						}
						eatFood(networkHandler, i, item);
						return 10;
					}
				}
			}
		}
		return 10;
	}

	private void moveInventory(int slot1, int slot2, boolean switchItems) {
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
		NetworkHandler networkHandler = world.getNetworkHandler();
		lastInventoryAction += 1;
		InventoryItem slot1Item = inventory.getItemAt(slot1);
		Packet inventoryPacket = (Packet) ReflectionUtil.instantiate(
				interfaceActionClass, new Class[] { Integer.TYPE, Integer.TYPE,
						Integer.TYPE, Boolean.TYPE, inventoryItemClass,
						Short.TYPE }, 0, calculateSendIndex(slot1), 0, false,
				slot1Item, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(slot1, null);
		lastInventoryAction += 1;
		InventoryItem slot2Item = inventory.getItemAt(slot2);
		inventoryPacket = (Packet) ReflectionUtil.instantiate(
				interfaceActionClass, new Class[] { Integer.TYPE, Integer.TYPE,
						Integer.TYPE, Boolean.TYPE, inventoryItemClass,
						Short.TYPE }, 0, calculateSendIndex(slot2), 0, false,
				slot2Item, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(slot2, slot1Item);
		if(!switchItems)
			return;
		lastInventoryAction += 1;
		inventoryPacket = (Packet) ReflectionUtil.instantiate(
				interfaceActionClass, new Class[] { Integer.TYPE, Integer.TYPE,
						Integer.TYPE, Boolean.TYPE, inventoryItemClass,
						Short.TYPE }, 0, calculateSendIndex(slot1), 0, false,
				null, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(slot1, slot2Item);
	}

	private int calculateSendIndex(int index) {
		if(index >= 0 && index < 9)
			return index + 36;
		return index;
	}

	private void eatFood(NetworkHandler networkHandler, int index,
			InventoryItem item) {
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		int currentIndex = inventory.getSelectedIndex();
		InventoryItemSelectPacket selectPacket = (InventoryItemSelectPacket) ReflectionUtil
				.instantiate(inventoryItemSelectClass,
						new Class<?>[] { Integer.TYPE }, index);
		networkHandler.sendPacket(selectPacket);
		BlockPlacePacket placePacket = (BlockPlacePacket) ReflectionUtil
				.instantiate(blockPlaceClass, new Class<?>[] { Integer.TYPE,
						Integer.TYPE, Integer.TYPE, Integer.TYPE,
						inventoryItemClass }, -1, -1, -1, -1, item);
		networkHandler.sendPacket(placePacket);
		selectPacket = (InventoryItemSelectPacket) ReflectionUtil.instantiate(
				inventoryItemSelectClass, new Class<?>[] { Integer.TYPE },
				currentIndex);
		networkHandler.sendPacket(selectPacket);
		player.setHealth(player.getHealth() + health);
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("eat")) {
			if(parts.length != 3 || !StringTools.isInteger(parts[2])) {
				displayText(ChatColor.GRAY
						+ "Use /eat <hp <health>|id <food id>>");
				return;
			}
			if(parts[1].equalsIgnoreCase("hp")) {
				health = Integer.parseInt(parts[2]);
				displayText(ChatColor.GRAY + "Health to eat at set to: "
						+ ChatColor.GOLD + health);
			} else if(parts[1].equalsIgnoreCase("id")) {
				foodID = Integer.parseInt(parts[2]);
				displayText(ChatColor.GRAY + "ID of food to eat set to: "
						+ ChatColor.GOLD + foodID);
			}
		}
	}

	@Override
	public void onEvent(Event event) {
		PacketEvent packetEvent = (PacketEvent) event;
		Packet packet = packetEvent.getPacket();
		if(!(interfaceActionClass.isInstance(packet)))
			return;
		try {
			Field lastActionField = interfaceActionClass.getDeclaredField("d");
			Field lastActionSlotField = interfaceActionClass
					.getDeclaredField("b");
			int lastActionSlot = lastActionSlotField.getInt(packet);
			if(lastActionSlotInvalid) {
				lastActionSlotField.setInt(packet, 5);
				lastInventoryAction += 1;
				lastActionField.setShort(packet, lastInventoryAction);
				lastActionSlotInvalid = false;
				return;
			}
			if(lastActionSlot > 50 && invalidCount < 4) {
				lastActionSlotField.setInt(packet, 5);
				lastInventoryAction += 1;
				lastActionField.setShort(packet, lastInventoryAction);
				lastActionSlotInvalid = true;
				invalidCount++;
				return;
			}
			lastInventoryAction = lastActionField.getShort(packet);
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

}

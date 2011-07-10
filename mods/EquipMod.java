import java.lang.reflect.Field;

import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.*;
import org.lwjgl.input.Mouse;

public class EquipMod extends Mod implements EventListener {
	private Class<?> inventoryPacketClass;
	private Class<?> inventoryItemClass;
	private boolean lastPressed = false;
	private short lastInventoryAction = 0;
	private boolean lastActionSlotInvalid = false;
	private int invalidCount = 4;

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Equip Mod";
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
	public void onStart() {
		if(inventoryPacketClass == null)
			inventoryPacketClass = ClassRepository.getClassByName("qs");
		if(inventoryItemClass == null)
			inventoryItemClass = ClassRepository
					.getClassForInterface("InventoryItem");
		eventManager.addListener(PacketEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(PacketEvent.class, this);
	}

	@Override
	public int loop() throws InterruptedException {
		try {
			if(minecraft.getWorld() == null
					|| !(minecraft.getWorld() instanceof MultiplayerWorld))
				return 1000;
			if(!Mouse.isButtonDown(2) && lastPressed) {
				lastPressed = false;
				return 0;
			} else if(Mouse.isButtonDown(2) && !lastPressed) {
				lastPressed = true;
			} else if(Mouse.isButtonDown(2) && lastPressed)
				return 0;
			else
				return 0;
			Player player = minecraft.getPlayer();
			Inventory inventory = player.getInventory();
			boolean remove = false;
			for(int i = 0; i < 4; i++)
				if(inventory.getArmor()[i] != null)
					remove = true;
			if(remove) {
				int armorCount = 0;
				for(InventoryItem armor : inventory.getArmor())
					if(armor != null)
						armorCount++;
				int freeSpaces = 0;
				for(InventoryItem item : inventory.getItems())
					if(item == null)
						freeSpaces++;
				if(freeSpaces < armorCount)
					return 150;
				int[] takenFreeSpaceIndexes = new int[armorCount];
				for(int armorIndex = 3; armorIndex >= 0; armorIndex--) {
					InventoryItem armor = inventory.getArmor()[armorIndex];
					if(armor == null)
						continue;
					for(int freeSpaceIndex = 0; freeSpaceIndex < inventory
							.getItems().length; freeSpaceIndex++) {
						if(inventory.getItemAt(freeSpaceIndex) != null)
							continue;
						for(int takenFreeSpaceIndex : takenFreeSpaceIndexes)
							if(takenFreeSpaceIndex == freeSpaceIndex)
								continue;

					}
					int freeSpaceIndex = inventory.getIndexOfEmptySlot();
					if(freeSpaceIndex == -1)
						return 150;
					System.out.println(freeSpaceIndex);
					moveArmor(armorIndex, freeSpaceIndex, armor);
				}
			} else {
				for(InventoryItem item : inventory.getItems())
					if(item != null)
						for(int armor = 302; armor < 318; armor++)
							if(armor == item.getID())
								for(int type = 0; type < 4; type++)
									if(checkArmor(type, armor))
										moveInventory(getIndexOf(item),
												3 - type, item);
				invalidCount = 0;
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		return 0;
	}

	private int getIndexOf(InventoryItem item) {
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		InventoryItem[] items = inventory.getItems();
		for(int i = 0; i < 36; i++)
			if(items[i] == item)
				return i;
		return -1;
	}

	private void moveInventory(int inventorySlot, int armorSlot,
			InventoryItem item) {
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
		NetworkHandler networkHandler = world.getNetworkHandler();
		lastInventoryAction += 1;
		Packet inventoryPacket = (Packet) ReflectionUtil
				.instantiate(inventoryPacketClass, new Class[] { Integer.TYPE,
						Integer.TYPE, Integer.TYPE, inventoryItemClass,
						Short.TYPE }, 0, calculateSendIndex(inventorySlot), 0,
						item, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(inventorySlot, null);
		lastInventoryAction += 1;
		inventoryPacket = (Packet) ReflectionUtil.instantiate(
				inventoryPacketClass, new Class[] { Integer.TYPE, Integer.TYPE,
						Integer.TYPE, inventoryItemClass, Short.TYPE }, 0,
				5 + (3 - armorSlot), 0, null, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(armorSlot + 36, item);
	}

	private void moveArmor(int armorSlot, int inventorySlot, InventoryItem item) {
		Player player = minecraft.getPlayer();
		Inventory inventory = player.getInventory();
		MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
		NetworkHandler networkHandler = world.getNetworkHandler();
		lastInventoryAction += 1;
		Packet inventoryPacket = (Packet) ReflectionUtil.instantiate(
				inventoryPacketClass, new Class[] { Integer.TYPE, Integer.TYPE,
						Integer.TYPE, inventoryItemClass, Short.TYPE }, 0,
				5 + (3 - armorSlot), 0, item, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(armorSlot + 36, null);
		lastInventoryAction += 1;
		inventoryPacket = (Packet) ReflectionUtil
				.instantiate(inventoryPacketClass, new Class[] { Integer.TYPE,
						Integer.TYPE, Integer.TYPE, inventoryItemClass,
						Short.TYPE }, 0, calculateSendIndex(inventorySlot), 0,
						null, lastInventoryAction);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(inventorySlot, item);
	}

	private int calculateSendIndex(int index) {
		if(index >= 0 && index < 9)
			return index + 36;
		return index;
	}

	private boolean checkArmor(int type, int id) {
		if(id < 302 || id > 317)
			return false;
		id -= 298 + type;
		return id % 4 == 0;
	}

	@Override
	public void onEvent(Event event) {
		PacketEvent packetEvent = (PacketEvent) event;
		Packet packet = packetEvent.getPacket();
		if(!(inventoryPacketClass.isInstance(packet)))
			return;
		try {
			Field lastActionField = inventoryPacketClass.getDeclaredField("d");
			Field lastActionSlotField = inventoryPacketClass
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

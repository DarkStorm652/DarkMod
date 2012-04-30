import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;
import org.darkstorm.tools.strings.StringTools;

public class AutoEatMod extends Mod implements CommandListener {

	private int health = 8, foodID = 320;

	private int invalidCount = 4;
	private short lastInventoryAction = 0;
	private boolean lastActionSlotInvalid = false;

	public AutoEatMod() {
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
							eatFood(networkHandler, slotIndex,
									inventory.getItemAt(slotIndex));
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
		Packet102WindowClick inventoryPacket = (Packet102WindowClick) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet102WindowClick.class));
		inventoryPacket.setAction(lastInventoryAction);
		inventoryPacket.setButton(0);
		inventoryPacket.setHoldingShift(false);
		inventoryPacket.setSlot(calculateSendIndex(slot1));
		inventoryPacket.setWindowID(0);
		inventoryPacket.setItem(slot1Item);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(slot1, null);
		lastInventoryAction += 1;
		InventoryItem slot2Item = inventory.getItemAt(slot2);
		inventoryPacket = (Packet102WindowClick) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet102WindowClick.class));
		inventoryPacket.setAction(lastInventoryAction);
		inventoryPacket.setButton(0);
		inventoryPacket.setHoldingShift(false);
		inventoryPacket.setSlot(calculateSendIndex(slot2));
		inventoryPacket.setWindowID(0);
		inventoryPacket.setItem(slot2Item);
		networkHandler.sendPacket(inventoryPacket);
		inventory.setItemAt(slot2, slot1Item);
		if(!switchItems)
			return;
		lastInventoryAction += 1;
		inventoryPacket = (Packet102WindowClick) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet102WindowClick.class));
		inventoryPacket.setAction(lastInventoryAction);
		inventoryPacket.setButton(0);
		inventoryPacket.setHoldingShift(false);
		inventoryPacket.setSlot(calculateSendIndex(slot1));
		inventoryPacket.setWindowID(0);
		inventoryPacket.setItem(null);
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
		Packet16BlockItemSwitch selectPacket = (Packet16BlockItemSwitch) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet16BlockItemSwitch.class));
		selectPacket.setID(index);
		networkHandler.sendPacket(selectPacket);
		Packet15BlockPlace placePacket = (Packet15BlockPlace) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet15BlockPlace.class));
		placePacket.setX(-1);
		placePacket.setY(-1);
		placePacket.setZ(-1);
		placePacket.setDirection(-1);
		placePacket.setItem(item);
		networkHandler.sendPacket(placePacket);
		selectPacket = (Packet16BlockItemSwitch) ReflectionUtil
				.instantiate(ClassRepository
						.getClassForInterface(Packet16BlockItemSwitch.class));
		selectPacket.setID(currentIndex);
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
		if(!(packetEvent.getPacket() instanceof Packet102WindowClick))
			return;
		Packet102WindowClick packet = (Packet102WindowClick) packetEvent
				.getPacket();
		int lastActionSlot = packet.getSlot();
		if(lastActionSlotInvalid) {
			packet.setSlot(5);
			lastInventoryAction += 1;
			packet.setAction(lastInventoryAction);
			lastActionSlotInvalid = false;
			return;
		}
		if(lastActionSlot > 50 && invalidCount < 4) {
			packet.setSlot(5);
			lastInventoryAction += 1;
			packet.setAction(lastInventoryAction);
			lastActionSlotInvalid = true;
			invalidCount++;
			return;
		}
		lastInventoryAction = packet.getAction();
	}

}

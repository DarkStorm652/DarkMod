import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.strings.StringTools;

public class ItemMod extends Mod implements CommandListener {
	private Class<?> inventoryItemClass;

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getName() {
		return "Item Mod";
	}

	@Override
	public String getShortDescription() {
		return "Adds /i and /item to the game";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public void onStart() {
		if(inventoryItemClass == null)
			inventoryItemClass = ClassRepository
					.getClassForInterface(InventoryItem.class);
		commandManager.registerListener(new Command("i",
				"/i <id> [amount] [damage]", "Gives you an item"), this);
		commandManager.registerListener(new Command("item",
				"/item <id> [amount] [damage]", "Gives you an item"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("item");
		commandManager.unregisterListener("i");
	}

	@Override
	public int loop() throws InterruptedException {
		return 1000;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if((parts[0].equalsIgnoreCase("item") || parts[0].equalsIgnoreCase("i"))
				&& parts.length >= 2
				&& parts.length <= 4
				&& StringTools.isInteger(parts[1])) {
			int id = Integer.parseInt(parts[1]);
			int amount = 1;
			int damage = 0;
			if(parts.length > 2)
				if(StringTools.isInteger(parts[2]))
					amount = Integer.parseInt(parts[2]);
				else
					displayText(ChatColor.RED + "Invalid amount");
			if(parts.length > 3)
				if(StringTools.isInteger(parts[3]))
					damage = Integer.parseInt(parts[3]);
				else
					displayText(ChatColor.RED + "Invalid damage");
			Player player = minecraft.getPlayer();
			Inventory inventory = player.getInventory();
			if(amount == -1)
				inventory.setItemAt(inventory.getIndexOfEmptySlot(),
						(InventoryItem) ReflectionUtil.instantiate(
								inventoryItemClass, new Class<?>[] {
										Integer.TYPE, Integer.TYPE,
										Integer.TYPE }, id, -1, 0));
			int stackCount = amount / 64;
			int remainder = amount % 64;
			if(player == null)
				return;
			int index;
			for(index = 0; index < inventory.getItems().length; index++) {
				InventoryItem item = inventory.getItemAt(index);
				if(item != null && item.getID() == id
						&& item.getStackCount() < 64) {
					int amountRemaining = 64 - item.getStackCount();
					if(amountRemaining >= amount) {
						inventory
								.setItemAt(
										index,
										(InventoryItem) ReflectionUtil
												.instantiate(
														inventoryItemClass,
														new Class<?>[] {
																Integer.TYPE,
																Integer.TYPE,
																Integer.TYPE },
														id,
														amount
																+ item
																		.getStackCount(),
														damage));
						return;
					} else {
						if(remainder == amountRemaining) {
							inventory
									.setItemAt(
											index,
											(InventoryItem) ReflectionUtil
													.instantiate(
															inventoryItemClass,
															new Class<?>[] {
																	Integer.TYPE,
																	Integer.TYPE,
																	Integer.TYPE },
															id,
															remainder
																	+ amountRemaining,
															damage));
						} else if(remainder < amountRemaining) {
							inventory
									.setItemAt(
											index,
											(InventoryItem) ReflectionUtil
													.instantiate(
															inventoryItemClass,
															new Class<?>[] {
																	Integer.TYPE,
																	Integer.TYPE,
																	Integer.TYPE },
															id,
															remainder
																	+ amountRemaining,
															damage));
							stackCount--;
							remainder = 64 - (amountRemaining - remainder);
							inventory.setItemAt(index,
									(InventoryItem) ReflectionUtil.instantiate(
											inventoryItemClass, new Class<?>[] {
													Integer.TYPE, Integer.TYPE,
													Integer.TYPE }, id, 64,
											damage));
						} else if(remainder > amountRemaining) {
							inventory.setItemAt(index,
									(InventoryItem) ReflectionUtil.instantiate(
											inventoryItemClass, new Class<?>[] {
													Integer.TYPE, Integer.TYPE,
													Integer.TYPE }, id, 64,
											damage));
							stackCount--;
							remainder = remainder - amountRemaining;
						}
					}
				}
			}
			while((index = inventory.getIndexOfEmptySlot()) != -1) {
				if(stackCount > 0) {
					inventory.setItemAt(index, (InventoryItem) ReflectionUtil
							.instantiate(inventoryItemClass, new Class<?>[] {
									Integer.TYPE, Integer.TYPE, Integer.TYPE },
									id, 64, damage));
					stackCount--;
				} else if(remainder > 0) {
					inventory.setItemAt(index, (InventoryItem) ReflectionUtil
							.instantiate(inventoryItemClass, new Class<?>[] {
									Integer.TYPE, Integer.TYPE, Integer.TYPE },
									id, remainder, damage));
					break;
				} else
					return;
			}

		}
	}

}

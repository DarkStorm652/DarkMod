package org.darkstorm.minecraft.darkmod.hooks.client;

public interface Inventory {
	public InventoryItem[] getItems();

	public void setItems(InventoryItem[] items);

	public InventoryItem[] getArmor();

	public void setArmor(InventoryItem[] armor);

	public int getSelectedIndex();

	public void setSelectedIndex(int selectedIndex);

	public int getIndexOf(int id);

	public int getIndexOf(InventoryItem inventoryItem);

	public int getIndexOfEmptySlot();

	public void select(int id, boolean unused);

	public boolean removeOne(int id);

	public void setItemAt(int slot, InventoryItem inventoryItem);

	public int getSize();

	public InventoryItem getItemAt(int slot);

	public InventoryItem getArmorAt(int slot);

	public int getMaxStackSize();

	public InventoryItem getSelectedItem();
}

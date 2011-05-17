import java.util.*;

import javax.swing.JTable;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.strings.StringTools;

public class BlockLocatorMod extends Mod implements CommandListener {
	private BlockLocatorUI ui;
	private int blockID = -1;
	private int radius = 30;

	public BlockLocatorMod() {
		ui = new BlockLocatorUI();
	}

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Block Locator Mod";
	}

	@Override
	public String getShortDescription() {
		return "Locates blocks";
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
		commandManager.registerListener(new Command("blockid", "/blockid <id>",
				"Sets the ID for block searching"), this);
		commandManager.registerListener(
				new Command("blockradius", "/blockradius <radius>",
						"Sets the radius for block searching"), this);
		ui.setVisible(true);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("blockid");
		commandManager.unregisterListener("blockradius");
		ui.setVisible(false);
		JTable list = ui.getList();
		DefaultTableModel model = (DefaultTableModel) list.getModel();
		Vector<?> dataVector = model.getDataVector();
		dataVector.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public int loop() {
		try {
			if(!ui.isVisible())
				handler.stopMod(getName());
			World world = minecraft.getWorld();
			JTable list = ui.getList();
			DefaultTableModel model = (DefaultTableModel) list.getModel();
			TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) list
					.getRowSorter();
			if(world != null) {
				Player currentPlayer = minecraft.getPlayer();
				int playerX = (int) java.lang.Math.round(currentPlayer.getX());
				int playerY = (int) java.lang.Math.round(currentPlayer.getY());
				int playerZ = (int) java.lang.Math.round(currentPlayer.getZ());
				ui.setTitle("Block Locator (" + playerX + ", " + playerY + ", "
						+ playerZ + ")");
				ArrayList<Block> blocks = new ArrayList<Block>();
				for(int x = playerX - radius; x < playerX + radius; x++)
					for(int y = playerY - radius; y < playerY + radius; y++)
						for(int z = playerZ - radius; z < playerZ + radius; z++)
							if(world.getBlockIDAt(x, y, z) == blockID)
								blocks.add(new Block(x, y, z));
				label: for(int row = 0; row < model.getRowCount(); row++) {
					int x = (Integer) model.getValueAt(row, 1);
					int y = (Integer) model.getValueAt(row, 2);
					int z = (Integer) model.getValueAt(row, 3);
					for(Block block : blocks) {
						if(x == block.getX() && y == block.getY()
								&& z == block.getZ()) {
							model.setValueAt(getDistanceTo(x, y, z,
									currentPlayer), row, 0);
							continue label;
						}
					}
					model.removeRow(row);
				}
				label: for(Block block : blocks) {
					for(int row = 0; row < model.getRowCount(); row++) {
						int x = (Integer) model.getValueAt(row, 1);
						int y = (Integer) model.getValueAt(row, 2);
						int z = (Integer) model.getValueAt(row, 3);
						if(x == block.getX() && y == block.getY()
								&& z == block.getZ())
							continue label;
					}
					model.addRow(new Object[] {
							getDistanceTo(block.getX(), block.getY(), block
									.getZ(), minecraft.getPlayer()),
							block.getX(), block.getY(), block.getZ() });
				}
				sorter.sort();
				list.repaint();
			} else {
				ui.setTitle("Block Locator");
				for(int i = model.getRowCount() - 1; i >= 0; i--)
					model.removeRow(i);
			}
		} catch(Exception exception) {}
		return 500;
	}

	private double getDistanceTo(double x, double y, double z, Player player) {
		double x2 = player.getX();
		double y2 = player.getY();
		double z2 = player.getZ();
		double xResult = java.lang.Math.pow(java.lang.Math.max(x, x2)
				- java.lang.Math.min(x, x2), 2);
		double yResult = java.lang.Math.pow(java.lang.Math.max(y, y2)
				- java.lang.Math.min(y, y2), 2);
		double zResult = java.lang.Math.pow(java.lang.Math.max(z, z2)
				- java.lang.Math.min(z, z2), 2);
		return java.lang.Math.sqrt(xResult + yResult + zResult);
	}

	private class Block {
		private int x, y, z;

		public Block(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public int getX() {
			return x;
		}

		public int getY() {
			return y;
		}

		public int getZ() {
			return z;
		}

	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("blockid") && parts.length == 2
				&& StringTools.isInteger(parts[1])) {
			blockID = Integer.parseInt(parts[1]);
			JTable list = ui.getList();
			DefaultTableModel model = (DefaultTableModel) list.getModel();
			Vector<?> dataVector = model.getDataVector();
			dataVector.clear();
			displayText("Block ID for searching is now " + ChatColor.LIME
					+ blockID);
		} else if(parts[0].equalsIgnoreCase("blockradius") && parts.length == 2
				&& StringTools.isInteger(parts[1])) {
			radius = Integer.parseInt(parts[1]);
			displayText("Block radius for searching is now " + ChatColor.LIME
					+ radius);
		}
	}

}

import java.util.List;

import javax.swing.JTable;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.events.RenderEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.*;

public class PlayerLocationMod extends Mod implements EventListener {
	private PlayerLocationUI ui;

	public PlayerLocationMod() {
		ui = new PlayerLocationUI();
	}

	@Override
	public void onStart() {
		eventManager.addListener(RenderEvent.class, this);
		ui.setVisible(true);
	}

	@Override
	public void onStop() {
		ui.setVisible(false);
		eventManager.removeListener(RenderEvent.class, this);
	}

	@Override
	public int loop() {
		return 500;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onEvent(Event event) {
		if(!ui.isVisible())
			handler.stopMod(getName());
		World world = minecraft.getWorld();
		JTable list = ui.getList();
		DefaultTableModel model = (DefaultTableModel) list.getModel();
		if(world != null) {
			List<Humanoid> worldPlayers = world.getPlayers();
			for(int row = 0; row < model.getRowCount(); row++) {
				boolean containsPlayerMatch = false;
				String playerName = (String) model.getValueAt(row, 0);
				for(Humanoid player : worldPlayers) {
					if(playerName.equals(player.getName())) {
						model.setValueAt(getDistanceTo(player.getX(), player
								.getY(), player.getZ(), minecraft.getPlayer()),
								row, 1);
						model.setValueAt(player.getX(), row, 2);
						model.setValueAt(player.getY(), row, 3);
						model.setValueAt(player.getZ(), row, 4);
						containsPlayerMatch = true;
					}
				}
				if(!containsPlayerMatch)
					model.removeRow(row);
			}
			for(Humanoid player : worldPlayers) {
				if(!player.equals(minecraft.getPlayer())) {
					boolean containsRowMatch = false;
					String playerName = player.getName();
					for(int row = 0; row < model.getRowCount(); row++)
						if(playerName.equals(model.getValueAt(row, 0)))
							containsRowMatch = true;
					if(!containsRowMatch)
						model.addRow(new Object[] {
								player.getName(),
								getDistanceTo(player.getX(), player.getY(),
										player.getZ(), minecraft.getPlayer()),
								player.getX(), player.getY(), player.getZ() });
				}
			}
			TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) list
					.getRowSorter();
			sorter.sort();
		} else
			for(int i = 0; i < model.getRowCount(); i++)
				model.removeRow(i);
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

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public String getName() {
		return "Player Locator Mod";
	}

	@Override
	public String getShortDescription() {
		return "Locates players";
	}

	@Override
	public String getFullDescription() {
		return "<html>Shows a table with players and their coords + distances"
				+ "to you. Useful in PvP for locating players' bases</html>";
	}
}

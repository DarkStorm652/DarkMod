package org.darkstorm.minecraft.darkmod.mod;

import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.mod.Mod.ModControl;
import org.darkstorm.minecraft.darkmod.ui.DarkModUI;
import org.darkstorm.tools.loopsystem.*;

public class ModMenuHandler implements ActionListener, LoopStopListener {
	private ModHandler modHandler;
	private JMenu menu;
	private ArrayList<JMenuItem> menuItems;
	private JSeparator separator;

	ModMenuHandler(ModHandler modHandler) {
		this.modHandler = modHandler;
		menuItems = new ArrayList<JMenuItem>();
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		menu = new JMenu("Mods");
		JMenuItem reload = new JMenuItem("Reload");
		reload.addActionListener(this);
		menu.add(reload);
		menu.add(new JSeparator());
		separator = (JSeparator) menu.add(new JSeparator());
		ui.addMenu(menu);
		LoopManager loopManager = modHandler.getLoopManager();
		for(LoopController loopController : loopManager.getLoopControllers())
			loopController.addLoopStopListener(this);
	}

	void updateMod(Mod mod) {
		if(mod.getControlOption() != ModControl.ACTION
				&& mod.getControlOption() != ModControl.TOGGLE)
			return;
		String modName = mod.getName();
		boolean hasItem = false;
		for(JMenuItem menuItem : menuItems)
			if(modName.equals(menuItem.getText()))
				if(!hasItem) {
					hasItem = true;
					if(mod.getControlOption() == ModControl.TOGGLE)
						menuItem.setSelected(mod.isRunning());
				} else
					menu.remove(menuItem);
		if(!hasItem) {
			JMenuItem menuItem = null;
			if(mod.getControlOption() == ModControl.TOGGLE)
				menuItem = new JCheckBoxMenuItem(mod.getName());
			else if(mod.getControlOption() == ModControl.ACTION)
				menuItem = new JMenuItem(mod.getName());
			menuItem.setToolTipText(mod.getShortDescription());
			menuItem.setActionCommand(mod.getName());
			menuItem.addActionListener(this);
			menuItems.add(menuItem);
			if(mod.getControlOption() == ModControl.TOGGLE)
				menu.add(menuItem);
			else if(mod.getControlOption() == ModControl.ACTION)
				menu.add(menuItem, getSeparatorIndex() - 1);
		}
	}

	private int getSeparatorIndex() {
		List<?> componentList = Arrays.asList(menu.getMenuComponents());
		return componentList.indexOf(separator);
	}

	public void removeMod(Mod mod) {
		String modName = mod.getName();
		JCheckBoxMenuItem[] menuItemsArray = menuItems
				.toArray(new JCheckBoxMenuItem[menuItems.size()]);
		for(JCheckBoxMenuItem menuItem : menuItemsArray) {
			if(modName.equals(menuItem.getText())) {
				menu.remove(menuItem);
				menuItems.remove(menuItem);
			}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String modName = e.getActionCommand();
		if(modName.equals("reload")) {
			modHandler.reloadMods();
			return;
		}
		Mod mod = modHandler.getModByName(modName);
		if(mod == null)
			return;
		for(JMenuItem menuItem : menuItems)
			if(modName.equals(menuItem.getText()))
				if(mod.getControlOption() != ModControl.TOGGLE)
					try {
						mod.loop();
					} catch(Exception exception) {
						exception.printStackTrace();
					}
				else if(menuItem.isSelected() && !mod.isRunning())
					mod.start();
				else if(!menuItem.isSelected() && mod.isRunning())
					mod.stop();
		updateMod(mod);
	}

	@Override
	public void onLoopStop(Loopable loopable) {
		if(loopable instanceof Mod)
			updateMod((Mod) loopable);
	}
}

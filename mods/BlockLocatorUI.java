import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.tools.*;

@SuppressWarnings("serial")
public class BlockLocatorUI extends JDialog {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JTable list;
	private JPopupMenu popupMenu;
	private JMenuItem leftClickItem;
	private JMenuItem rightClickItem;
	private JMenuItem removeItem;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private BlockLocatorMod mod;
	private Class<?> blockPlaceClass;
	private Class<?> inventoryItemClass;

	public BlockLocatorUI(BlockLocatorMod mod) {
		super(DarkMod.getInstance().getUI());
		this.mod = mod;
		blockPlaceClass = ClassRepository
				.getClassForInterface(BlockPlacePacket.class);
		inventoryItemClass = ClassRepository
				.getClassForInterface(InventoryItem.class);
		initComponents();
		TableModel model = list.getModel();
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(
				model);
		list.setRowSorter(sorter);
		sorter.sort();
	}

	public JTable getList() {
		return list;
	}

	private void leftClickItemActionPerformed(ActionEvent e) {
		int selectedRow = list.getSelectedRow();
		if(selectedRow == -1)
			return;
		DefaultTableModel model = (DefaultTableModel) list.getModel();
		int x = (Integer) model.getValueAt(selectedRow, 1);
		int y = (Integer) model.getValueAt(selectedRow, 2);
		int z = (Integer) model.getValueAt(selectedRow, 3);
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			BlockDigPacket digPacket = (BlockDigPacket) ReflectionUtil
					.instantiate(ClassRepository
							.getClassForInterface(BlockDigPacket.class),
							new Class<?>[] { Integer.TYPE, Integer.TYPE,
									Integer.TYPE, Integer.TYPE, Integer.TYPE },
							0, x, y, z, getFreeFace(x, y, z));
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler handler = world.getNetworkHandler();
			handler.sendPacket(digPacket);
		}
	}

	private void listMousePressed(MouseEvent e) {
		int selectedRow = list.getSelectedRow();
		if(e.isPopupTrigger() && selectedRow != -1) {
			DarkMod darkMod = DarkMod.getInstance();
			AccessHandler accessHandler = darkMod.getAccessHandler();
			Minecraft minecraft = accessHandler.getMinecraft();
			if(minecraft.getPlayer() == null)
				return;
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	private void rightClickItemActionPerformed(ActionEvent e) {
		int selectedRow = list.getSelectedRow();
		if(selectedRow == -1)
			return;
		selectedRow = list.convertRowIndexToModel(selectedRow);
		DefaultTableModel model = (DefaultTableModel) list.getModel();
		int x = (Integer) model.getValueAt(selectedRow, 1);
		int y = (Integer) model.getValueAt(selectedRow, 2);
		int z = (Integer) model.getValueAt(selectedRow, 3);
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			BlockPlacePacket placePacket = (BlockPlacePacket) ReflectionUtil
					.instantiate(blockPlaceClass, new Class<?>[] {
							Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, inventoryItemClass }, x, y, z,
							getFreeFace(x, y, z), null);
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler handler = world.getNetworkHandler();
			handler.sendPacket(placePacket);
		}
	}

	private void removeItemActionPerformed(ActionEvent e) {
		int selectedRow = list.getSelectedRow();
		if(selectedRow == -1)
			return;
		DefaultTableModel model = (DefaultTableModel) list.getModel();
		int x = (Integer) model.getValueAt(selectedRow, 1);
		int y = (Integer) model.getValueAt(selectedRow, 2);
		int z = (Integer) model.getValueAt(selectedRow, 3);
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		if(minecraft.getWorld() instanceof MultiplayerWorld) {
			BlockDigPacket digPacket = (BlockDigPacket) ReflectionUtil
					.instantiate(ClassRepository
							.getClassForInterface(BlockDigPacket.class),
							new Class<?>[] { Integer.TYPE, Integer.TYPE,
									Integer.TYPE, Integer.TYPE, Integer.TYPE },
							2, x, y, z, getFreeFace(x, y, z));
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler handler = world.getNetworkHandler();
			handler.sendPacket(digPacket);
		}
	}

	private int getFreeFace(int x, int y, int z) {
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		World world = minecraft.getWorld();
		if(world == null)
			return 0;
		if(world.getBlockIDAt(x + 1, y, z) == 0)
			return 5;
		else if(world.getBlockIDAt(x - 1, y, z) == 0)
			return 4;
		else if(world.getBlockIDAt(x, y, z + 1) == 0)
			return 3;
		else if(world.getBlockIDAt(x, y, z - 1) == 0)
			return 2;
		else if(world.getBlockIDAt(x, y + 1, z) == 0)
			return 1;
		else if(world.getBlockIDAt(x, y - 1, z) == 0)
			return 0;
		else
			return 0;
	}

	private void thisWindowClosed(WindowEvent e) {
		mod.stop();
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		list = new JTable();
		popupMenu = new JPopupMenu();
		leftClickItem = new JMenuItem();
		rightClickItem = new JMenuItem();
		removeItem = new JMenuItem();

		// ======== this ========
		setTitle("Block Locator");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				thisWindowClosed(e);
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== scrollPane ========
		{

			// ---- list ----
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setForeground(Color.black);
			list.setModel(new DefaultTableModel(new Object[][] {},
					new String[] { "Distance", "X", "Y", "Z" }) {
				Class[] columnTypes = new Class[] { Double.class,
						Integer.class, Integer.class, Integer.class };
				boolean[] columnEditable = new boolean[] { false, false, false,
						false };

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			list.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					listMousePressed(e);
				}
			});
			scrollPane.setViewportView(list);
		}
		contentPane.add(scrollPane, BorderLayout.CENTER);
		setSize(480, 345);
		setLocationRelativeTo(getOwner());

		// ======== popupMenu ========
		{

			// ---- leftClickItem ----
			leftClickItem.setText("Left click");
			leftClickItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					leftClickItemActionPerformed(e);
				}
			});
			popupMenu.add(leftClickItem);

			// ---- rightClickItem ----
			rightClickItem.setText("Right click");
			rightClickItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					rightClickItemActionPerformed(e);
				}
			});
			popupMenu.add(rightClickItem);

			// ---- removeItem ----
			removeItem.setText("Remove");
			removeItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeItemActionPerformed(e);
				}
			});
			popupMenu.add(removeItem);
		}
		// //GEN-END:initComponents
	}
}

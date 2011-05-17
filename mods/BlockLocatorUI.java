import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.tools.*;

@SuppressWarnings("serial")
public class BlockLocatorUI extends JDialog {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JTable list;
	private JPopupMenu popupMenu;
	private JMenuItem openChestItem;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private Class<?> blockPlaceClass;
	private Class<?> inventoryItemClass;

	public BlockLocatorUI() {
		super(DarkMod.getInstance().getUI());
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

	private void openChestItemActionPerformed(ActionEvent e) {
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
			BlockPlacePacket placePacket = (BlockPlacePacket) ReflectionUtil
					.instantiate(blockPlaceClass, new Class<?>[] {
							Integer.TYPE, Integer.TYPE, Integer.TYPE,
							Integer.TYPE, inventoryItemClass }, x, y, z, 1,
							null);
			MultiplayerWorld world = (MultiplayerWorld) minecraft.getWorld();
			NetworkHandler handler = world.getNetworkHandler();
			handler.sendPacket(placePacket);
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
			DefaultTableModel model = (DefaultTableModel) list.getModel();
			double distance = (Double) model.getValueAt(selectedRow, 0);
			System.out.println(distance);
			if(distance < 7)
				popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		list = new JTable();
		popupMenu = new JPopupMenu();
		openChestItem = new JMenuItem();

		// ======== this ========
		setTitle("Chest Locator");
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

			// ---- openChestItem ----
			openChestItem.setText("Open chest");
			openChestItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					openChestItemActionPerformed(e);
				}
			});
			popupMenu.add(openChestItem);
		}
		// //GEN-END:initComponents
	}
}



import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.DarkMod;

@SuppressWarnings("serial")
public class PlayerLocationUI extends JDialog {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JScrollPane scrollPane;
	private JTable list;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public PlayerLocationUI() {
		super(DarkMod.getInstance().getUI());
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

	@SuppressWarnings("unchecked")
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		scrollPane = new JScrollPane();
		list = new JTable();

		// ======== this ========
		setTitle("Player Locations");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== scrollPane ========
		{

			// ---- list ----
			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.setForeground(Color.black);
			list.setEnabled(false);
			list.setRowSelectionAllowed(false);
			list.setModel(new DefaultTableModel(new Object[][] {},
					new String[] { "Player Name", "Distance", "X", "Y", "Z" }) {
				Class[] columnTypes = new Class[] { String.class, Double.class,
						Double.class, Double.class, Double.class };
				boolean[] columnEditable = new boolean[] { false, false, false,
						false, false };

				@Override
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}

				@Override
				public boolean isCellEditable(int rowIndex, int columnIndex) {
					return columnEditable[columnIndex];
				}
			});
			scrollPane.setViewportView(list);
		}
		contentPane.add(scrollPane, BorderLayout.CENTER);
		setSize(480, 345);
		setLocationRelativeTo(getOwner());
		// //GEN-END:initComponents
	}
}

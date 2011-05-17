package org.darkstorm.minecraft.darkmod.ui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.mod.*;

@SuppressWarnings("serial")
public class ModHandlerUI extends JFrame {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JMenu menu2;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JMenuItem menuItem5;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JSplitPane splitPane1;
	private JTable modTable;
	private JPanel modInfoCardPanel;
	private JLabel selectModLabel;
	private JPanel modInfoPanel;
	private JLabel label1;
	private JTextField modNameField;
	private JLabel label3;
	private JScrollPane scrollPane2;
	private JEditorPane modDescriptionPane;
	private JPanel panel3;
	private JButton optionsButton;
	private JButton stopButton;
	private JButton startButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private ModHandler modHandler;

	public ModHandlerUI(ModHandler modHandler) {
		this.modHandler = modHandler;
		initComponents();
		DarkMod darkMod = DarkMod.getInstance();
		DarkModUI ui = darkMod.getUI();
		JMenuItem modHandlerItem = ui.getModHandlerItem();
		modHandlerItem.setEnabled(true);
	}

	public void updateMods() {
		Mod[] mods = modHandler.getMods();
		DefaultTableModel model = (DefaultTableModel) modTable.getModel();
		for(int i = 0; i < model.getRowCount(); i++)
			model.removeRow(i);
		for(Mod mod : mods)
			model.addRow(new Object[] { mod.getName() });
	}

	private void optionsButtonActionPerformed(ActionEvent e) {

	}

	private void stopButtonActionPerformed(ActionEvent e) {

	}

	private void startButtonActionPerformed(ActionEvent e) {

	}

	private void modTablePropertyChange(PropertyChangeEvent e) {
		if(modTable.getSelectedRow() != -1) {

		}
	}

	@SuppressWarnings("unchecked")
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menu2 = new JMenu();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		menuItem5 = new JMenuItem();
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		splitPane1 = new JSplitPane();
		JScrollPane scrollPane1 = new JScrollPane();
		modTable = new JTable();
		modInfoCardPanel = new JPanel();
		selectModLabel = new JLabel();
		modInfoPanel = new JPanel();
		label1 = new JLabel();
		modNameField = new JTextField();
		label3 = new JLabel();
		scrollPane2 = new JScrollPane();
		modDescriptionPane = new JEditorPane();
		panel3 = new JPanel();
		optionsButton = new JButton();
		stopButton = new JButton();
		startButton = new JButton();

		// ======== this ========
		setTitle("Mod Handler");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== menuBar1 ========
		{

			// ======== menu1 ========
			{
				menu1.setText("File");

				// ---- menuItem1 ----
				menuItem1.setText("Load mod...");
				menuItem1.setMnemonic('L');
				menu1.add(menuItem1);

				// ---- menuItem2 ----
				menuItem2.setText("Reload mods");
				menuItem2.setMnemonic('R');
				menu1.add(menuItem2);
			}
			menuBar1.add(menu1);

			// ======== menu2 ========
			{
				menu2.setText("Mod");

				// ---- menuItem3 ----
				menuItem3.setText("Start");
				menu2.add(menuItem3);

				// ---- menuItem4 ----
				menuItem4.setText("Stop");
				menu2.add(menuItem4);
				menu2.addSeparator();

				// ---- menuItem5 ----
				menuItem5.setText("Options");
				menu2.add(menuItem5);
			}
			menuBar1.add(menu2);
		}
		setJMenuBar(menuBar1);

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout());

				// ======== splitPane1 ========
				{
					splitPane1.setOneTouchExpandable(true);

					// ======== scrollPane1 ========
					{

						// ---- modTable ----
						modTable.setModel(new DefaultTableModel(
								new Object[][] {}, new String[] { "Name" }) {
							Class[] columnTypes = new Class[] { String.class };
							boolean[] columnEditable = new boolean[] { false };

							@Override
							public Class<?> getColumnClass(int columnIndex) {
								return columnTypes[columnIndex];
							}

							@Override
							public boolean isCellEditable(int rowIndex,
									int columnIndex) {
								return columnEditable[columnIndex];
							}
						});
						modTable
								.setPreferredScrollableViewportSize(new Dimension(
										200, 0));
						modTable
								.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
						modTable
								.addPropertyChangeListener(new PropertyChangeListener() {
									public void propertyChange(
											PropertyChangeEvent e) {
										modTablePropertyChange(e);
									}
								});
						scrollPane1.setViewportView(modTable);
					}
					splitPane1.setLeftComponent(scrollPane1);

					// ======== modInfoCardPanel ========
					{
						modInfoCardPanel.setLayout(new CardLayout());

						// ---- selectModLabel ----
						selectModLabel.setText("Please select a mod.");
						selectModLabel
								.setHorizontalAlignment(SwingConstants.CENTER);
						modInfoCardPanel.add(selectModLabel, "unselectedCard");

						// ======== modInfoPanel ========
						{
							modInfoPanel.setLayout(new GridBagLayout());
							((GridBagLayout) modInfoPanel.getLayout()).columnWeights = new double[] { 1.0 };
							((GridBagLayout) modInfoPanel.getLayout()).rowWeights = new double[] {
									0.0, 0.0, 0.0, 1.0, 0.0 };

							// ---- label1 ----
							label1.setText("Name");
							label1.setVerticalAlignment(SwingConstants.BOTTOM);
							modInfoPanel.add(label1, new GridBagConstraints(0,
									0, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

							// ---- modNameField ----
							modNameField.setEditable(false);
							modNameField.setEnabled(false);
							modNameField.setBackground(UIManager
									.getColor("TextArea.background"));
							modInfoPanel.add(modNameField,
									new GridBagConstraints(0, 1, 1, 1, 0.0,
											0.0, GridBagConstraints.CENTER,
											GridBagConstraints.BOTH,
											new Insets(0, 0, 0, 0), 0, 0));

							// ---- label3 ----
							label3.setText("Description");
							modInfoPanel.add(label3, new GridBagConstraints(0,
									2, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

							// ======== scrollPane2 ========
							{

								// ---- modDescriptionPane ----
								modDescriptionPane.setContentType("text/html");
								modDescriptionPane.setEditable(false);
								modDescriptionPane.setEnabled(false);
								scrollPane2.setViewportView(modDescriptionPane);
							}
							modInfoPanel.add(scrollPane2,
									new GridBagConstraints(0, 3, 1, 1, 0.0,
											0.0, GridBagConstraints.CENTER,
											GridBagConstraints.BOTH,
											new Insets(0, 0, 0, 0), 0, 0));

							// ======== panel3 ========
							{
								panel3.setLayout(new GridLayout());

								// ---- optionsButton ----
								optionsButton.setText("Options");
								optionsButton.setEnabled(false);
								optionsButton
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												optionsButtonActionPerformed(e);
											}
										});
								panel3.add(optionsButton);

								// ---- stopButton ----
								stopButton.setText("Stop");
								stopButton.setEnabled(false);
								stopButton
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												stopButtonActionPerformed(e);
											}
										});
								panel3.add(stopButton);

								// ---- startButton ----
								startButton.setText("Start");
								startButton
										.addActionListener(new ActionListener() {
											public void actionPerformed(
													ActionEvent e) {
												startButtonActionPerformed(e);
											}
										});
								panel3.add(startButton);
							}
							modInfoPanel.add(panel3, new GridBagConstraints(0,
									4, 1, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));
						}
						modInfoCardPanel.add(modInfoPanel, "selectedCard");
					}
					splitPane1.setRightComponent(modInfoCardPanel);
				}
				contentPanel.add(splitPane1, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setSize(550, 350);
		setLocationRelativeTo(getOwner());
		// //GEN-END:initComponents
	}
}

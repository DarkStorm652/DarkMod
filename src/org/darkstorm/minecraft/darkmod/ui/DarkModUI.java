package org.darkstorm.minecraft.darkmod.ui;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import org.darkstorm.minecraft.darkmod.*;
import org.darkstorm.minecraft.darkmod.access.AccessHandler;
import org.darkstorm.minecraft.darkmod.hooks.client.Minecraft;
import org.darkstorm.minecraft.darkmod.mod.ModHandler;
import org.darkstorm.minecraft.darkmod.tools.*;

@SuppressWarnings("serial")
public class DarkModUI extends JFrame {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem modHandlerItem;
	private JMenuItem newSessionIDItem;
	private JSeparator separator1;
	private JMenuItem quitItem;
	private JButton screenshotButton;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private Canvas canvas;
	private Component menuToButtonFiller;

	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	public DarkModUI() {
		initComponents();
		insertMenuToButtonFiller();
		screenshotButton.setFocusable(false);
		setIcon();
		addCanvas();
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
	}

	private void insertMenuToButtonFiller() {
		int index = menuBar.getComponentIndex(screenshotButton);
		menuToButtonFiller = Box.createHorizontalGlue();
		menuBar.add(menuToButtonFiller, index);
	}

	private void setIcon() {
		Image icon = Tools.getIcon("icon_obsidian");
		if(icon != null)
			setIconImage(icon);
	}

	private void addCanvas() {
		canvas = new Canvas();
		add(canvas, "Center");
		canvas.setPreferredSize(new Dimension(854, 480));
	}

	private void windowResized(ComponentEvent e) {
		if(canvas != null)
			canvas.setPreferredSize(canvas.getSize());
	}

	private void quitItemActionPerformed(ActionEvent e) {
		setVisible(false);
		dispose();
		System.exit(0);
	}

	private void modHandlerItemActionPerformed(ActionEvent e) {
		DarkMod darkMod = DarkMod.getInstance();
		ModHandler modHandler = darkMod.getModHandler();
		modHandler.showUI();
	}

	private void screenshotButtonActionPerformed(ActionEvent e) {
		if(canvas == null)
			return;
		DarkMod darkMod = DarkMod.getInstance();
		AccessHandler accessHandler = darkMod.getAccessHandler();
		Minecraft minecraft = accessHandler.getMinecraft();
		minecraft.takeScreenshot(Tools.getMinecraftDirectory(),
				canvas.getWidth(), canvas.getHeight());
	}

	public JMenuItem getModHandlerItem() {
		return modHandlerItem;
	}

	private void newSessionIDItemActionPerformed(ActionEvent e) {
		final DarkMod darkMod = DarkMod.getInstance();
		if(darkMod.getUsername() == null || darkMod.isPlayingOffline()
				|| canvas == null)
			return;
		new Thread() {
			@Override
			public void run() {
				LoginUtil loginUtil = new LoginUtil();
				loginUtil.login(darkMod.getUsername(), darkMod.getPassword());
				if(loginUtil.isLoggedIn()) {
					darkMod.setSessionID(loginUtil.getSessionID());
					JOptionPane.showMessageDialog(DarkModUI.this,
							"Successfully retrieved new session ID!",
							"Success", JOptionPane.INFORMATION_MESSAGE);
				}
			};
		}.start();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		menuBar = new JMenuBar();
		fileMenu = new JMenu();
		modHandlerItem = new JMenuItem();
		JSeparator separator = new JSeparator();
		newSessionIDItem = new JMenuItem();
		separator1 = new JSeparator();
		quitItem = new JMenuItem();
		screenshotButton = new JButton();

		// ======== this ========
		setTitle("DarkMod");
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				windowResized(e);
			}
		});
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		// ======== menuBar ========
		{

			// ======== fileMenu ========
			{
				fileMenu.setText("File");
				fileMenu.setMnemonic('F');

				// ---- modHandlerItem ----
				modHandlerItem.setText("Mod Handler");
				modHandlerItem.setMnemonic('M');
				modHandlerItem.setEnabled(false);
				modHandlerItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						modHandlerItemActionPerformed(e);
					}
				});
				fileMenu.add(modHandlerItem);
				fileMenu.add(separator);

				// ---- newSessionIDItem ----
				newSessionIDItem.setText("Relogin");
				newSessionIDItem.setEnabled(false);
				newSessionIDItem.setMnemonic('R');
				newSessionIDItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						newSessionIDItemActionPerformed(e);
					}
				});
				fileMenu.add(newSessionIDItem);
				fileMenu.add(separator1);

				// ---- quitItem ----
				quitItem.setText("Quit");
				quitItem.setMnemonic('Q');
				quitItem.setIcon(new ImageIcon(getClass().getResource(
						"/resources/stock_exit.png")));
				quitItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						quitItemActionPerformed(e);
					}
				});
				fileMenu.add(quitItem);
			}
			menuBar.add(fileMenu);

			// ---- screenshotButton ----
			screenshotButton.setText("Screenshot");
			screenshotButton.setEnabled(false);
			screenshotButton.setIcon(new ImageIcon(getClass().getResource(
					"/resources/camera.png")));
			screenshotButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					screenshotButtonActionPerformed(e);
				}
			});
			menuBar.add(screenshotButton);
		}
		setJMenuBar(menuBar);
		setSize(854, 480);
		// //GEN-END:initComponents
	}

	public Canvas getCanvas() {
		return canvas;
	}

	public void addMenu(JMenu menu) {
		int index = menuBar.getComponentIndex(menuToButtonFiller);
		menuBar.add(menu, index);
		setJMenuBar(menuBar);
	}

	public JButton getScreenshotButton() {
		return screenshotButton;
	}

	public JMenuItem getNewSessionIDItem() {
		return newSessionIDItem;
	}
}

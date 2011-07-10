package org.darkstorm.minecraft.darkmod.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.*;

import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.loopsystem.*;

@SuppressWarnings("serial")
public class LoginUI extends JPanel {
	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JPanel borderedPanel;
	private JPanel cardPanel;
	private JPanel loginFormPanel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JCheckBox rememberLoginCheckBox;
	private JCheckBox checkForUpdatesCheckBox;
	private JPanel panel2;
	private JButton playOfflineButton;
	private JButton loginButton;
	private JPanel dialogPanel;
	private JPanel dialogButtonPanel;
	private JButton dialogButton1;
	private JButton dialogButton2;
	private JPanel dialogInnerPanel;
	private JLabel dialogLabel;
	private JProgressBar dialogProgressBar;
	// JFormDesigner - End of variables declaration //GEN-END:variables

	private static final int[] POSSIBLE_TERRAIN_SPOTS = { 1, 2, 3, 4, 5, 6, 7,
			8, 9, 10, 11, 12, 17, 18, 19, 20, 21, 22, 23, 24, 25, 28, 33, 34,
			35, 36, 37, 38, 39, 40, 41, 44, 45, 49, 50, 51, 52, 53, 54, 55, 56,
			57, 60, 61, 62, 65, 66, 67, 68, 69, 70, 71, 73, 74, 75, 76, 87, 88,
			103, 104, 105, 106, 119, 120, 121, 129 };

	private LoginUtil loginUtil = new LoginUtil();
	private LoopManager loopManager;
	private VolatileImage image;
	private Image backgroundImage;
	private LoopController loginLoop;
	private LoopController loginDisplayLoop;
	private boolean dialogButton1Pressed, dialogButton2Pressed;

	private boolean loginPerformed = false;
	private boolean playOfflinePerformed = false;

	public LoginUI() {
		loopManager = new LoopManager(new ThreadGroup("Login"), false);
		initComponents();
		setPreferredSize(new Dimension(854, 480));
		Image loaded = Tools.getIcon("terrain");
		BufferedImage image = new BufferedImage(loaded.getWidth(this), loaded
				.getHeight(this), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();
		Random random = new Random();
		int i = random.nextInt(POSSIBLE_TERRAIN_SPOTS.length);
		graphics.drawImage(loaded, 0, 0, this);
		BufferedImage terrain = image.getSubimage(
				((POSSIBLE_TERRAIN_SPOTS[i] - 1) % 16) * 16,
				((POSSIBLE_TERRAIN_SPOTS[i] - 1) / 16) * 16, 16, 16);
		backgroundImage = terrain.getScaledInstance(32, 32, 16);
		setBackground(new Color(0, 0, 0, 0));
		if(loginUtil.loadLogin()) {
			usernameField.setText(loginUtil.getUsername());
			passwordField.setText(loginUtil.getPassword());
			rememberLoginCheckBox.setSelected(loginUtil.shouldRememberLogin());
			checkForUpdatesCheckBox.setSelected(loginUtil
					.shouldCheckForUpdates());
		}
		createLoginLoop();
		createLoginDisplayLoop();
	}

	private void createLoginLoop() {
		Loopable loop = new Loopable() {
			@Override
			public int loop() throws InterruptedException {
				showLoggingIn();
				String result = loginUtil.login(usernameField.getText(),
						new String(passwordField.getPassword()));
				if(result != null)
					showError(result);
				dialogLabel.setText("Logged in.");
				dialogProgressBar.setValue(dialogProgressBar.getMaximum());
				dialogProgressBar.setIndeterminate(false);
				return Loopable.STOP;
			}
		};
		loginLoop = loopManager.addLoopable(loop);
	}

	private void createLoginDisplayLoop() {
		Loopable loop = new Loopable() {

			@Override
			public int loop() throws InterruptedException {
				dialogButton1.setText("Cancel");
				dialogButton1.setVisible(true);
				dialogButton2.setText("Retry");
				dialogButton2.setVisible(true);
				dialogLabel.setText("Logging in...");
				dialogProgressBar.setVisible(true);
				dialogProgressBar.setValue(0);
				dialogProgressBar.setIndeterminate(true);
				switchCard("dialog");
				while(!dialogButton1Pressed && !dialogButton2Pressed
						&& !loginUtil.isLoggedIn())
					Tools.sleep(250);
				if(dialogButton1Pressed) {
					dialogButton1Pressed = false;
					if(loginLoop.isAlive())
						loginLoop.stop();
					switchCard("loginForm");
				} else if(dialogButton2Pressed) {
					dialogButton2Pressed = false;
					loginPerformed = true;
					authenticate();
				}
				return Loopable.STOP;
			}
		};
		loginDisplayLoop = loopManager.addLoopable(loop);
	}

	private void loginActionPerformed(ActionEvent e) {
		loginPerformed = true;
	}

	private void playOfflineActionPerformed(ActionEvent e) {
		playOfflinePerformed = true;
	}

	@Override
	public void paint(Graphics g2) {
		int w = getWidth() / 2;
		int h = getHeight() / 2;
		if((image == null) || (image.getWidth() != w)
				|| (image.getHeight() != h))
			image = createVolatileImage(w, h);

		Graphics g = image.getGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		for(int x = 0; x <= w / 32; x++) {
			for(int y = 0; y <= h / 32; y++)
				g.drawImage(backgroundImage, (x * 32) - ((32 - (w % 32)) / 2),
						(y * 32) - ((32 - (h % 32)) / 2), null);
		}
		g.setColor(Color.LIGHT_GRAY);

		String msg = "DarkMod Launcher";
		g.setFont(new Font(null, 1, 20));
		FontMetrics fm = g.getFontMetrics();
		g.drawString(msg, w / 2 - fm.stringWidth(msg) / 2, h / 2
				- fm.getHeight() * 2);

		g.dispose();
		g2.drawImage(image, 0, 0, w * 2, h * 2, null);
		super.paint(g2);
	}

	public void authenticate() {
		if(loginLoop.isAlive()) {
			while(loginLoop.isAlive())
				Tools.sleep(250);
			return;
		}
		while(!loginPerformed && !playOfflinePerformed)
			Tools.sleep(250);
		loginUtil.setUsername(usernameField.getText());
		loginUtil.setPassword(new String(passwordField.getPassword()));
		loginUtil.setCheckForUpdates(checkForUpdatesCheckBox.isSelected());
		if(rememberLoginCheckBox.isSelected())
			loginUtil.saveLogin();
		if(loginPerformed) {
			if(loginLoop.isAlive())
				loginLoop.stop();
			loginLoop.start();
		} else
			loginUtil.playOffline(usernameField.getText());
		loginPerformed = false;
		playOfflinePerformed = false;
	}

	private void showLoggingIn() {
		if(loginDisplayLoop.isAlive())
			loginDisplayLoop.stop();
		loginDisplayLoop.start();
	}

	public void showError(String message) {
		dialogButton1.setText("Cancel");
		dialogButton1.setVisible(true);
		dialogButton2.setText("Retry");
		dialogButton2.setVisible(true);
		dialogLabel.setText(message);
		dialogProgressBar.setVisible(false);
		switchCard("dialog");
		while(!dialogButton1Pressed && !dialogButton2Pressed) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException exception) {}
		}
		if(dialogButton1Pressed) {
			dialogButton1Pressed = false;
			switchCard("loginForm");
		} else if(dialogButton2Pressed) {
			dialogButton2Pressed = false;
			loginPerformed = true;
		}
	}

	public boolean showConfirm(String message) {
		dialogButton1.setText("No");
		dialogButton1.setVisible(true);
		dialogButton2.setText("Yes");
		dialogButton2.setVisible(true);
		dialogLabel.setText(message);
		dialogProgressBar.setVisible(false);
		switchCard("dialog");
		while(!dialogButton1Pressed && !dialogButton2Pressed) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException exception) {}
		}
		if(dialogButton1Pressed) {
			dialogButton1Pressed = false;
			return false;
		} else if(dialogButton2Pressed) {
			dialogButton2Pressed = false;
			return true;
		} else
			return false;
	}

	public void showMessage(String message) {
		dialogButton1.setText("");
		dialogButton1.setVisible(false);
		dialogButton2.setText("OK");
		dialogButton2.setVisible(true);
		dialogLabel.setText(message);
		dialogProgressBar.setVisible(false);
		switchCard("dialog");
		while(!dialogButton1Pressed && !dialogButton2Pressed) {
			try {
				Thread.sleep(250);
			} catch(InterruptedException exception) {}
		}
		dialogButton1Pressed = false;
		dialogButton2Pressed = false;
		return;
	}

	public LoginUtil getLoginUtil() {
		return loginUtil;
	}

	public boolean isCheckForUpdatesSelected() {
		return checkForUpdatesCheckBox.isSelected();
	}

	private void switchCard(String name) {
		CardLayout layout = (CardLayout) cardPanel.getLayout();
		layout.show(cardPanel, name);
		dialogButton1Pressed = false;
		dialogButton2Pressed = false;
	}

	private void dialogButton1ActionPerformed(ActionEvent e) {
		dialogButton1Pressed = true;
	}

	private void dialogButton2ActionPerformed(ActionEvent e) {
		dialogButton2Pressed = true;
	}

	public void setDialogText(String text) {
		System.out.println("Message: " + text);
		dialogLabel.setText(text);
		switchCard("dialog");
	}

	public JLabel getDialogLabel() {
		return dialogLabel;
	}

	public JProgressBar getDialogProgressBar() {
		return dialogProgressBar;
	}

	public JButton getDialogButton1() {
		return dialogButton1;
	}

	public JButton getDialogButton2() {
		return dialogButton2;
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		borderedPanel = new JPanel();
		cardPanel = new JPanel();
		loginFormPanel = new JPanel();
		JLabel usernameLabel = new JLabel();
		usernameField = new JTextField();
		JLabel passwordLabel = new JLabel();
		passwordField = new JPasswordField();
		JLabel spacerLabel = new JLabel();
		rememberLoginCheckBox = new JCheckBox();
		checkForUpdatesCheckBox = new JCheckBox();
		panel2 = new JPanel();
		playOfflineButton = new JButton();
		loginButton = new JButton();
		dialogPanel = new JPanel();
		dialogButtonPanel = new JPanel();
		dialogButton1 = new JButton();
		dialogButton2 = new JButton();
		dialogInnerPanel = new JPanel();
		dialogLabel = new JLabel();
		dialogProgressBar = new JProgressBar();

		// ======== this ========
		setBackground(Color.black);
		setLayout(new GridBagLayout());
		((GridBagLayout) getLayout()).columnWidths = new int[] { 0, 250, 0, 0 };
		((GridBagLayout) getLayout()).rowHeights = new int[] { 0, 0, 0, 0 };
		((GridBagLayout) getLayout()).columnWeights = new double[] { 1.0, 0.0,
				1.0, 1.0E-4 };
		((GridBagLayout) getLayout()).rowWeights = new double[] { 1.0, 0.0,
				1.0, 1.0E-4 };

		// ======== borderedPanel ========
		{
			borderedPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
			borderedPanel.setLayout(new BorderLayout());

			// ======== cardPanel ========
			{
				cardPanel.setLayout(new CardLayout());

				// ======== loginFormPanel ========
				{
					loginFormPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
					loginFormPanel.setLayout(new GridBagLayout());
					((GridBagLayout) loginFormPanel.getLayout()).columnWeights = new double[] {
							0.0, 1.0 };

					// ---- usernameLabel ----
					usernameLabel.setText("Username: ");
					loginFormPanel.add(usernameLabel, new GridBagConstraints(0,
							0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
					loginFormPanel.add(usernameField, new GridBagConstraints(1,
							0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- passwordLabel ----
					passwordLabel.setText("Password: ");
					loginFormPanel.add(passwordLabel, new GridBagConstraints(0,
							1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
					loginFormPanel.add(passwordField, new GridBagConstraints(1,
							1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- spacerLabel ----
					spacerLabel.setText("      ");
					loginFormPanel.add(spacerLabel, new GridBagConstraints(0,
							2, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));

					// ---- rememberLoginCheckBox ----
					rememberLoginCheckBox.setText("Remember login info");
					loginFormPanel.add(rememberLoginCheckBox,
							new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

					// ---- checkForUpdatesCheckBox ----
					checkForUpdatesCheckBox
							.setText("Check for MC & DM updates");
					loginFormPanel.add(checkForUpdatesCheckBox,
							new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0,
									GridBagConstraints.CENTER,
									GridBagConstraints.BOTH, new Insets(0, 0,
											0, 0), 0, 0));

					// ======== panel2 ========
					{
						panel2.setLayout(new GridLayout());

						// ---- playOfflineButton ----
						playOfflineButton.setText("Play Offline");
						playOfflineButton
								.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										playOfflineActionPerformed(e);
									}
								});
						panel2.add(playOfflineButton);

						// ---- loginButton ----
						loginButton.setText("Login");
						loginButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								loginActionPerformed(e);
							}
						});
						panel2.add(loginButton);
					}
					loginFormPanel.add(panel2, new GridBagConstraints(0, 5, 2,
							1, 0.0, 0.0, GridBagConstraints.CENTER,
							GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0,
							0));
				}
				cardPanel.add(loginFormPanel, "loginForm");

				// ======== dialogPanel ========
				{
					dialogPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
					dialogPanel.setLayout(new BorderLayout());

					// ======== dialogButtonPanel ========
					{
						dialogButtonPanel.setLayout(new GridBagLayout());
						((GridBagLayout) dialogButtonPanel.getLayout()).columnWidths = new int[] {
								0, 0, 0 };
						((GridBagLayout) dialogButtonPanel.getLayout()).rowHeights = new int[] {
								0, 0 };
						((GridBagLayout) dialogButtonPanel.getLayout()).columnWeights = new double[] {
								1.0, 1.0, 1.0E-4 };
						((GridBagLayout) dialogButtonPanel.getLayout()).rowWeights = new double[] {
								1.0, 1.0E-4 };

						// ---- dialogButton1 ----
						dialogButton1.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								dialogButton1ActionPerformed(e);
							}
						});
						dialogButtonPanel.add(dialogButton1,
								new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
										GridBagConstraints.CENTER,
										GridBagConstraints.BOTH, new Insets(0,
												0, 0, 0), 0, 0));

						// ---- dialogButton2 ----
						dialogButton2.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent e) {
								dialogButton2ActionPerformed(e);
							}
						});
						dialogButtonPanel.add(dialogButton2,
								new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
										GridBagConstraints.CENTER,
										GridBagConstraints.BOTH, new Insets(0,
												0, 0, 0), 0, 0));
					}
					dialogPanel.add(dialogButtonPanel, BorderLayout.SOUTH);

					// ======== dialogInnerPanel ========
					{
						dialogInnerPanel.setLayout(new BorderLayout());

						// ---- dialogLabel ----
						dialogLabel
								.setHorizontalAlignment(SwingConstants.CENTER);
						dialogLabel.setFont(dialogLabel.getFont().deriveFont(
								dialogLabel.getFont().getStyle() & ~Font.BOLD,
								dialogLabel.getFont().getSize() + 4f));
						dialogInnerPanel.add(dialogLabel, BorderLayout.CENTER);
						dialogInnerPanel.add(dialogProgressBar,
								BorderLayout.SOUTH);
					}
					dialogPanel.add(dialogInnerPanel, BorderLayout.CENTER);
				}
				cardPanel.add(dialogPanel, "dialog");
			}
			borderedPanel.add(cardPanel, BorderLayout.CENTER);
		}
		add(borderedPanel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		// //GEN-END:initComponents
	}
}

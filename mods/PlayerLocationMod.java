import java.text.DecimalFormat;
import java.util.*;

import javax.swing.JTable;
import javax.swing.table.*;

import org.darkstorm.minecraft.darkmod.events.*;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.Event;
import org.lwjgl.opengl.GL11;

public class PlayerLocationMod extends Mod implements EventListener {
	private PlayerLocationUI ui;
	private DecimalFormat format = new DecimalFormat("#0.000");
	private AutoAttackMod attackMod;
	private LockonMod lockonMod;

	public PlayerLocationMod() {
		ui = new PlayerLocationUI();
	}

	@Override
	public void onStart() {
		attackMod = (AutoAttackMod) handler.getModByName("Auto Attack Mod");
		lockonMod = (LockonMod) handler.getModByName("Lock-on Mod");
		eventManager.addListener(TickEvent.class, this);
		eventManager.addListener(RenderEvent.class, this);
		ui.setVisible(true);
	}

	@Override
	public void onStop() {
		ui.setVisible(false);
		eventManager.removeListener(RenderEvent.class, this);
		eventManager.removeListener(TickEvent.class, this);
	}

	@Override
	public int loop() {
		return 500;
	}

	@Override
	public void onEvent(Event event) {
		if(event instanceof TickEvent)
			onTick((TickEvent) event);
		else if(event instanceof RenderEvent)
			onRender((RenderEvent) event);

	}

	private void onTick(TickEvent event) {
		if(!ui.isVisible())
			return;
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
						model.setValueAt(
								getDistanceTo(minecraft.getPlayer(),
										player.getX(), player.getY(),
										player.getZ()), row, 1);
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
								getDistanceTo(minecraft.getPlayer(),
										player.getX(), player.getY(),
										player.getZ()), player.getX(),
								player.getY(), player.getZ() });
				}
			}
			@SuppressWarnings("unchecked")
			TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) list
					.getRowSorter();
			try {
				sorter.sort();
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		} else
			for(int i = 0; i < model.getRowCount(); i++)
				model.removeRow(i);
	}

	private void onRender(RenderEvent event) {
		final Player player = minecraft.getPlayer();
		World world = minecraft.getWorld();
		if(player == null || world == null)
			return;
		if(event.getStatus() == RenderEvent.RENDER) {
			try {

				GL11.glEnable(GL11.GL_TEXTURE_2D);

				List<Humanoid> players = new ArrayList<Humanoid>(
						world.getPlayers());
				Collections.sort(players, new Comparator<Humanoid>() {
					@Override
					public int compare(Humanoid o1, Humanoid o2) {
						return Double.compare(getDistanceBetween(player, o1),
								getDistanceBetween(player, o2));
					}
				});

				GL11.glMatrixMode(GL11.GL_PROJECTION);
				GL11.glEnable(GL11.GL_COLOR_MATERIAL);
				GL11.glLoadIdentity();
				GL11.glOrtho(0.0D, minecraft.getCanvas().getWidth(), minecraft
						.getCanvas().getHeight(), 0.0D, 1000D, 3000D);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				GL11.glLoadIdentity();
				GL11.glTranslatef(0.0F, 0.0F, -2000F);
				GL11.glLineWidth(1.0F);

				Humanoid trackedPlayer = null;
				List<Humanoid> friends = new ArrayList<Humanoid>();
				List<Humanoid> otherPlayers = new ArrayList<Humanoid>();
				for(Humanoid p : players) {
					if(p.getID() == player.getID())
						continue;
					if(lockonMod.isTracking()
							&& lockonMod.getLockonName() != null
							&& lockonMod.getLockonName().equalsIgnoreCase(
									ChatColor.removeColors(p.getName()))) {
						trackedPlayer = p;
						continue;
					}
					if(attackMod.isFriend(p.getName())) {
						friends.add(p);
						continue;
					}
					if(attackMod.getTeam() != null
							&& attackMod.getTeam().isOnTeam(p))
						continue;
					otherPlayers.add(p);
				}
				int index = 0;
				if(trackedPlayer != null) {
					renderPlayerName(trackedPlayer, index, 0xFF0000);
					index += 2;
				}
				for(Humanoid friend : friends)
					renderPlayerName(friend, index++,
							getColor(friend.getName()));
				if(friends.size() > 0)
					index++;
				for(Humanoid p : otherPlayers)
					renderPlayerName(p, index++, getColor(p.getName()));

				GL11.glDisable(GL11.GL_TEXTURE_2D);
			} catch(Exception exception) {
				exception.printStackTrace();
			}
		} else if(event.getStatus() == RenderEvent.RENDER_ENTITIES_END) {
			double mX = player.getX();
			double mY = player.getY();
			double mZ = player.getZ();
			double horizontalSize = 0.45;
			double verticalSize = 0.35;
			int drawnEntities = 0;
			List<Humanoid> players = new ArrayList<Humanoid>(world.getPlayers());
			Collections.sort(players, new Comparator<Humanoid>() {
				@Override
				public int compare(Humanoid o1, Humanoid o2) {
					return Double.compare(getDistanceBetween(player, o1),
							getDistanceBetween(player, o2));
				}
			});
			for(int x = 0; x < players.size(); x++) {
				Humanoid p = players.get(x);
				int nameColor = getColor(p.getName());
				float lineSize = 1.5f;
				if(lockonMod.isTracking()
						&& lockonMod.getLockonName() != null
						&& lockonMod.getLockonName().equalsIgnoreCase(
								ChatColor.removeColors(p.getName()))) {
					nameColor = 0xFF0000;
					lineSize = 2.5f;
				} else if(drawnEntities > 10
						&& !attackMod.isFriend(p.getName()))
					continue;
				if(attackMod.getTeam() != null
						&& attackMod.getTeam().isOnTeam(p))
					continue;
				double X = p.getX();
				double Y = p.getY();
				double Z = p.getZ();
				double dX = (mX - X);
				double dY = (mY - Y);
				double dZ = (mZ - Z);

				if(X != mX && Y != mY && Z != mZ) {
					GL11.glPushMatrix();
					setColor(nameColor);
					GL11.glLineWidth(lineSize);
					GL11.glDisable(GL11.GL_TEXTURE_2D);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA,
							GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);
					GL11.glBegin(GL11.GL_LINE_LOOP);
					GL11.glVertex2d(0, 0);
					GL11.glVertex3d((-dX + horizontalSize) - 0.5,
							(verticalSize - dY) + 1.0,
							(-dZ - horizontalSize) + 0.5);
					GL11.glEnd();
					GL11.glBegin(GL11.GL_LINE_LOOP);
					GL11.glVertex3d(-dX - 0.5, -dY, -dZ - 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY + 2.0, -dZ - 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY + 2.0, -dZ - 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY + 2.0, -dZ + 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY + 2.0, -dZ + 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY + 2.0, -dZ - 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY, -dZ - 0.5);

					GL11.glVertex3d(-dX + 0.5, -dY, -dZ - 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY + 2.0, -dZ - 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY, -dZ - 0.5);

					GL11.glVertex3d(-dX + 0.5, -dY, -dZ + 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY + 2.0, -dZ + 0.5);
					GL11.glVertex3d(-dX + 0.5, -dY, -dZ + 0.5);

					GL11.glVertex3d(-dX - 0.5, -dY, -dZ + 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY + 2.0, -dZ + 0.5);
					GL11.glVertex3d(-dX - 0.5, -dY, -dZ + 0.5);
					GL11.glEnd();

					GL11.glDisable(GL11.GL_LINE_SMOOTH);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glEnable(GL11.GL_TEXTURE_2D);
					GL11.glPopMatrix();
				}
				drawnEntities++;
			}

			EntityTarget target = minecraft.getPlayerTarget();
			if(target == null)
				return;
			double X = target.getTargetX();
			double Y = target.getTargetY();
			double Z = target.getTargetZ();
			double dX = (mX - X);
			double dY = (mY - Y);
			double dZ = (mZ - Z);

			GL11.glPushMatrix();
			GL11.glColor4f(1f, 0f, 0f, 1f);
			GL11.glLineWidth(1.7f);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glBegin(GL11.GL_LINE_LOOP);
			GL11.glVertex3d(-dX, -dY, -dZ);
			GL11.glVertex3d(-dX, -dY + 1.0, -dZ);
			GL11.glVertex3d(-dX + 1.0, -dY + 1.0, -dZ);
			GL11.glVertex3d(-dX + 1.0, -dY + 1.0, -dZ + 1.0);
			GL11.glVertex3d(-dX, -dY + 1.0, -dZ + 1.0);
			GL11.glVertex3d(-dX, -dY + 1.0, -dZ);
			GL11.glVertex3d(-dX, -dY, -dZ);

			GL11.glVertex3d(-dX + 1.0, -dY, -dZ);
			GL11.glVertex3d(-dX + 1.0, -dY + 1.0, -dZ);
			GL11.glVertex3d(-dX + 1.0, -dY, -dZ);

			GL11.glVertex3d(-dX + 1.0, -dY, -dZ + 1.0);
			GL11.glVertex3d(-dX + 1.0, -dY + 1.0, -dZ + 1.0);
			GL11.glVertex3d(-dX + 1.0, -dY, -dZ + 1.0);

			GL11.glVertex3d(-dX, -dY, -dZ + 1.0);
			GL11.glVertex3d(-dX, -dY + 1.0, -dZ + 1.0);
			GL11.glVertex3d(-dX, -dY, -dZ + 1.0);
			GL11.glEnd();

			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glPopMatrix();
		}
	}

	private void renderPlayerName(Humanoid player, int index, int color) {
		Font font = minecraft.getFont();
		int y = 5 + (index * 10);
		int tagDistance = -(font.getStringWidth("[x] "));
		int distance = font.getStringWidth("12345678901234") + 10;
		int x = minecraft.getCanvas().getWidth() - distance
				- font.getStringWidth("9999.999");
		String name = player.getName();
		AutoAttackMod.Team team = AutoAttackMod.Team.getTeam(name);
		if(team != null && team.getColor() != null) {
			font.drawStringWithShadow(ChatColor.GRAY + "[" + team.getColor()
					+ team.getName().charAt(0) + ChatColor.GRAY + "]", x
					+ tagDistance, y, 0xffffff);
		}
		font.drawStringWithShadow(ChatColor.removeColors(player.getName()), x,
				y, color);
		font.drawStringWithShadow(format.format(getDistanceBetween(
				minecraft.getPlayer(), player)), x + distance, y, color);
	}

	private void setColor(int color) {
		float r = (color >> 16 & 0xff) / 255F;
		float g = (color >> 8 & 0xff) / 255F;
		float b = (color & 0xff) / 255F;

		GL11.glColor4f(r, g, b, 1f);
	}

	private int getColor(String player) {
		return (player.hashCode() & 0xaaaaaa) + 0x444444;
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

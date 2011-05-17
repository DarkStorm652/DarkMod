import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.events.PlayerProcessEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.*;
import org.darkstorm.tools.strings.StringTools;
import org.lwjgl.input.Keyboard;

public class FlyingMod extends Mod implements EventListener, CommandListener {
	private DarkMod darkMod = DarkMod.getInstance();
	private boolean enableHovering = false;
	private boolean lastPressed = false;
	private int mode = GLIDE;
	private double flySpeed = 0.5;

	private static final int GLIDE = 1;
	private static final int WALK = 2;

	@Override
	public String getName() {
		return "Flying Mod";
	}

	@Override
	public String getShortDescription() {
		return "IT'S A BIRD! NO, IT'S A PLANE! NO, IT'S "
				+ darkMod.getUsername()
				+ "!!! Hold CTRL + Space to ascend, Shift + Space to descend, hold C or press CTRL + Shift + C to hover.";
	}

	@Override
	public String getFullDescription() {
		return "<html>Allows you to <b>FLY!</b><br />"
				+ "Hold CTRL + Space to fly, hold C to hover.<br />"
				+ "On SSP, you may land from any height and not die, "
				+ "but on SMP falling from any height will cause damage "
				+ "(which will be based on the distance you fall). Falling "
				+ "from heigher than 15 blocks is sure-death without armor. "
				+ "Armor does assist in decreasing damage done when falling."
				+ "</html>";
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
	public void onStart() {
		eventManager.addListener(PlayerProcessEvent.class, this);
		commandManager.registerListener(new Command("flymode",
				"/flymode <glide|walk>", "Makes you fly"), this);
		commandManager.registerListener(new Command("flyspeed",
				"/flyspeed <speed>", "Sets the flying speed. Default is 0.5"),
				this);

	}

	@Override
	public void onStop() {
		eventManager.removeListener(PlayerProcessEvent.class, this);
		commandManager.unregisterListener("flymode");
		commandManager.unregisterListener("flyspeed");
	}

	@Override
	public int loop() {
		return 500;
	}

	@Override
	public void onEvent(Event event) {
		if(PlayerProcessEvent.class.isInstance(event)
				&& ((PlayerProcessEvent) event).getStatus() == PlayerProcessEvent.FINISH
				&& minecraft.getPlayer() != null) {
			PlayerProcessEvent playerProcessEvent = (PlayerProcessEvent) event;
			Player player = playerProcessEvent.getPlayer();
			if(player.equals(minecraft.getPlayer())) {
				switch(mode) {
				case WALK:
					if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
						player.setOnGround(true);
				case GLIDE:
					setSpeed();
				}
			}
		}
	}

	private void setSpeed() {
		Player player = minecraft.getPlayer();
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)
				&& Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
			player.setSpeedY(flySpeed);
		} else if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)
				&& Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			player.setSpeedY(-flySpeed);
		} else if(Keyboard.isKeyDown(Keyboard.KEY_C) || enableHovering)
			player.setSpeedY(0);
		if(!Keyboard.isKeyDown(Keyboard.KEY_SPACE)
				|| Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			player.setOnGround(true);
		if(Keyboard.isKeyDown(Keyboard.KEY_C)
				&& Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
				&& Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !lastPressed) {
			enableHovering = !enableHovering;
			lastPressed = true;
		} else if((!Keyboard.isKeyDown(Keyboard.KEY_C) || !Keyboard
				.isKeyDown(Keyboard.KEY_LCONTROL))
				&& lastPressed)
			lastPressed = false;
		player.setFallDistance(0);
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts.length != 2)
			return;
		if(parts[0].equalsIgnoreCase("flymode")) {
			if(parts[1].equalsIgnoreCase("walk")) {
				mode = WALK;
				displayText("Flying mode set to " + ChatColor.CYAN + "walk");
			} else if(parts[1].equalsIgnoreCase("glide")) {
				mode = GLIDE;
				displayText("Flying mode set to " + ChatColor.LIME + "glide");
			} else
				displayText("Invalid fly mode");
		} else if(parts[0].equalsIgnoreCase("flyspeed")
				&& StringTools.isDouble(parts[1])) {
			flySpeed = Double.parseDouble(parts[1]);
			displayText("Flying speed set to " + ChatColor.YELLOW + flySpeed);
		}
	}

}

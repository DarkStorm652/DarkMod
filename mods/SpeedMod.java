import org.darkstorm.minecraft.darkmod.DarkMod;
import org.darkstorm.minecraft.darkmod.events.PlayerProcessEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Player;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.*;
import org.darkstorm.tools.strings.StringTools;
import org.lwjgl.input.Keyboard;

public class SpeedMod extends Mod implements EventListener, CommandListener {
	private DarkMod darkMod = DarkMod.getInstance();
	private double speed = 1;
	private int mode = REGULAR;

	private static final int REGULAR = 0;
	private static final int ABRUPT = 1;

	@Override
	public String getShortDescription() {
		return "* " + darkMod.getUsername() + " USED EXTREMESPEED! "
				+ "Hold CTRL while moving to move extremely fast, press X to "
				+ "change speed";
	}

	@Override
	public String getFullDescription() {
		return "<html>Hold CTRL while moving to go extremely fast. Pressing "
				+ "X will change the speed at which you go when holding "
				+ "CTRL and moving.</html>";
	}

	@Override
	public String getName() {
		return "Speed Mod";
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
		commandManager.registerListener(new Command("speed", "/speed <speed>",
				"Sets the speed. Default is 1.0"), this);
		commandManager
				.registerListener(
						new Command("speedmode", "/speedmode <regular|abrupt>",
								"Sets the speed mode. Default is regular, abrupt stops abruplty without glide"),
						this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(PlayerProcessEvent.class, this);
		commandManager.unregisterListener("speed");
		commandManager.unregisterListener("speedmode");
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
				try {
					double speedIncrement = speed;
					if(Keyboard.isKeyDown(Keyboard.KEY_W))
						speedIncrement *= -1;

					float rotation = player.getRotationX();
					double speedIncreaseX, speedIncreaseZ;
					if(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)
							&& (Keyboard.isKeyDown(Keyboard.KEY_W)
									|| Keyboard.isKeyDown(Keyboard.KEY_A)
									|| Keyboard.isKeyDown(Keyboard.KEY_D) || Keyboard
									.isKeyDown(Keyboard.KEY_S))) {
						if(!Keyboard.isKeyDown(Keyboard.KEY_W)
								&& !Keyboard.isKeyDown(Keyboard.KEY_S)) {
							if(Keyboard.isKeyDown(Keyboard.KEY_D))
								rotation -= 90.0D;
							if(Keyboard.isKeyDown(Keyboard.KEY_A))
								rotation += 90.0D;
						} else if(Keyboard.isKeyDown(Keyboard.KEY_W)) {
							if(Keyboard.isKeyDown(Keyboard.KEY_D))
								rotation += 45.0D;
							if(Keyboard.isKeyDown(Keyboard.KEY_A))
								rotation -= 45.0D;
						} else if(Keyboard.isKeyDown(Keyboard.KEY_S)) {
							if(Keyboard.isKeyDown(Keyboard.KEY_D))
								rotation -= 45.0D;
							if(Keyboard.isKeyDown(Keyboard.KEY_A))
								rotation += 45.0D;
						}
						float pi = 3.14159265F;
						speedIncreaseX = speedIncrement
								* minecraft.cos(rotation * (pi / 180.0F));
						speedIncreaseZ = speedIncrement
								* -minecraft.sin(rotation * (pi / 180.0F));
					} else if(!Keyboard.isKeyDown(Keyboard.KEY_W)
							&& !Keyboard.isKeyDown(Keyboard.KEY_A)
							&& !Keyboard.isKeyDown(Keyboard.KEY_D)
							&& !Keyboard.isKeyDown(Keyboard.KEY_S)) {
						speedIncrement = 0;
						speedIncreaseX = 0;
						speedIncreaseZ = 0;
					} else
						return;

					double speedX, speedZ;
					if(mode == REGULAR) {
						speedX = player.getSpeedX() + speedIncreaseX;
						speedZ = player.getSpeedZ() + speedIncreaseZ;
					} else if(mode == ABRUPT) {
						speedX = speedIncreaseX;
						speedZ = speedIncreaseZ;
					} else
						return;
					player.setSpeedX(speedX);
					player.setSpeedZ(speedZ);
				} catch(Exception exception) {
					exception.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("speed") && parts.length == 2
				&& StringTools.isDouble(parts[1])) {
			speed = Double.parseDouble(parts[1]);
			displayText("Set speed to " + ChatColor.YELLOW + speed);
		} else if(parts[0].equalsIgnoreCase("speedmode")) {
			if(parts.length == 1) {
				String modeMessage;
				if(mode == REGULAR)
					modeMessage = ChatColor.LIME + "regular";
				else if(mode == ABRUPT)
					modeMessage = ChatColor.CYAN + "abrupt";
				else
					modeMessage = ChatColor.RED + "invalid";
				displayText("Current speed mode is " + modeMessage);
			} else if(parts[1].equalsIgnoreCase("regular")) {
				mode = REGULAR;
				displayText("Set speed mode to " + ChatColor.LIME + "regular");
			} else if(parts[1].equalsIgnoreCase("abrupt")) {
				mode = ABRUPT;
				displayText("Set speed mode to " + ChatColor.CYAN + "abrupt");
			} else
				displayText("Invalid speed mode");
		}
	};
}

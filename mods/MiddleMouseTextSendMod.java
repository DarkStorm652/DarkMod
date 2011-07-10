import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.lwjgl.input.Mouse;

public class MiddleMouseTextSendMod extends Mod implements CommandListener {
	private String text = null;
	private Object lock = new Object();

	private boolean lastPressed;

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Shortcut Text Send Mod";
	}

	@Override
	public String getShortDescription() {
		return "Sends text on middle mouse click";
	}

	@Override
	public void onStart() {
		commandManager.registerListener(new Command("mmtext", "/mmtext [text]",
				"Sets the shortcut text. Do not supply text to clear"), this);
	}

	@Override
	public void onStop() {
		commandManager.unregisterListener("mmtext");
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		if(minecraft.getWorld() != null
				&& minecraft.getWorld() instanceof MultiplayerWorld
				&& text != null) {
			if(Mouse.isButtonDown(2) && !lastPressed) {
				synchronized(lock) {
					((MultiplayerPlayer) minecraft.getPlayer()).sendText(text);
				}
				lastPressed = true;
			} else if(!Mouse.isButtonDown(2) && lastPressed)
				lastPressed = false;
		}
		return 50;
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("mmtext")) {
			if(parts.length > 1) {
				synchronized(lock) {
					text = parts[1];
					if(parts.length > 2)
						for(int i = 2; i < parts.length; i++)
							text += " " + parts[i];
					displayText(ChatColor.GRAY + "Shortcut text set to: "
							+ ChatColor.GOLD + text);
				}
			} else {
				synchronized(lock) {
					text = null;
					displayText(ChatColor.GRAY + "Shortcut text reset");
				}
			}
		}
	}

}

import org.darkstorm.minecraft.darkmod.events.PlayerProcessEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.*;
import org.lwjgl.input.Keyboard;

public class SingleplayerCommandsMod extends Mod implements EventListener {
	private Class<?> chatScreenClass;
	private boolean open = false;

	@Override
	public ModControl getControlOption() {
		return ModControl.NONE;
	}

	@Override
	public String getName() {
		return "Singleplayer Commands Mod";
	}

	@Override
	public String getShortDescription() {
		return "Allows for commands in single-player mode";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public int loop() throws InterruptedException {
		return 1000;
	}

	@Override
	public void onStart() {
		if(chatScreenClass == null) {
			try {
				ClassLoader classLoader = accessHandler.getClassLoader();
				chatScreenClass = classLoader.loadClass("fr");
			} catch(Throwable exception) {
				exception.printStackTrace();
			}
		}
		eventManager.addListener(PlayerProcessEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(PlayerProcessEvent.class, this);
	}

	@Override
	public void onEvent(Event event) {
		try {
			PlayerProcessEvent playerProcessEvent = (PlayerProcessEvent) event;
			Player player = playerProcessEvent.getPlayer();
			if(player.equals(minecraft.getPlayer())
					&& minecraft.getWorld() != null) {
				if(minecraft.getWorld() instanceof MultiplayerWorld) {
					open = false;
					return;
				}
				if(Keyboard.isKeyDown(Keyboard.KEY_T)) {
					if(!open)
						minecraft.displayScreen((GuiScreen) chatScreenClass
								.newInstance());
					open = !open;
				}
			}
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}
}

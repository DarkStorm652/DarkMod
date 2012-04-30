import java.util.regex.*;

import org.darkstorm.minecraft.darkmod.events.ChatEvent;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.Event;

public class ColorChatMod extends Mod {

	public ColorChatMod() {
	}

	@Override
	public void onStart() {
		eventManager.addListener(ChatEvent.class, this);
	}

	@Override
	public void onStop() {
		eventManager.removeListener(ChatEvent.class, this);
	}

	@Override
	public int loop() throws InterruptedException {
		return 9000;
	}

	@Override
	public String getName() {
		return "Chat Color Mod";
	}

	@Override
	public String getShortDescription() {
		return "Replaces &colors with their color codes";
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
	public void onEvent(Event event) {
		ChatEvent chatEvent = (ChatEvent) event;
		Pattern pattern = Pattern.compile("(?i)&[0-9a-f]");
		String message = chatEvent.getMessage();
		Matcher matcher = pattern.matcher(message);
		while(matcher.find())
			message = message.substring(0, matcher.start()) + "§"
					+ message.substring(matcher.end() - 1);
		chatEvent.setMessage(message);
	}
}

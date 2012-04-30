import java.math.BigInteger;
import java.util.*;

import org.darkstorm.minecraft.darkmod.events.ChatEvent;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.tools.events.Event;

public class NoSpamMod extends Mod {
	private Map<String, BigInteger> messages = new HashMap<String, BigInteger>();

	public NoSpamMod() {
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
		return "No Spam Mod";
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
		if(chatEvent.getStatus() != ChatEvent.DISPLAYED)
			return;
		String message = chatEvent.getMessage();
		// updateMessages();
		if(messages.get(message) != null)
			chatEvent.setCancelled(true);
		BigInteger newTime = BigInteger
				.valueOf(System.currentTimeMillis() + 2500);
		messages.put(message, newTime);
	}

	private void updateMessages() {
		BigInteger currentTime = BigInteger.valueOf(System.currentTimeMillis());
		List<String> messageList = new ArrayList<String>(messages.keySet());
		for(String message : messageList) {
			BigInteger time = messages.get(message);
			if(time.compareTo(currentTime) <= 0) {
				System.out.println("Removing message: " + message);
				messages.remove(message);
			}
		}
	}
}

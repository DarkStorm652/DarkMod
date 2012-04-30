import javax.script.*;

import org.darkstorm.minecraft.darkmod.events.ChatEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.*;
import org.darkstorm.minecraft.darkmod.hooks.client.packets.Packet3Chat;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.*;
import org.darkstorm.tools.events.Event;

public class ReactionTestMod extends Mod {

	public ReactionTestMod() {
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
		return "Reaction Test Mod";
	}

	@Override
	public String getShortDescription() {
		return "Solves Reaction Test puzzles.";
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
		World world = minecraft.getWorld();
		if(world == null || !(world instanceof MultiplayerWorld))
			return;
		MultiplayerWorld mpWorld = (MultiplayerWorld) world;
		String message = ChatColor.removeColors(chatEvent.getMessage());
		if(message.contains("* First player who ")) {
			String[] parts = message.split("First player who ")[1].split(" ");
			String type = parts[0];
			String puzzle = parts[1];
			puzzle = puzzle.substring(1, puzzle.length() - 2);
			if(type.equals("types")) {
				Packet3Chat chatPacket = (Packet3Chat) ReflectionUtil
						.instantiate(ClassRepository
								.getClassForInterface(Packet3Chat.class));
				chatPacket.setMessage(puzzle);
				mpWorld.getNetworkHandler().sendPacket(chatPacket);
			} else if(type.equals("solves")) {
				try {
					ScriptEngineManager mgr = new ScriptEngineManager();
					ScriptEngine engine = mgr.getEngineByName("JavaScript");
					Packet3Chat chatPacket = (Packet3Chat) ReflectionUtil
							.instantiate(ClassRepository
									.getClassForInterface(Packet3Chat.class));
					String solved = engine.eval(puzzle).toString();
					solved = solved.split("\\.")[0];
					chatPacket.setMessage(solved);
					mpWorld.getNetworkHandler().sendPacket(chatPacket);
				} catch(ScriptException exception) {
					exception.printStackTrace();
				}
			}
		} else if(message.contains("[RTest]")
				&& message.contains("First Player to Type: ")) {
			String puzzle = message.split("First Player to Type: ")[1]
					.split(" ")[0];
			Packet3Chat chatPacket = (Packet3Chat) ReflectionUtil
					.instantiate(ClassRepository
							.getClassForInterface(Packet3Chat.class));
			chatPacket.setMessage(puzzle);
			mpWorld.getNetworkHandler().sendPacket(chatPacket);
		}
	}
}

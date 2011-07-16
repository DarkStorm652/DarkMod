import java.lang.reflect.Field;
import java.util.Vector;

import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Packet;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.tools.events.*;

public class PacketMod extends Mod implements EventListener, CommandListener {
	private Vector<String> filterList = new Vector<String>();
	private Object lock = new Object();

	@Override
	public ModControl getControlOption() {
		return ModControl.TOGGLE;
	}

	@Override
	public String getFullDescription() {
		return "";
	}

	@Override
	public String getName() {
		return "Packet Mod";
	}

	@Override
	public String getShortDescription() {
		return "";
	}

	@Override
	public boolean hasOptions() {
		return false;
	}

	@Override
	public void onStart() {
		eventManager.addListener(PacketEvent.class, this);
		commandManager.registerListener(new Command("pf", "", ""), this);

	}

	@Override
	public void onStop() {
		eventManager.removeListener(PacketEvent.class, this);
		commandManager.unregisterListener("pf");
	}

	@Override
	public int loop() {
		return 500;
	}

	@Override
	public void onEvent(Event event) {
		try {
			PacketEvent packetEvent = (PacketEvent) event;
			Packet packet = packetEvent.getPacket();
			Class<?> packetClass = packet.getClass();
			synchronized(lock) {
				if(filterList.contains(packetClass.getName()))
					return;
			}
			String fields = "";
			for(Field field : packetClass.getDeclaredFields()) {
				if(!field.isAccessible())
					field.setAccessible(true);
				fields += " " + field.getName() + ":" + field.get(packet);
			}
			String direction = " ? ";
			switch(packetEvent.getStatus()) {
			case PacketEvent.SENT:
				direction = "-> ";
				break;
			case PacketEvent.RECEIVED:
				direction = "<- ";
				break;
			}
			System.out.println(direction + packetClass.getName() + fields);
		} catch(Exception exception) {
			System.out.println(exception.toString());
		}
	}

	@Override
	public void onCommand(String command) {
		String[] parts = command.split(" ");
		if(parts[0].equalsIgnoreCase("pf") && parts.length == 2
				&& parts[1].length() > 0) {
			synchronized(lock) {
				if(!filterList.contains(parts[1])) {
					filterList.add(parts[1]);
					displayText(ChatColor.GRAY + "Filtering packet: "
							+ ChatColor.GOLD + parts[1]);
				} else {
					filterList.remove(parts[1]);
					displayText(ChatColor.GRAY + "Unfiltering packet: "
							+ ChatColor.GOLD + parts[1]);
				}
			}
		}
	}

}

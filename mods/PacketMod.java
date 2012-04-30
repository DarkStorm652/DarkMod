import java.lang.reflect.Field;
import java.util.Vector;

import org.darkstorm.minecraft.darkmod.events.PacketEvent;
import org.darkstorm.minecraft.darkmod.hooks.client.Packet;
import org.darkstorm.minecraft.darkmod.mod.Mod;
import org.darkstorm.minecraft.darkmod.mod.commands.*;
import org.darkstorm.minecraft.darkmod.mod.methods.constants.ChatColor;
import org.darkstorm.minecraft.darkmod.tools.ClassRepository;
import org.darkstorm.tools.events.*;

public class PacketMod extends Mod implements EventListener, CommandListener {
	private Vector<String> filterList = new Vector<String>();
	private Object lock = new Object();
	private boolean allowSent = true;
	private boolean allowReceived = true;
	private boolean reversed = false;

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
		commandManager.registerListener(new Command("filter", "", ""), this);

	}

	@Override
	public void onStop() {
		eventManager.removeListener(PacketEvent.class, this);
		commandManager.unregisterListener("pf");
		commandManager.unregisterListener("filter");
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
				if(filterList.contains(packetClass.getName())) {
					if(!reversed)
						return;
				} else if(reversed)
					return;
			}
			String direction = " ? ";
			switch(packetEvent.getStatus()) {
			case PacketEvent.SENT:
				if(!allowSent)
					return;
				direction = "-> ";
				break;
			case PacketEvent.RECEIVED:
				if(!allowReceived)
					return;
				direction = "<- ";
				break;
			}
			String fields = "";
			for(Field field : packetClass.getDeclaredFields()) {
				if(!field.isAccessible())
					field.setAccessible(true);
				fields += " " + field.getName() + ":" + field.get(packet);
			}
			Class<?> interfaceForClass = ClassRepository
					.getInterfaceForClass(packetClass);
			if(interfaceForClass == null)
				interfaceForClass = packetClass;
			System.out.println(direction + interfaceForClass.getSimpleName()
					+ " (" + packetClass.getName() + ")" + fields);
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
		} else if(parts[0].equalsIgnoreCase("filter") && parts.length == 2
				&& parts[1].length() > 0) {
			if(parts[1].equalsIgnoreCase("sent")) {
				allowSent = !allowSent;
				displayText(ChatColor.GRAY + "Sent packets will "
						+ (allowSent ? "now" : "no longer") + " be displayed.");
			} else if(parts[1].equalsIgnoreCase("received")) {
				allowReceived = !allowReceived;
				displayText(ChatColor.GRAY + "Received packets will "
						+ (allowReceived ? "now" : "no longer")
						+ " be displayed.");
			} else if(parts[1].equalsIgnoreCase("clear")) {
				synchronized(lock) {
					filterList.clear();
					allowSent = true;
					allowReceived = true;
				}
				displayText(ChatColor.GRAY
						+ "Packet filtering info was cleared.");
			} else if(parts[1].equalsIgnoreCase("reverse")) {
				reversed = !reversed;
				displayText(ChatColor.GRAY + "Packet filtering is "
						+ (reversed ? "now" : "no longer") + " reversed.");
			}
		}
	}

}

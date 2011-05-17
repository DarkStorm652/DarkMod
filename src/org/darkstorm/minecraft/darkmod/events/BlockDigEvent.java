package org.darkstorm.minecraft.darkmod.events;

import org.darkstorm.tools.events.Event;

public class BlockDigEvent extends Event {
	public static final int BEGIN_DIG = 0;
	public static final int END_DIG = 2;
	public static final int BLOCK_REMOVED = 1;

	public BlockDigEvent(int status) {
		super(status);
	}

	public int getStatus() {
		return (Integer) info[0];
	}
}

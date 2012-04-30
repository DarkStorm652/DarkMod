package org.darkstorm.minecraft.darkmod.events;

import org.darkstorm.tools.events.Event;

public class RenderEvent extends Event {
	public static final int RENDER = 0, RENDER_ENTITIES = 1,
			RENDER_ENTITIES_END = 2;

	public RenderEvent(int status) {
		super(status);
	}

	public int getStatus() {
		return (Integer) info[0];
	}
}

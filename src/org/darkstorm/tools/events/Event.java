package org.darkstorm.tools.events;

import org.darkstorm.tools.exceptions.NullArgumentException;

public class Event {
	protected Object[] info;

	public Event(Object... info) {
		if(info == null)
			throw new NullArgumentException(0, "Object[]");
		this.info = info;
	}

	public Object[] getInfo() {
		return info.clone();
	}
}

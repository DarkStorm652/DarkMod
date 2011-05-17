package org.darkstorm.tools.events;

import java.util.ArrayList;

public class EventManager {
	private ArrayList<EventSender> eventSenders;
	private Object eventSendersLock = new Object();

	public EventManager() {
		eventSenders = new ArrayList<EventSender>();
	}

	public void addListener(Class<? extends Event> eventClass,
			EventListener listener) {
		synchronized(eventSendersLock) {
			boolean senderExists = false;
			for(EventSender sender : eventSenders) {
				if(eventClass.isAssignableFrom(sender.getListenerEventClass())) {
					sender.addListener(listener);
					senderExists = true;
				}
			}
			if(!senderExists) {
				EventSender sender = new EventSender(eventClass);
				eventSenders.add(sender);
				sender.addListener(listener);
			}
		}
	}

	public void removeListener(Class<? extends Event> eventClass,
			EventListener listener) {
		synchronized(eventSendersLock) {
			for(EventSender sender : eventSenders) {
				if(eventClass.isAssignableFrom(sender.getListenerEventClass())) {
					sender.removeListener(listener);
					if(sender.getListeners().length == 0)
						eventSenders.remove(sender);
					break;
				}
			}
		}
	}

	public void clearListeners() {
		synchronized(eventSendersLock) {
			eventSenders.clear();
		}
	}

	public void sendEvent(Event event) {
		synchronized(eventSendersLock) {
			for(EventSender sender : eventSenders) {
				Class<? extends Event> eventClass = sender
						.getListenerEventClass();
				if(eventClass.isInstance(event))
					sender.sendEvent(event);
			}
		}
	}

	public EventListener[] getListeners(Class<? extends Event> eventClass) {
		synchronized(eventSendersLock) {
			for(EventSender sender : eventSenders)
				if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
					return sender.getListeners();
			return new EventListener[0];
		}
	}
}

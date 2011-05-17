package org.darkstorm.tools.events;

import java.util.ArrayList;

class EventSender {
	private ArrayList<EventListener> listeners;
	private Class<? extends Event> listenerEventClass;
	private Object listenersLock;

	public EventSender(Class<? extends Event> listenerEventClass) {
		listeners = new ArrayList<EventListener>();
		this.listenerEventClass = listenerEventClass;
		listenersLock = new Object();
	}

	public boolean addListener(EventListener listener) {
		synchronized(listenersLock) {
			return listeners.add(listener);
		}
	}

	public boolean removeListener(EventListener listener) {
		synchronized(listenersLock) {
			return listeners.remove(listener);
		}
	}

	public EventListener[] getListeners() {
		return listeners.toArray(new EventListener[listeners.size()]);
	}

	public void sendEvent(Event event) {
		synchronized(listenersLock) {
			Class<?> eventClass = event.getClass();
			if(eventClass.isAssignableFrom(listenerEventClass))
				for(EventListener listener : listeners)
					listener.onEvent(event);
		}
	}

	public Class<? extends Event> getListenerEventClass() {
		return listenerEventClass;
	}
}

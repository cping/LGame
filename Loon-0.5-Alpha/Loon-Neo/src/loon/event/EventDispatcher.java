package loon.event;

import loon.utils.ListMap;
import loon.utils.TArray;

public class EventDispatcher {

	private ListMap<Integer, TArray<IEventListener>> _events = new ListMap<Integer, TArray<IEventListener>>();

	public EventDispatcher() {

	}

	public void dispatchEvent(int type) {
		dispatchEvent(type, null);
	}

	public void dispatchEvent(int type, Object data) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners == null) {
			return;
		}
		for (IEventListener listener : listeners) {
			listener.onReciveEvent(type, this, data);
		}
	}

	public void addEventListener(int type, IEventListener listener) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners == null) {
			listeners = new TArray<IEventListener>();
			_events.put(type, listeners);
		}
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	public void removeEventListener(int type, IEventListener listener) {
		TArray<IEventListener> listeners = _events.get(type);
		if (listeners != null) {
			listeners.remove(listener);
		}
	}
}

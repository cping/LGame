package loon.tea.dom;

import org.teavm.jso.JSObject;

public interface EventTargetWrapper extends JSObject {

	void addEventListener(String type, EventListenerWrapper listener);

	void addEventListener(String type, EventListenerWrapper listener, boolean capture);

	void removeEventListener(String type, EventListenerWrapper listener);

	void removeEventListener(String type, EventListenerWrapper listener, boolean capture);

	boolean dispatchEvent(EventWrapper event);
}

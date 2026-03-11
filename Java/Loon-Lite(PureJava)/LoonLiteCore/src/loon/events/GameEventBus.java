/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.events;

import loon.LRelease;
import loon.LSystem;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class GameEventBus<T> implements LRelease {

	private final ObjectMap<GameEventType, TArray<GameEventListener<T>>> listeners = new ObjectMap<GameEventType, TArray<GameEventListener<T>>>();

	private boolean loggingEnabled = false;

	public void subscribe(GameEventType type, GameEventListener<T> listener) {
		TArray<GameEventListener<T>> typeListeners = listeners.get(type);
		if (typeListeners == null) {
			typeListeners = new TArray<>();
			listeners.put(type, typeListeners);
		}
		typeListeners.add(listener);
	}

	public void unsubscribe(GameEventType type, GameEventListener<T> listener) {
		TArray<GameEventListener<T>> typeListeners = listeners.get(type);
		if (typeListeners != null) {
			typeListeners.remove(listener);
			if (typeListeners.isEmpty()) {
				listeners.remove(type);
			}
		}
	}

	public void publish(GameEvent<T> event) {
		TArray<GameEventListener<T>> typeListeners = listeners.get(event.evetype);
		if (typeListeners != null) {
			for (GameEventListener<T> listener : typeListeners) {
				((GameEventListener<T>) listener).onEvent(event);
			}
		}
		if (loggingEnabled) {
			LSystem.debug("[Event] [{0}] Source: {1}, Target: {2}", event.evetype,
					event.source.getClass().getSimpleName(),
					event.target != null ? event.target.getClass().getSimpleName() : "null");

		}
	}

	public void publishAll(TArray<GameEvent<T>> events) {
		for (GameEvent<T> event : events) {
			publish(event);
		}
	}

	public void setLoggingEnabled(boolean enabled) {
		this.loggingEnabled = enabled;
	}

	@Override
	public void close() {
		listeners.clear();
	}

}

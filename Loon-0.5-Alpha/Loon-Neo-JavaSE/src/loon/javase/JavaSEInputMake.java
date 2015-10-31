/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.javase;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import loon.LObject;
import loon.event.InputMake;
import loon.event.KeyMake;
import loon.event.MouseMake;
import loon.event.SysTouch;
import loon.event.TouchMake;
import loon.geom.Vector2f;
import loon.utils.reply.Port;

public class JavaSEInputMake extends InputMake {

	protected final JavaSEGame game;

	private final Deque<KeyMake.Event> kevQueue = new ConcurrentLinkedDeque<>();

	private boolean mouseDown;
	private Vector2f pivot;
	private float x, y;
	private int currentId;

	public JavaSEInputMake(JavaSEGame game) {
		this.game = game;
		if (game.setting.emulateTouch) {
			emulateTouch();
		}
	}

	public void postKey(long time, int keyCode, boolean pressed, char typedCh,
			int modFlags) {
		KeyMake.Event event = new KeyMake.KeyEvent(0, time, typedCh, keyCode,
				pressed);
		event.setFlag(modFlags);
		kevQueue.add(event);
	}

	public boolean convertImagesOnLoad = true;
	protected void emulateTouch() {
		keyboardEvents.connect(new Port<KeyMake.Event>() {
			public void onEmit(KeyMake.Event event) {
				if (event instanceof KeyMake.KeyEvent) {
					KeyMake.KeyEvent kevent = (KeyMake.KeyEvent) event;
					if (kevent.down) {
						pivot = new Vector2f(x, y);
					}
				}
			}
		});

		mouseEvents.connect(new Port<MouseMake.Event>() {
			public void onEmit(MouseMake.Event event) {
				MouseMake.ButtonEvent bevent = (MouseMake.ButtonEvent) event;
				if (bevent.button == SysTouch.LEFT) {
					if (mouseDown = bevent.down) {
						currentId += 2;
						dispatchTouch(event, TouchMake.Event.Kind.START);
					} else {
						pivot = null;
						dispatchTouch(event, TouchMake.Event.Kind.END);
					}
				}
				if (mouseDown) {
					dispatchTouch(event, TouchMake.Event.Kind.MOVE);
				}
				x = event.x;
				y = event.y;
			}
		});
	}

	@Override
	public boolean hasHardwareKeyboard() {
		return true;
	}

	@Override
	public boolean hasMouse() {
		return true;
	}

	@Override
	public boolean hasTouch() {
		return game.setting.emulateTouch;
	}

	void init() {
	}

	void update() {
		KeyMake.Event kev;
		while ((kev = kevQueue.poll()) != null) {
			keyboardEvents.emit(kev);
		}
	}

	private void dispatchTouch(MouseMake.Event event, TouchMake.Event.Kind kind) {
		float ex = event.x, ey = event.y;
		TouchMake.Event main = toTouch(event.time, ex, ey, kind, 0);
		TouchMake.Event[] evs = (pivot == null) ? new TouchMake.Event[] { main }
				: new TouchMake.Event[] {
						main,
						toTouch(event.time, 2 * pivot.x - ex, 2 * pivot.y - ey,
								kind, 1) };
		touchEvents.emit(evs);
	}

	private TouchMake.Event toTouch(double time, float x, float y,
			TouchMake.Event.Kind kind, int idoff) {
		return new TouchMake.Event(0, time, x, y, kind, currentId + idoff);
	}

	@Override
	public void callback(LObject o) {

	}
}

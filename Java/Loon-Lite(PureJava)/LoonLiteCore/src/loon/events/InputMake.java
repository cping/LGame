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
package loon.events;

import loon.LObject;
import loon.utils.reply.Act;

public abstract class InputMake {

	public LObject<?> tag;

	public boolean mouseEnabled = true;

	public boolean touchEnabled = true;

	public boolean keyboardEnabled = true;

	public Act<MouseMake.Event> mouseEvents = Act.create();

	public Act<TouchMake.Event[]> touchEvents = Act.create();

	public Act<KeyMake.Event> keyboardEvents = Act.create();

	public boolean hasMouse() {
		return false;
	}

	public boolean hasTouch() {
		return false;
	}

	public boolean hasHardwareKeyboard() {
		return false;
	}

	public boolean hasMouseLock() {
		return false;
	}

	public boolean isMouseLocked() {
		return false;
	}

	public void setMouseLocked(boolean locked) {
	}


	protected int modifierFlags(boolean altP, boolean ctrlP, boolean metaP,
			boolean shiftP) {
		return Event.InputEvent.modifierFlags(altP, ctrlP, metaP, shiftP);
	}

	protected void emitKeyPress(double time, int keyCode, char keyChar,
			boolean down, int flags) {
		KeyMake.KeyEvent event = new KeyMake.KeyEvent(0, time, keyChar,
				keyCode, down);
		event.setFlag(flags);
		keyboardEvents.emit(event);
	}

	protected void emitMouseButton(double time, float x, float y, int btnid,
			boolean down, int flags) {
		MouseMake.ButtonEvent event = new MouseMake.ButtonEvent(0, time, x, y,
				btnid, down);
		event.setFlag(flags);
		mouseEvents.emit(event);
	}


	public abstract void callback(LObject<?> o);


}

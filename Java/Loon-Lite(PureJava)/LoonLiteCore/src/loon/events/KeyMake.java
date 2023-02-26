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

import loon.utils.reply.Port;

public abstract class KeyMake {

	public static enum TextType {
		DEFAULT, NUMBER, EMAIL, URL;
	}
	
	public static class Event extends loon.events.Event.Input {
		public char keyChar;

		protected Event(int flags, char ch, double time) {
			super(flags, time);
			this.keyChar = ch;
		}
	}

	public static class KeyEvent extends Event {

		public final int keyCode;

		public final boolean down;

		public KeyEvent(int flags, double time, char keyChar, int keyCode,
				boolean down) {
			super(flags, keyChar, time);
			this.keyCode = keyCode;
			this.down = down;
		}

		@Override
		protected String name() {
			return "Key";
		}

		@Override
		protected void addFields(StringBuilder builder) {
			super.addFields(builder);
			builder.append(", keyCode=").append(keyCode).append(", down=")
					.append(down);
		}
	}

	public static abstract class KeyPort extends Port<Event> {
		public void onEmit(Event event) {
			if (event instanceof KeyEvent)
				onEmit((KeyEvent) event);
		}

		public abstract void onEmit(KeyEvent event);
	}

}

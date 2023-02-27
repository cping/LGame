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

import loon.utils.StrBuilder;

public class TouchMake {

	public static class Event extends loon.events.Event.XYEvent {

		public static enum Kind {
			START(true, false), MOVE(false, false), END(false, true), CANCEL(
					false, true);
			public final boolean isStart, isEnd;

			Kind(boolean isStart, boolean isEnd) {
				this.isStart = isStart;
				this.isEnd = isEnd;
			}
		};

		public final Kind kind;

		public final int id;

		public final float pressure;

		public final float size;

		public Event(int flags, double time, float x, float y, Kind kind, int id) {
			this(flags, time, x, y, kind, id, -1, -1);
		}

		public Event(int flags, double time, float x, float y, Kind kind,
				int id, float pressure, float size) {
			super(flags, time, x, y);
			this.kind = kind;
			this.id = id;
			this.pressure = pressure;
			this.size = size;
		}

		@Override
		protected String name() {
			return "Touch";
		}

		@Override
		protected void addFields(StrBuilder builder) {
			super.addFields(builder);
			builder.append(", kind=").append(kind).append(", id=").append(id)
					.append(", pressure=").append(pressure).append(", size=")
					.append(size);
		}
	}
}

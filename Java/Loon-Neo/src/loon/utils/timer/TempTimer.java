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
package loon.utils.timer;

import loon.LSystem;
import loon.Screen;
import loon.utils.MathUtils;
import loon.utils.reply.Port;

/**
 * 一个静态的临时时间存储对象,用于全局缓存计时
 */
public class TempTimer {

	private static float _savedTime;

	private static float _time;

	private static Port<LTimerContext> _port;

	public static void reset() {
		_time = 0f;
	}

	public static void set(float timer) {
		_time = timer;
	}

	public static void saved(float timer) {
		_savedTime = timer;
	}

	public static void saved() {
		saved(_time);
	}

	public static void update(LTimerContext context) {
		update(context.getMilliseconds());
	}

	public static void update(float dt) {
		_time += dt;
	}

	public static void bindScreen() {
		if (LSystem.getProcess() != null) {
			bindScreen(LSystem.getProcess().getScreen());
		}
	}

	public static void unbindScreen() {
		if (LSystem.getProcess() != null) {
			unbindScreen(LSystem.getProcess().getScreen());
		}
	}

	public static void unbindScreen(Screen screen) {
		if (screen == null) {
			return;
		}
		if (_port != null) {
			screen.remove(_port);
			_port = null;
		}
	}

	public static void bindScreen(Screen screen) {
		if (screen == null) {
			return;
		}
		reset();
		if (_port == null) {
			_port = new Port<LTimerContext>() {
				@Override
				public void onEmit(LTimerContext clock) {
					update(clock);
				}
			};
			screen.add(_port, true);
		}
	}

	public static float getSaveTime() {
		return _savedTime;
	}

	public static float getTime() {
		return _time;
	}

	public static float getSinus() {
		return MathUtils.sin((_time * 3.141593f));
	}

	public static float getSinus(float mult) {
		return MathUtils.sin(((_time * 3.141593f) * mult));
	}

}

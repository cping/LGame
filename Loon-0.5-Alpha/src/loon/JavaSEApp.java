/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon;

import java.awt.Canvas;

import loon.core.CallQueue;

//桌面加载用类(需要lwjgl.jar支持)
public abstract class JavaSEApp extends CallQueue {

	protected Canvas _AWT_Canvas;

	protected static Class<?> getType(Object o) {

		if (o instanceof Integer) {
			return Integer.TYPE;
		} else if (o instanceof Float) {
			return Float.TYPE;
		} else if (o instanceof Double) {
			return Double.TYPE;
		} else if (o instanceof Long) {
			return Long.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Short) {
			return Short.TYPE;
		} else if (o instanceof Boolean) {
			return Boolean.TYPE;
		} else {
			return o.getClass();
		}
	}

	public Canvas getAWTCanvas() {
		return _AWT_Canvas;
	}

	public void setAWTCanvas(Canvas canvas) {
		_AWT_Canvas = canvas;
	}

	public void exit() {

	}

	public abstract void onPause();

	public abstract void onResume();

	public abstract void onExit();
}

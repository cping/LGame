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
package loon.android;

import android.util.Log;

public class AndroidLog extends loon.Log {

	private final String logMes;

	public AndroidLog(String log) {
		this.logMes = log;
	}

	@Override
	protected void callNativeLog(Level level, String msg, Throwable e) {
		if (e == null) {
			if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
				Log.d(logMes + "-" + level, msg);
			} else if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
				Log.w(logMes + "-" + level, msg);
			} else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id) {
				Log.e(logMes + "-" + level, msg);
			} else {
				Log.i(logMes + "-" + level, msg);
			}
		} else {
			if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
				Log.d(logMes + "-" + level, msg, e);
			} else if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
				Log.w(logMes + "-" + level, msg, e);
			} else if (level.id == Level.ALL.id || level.id <= Level.ERROR.id) {
				Log.e(logMes + "-" + level, msg, e);
			} else {
				Log.i(logMes + "-" + level, msg, e);
			}
			if (e != null) {
				e.printStackTrace(System.out);
			}
		}
	}

	@Override
	public void onError(Throwable e) {
		// stop the game repaint
		// LSystem.AUTO_REPAINT = false;
	}
}

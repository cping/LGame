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
package loon;

import loon.canvas.LColor;
import loon.utils.StringUtils;

public abstract class Log {

	public abstract void onError(Throwable e);

	public static class Level {

		public static final Level ALL = new Level("All", 0);

		public static final Level DEBUG = new Level("Debug", 1);

		public static final Level INFO = new Level("Info", 2);

		public static final Level WARN = new Level("Warn", 3);

		public static final Level ERROR = new Level("Error", 4);

		public static final Level IGNORE = new Level("Ignore", 5);

		public final String levelString;

		public final int id;

		private Level(String levelString, int levelInt) {
			this.levelString = levelString;
			this.id = levelInt;
		}

		@Override
		public String toString() {
			return levelString;
		}

		public int toType() {
			return id;
		}
	}

	public static interface Collector {
		void logged(Level level, String msg, Throwable e);
	}

	private Collector _collector;

	private Level _minLevel = Level.DEBUG;

	private LColor _defaultColor = LColor.white;

	private LColor _otherColor = LColor.red;

	public void setDefaultColor(LColor c) {
		_defaultColor = c;
	}

	public void setOtherColor(LColor c) {
		_otherColor = c;
	}

	public LColor getDefaultColor() {
		return _defaultColor;
	}

	public LColor getOtherColor() {
		return _otherColor;
	}

	public void setCollector(Collector collector) {
		this._collector = collector;
	}

	public void setMinLevel(Level level) {
		_minLevel = level;
	}

	public void debug(String msg) {
		debug(msg, (Throwable) null);
	}

	public void debug(String msg, Object... args) {
		debug(format(msg, args), (Throwable) null);
	}

	public void debug(String msg, Throwable e) {
		call(Level.DEBUG, msg, e);
	}

	public void info(String msg) {
		info(msg, (Throwable) null);
	}

	public void info(String msg, Object... args) {
		info(format(msg, args), (Throwable) null);
	}

	public void info(String msg, Throwable e) {
		call(Level.INFO, msg, e);
	}

	public void warn(String msg) {
		warn(msg, (Throwable) null);
	}

	public void warn(String msg, Object... args) {
		warn(format(msg, args), (Throwable) null);
	}

	public void warn(String msg, Throwable e) {
		call(Level.WARN, msg, e);
	}

	public void error(String msg) {
		error(msg, (Throwable) null);
	}

	public void error(String msg, Object... args) {
		error(format(msg, args), (Throwable) null);
	}

	public void error(String msg, Throwable e) {
		call(Level.ERROR, msg, e);
	}

	protected String format(String msg, Object[] args) {
		if (args == null) {
			return msg;
		}
		return StringUtils.format(msg, args);
	}

	protected void call(Level level, String msg, Throwable e) {
		if (LSystem.isConsoleLog()) {
			if (_collector != null) {
				_collector.logged(level, msg, e);
			}
			if (level.id >= _minLevel.id) {
				callNativeLog(level, msg, e);
				LGame game = LSystem.base();
				if (game != null) {
					LSetting setting = game.setting;
					Display display = game.displayImpl;
					if (display != null && (setting.isDebug || setting.isDisplayLog)) {
						LColor color = _defaultColor;
						if (level.id > Level.INFO.id) {
							color = _otherColor;
						}
						if (display != null) {
							if (e == null) {
								display.addLog(msg, color);
							} else {
								display.addLog(msg + " [ " + e.getMessage() + " ] ", color);
							}
						}

					}
				}
			}
			if (e != null) {
				onError(e);
			}
		}
	}

	protected abstract void callNativeLog(Level level, String msg, Throwable e);
}

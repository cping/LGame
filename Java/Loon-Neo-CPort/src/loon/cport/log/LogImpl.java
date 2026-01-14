/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package loon.cport.log;

import loon.LSystem;
import loon.Log.Level;

final public class LogImpl {

	public void exception(Object o) {
		System.err.println(o);
	}

	public void debugWrite(String text) {
		System.out.println(text);
	}

	private static final int MAX_LOG_MESSAGES = 128;

	private static LogMessage[] store;

	private static int oldestMessageIndex;

	private static int newestMessageIndex;

	private Level level = Level.INFO;

	private LogFormat logFormat;

	private String app;

	static {
		clear();
	}

	LogImpl(Class<?> clazz) {
		this(LSystem.getExtension(clazz.getName()), 0);
	}

	LogImpl(String app, int type) {
		this.logFormat = new LogFormat(true, type);
		this.app = app;
		this.level = Level.ALL;
	}

	public Level getLogLevel() {
		return level;
	}

	/**
	 * 设定当前日志等级
	 *
	 * @param level
	 */
	public void setLevel(int level) {
		if (level == Level.DEBUG.id) {
			this.level = Level.DEBUG;
		} else if (level == Level.INFO.id) {
			this.level = Level.INFO;
		} else if (level == Level.WARN.id) {
			this.level = Level.WARN;
		} else if (level == Level.ERROR.id) {
			this.level = Level.ERROR;
		} else if (level == Level.IGNORE.id) {
			this.level = Level.IGNORE;
		} else if (level == Level.ALL.id) {
			this.level = Level.ALL;
		} else {
			throw new IllegalArgumentException("Levels of error messages !");
		}
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public boolean isVisible() {
		return logFormat.isShow();
	}

	public void setVisible(boolean show) {
		logFormat.setShow(show);
	}

	public void hide() {
		setVisible(false);
	}

	public void show() {
		setVisible(true);
	}

	public void d(String message) {
		if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
			addLogMessage(message, Level.DEBUG, null);
		}
	}

	public void d(String message, Throwable tw) {
		if (level.id == Level.ALL.id || level.id <= Level.DEBUG.id) {
			addLogMessage(message, Level.DEBUG, tw);
		}
	}

	public void i(String message) {
		if (level.id == Level.ALL.id || level.id <= Level.INFO.id) {
			addLogMessage(message, Level.INFO, null);
		}
	}

	public void i(String message, Throwable tw) {
		if (level.id <= Level.INFO.id) {
			addLogMessage(message, Level.INFO, tw);
		}
	}

	public void w(String message) {
		if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
			addLogMessage(message, Level.WARN, null);
		}
	}

	public void w(String message, Throwable tw) {
		if (level.id == Level.ALL.id || level.id <= Level.WARN.id) {
			addLogMessage(message, Level.WARN, tw);
		}
	}

	public void e(String message) {
		if (level.id == Level.ALL.id || level.id <= Level.ERROR.id) {
			addLogMessage(message, Level.ERROR, null);
		}
	}

	public void e(String message, Throwable tw) {
		if (level.id <= Level.ERROR.id) {
			addLogMessage(message, Level.ERROR, tw);
		}
	}

	public LogFormat getLogFormat() {
		return logFormat;
	}

	public boolean isDebugEnabled() {
		return level.id <= Level.DEBUG.id;
	}

	public boolean isInfoEnabled() {
		return level.id <= Level.INFO.id;
	}

	/**
	 * 添加对应的日志记录
	 *
	 * @param message
	 * @param level
	 * @param throwable
	 */
	public void addLogMessage(String message, Level level, Throwable throwable) {
		if (message == null) {
			message = "";
		}
		String text = message;
		if (throwable != null) {
			text += " " + throwable.toString();
		}
		newestMessageIndex = (newestMessageIndex + 1) % MAX_LOG_MESSAGES;
		if (newestMessageIndex == oldestMessageIndex) {
			store[newestMessageIndex].setLogMessage(level, text);
			oldestMessageIndex = (oldestMessageIndex + 1) % MAX_LOG_MESSAGES;
		} else {
			store[newestMessageIndex] = new LogMessage(level, text);
			if (oldestMessageIndex < 0) {
				oldestMessageIndex = 0;
			}
		}
		LogMessage log = store[newestMessageIndex];
		logFormat.out(log.time, app, log.level.levelString, log.message);
		if (throwable != null) {
			throwable.printStackTrace(System.err);
		}
	}

	public  LogMessage[] getLogMessages() {
		int numberOfMessages;
		if (newestMessageIndex < 0) {
			numberOfMessages = 0;
		} else if (newestMessageIndex >= oldestMessageIndex) {
			numberOfMessages = newestMessageIndex - oldestMessageIndex + 1;
		} else {
			numberOfMessages = MAX_LOG_MESSAGES;
		}
		LogMessage[] copy = new LogMessage[numberOfMessages];
		for (int i = 0; i < numberOfMessages; i++) {
			int index = newestMessageIndex - i;
			if (index < 0) {
				index = MAX_LOG_MESSAGES + index;
			}
			copy[numberOfMessages - i - 1] = store[index];
		}
		return copy;
	}

	public static  void clear() {
		oldestMessageIndex = -1;
		newestMessageIndex = -1;
		store = new LogMessage[MAX_LOG_MESSAGES];
	}

}

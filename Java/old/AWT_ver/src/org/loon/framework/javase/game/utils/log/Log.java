package org.loon.framework.javase.game.utils.log;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.utils.FileUtils;

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
final public class Log {

	private static final int MAX_LOG_MESSAGES = 25;

	private static LogMessage[] store;

	private static int oldestMessageIndex;

	private static int newestMessageIndex;

	private Level level = Level.INFO;

	private LogFormat logFormat;

	private String app;

	static {
		clear();
	}

	Log(Class<?> clazz) {
		this(FileUtils.getExtension(clazz.getName()));
	}

	Log(String app) {
		// 以下参数分别为"保存位置,是否在控制台显示,是否保存为本地文件,是否以标准状态输出"
		this.logFormat = new LogFormat(LSystem.LOG_FILE, true, false, true);
		this.app = app;
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
		if (level == Level.DEBUG.level) {
			this.level = Level.DEBUG;
		} else if (level == Level.INFO.level) {
			this.level = Level.INFO;
		} else if (level == Level.WARN.level) {
			this.level = Level.WARN;
		} else if (level == Level.ERROR.level) {
			this.level = Level.ERROR;
		} else if (level == Level.IGNORE.level) {
			this.level = Level.IGNORE;
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

	public boolean isSave() {
		return logFormat.isSave();
	}

	public void setSave(boolean save) {
		logFormat.setSave(save);
	}

	public String getFileName() {
		return logFormat.getFileName();
	}

	public void setFileName(String fileName) {
		logFormat.setFileName(fileName);
		setSave(true);
	}

	public void hide() {
		setVisible(false);
		setSave(false);
	}

	public void show() {
		setVisible(true);
		setSave(true);
	}

	public void log(String message) {
		addLogMessage(message, level, null);
	}

	public void log(String message, Throwable tw) {
		addLogMessage(message, level, tw);
	}

	public void debug(String message) {
		if (level.level <= Level.DEBUG.level) {
			addLogMessage(message, Level.DEBUG, null);
		}
	}

	public void debug(String message, Throwable tw) {
		if (level.level <= Level.DEBUG.level) {
			addLogMessage(message, Level.DEBUG, tw);
		}
	}

	public void info(String message) {
		if (level.level <= Level.INFO.level) {
			addLogMessage(message, Level.INFO, null);
		}
	}

	public void info(String message, Throwable tw) {
		if (level.level <= Level.INFO.level) {
			addLogMessage(message, Level.INFO, tw);
		}
	}

	public void warn(String message) {
		if (level.level <= Level.WARN.level) {
			addLogMessage(message, Level.WARN, null);
		}
	}

	public void warn(String message, Throwable tw) {
		if (level.level <= Level.WARN.level) {
			addLogMessage(message, Level.WARN, tw);
		}
	}

	public void error(String message) {
		if (level.level <= Level.ERROR.level) {
			addLogMessage(message, Level.ERROR, null);
		}
	}

	public void error(String message, Throwable tw) {
		if (level.level <= Level.ERROR.level) {
			addLogMessage(message, Level.ERROR, tw);
		}
	}

	public LogFormat getLogFormat() {
		return logFormat;
	}

	public boolean isDebugEnabled() {
		return level.level <= Level.DEBUG.level;
	}

	public boolean isInfoEnabled() {
		return level.level <= Level.INFO.level;
	}

	/**
	 * 添加对应的日志记录
	 * 
	 * @param message
	 * @param level
	 * @param throwable
	 */
	private synchronized void addLogMessage(String message, Level level,
			Throwable throwable) {
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
	}

	public synchronized LogMessage[] getLogMessages() {
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

	public static synchronized void clear() {
		oldestMessageIndex = -1;
		newestMessageIndex = -1;
		store = new LogMessage[MAX_LOG_MESSAGES];
	}


}

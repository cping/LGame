package org.loon.framework.javase.game.utils.log;

import java.text.SimpleDateFormat;
import java.util.Date;

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
public class LogMessage {

	static private String LOG_DEFAULT_DATE = "yyyy-MM-dd HH:mm:ss,SSS";

	static private SimpleDateFormat LOG_DEFAULT_DATE_FORMAT = new SimpleDateFormat(
			LOG_DEFAULT_DATE);

	static private Date date = new Date();

	public Level level;

	public String time;

	public String message;

	protected LogMessage(Level level, String message) {
		setLogMessage(level, message);
	}

	protected void setLogMessage(Level level, String message) {
		this.level = level;
		this.message = message;
		date.setTime(System.currentTimeMillis());
		this.time = LOG_DEFAULT_DATE_FORMAT.format(date);
	}

	public Level getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public String getTime() {
		return time;
	}

	public String toString() {
		return (time + " [" + level + "] " + message).intern();
	}

}

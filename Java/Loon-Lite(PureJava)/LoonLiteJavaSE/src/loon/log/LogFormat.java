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
package loon.log;

import loon.LSystem;

public class LogFormat {

	final static private int TIME_INDEX = 0;

	final static private int APP_INDEX = 1;

	final static private int MODULE_INDEX = 2;

	final static private int MESSAGE_INDEX = 3;

	final static private String[] LOG_TITLE = { "time", "app", "module", "message" };

	final static private String[] LOG_TAG = { "-", "-", "-", "-" };

	private int limitTagSize;

	private int count;

	private String logMsg;

	private boolean show;

	protected final int[] logTypeStyle;

	protected int logType;

	public LogFormat(boolean s, int t) {
		this(s, t, 25, 15, 7, 256, 64);
	}

	public LogFormat(boolean s, int t, int timeSize, int appSize, int moduleSize, int messageSize, int maxTagSize) {
		this.show = s;
		this.logType = t;
		this.limitTagSize = maxTagSize;
		this.logTypeStyle = new int[MESSAGE_INDEX + 1];
		logTypeStyle[TIME_INDEX] = timeSize;
		logTypeStyle[APP_INDEX] = appSize;
		logTypeStyle[MODULE_INDEX] = moduleSize;
		logTypeStyle[MESSAGE_INDEX] = messageSize;
	}

	private String formatString(String str[], String pad, String sp) {
		return formatString(str, pad, sp, true);
	}

	private String formatString(String str[], String pad, String sp, boolean tag) {
		StringBuffer sbr = new StringBuffer();
		if (tag) {
			for (int i = 0; i < str.length; i++) {
				int size = str[i].length();
				if (size > logTypeStyle[i] || size > limitTagSize) {
					sbr.append(str[i].substring(0, logTypeStyle[i]) + sp);
					continue;
				}
				sbr.append(str[i]);
				for (int j = size; j < logTypeStyle[i] && j < limitTagSize; j++) {
					sbr.append(pad);
				}
				sbr.append(sp);
			}
		} else {
			for (int i = 0; i < str.length; i++) {
				if (str[i].length() > logTypeStyle[i]) {
					sbr.append(str[i].substring(0, logTypeStyle[i]) + sp);
					continue;
				}
				sbr.append(str[i]);
				for (int j = str[i].length(); j < logTypeStyle[i]; j++) {
					sbr.append(pad);
				}
				sbr.append(sp);
			}
		}
		return sbr.toString();
	}

	public synchronized void title(int flag, String msg) {
		switch (flag) {
		case 0:
			System.out.print(msg);
			break;
		case 1:
			System.err.print(msg);
			break;
		}
	}

	public synchronized void out(String msg) {
		if (!show) {
			return;
		}
		title(logType, msg);
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public int getLimitTagSize() {
		return limitTagSize;
	}

	public void setLimitTagSize(int tagSize) {
		this.limitTagSize = tagSize;
	}

	public synchronized void out(String tm, String app, String level, String msg) {
		String value[] = { tm, app, level, msg };
		if (count++ % 9999 == 0) {
			logMsg = new StringBuffer(formatString(LOG_TAG, "-", " ")).append(LSystem.LS)
					.append(formatString(LOG_TITLE, " ", " ")).append(LSystem.LS)
					.append(formatString(LOG_TAG, "-", " ")).append(LSystem.LS).append(formatString(value, " ", " "))
					.append(LSystem.LS).toString();
		} else {
			logMsg = formatString(value, " ", " ", false) + LSystem.LS;
		}
		out(logMsg);
	}

}

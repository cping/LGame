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
import loon.cport.bridge.SDLCall;

public class LogFormat {

	private final static int TIME_INDEX = 0;

	private final static int APP_INDEX = 1;

	private final static int MODULE_INDEX = 2;

	private final static int MESSAGE_INDEX = 3;

	private final static String[] LOG_TITLE = { "time", "app", "module", "message" };

	private final static String[] LOG_TAG = { "-", "-", "-", "-" };

	private final StringBuffer context = new StringBuffer();

	protected final int[] logTypeStyle;

	private int limitTagSize;

	private int count;

	private int newLineCount;

	private String logMsg;

	private boolean show;

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
		context.setLength(0);
		if (tag) {
			for (int i = 0; i < str.length; i++) {
				int size = str[i].length();
				if (size > logTypeStyle[i] || size > limitTagSize) {
					context.append(str[i].substring(0, logTypeStyle[i]) + sp);
					continue;
				}
				context.append(str[i]);
				for (int j = size; j < logTypeStyle[i] && j < limitTagSize; j++) {
					context.append(pad);
				}
				context.append(sp);
			}
		} else {
			for (int i = 0; i < str.length; i++) {
				if (str[i].length() > logTypeStyle[i]) {
					context.append(str[i].substring(0, logTypeStyle[i]) + sp);
					continue;
				}
				context.append(str[i]);
				for (int j = str[i].length(); j < logTypeStyle[i]; j++) {
					context.append(pad);
				}
				context.append(sp);
			}
		}
		return context.toString();
	}

	public void out(String msg) {
		if (!show) {
			return;
		}
		SDLCall.logPrintln(msg);
		newLineCount++;
	}

	public int getNewLineCount() {
		return newLineCount;
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

	public void out(String tm, String app, String level, String msg) {
		if (msg != null && msg.length() > 0) {
			final String values[] = { tm, app, level, msg };
			if (count++ % 9999 == 0) {
				logMsg = new StringBuffer(formatString(LOG_TAG, "-", " ")).append(LSystem.LS)
						.append(formatString(LOG_TITLE, " ", " ")).append(LSystem.LS)
						.append(formatString(LOG_TAG, "-", " ")).append(LSystem.LS).toString();
				out(logMsg);
				out(formatString(values, " ", " ") + LSystem.LS);
				newLineCount = 0;
				return;
			} else {
				logMsg = formatString(values, " ", " ", false);
			}
			out(logMsg);
		}
	}

}

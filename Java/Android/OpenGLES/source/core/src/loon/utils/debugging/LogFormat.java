package loon.utils.debugging;

import loon.core.LSystem;

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
public class LogFormat {

	final static private int LOG_LEN[] = { 24, 15, 7, 100 };

	final static private String LOG_TITLE[] = { "time", "app", "module",
			"message" };

	final static private String LOG_TAG[] = { "-", "-", "-", "-" };

	private int count;

	public int Type;

	private String logMsg;

	private boolean show;

	public LogFormat(boolean s, int t) {
		this.show = s;
		this.Type = t;
	}

	private static String formatString(String str[], String pad, String sp) {
		StringBuffer sbr = new StringBuffer();
		for (int i = 0; i < str.length; i++) {
			if (str[i].length() > LOG_LEN[i]) {
				sbr.append(str[i].substring(0, LOG_LEN[i]) + sp);
				continue;
			}
			sbr.append(str[i]);
			for (int j = str[i].length(); j < LOG_LEN[i]; j++) {
				sbr.append(pad);
			}
			sbr.append(sp);
		}
		return sbr.toString();
	}

	public synchronized void title(int flag, String msg) {
		switch (flag) {
		case 0:
			android.util.Log.i("info", msg);
			break;
		case 1:
			android.util.Log.e("err", msg);
			break;
		}
	}

	public synchronized void out(String msg) {
		if (!show) {
			return;
		}
		title(Type, msg);
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public synchronized void out(String tm, String app, String level, String msg) {
		String value[] = { tm, app, level, msg };
		if (count++ % 9999 == 0) {
			logMsg = new StringBuffer(formatString(LOG_TAG, "-", " "))
					.append(LSystem.LS)
					.append(formatString(LOG_TITLE, " ", " "))
					.append(LSystem.LS).append(formatString(LOG_TAG, "-", " "))
					.append(LSystem.LS).append(formatString(value, " ", " "))
					.append(LSystem.LS).toString();
		} else {
			logMsg = formatString(value, " ", " ") + LSystem.LS;
		}
		out(logMsg);
	}

}

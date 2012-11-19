package org.loon.framework.javase.game.utils.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;

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
public class LogFormat {

	final static private int LOG_LEN[] = { 24, 15, 7, 100 };

	final static private String LOG_TITLE[] = { "time", "app", "module",
			"message" };

	final static private String LOG_TAG[] = { "-", "-", "-", "-" };

	final static private long LOG_DEFAULT_KB = 1024 * 1024 * 10;

	private int count;

	private String logMsg, fileName;

	private boolean show, save, flag;

	private File file;

	public LogFormat(String fileName, boolean show, boolean save, boolean flag) {
		this.show = show;
		this.save = save;
		this.flag = flag;
		this.setFileName(fileName);
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
		PrintStream out = null;
		switch (flag) {
		case 0:
			out = System.out;
			out.print(msg);
			break;
		case 1:
			out = System.err;
			out.print(msg);
			break;
		}
	}

	public synchronized void out(String msg) {
		if (!show) {
			return;
		}
		title(flag ? 0 : 1, msg);
	}

	public synchronized void save(String msg) {
		if (!save) {
			return;
		}
		try {
			Writer out = new OutputStreamWriter(
					new FileOutputStream(file, true), LSystem.encoding);
			out.write(msg);
			out.flush();
			out.close();
		} catch (Exception e) {
		}
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public boolean isSave() {
		return save;
	}

	public void setSave(boolean save) {
		this.save = save;
		if (save) {
			this.file = new File(fileName);
			try {
				FileUtils.makedirs(file);
			} catch (IOException e) {
			}
			long filekb = FileUtils.getKB(file);
			if (filekb > LOG_DEFAULT_KB) {
				file.delete();
			}
		}
	}

	public boolean isShow() {
		return show;
	}

	public void setShow(boolean show) {
		this.show = show;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public synchronized void out(String tm, String app, String level, String msg) {
		String value[] = { tm, app, level, msg };
		if (count++ % 9999 == 0) {
			logMsg = new StringBuffer(formatString(LOG_TAG, "-", " ")).append(
					LSystem.LS).append(formatString(LOG_TITLE, " ", " "))
					.append(LSystem.LS).append(formatString(LOG_TAG, "-", " "))
					.append(LSystem.LS).append(formatString(value, " ", " "))
					.append(LSystem.LS).toString();
		} else {
			logMsg = formatString(value, " ", " ") + LSystem.LS;
		}
		out(logMsg);
		save(logMsg);
	}

}

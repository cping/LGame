package loon.build.sys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Log {

	final static Map<String, Log> cache = new HashMap<String, Log>();

	public static boolean stdout = true;

	public static boolean debug = true;

	public static Log getLog(String name) {
		Log log = cache.get(name);
		if (log == null) {
			log = new Log(name, "log-" + name + ".log");
			cache.put(name, log);
		}
		return log;
	}

	private PrintWriter out;
	private SimpleDateFormat time;
	private Date now = new Date();

	public static PrintWriter getWriter() {
		Log a = Log.getLog(LSystem.PROJECT);
		return new PrintWriter(a.out) {
			@Override
			public void write(String str) {
				write(str, 0, str.length());
				if (stdout)
					System.out.print(str);
			}

			@Override
			public void println() {
				write("\n");
			}
		};
	}

	private Log(String name, String fn) {
		try {
			File f = new File(fn);
			if (debug) {
				System.out
						.println("Log " + name + ":\n" + f.getCanonicalPath());
			}
			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
					f, true), "utf8"), true);
			time = new SimpleDateFormat("H:m:s");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void logTo(String name, Object msg) {
		Log.getLog(name).log0(msg);
	}

	public static void logTo(String name, Object msg, Throwable t) {
		Log.getLog(name).log0(msg, t);
	}

	public static void log(Object msg) {
		Log.getLog(LSystem.PROJECT).log0(msg);
	}

	public static void log(Object msg, Throwable t) {
		Log.getLog(LSystem.PROJECT).log0(msg, t);
	}

	public synchronized void log0(Object o, Throwable t) {
		if (out == null || o == null) {
			return;
		}
		String s0 = o.toString();
		if (!debug && s0.startsWith("[Debug]"))
			return;
		try {
			now.setTime(System.currentTimeMillis());
			StringBuilder sbr = new StringBuilder();
			sbr.append(time.format(now)).append(" ").append(s0);
			if (t == null) {
				sbr.append("\r\n");
			} else {
				sbr.append(", Error:\r\n");
			}
			out.write(sbr.toString());
			if (t != null) {
				t.printStackTrace(out);
			}
			out.flush();
			if (stdout) {
				System.out.print(sbr.toString());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public synchronized void log0(Object o) {
		log0(o, null);
	}
}

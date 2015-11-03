package loon.web.server.mini;

import java.lang.reflect.Method;

public class Browse {

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osIsWindowsXP;

	final static private boolean osIsWindows2003;

	final static private boolean osBit64;

	final static public String OS_NAME;

	final static public int JAVA_13 = 0;

	final static public int JAVA_14 = 1;

	final static public int JAVA_15 = 2;

	final static public int JAVA_16 = 3;

	final static public int JAVA_17 = 4;

	final static public int JAVA_18 = 5;

	final static public int JAVA_19 = 6;

	static {
		OS_NAME = System.getProperty("os.name").toLowerCase();
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		osIsWindowsXP = OS_NAME.startsWith("Windows")
				&& (OS_NAME.compareTo("5.1") >= 0);
		osIsWindows2003 = "windows 2003".equals(OS_NAME);
		osBit64 = System.getProperty("os.arch").equals("amd64");
	}

	public static boolean isLinux() {
		return osIsLinux;
	}

	public static boolean isMacOS() {
		return osIsMacOs;
	}

	public static boolean isUnix() {
		return osIsUnix;
	}

	public static boolean isWindows() {
		return osIsWindows;
	}

	public static boolean isWindowsXP() {
		return osIsWindowsXP;
	}

	public static boolean isWindows2003() {
		return osIsWindows2003;
	}

	public static boolean isBit64() {
		return osBit64;
	}

	private static void browse(String url) throws Exception {
		if (isMacOS()) {
			Class<?> fileMgr = Class.forName("com.apple.eio.FileManager");
			Method openURL = fileMgr.getDeclaredMethod("openURL",
					new Class[] { String.class });
			openURL.invoke(null, new Object[] { url });
		} else if (isWindows()) {
			Runtime.getRuntime().exec(
					"rundll32 url.dll,FileProtocolHandler " + url);
		} else {
			String[] browsers = { "google-chrome", "firefox", "mozilla",
					"opera", "epiphany", "konqueror", "netscape", "links",
					"lynx", "epiphany", "conkeror", "midori", "kazehakase" };
			String browser = null;
			for (int count = 0; count < browsers.length && browser == null; count++) {
				if (Runtime.getRuntime()
						.exec(new String[] { "which", browsers[count] })
						.waitFor() == 0) {
					browser = browsers[count];
				}
			}
			if (browser == null) {
				throw new Exception("Could not find web browser");
			} else {
				Runtime.getRuntime().exec(new String[] { browser, url });
			}
		}
	}

	public static void openURL(String url) {
		try {
			java.net.URI uri = new java.net.URI(url);
			java.awt.Desktop.getDesktop().browse(uri);
		} catch (Throwable e) {
			try {
				browse(url);
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

}

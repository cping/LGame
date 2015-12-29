package loon.build.sys;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/*
 * 此类用来处理JDK项目的构建
 */
public class JDK {

	final static private boolean osIsLinux;

	final static private boolean osIsUnix;

	final static private boolean osIsMacOs;

	final static private boolean osIsWindows;

	final static private boolean osIsX86;

	final static private boolean osBit64;

	final static public String OS_NAME;

	final static public String OS_ARCH;

	private boolean jdk;

	static {
		OS_NAME = System.getProperty("os.name").toLowerCase();
		OS_ARCH = System.getProperty("os.arch");
		osIsLinux = OS_NAME.indexOf("linux") != -1;
		osIsUnix = OS_NAME.indexOf("nix") != -1 || OS_NAME.indexOf("nux") != 1;
		osIsMacOs = OS_NAME.indexOf("mac") != -1;
		osIsWindows = OS_NAME.indexOf("windows") != -1;
		osBit64 = OS_ARCH.equals("amd64");
		osIsX86 = OS_ARCH.indexOf("x86") >= 0;
	}

	public static boolean isLinux() {
		return osIsLinux;
	}

	public static boolean isUnix() {
		return osIsUnix;
	}

	public static boolean isMacos() {
		return osIsMacOs;
	}

	public static boolean isWindows() {
		return osIsWindows;
	}

	public static boolean isX86() {
		return osIsX86;
	}

	public static boolean isBit64() {
		return osBit64;
	}

	/**
	 * findJdk标记为是否仅查找jdk，为fasle时jre也会被采纳
	 * 
	 * @param findJdk
	 */
	public JDK(boolean findJdk) {
		this.jdk = findJdk;
	}

	private static void debug(String s) {
		Log.log("[Debug]" + s);
	}

	private String searchPath(String[] paths) {
		String driver = "";
		if (osIsWindows) {
			driver = System.getenv("SystemDrive") + "/";
		}
		return searchPath(paths, driver);
	}

	private String searchPath(String[] paths, String driver) {
		for (String path : paths) {
			String s = searchAPath(driver + path);
			if (!s.isEmpty()) {
				return s;
			}
		}
		return "";
	}

	private String searchAPath(String path) {
		try {
			debug("search " + path);
			File p = new File(path);
			String latestVer = "";
			String ret = "";
			if (p.exists() && p.isDirectory()) {
				for (File f : p.listFiles()) {
					if (f.isDirectory()) {
						String fn = f.getName().trim().toLowerCase();
						boolean isJavaDir = jdk ? (fn.indexOf("jdk") >= 0 || fn
								.indexOf("java") >= 0)
								: (fn.indexOf("jdk") >= 0
										|| fn.indexOf("jre") >= 0 || fn
										.indexOf("java") >= 0);
						if (!isJavaDir) {
							continue;
						}
						debug("check java dir:" + f.getCanonicalPath());
						boolean found = false;
						if (jdk) {
							if (osIsWindows) {
								found = new File(f, "bin/javac.exe").exists();
							} else {
								found = new File(f, "bin/javac").exists();
							}
						} else {
							if (osIsWindows) {
								found = new File(f, "bin/java.exe").exists();
							} else {
								found = new File(f, "bin/java").exists();
							}
						}
						if (found) {
							String ver = getVersion(f.getName());
							if (ver.compareTo(latestVer) > 0) {
								latestVer = ver;
								ret = f.getCanonicalPath();
							}

						}
					}
				}
			}
			return ret;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	private String getVersion(String s) {
		int p1 = -1;
		int p2 = s.length();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isDigit(c) || c == '.' || c == '_') {
				if (p1 == -1) {
					p1 = i;
				} else {

				}
			} else {
				if (p1 >= 0) {
					p2 = i;
					break;
				} else {

				}
			}
		}
		if (p1 >= 0 && p2 > p1) {
			String s2 = s.substring(p1, p2).replace('_', '.');
			String[] ss = s2.split("\\.");
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 4; i++) {
				String s3 = i < ss.length ? ss[i] : "0";
				sb.append(String.format("%03d", Integer.parseInt(s3)));
				sb.append('.');
			}
			String ret = sb.toString();
			debug("find version:" + ret);
			return ret;
		}
		debug("cannot find version on:" + s);
		return "";
	}

	public String command(String cmd) throws IOException {
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd);
		InputStream ins = process.getInputStream();
		InputStreamReader str = new InputStreamReader(ins);
		BufferedReader br = new BufferedReader(str);
		StringBuilder sbr = new StringBuilder();
		String line = null;
		while ((line = br.readLine()) != null) {
			sbr.append(line);
			sbr.append('\n');
		}
		ins.close();
		return sbr.toString();
	}

	public String find() {
		return find(osBit64 ? 64 : 32);
	}

	public String find(int bit) {
		String path = "";
		// 首先获得javahome中设置，然后和系统目录中的进行版本比对，loon的原则是，谁版本高就用谁编译……
		String java_home = System.getenv("JAVA_HOME");
		if (jdk) {
			if (java_home != null && java_home.indexOf("jre") != -1) {
				java_home = null;
			}
		}
		if (osIsWindows) {
			if (osIsX86) { // x86系统
				// 此时查找64位系统，当然无效
				if (bit == 64) {
					System.err
							.println("This is Win32 but you need a 64bit JDK !");
				}
				// 尝试查找默认路径
				path = searchPath(new String[] { "Program Files/Java/" });
			} else {// 非x86
				if (bit == 32) { // 32位
					path = searchPath(new String[] { "Program Files (x86)/Java/" });
				} else if (bit == 0) {// 其它
					path = searchPath(new String[] { "Program Files/Java/",
							"Program Files (x86)/Java/" });
				} else if (bit == 64) {
					path = searchPath(new String[] { "Program Files/Java/" });
				} else {
					path = searchPath(new String[] { "Program Files/Java/" });
				}
			}
		} else {// linux
			path = searchPath(new String[] { "/usr/lib/jvm/", "/usr/java/",
					"/usr/local/java/", "/opt/" });
		}
		// 找不到时，尽人事的尝试搜索下D盘
		if (path == null || path.length() == 0) {
			if (osIsWindows) {
				path = searchPath(new String[] { "Program Files/Java/",
						"Program Files (x86)/Java/" }, "D:\\");
			} else { // 尝试下非windows目录
				path = searchPath(new String[] { "/usr/lib/jvm/", "/usr/java/",
						"/usr/local/java/", "/opt/" });
			}
		}
		if (osIsWindows && path == null || path.length() == 0) {
			path = searchPath(new String[] { "Java", "JDK", "JRE", "JVM" },
					"C:\\");
		}
		if (java_home != null && java_home.length() > 0) {
			int v1 = Integer.parseInt(getVersion(java_home).replace(".", "")
					.replace("0", ""));
			int v2 = Integer.parseInt(getVersion(path).replace(".", "")
					.replace("0", ""));
			if (v1 > v2) {
				path = java_home;
			}
		}
		return path;
	}

}

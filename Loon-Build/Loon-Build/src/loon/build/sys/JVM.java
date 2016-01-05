package loon.build.sys;

import loon.build.tools.StringUtils;

public class JVM {

	public static String getJavaHome(String javaHome) {
		if (StringUtils.isEmpty(javaHome)) {
			String javaPath = new JDK(false).find(0);
			if (!javaPath.isEmpty()) {
				javaHome = javaPath;
			}
		}
		return javaHome;
	}

	public static String getJavaExec(String javaHome) {
		String javaExecPath = null;
		if (StringUtils.isEmpty(javaHome)) {
			javaExecPath = getJavaHome(null);
		} else {
			javaExecPath = javaHome
					+ (JDK.isWindows() ? "/bin/javac.exe" : "/bin/javac");
		}
		return javaExecPath;
	}

}

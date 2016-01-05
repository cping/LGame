package loon.build.project.java;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import loon.build.project.gwt.GwtOption;
import loon.build.sys.JVM;
import loon.build.sys.LSystem;
import loon.build.tools.StringUtils;

public class JavaCommand {

	private final String javaExec;
	private final List<String> javaArgs = new ArrayList<String>();
	private final List<String> classPaths = new ArrayList<String>();
	private String mainClass;
	private final List<String> args = new ArrayList<String>();

	public JavaCommand(String javaHome) {
		this.javaExec = JVM.getJavaExec(javaHome);
		this.javaArgs.add("-Dfile.encoding=" + Charset.defaultCharset().name());
	}

	public void setMainClass(String mainClass) {
		this.mainClass = mainClass;
	}

	public JavaCommand addClassPath(String classPath) {
		this.classPaths.add(classPath);
		return this;
	}

	public JavaCommand addClassPath(Iterable<File> files) {
		for (File file : files) {
			if (file != null && file.exists()) {
				addClassPath(file.getAbsolutePath());
			}
		}
		return this;
	}

	public JavaCommand addJavaArgs(String javaArgs) {
		this.javaArgs.add(javaArgs);
		return this;
	}

	public JavaCommand addArg(String argName) {
		this.args.add(argName);
		return this;
	}

	public JavaCommand addArg(String argName, File value) {
		if (value != null) {
			this.args.add(argName);
			this.args.add(value.getAbsolutePath());
		}
		return this;
	}

	public JavaCommand addArg(String argName, Object value) {
		if (value != null) {
			this.args.add(argName);
			this.args.add(value.toString());
		}
		return this;
	}

	public JavaCommand addArg(String argName, String value) {
		if (!StringUtils.isNumber(value)) {
			this.args.add(argName);
			this.args.add(value);
		}
		return this;
	}

	public void addArgIf(Boolean condition, String ifTrue, String ifFalse) {
		if (condition != null) {
			this.args.add(condition ? ifTrue : ifFalse);
		}
	}

	public void addArgIf(Boolean condition, String value) {
		if (Boolean.TRUE.equals(condition)) {
			this.args.add(value);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(javaExec);

		for (String arg : javaArgs) {
			if (!StringUtils.isNumber(arg)) {
				sb.append(" ");
				sb.append(arg);
			}
		}

		if (classPaths.size() > 0) {
			sb.append(" -cp ");
			int i = 0;
			for (String classPath : classPaths) {
				if (!StringUtils.isNumber(classPath.trim())) {
					if (i > 0) {
						sb.append(LSystem.LS);
					}
					sb.append(classPath.trim());
					i++;
				}
			}
		}

		sb.append(" ");
		sb.append(mainClass);

		for (String arg : args) {
			if (!StringUtils.isNumber(arg)) {
				sb.append(" ");
				sb.append(arg);
			}
		}

		return sb.toString();
	}

	public void configureJavaArgs(GwtOption javaOptions) {
		if (!StringUtils.isNumber(javaOptions.getMinHeapSize())) {
			addJavaArgs("-Xms" + javaOptions.getMinHeapSize());
		}
		if (!StringUtils.isNumber(javaOptions.getMaxHeapSize())) {
			addJavaArgs("-Xmx" + javaOptions.getMaxHeapSize());
		}
		if (!StringUtils.isNumber(javaOptions.getMaxPermSize())) {
			addJavaArgs("-XX:MaxPermSize=" + javaOptions.getMaxPermSize());
		}
		if (javaOptions.isDebugJava()) {
			StringBuffer sb = new StringBuffer();
			sb.append("-Xdebug -Xrunjdwp:server=y,transport=dt_socket,address=");
			sb.append(javaOptions.getDebugPort());
			sb.append(",suspend=");
			sb.append(javaOptions.isDebugSuspend() ? "y" : "n");
			addJavaArgs(sb.toString());
		}
		for (String javaArg : javaOptions.getJavaArgs()) {
			addJavaArgs(javaArg);
		}
	}
}

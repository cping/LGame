package loon.build.project.java;

import java.util.ArrayList;
import java.util.List;

import loon.build.sys.JDK;

public class JavaPath {

	private List<String> sub;

	public JavaPath(ProjectName project) {
		sub = new ArrayList<String>();
	}

	public JavaPath(ProjectName project, String path) {
		this(project);
		add(path);
	}

	public void add(String path) {
		sub.add(path);
	}

	public String toCommandlineString() {
		char sep = JDK.isWindows() ? ';' : ':';
		StringBuilder sb = new StringBuilder();
		for (String p1 : sub) {
			if (sb.length() > 0) {
				sb.append(sep);
			}
			sb.append('"');
			sb.append(p1);
			sb.append('"');
		}
		return sb.toString();
	}
}

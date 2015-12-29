package loon.build.project.java;

import java.util.ArrayList;
import java.util.List;

import loon.build.sys.JDK;

public class JavaCall {

	private List<String> args;

	public JavaCall() {
		args = new ArrayList<String>();
	}

	private JavaPath cp;
	private ProjectName prj;
	private String clsName;

	public void setClasspath(JavaPath cp) {
		this.cp = cp;
	}

	public void setProject(ProjectName project) {
		this.prj = project;
	}

	public void setClassname(String cls) {
		this.clsName = cls;
	}

	public void execute() throws Exception {
		RunCompile e = new RunCompile(prj);
		e.setCmd(prj.projects.javaHome
				+ (JDK.isWindows() ? "/bin/java.exe" : "/bin/java"));
		e.addArg("-Xms1024m");
		e.addArg("-Xms2048m");
		if (cp != null) {
			e.addArg("-cp", cp.toCommandlineString());
		}
		if (clsName != null) {
			e.addArg(clsName);
		}
		for (String s : args) {
			e.addArg(s);
		}
		int code = e.execute();
		if (code < 0) {
			throw new RuntimeException("java failed with code:" + code);
		}
		prj.projects.totalJava++;
	}

	public void addArg(String s) {
		args.add(s);
	}

}

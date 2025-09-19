package loon.build.project.java;

import java.io.File;
import loon.build.sys.JDK;
import loon.build.tools.ArrayMap;

public class JarConfig {

	private ProjectName prj;
	private File dest;
	private File base;
	private ArrayMap manifest;

	public JarConfig() {
		manifest = new ArrayMap();
	}

	public void setProject(ProjectName project) {
		this.prj = project;
	}

	public void setDestFile(File jarFile) {
		this.dest = jarFile;
	}

	public void setBasedir(File buildDir) {
		this.base = buildDir;
	}

	public void addManifest(String key, String value) {
		manifest.put(key, value);
	}

	public void execute() throws Exception {
		RunCompile exec = new RunCompile(prj);
		exec.setCmd(prj.projects.javaHome + (JDK.isWindows() ? "/bin/jar.exe" : "/bin/jar"));
		int code;
		if (manifest.isEmpty()) {
			exec.addArg("cf");
			exec.addArg(dest.getCanonicalPath());
			exec.addArg("-C", base.getCanonicalPath());
			exec.addArg(".");
			code = exec.execute();
		} else {
			exec.addArg("cfm");
			exec.addArg(dest.getCanonicalPath());
			File mf = JarWrite.getTempFile("manifest");
			JarWrite.writeManifest(mf, manifest,true);
			exec.addArg(mf.getCanonicalPath());
			exec.addArg("-C", base.getCanonicalPath());
			exec.addArg(".");
			code = exec.execute();
			mf.delete();
		}
		if (code < 0) {
			throw new RuntimeException("Jar failed with code:" + code);
		}
		prj.projects.totalJar++;

	}
}

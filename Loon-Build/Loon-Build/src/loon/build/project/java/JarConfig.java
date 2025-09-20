package loon.build.project.java;

import java.io.File;
import loon.build.sys.JDK;
import loon.build.tools.ArrayMap;
import loon.build.tools.StringUtils;

public class JarConfig {

	private final ArrayMap manifest;

	private ProjectName prj;
	private File dest;
	private File base;

	private boolean dirToJar;

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

	public boolean isDirectoryToJar() {
		return dirToJar;
	}

	public JarConfig setDirectoryToJar(boolean d) {
		this.dirToJar = d;
		return this;
	}

	public void addManifest(String key, String value) {
		manifest.put(key, value);
	}

	public void execute(boolean jniNative, boolean mixJar) throws Exception {
		execute(jniNative, mixJar, null, null);
	}

	public void execute(boolean jniNative, boolean mixJar, String newDestPath, String newBasePath) throws Exception {
		RunCompile exec = new RunCompile(prj);
		exec.setCmd(prj.projects.javaHome + (JDK.isWindows() ? "/bin/jar.exe" : "/bin/jar"));
		int code;
		final String destPath = !StringUtils.isEmpty(newDestPath) ? newDestPath : dest.getCanonicalPath();
		final String basePath = !StringUtils.isEmpty(newBasePath) ? newBasePath : base.getCanonicalPath();
		if (manifest.isEmpty()) {
			exec.addArg("cf");
			exec.addArg(destPath);
			exec.addArg("-C", basePath);
			exec.addArg(".");
			code = exec.execute();
		} else {
			if (dirToJar) {
				exec.addArg("cvfm");
			} else {
				exec.addArg("cfm");
			}
			exec.addArg(destPath);
			File mf = JarWrite.getTempFile("manifest");
			JarWrite.writeManifest(mf, manifest, jniNative, mixJar);
			exec.addArg(mf.getCanonicalPath());
			exec.addArg("-C", basePath);
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

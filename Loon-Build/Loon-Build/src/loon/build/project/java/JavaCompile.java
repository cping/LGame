package loon.build.project.java;

import java.io.File;

public class JavaCompile {

	JavaPath classpath;
	ProjectName prj;
	String target;
	String source;
	String encoding;
	boolean debug;
	String srcdir;
	String destdir;
	String executable;

	public void setExecutable(String executable) {
		this.executable = executable;
	}


	public void setTarget(String target) {
		this.target = target;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public void setDestdir(String destdir) {
		this.destdir = destdir;
	}

	public void setClasspath(JavaPath classpath) {
		this.classpath = classpath;
	}

	public int execute() throws Exception {
		RunCompile exec = new RunCompile(prj);
		exec.setCmd(executable);
	
		if (!debug) {
			exec.addArg("-g:none");
		}
		if (destdir != null) {
			new File(destdir).mkdirs();
			exec.addArg("-d", destdir);
		}

		if (encoding != null) {
			exec.addArg("-encoding", encoding);
		}

		if (source != null) {
			exec.addArg("-source", source);
		}
		if (srcdir != null) {
			exec.addArg("-sourcepath", srcdir);
		}
		if (target != null) {
			exec.addArg("-target", target);
		}

		if (classpath != null) {
			String cp = classpath.toCommandlineString();
			if (!cp.isEmpty()) {
				exec.addArg("-cp", cp);
			}
		}

		File f = JarWrite.getTempFile("filelist");
		int cnt = JarWrite.writeFileList(f, new File(srcdir), new File(destdir));
	
		if (cnt == 0) {
			return cnt;
		}
		exec.addArg("@" + f.getCanonicalPath());
		int code = exec.execute();
		f.delete();
		prj.projects.totalJavac += cnt;

		if (code != 0) {
			return -Math.abs(code);
		}
		return cnt;
	}

	public void setProject(ProjectName prj) {
		this.prj = prj;
	}

	public void setSrcdir(String string) {
		this.srcdir = string;
	}

}

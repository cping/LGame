package loon.build.project.java;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import loon.build.sys.Log;

public class JarFileCopy {

	private ProjectName prj;
	private File todir;

	int count;
	List<JavaFileSet> fs;
	private File file;

	public JarFileCopy() {
		fs = new ArrayList<JavaFileSet>();
	}

	public void setProject(ProjectName project) {
		this.prj = project;

	}

	public void setTodir(File dir) {
		todir = dir;
	}

	public void addFileset(JavaFileSet fs1) {
		fs.add(fs1);

	}

	public int execute() throws IOException {
		count = 0;
		if (file != null) {
			copyFile(file);
		}
		for (JavaFileSet fs1 : fs) {
			copyFileSet(fs1);
		}
		prj.projects.totalCopy += count;
		return count;
	}

	private void copyFileSet(JavaFileSet fs1) throws IOException {
		for (File f : fs1) {
			for (File dir : fs1.dirs) {
				copySource(dir, f);
			}
		}

	}

	private void copySource(File base, File src) throws IOException {
		String s1 = src.getParentFile().getCanonicalPath();
		String s2 = base.getCanonicalPath();
		if (!s1.startsWith(s2)) {
			return;
		}
		String rel = s1.substring(s2.length()).replace('\\', '/');
		if (rel.startsWith("/"))
			rel = rel.substring(1);
		if (!rel.isEmpty() && !rel.endsWith("/"))
			rel = rel + "/";
		File target = new File(todir, rel + src.getName());
		copyOneFile(src, target);
	}

	private void copyFile(File src) throws IOException {
		if (!src.exists()) {
			JarWrite.err(prj.name + ":warning:file not exists for copy:" + src.getCanonicalPath());
			return;
		}
		if (src.isFile()) {
			File target = new File(todir, src.getName());
			copyOneFile(src, target);
		} else if (src.isDirectory()) {
			JavaFileSet fs1 = new JavaFileSet();
			fs1.addFile(src);
			copyFileSet(fs1);
		} else {
			JarWrite.err(prj.name + ":warning:file not copied, please check:" + src.getCanonicalPath());
			return;
		}

	}

	private void copyOneFile(File src, File target) throws IOException {
		if (JarWrite.isNewFile(src, target)) {
			toCopy(src, target);
		}
	}

	private void toCopy(File src, File target) throws IOException {
		target.getParentFile().mkdirs();
		if (prj.projects.verbose) {
			Log.log("[Info]" + prj.name + ":copy " + src.getCanonicalPath() + " -> " + target.getCanonicalPath());
		}
		FileOutputStream out = new FileOutputStream(target);
		FileInputStream in = new FileInputStream(src);
		long size = copy(in, out);
		prj.projects.totalCopys += size;
		in.close();
		out.close();
		target.setLastModified(src.lastModified());
		count++;

	}

	public static long copy(InputStream in, OutputStream outstream) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(outstream);
		byte[] buf = new byte[1024 * 10];
		long total = 0;
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
			total += len;
		}
		in.close();
		out.close();
		return total;
	}

	public void setFile(File file) {
		fs.clear();
		this.file = file;

	}
}

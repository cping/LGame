package loon.build.project.java;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import loon.build.sys.LSystem;
import loon.build.sys.Log;
import loon.build.tools.ArrayMap;
import loon.build.tools.FileIterator;

public class JarWrite {

	public static final long ignoreTimeMs = 30;

	public static boolean isNewFile(File src, File target) {
		if (!target.exists()) {
			return true;
		}
		long t2 = target.lastModified();
		long t1 = src.lastModified();
		return t1 > t2 + ignoreTimeMs;
	}

	public static void err(String s) {
		System.err.println(s);
		Log.log("[Error]" + s);
	}

	public static File getTempFile(String fn) throws IOException {
		return File.createTempFile(fn, null);
	}

	public static int writeFileList(File outf, File srcdir, File destdir) throws Exception {
		int cnt = 0;
		String base = srcdir.getCanonicalPath().replace('\\', '/');
		if (!base.endsWith("/")) {
			base = base + "/";
		}
		BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outf), LSystem.ENCODING));
		for (File f : new FileIterator(srcdir.getCanonicalPath())) {
			String fn = f.getName();
			if (f.isFile() && fn.endsWith(".java")) {
				String fn1 = f.getCanonicalPath().replace('\\', '/');
				if (!fn1.startsWith(base)) {
					Log.log("[Warning]Cannot list java file, please check:" + fn1);
					continue;
				}
				String fn2 = fn1.substring(base.length());
				File cls = new File(destdir, fn2.substring(0, fn2.length() - 5) + ".class");
				if (!isNewFile(f, cls)) {
					continue;
				}
				cnt++;
				out.write(fn1);
				out.write("\n");
			}
		}
		out.close();
		return cnt;
	}

	/**
	 * 写入Manifest文件
	 * 
	 * @param mf
	 * @param manifest
	 * @param mixJar
	 * @param enableNative
	 * @throws IOException
	 */
	public static void writeManifest(File mf, ArrayMap manifest, boolean enableNative, boolean mixJar)
			throws IOException {
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(mf), LSystem.ENCODING));
		out.println("Manifest-Version: 1.0");
		for (int i = 0; i < manifest.size(); i++) {
			final ArrayMap.Entry entry = manifest.getEntry(i);
			final String key = entry.getKey().toString().trim();
			if (mixJar && key.toLowerCase().startsWith("rsrc")) {
				continue;
			}
			String value = entry.getValue().toString().trim();
			out.println(String.format("%s: %s", key, value));
		}
		if (enableNative) {
			out.println("Enable-Native-Access: ALL-UNNAMED");
		}
		out.close();
	}

}

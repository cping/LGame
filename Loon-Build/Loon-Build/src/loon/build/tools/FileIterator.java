package loon.build.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class FileIterator implements Iterable<File> {

	List<File> buf;

	public FileIterator(String dir) {
		buf = new ArrayList<File>();
		File f = new File(dir);
		buf.add(f);
	}

	public static int getLineCnt(File f, int[] linecnt) throws Exception {
		int cnt = 0;
		BufferedReader in = new BufferedReader(new FileReader(f));
		String line;
		while ((line = in.readLine()) != null) {
			linecnt[0]++;
			if (line.trim().startsWith("'")) {
				linecnt[1]++;
			}
		}
		in.close();
		return cnt;
	}

	@Override
	public Iterator<File> iterator() {
		return new Iterator<File>() {

			@Override
			public boolean hasNext() {
				return buf.size() > 0;
			}

			@Override
			public File next() {
				File f = buf.remove(0);
				if (f.isDirectory()) {
					File[] sub = f.listFiles();
					buf.addAll(Arrays.asList(sub));
				}
				return f;
			}

			@Override
			public void remove() {
			}
		};
	}

}

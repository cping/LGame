package loon.build.project.java;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import loon.build.tools.FileIterator;

public class JavaFileSet implements Iterable<File> {

	ArrayList<File> dirs = new ArrayList<File>(10);
	private String excludeEnds;
	public boolean ignoreEclipsePrjFile;

	public void addFile(File file) {
		this.dirs.add(file);
	}

	public void setExcludesEndsWith(String substr) {
		this.excludeEnds = substr;

	}

	@Override
	public Iterator<File> iterator() {
		return new Iterator<File>() {

			ArrayList<Iterator<File>> list = new ArrayList<Iterator<File>>();
			{
				for (File dir : dirs) {
					list.add(new FileIterator(dir.getAbsolutePath()).iterator());
					checkNext();
				}
			}

			File buffer;

			@Override
			public boolean hasNext() {
				if (buffer == null) {
					return false;
				}
				return true;
			}

			private void checkNext() {
				for (Iterator<File> i : list) {
					if (!i.hasNext()) {
						buffer = null;
						return;
					}
					File f = i.next();
					boolean ok = true;
					while (true) {

						if (ok && f.isDirectory()) {
							ok = false;
						}
						if (ok && excludeEnds != null && f.isFile()
								&& f.getName().endsWith(excludeEnds)) {
							ok = false;
						}
						String name = f.getName().trim().toLowerCase();
						if (ok
								&& ignoreEclipsePrjFile
								&& (name.equals(".classpath")
										|| name.equals(".project") || name
											.equals(".bak"))) {
							ok = false;
						}
						if (ok == false) {
							if (!i.hasNext()) {
								buffer = null;
								return;
							}
							f = i.next();
							ok = true;
							continue;
						}
						break;
					}
					if (ok) {
						buffer = f;
					} else {
						buffer = null;
					}
				}
			}

			@Override
			public File next() {
				File ret = buffer;
				if (buffer != null){
					checkNext();
				}
				return ret;
			}

		};
	}

}

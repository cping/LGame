/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.teavm.assets;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import loon.teavm.TeaResourceLoader;
import loon.utils.ObjectMap;
import loon.utils.OrderedMap;
import loon.utils.TArray;

public class AssetPreloader {

	public static enum FileType {

		Classpath,

		Internal,

		External,

		Absolute,

		Local;
	}

	public TeaResourceLoader classpath(String path) {
		return new TeaResourceLoader(this, path, FileType.Classpath);
	}

	public TeaResourceLoader internal(String path) {
		return new TeaResourceLoader(this, path, FileType.Internal);
	}

	public boolean debug = false;

	private final OrderedMap<String, AssetData> fileMap;

	private final TArray<String> tmpPaths = new TArray<String>();

	public AssetPreloader() {
		fileMap = new OrderedMap<String, AssetData>();
	}

	public final OutputStream write(TeaResourceLoader file, boolean append, int bufferSize) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(Math.max(512, Math.min(bufferSize, 8192)));

		return new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				buffer.write(b);
			}

			@Override
			public void write(byte[] b) throws IOException {
				buffer.write(b);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				buffer.write(b, off, len);
			}

			@Override
			public void close() throws IOException {
				byte[] data = buffer.toByteArray();
				writeInternal(file, data, append, Math.max(data.length, bufferSize));
			}
		};
	}

	protected void writeInternal(TeaResourceLoader file, byte[] data, boolean append, int expectedLength) {
		String path = fixPath(file.path());
		byte[] newBytes = null;
		AssetData oldData = fileMap.get(path);
		if (append) {
			if (oldData == null) {
				newBytes = data;
			} else {
				byte[] oldBytes = oldData.getBytes();
				int newSize = data.length + oldBytes.length;
				newBytes = new byte[newSize];
				for (int i = 0; i < oldBytes.length; i++) {
					newBytes[i] = oldBytes[i];
				}
				for (int i = oldBytes.length, j = 0; i < newSize; i++, j++) {
					newBytes[i] = data[j];
				}
			}
		} else {
			newBytes = data;
		}

		putFileInternal(path, newBytes);

		TeaResourceLoader cur = file.parent();
		while (!isRootFolder(cur.path())) {
			String parentPath = fixPath(cur.path());
			if (!fileMap.containsKey(parentPath)) {
				putFolderInternal(parentPath);
			}
			cur = cur.parent();
		}
	}

	final public void putFileInternal(String path, byte[] bytes) {
		if (debug) {
			String pathStr = "\"" + path + "\"";
			System.out.println(getClass().getSimpleName() + " PUT FILE: " + pathStr + " Bytes: " + bytes.length);
		}
		if (path.isEmpty() || path.equals(".") || path.equals("/") || path.equals("./")) {
			throw new RuntimeException("Cannot put an empty folder name");
		}
		AssetData fileData = new AssetData(path, bytes);
		fileMap.put(path, fileData);
	}

	final public void putFolderInternal(String path) {
		if (debug) {
			String pathStr = "\"" + path + "\"";
			System.out.println(getClass().getSimpleName() + " PUT FOLDER: " + pathStr);
		}
		if (path.isEmpty() || path.equals(".") || path.equals("/") || path.equals("./")) {
			throw new RuntimeException("Cannot put an empty folder name");
		}
		AssetData fileData = new AssetData(path);
		fileMap.put(path, fileData);

	}

	final protected String fixPath(String path) {
		path = path.trim();
		if (path.startsWith("./")) {
			path = path.replace("./", "");
		}
		if (path.startsWith(".")) {
			path = path.replaceFirst(".", "");
		}
		if (!path.startsWith("/")) {
			path = "/" + path;
		}
		if (!path.endsWith("/")) {
			path = path + "/";
		}
		return path;
	}

	public void mkdirs(TeaResourceLoader file) {
		String path = fixPath(file.path());
		putFolderInternal(path);
		TeaResourceLoader cur = file.parent();
		while (!isRootFolder(cur.path())) {
			String parentPath = fixPath(cur.path());
			if (!fileMap.containsKey(parentPath)) {
				putFolderInternal(parentPath);
			}
			cur = cur.parent();
		}
	}

	public final TeaResourceLoader[] listToRes(String url) {
		String[] paths = list(url);
		TeaResourceLoader[] files = new TeaResourceLoader[paths.length];
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if ((path.length() > 0) && (path.charAt(path.length() - 1) == '/')) {
				path = path.substring(0, path.length() - 1);
			}
			files[i] = new TeaResourceLoader(this, path, FileType.Internal);
		}
		return files;
	}

	public boolean get(String url) {
		String path = fixPath(url);
		AssetData fileData = fileMap.get(path);
		boolean flag = fileData != null;
		if (debug) {
			String type = fileData != null && fileData.isDirectory() ? " CONTAINS FOLDER: " : " CONTAINS FILE: ";
			System.out.println(getClass().getSimpleName() + type + flag + " Path: " + url);
		}
		return flag;
	}

	public boolean contains(String url) {
		String path = fixPath(url);
		AssetData fileData = fileMap.get(path);
		boolean flag = fileData != null;
		if (debug) {
			String type = fileData != null && fileData.isDirectory() ? " CONTAINS FOLDER: " : " CONTAINS FILE: ";
			System.out.println(getClass().getSimpleName() + type + flag + " Path: " + url);
		}
		return flag;
	}

	public long length(String url) {
		String path = fixPath(url);
		AssetData data = getInternal(path);
		if (data != null && !data.isDirectory()) {
			byte[] bytes = data.getBytes();
			return bytes.length;
		}
		return 0;
	}

	public TeaResourceLoader[] list(String url, String suffix) {
		TeaResourceLoader[] list = listToRes(url);
		List<TeaResourceLoader> filtered = new ArrayList<TeaResourceLoader>(list.length);
		for (TeaResourceLoader f : list) {
			if (url.endsWith(suffix)) {
				filtered.add(f);
			}
		}
		return filtered.toArray(new TeaResourceLoader[filtered.size()]);
	}

	private boolean isRootFolder(String cur) {
		String path = fixPath(cur);
		if (path.isEmpty() || path.equals(".") || path.equals("/") || path.equals("./")) {
			return true;
		} else {
			return false;
		}
	}

	private String[] list(String file) {
		return list(file, true);
	}

	private String[] list(String file, boolean equals) {
		String dir = fixPath(file);

		boolean isRoot = isRootFolder(file);
		if (debug) {
			System.out.println("********** START LIST *** isRoot: " + isRoot + " DIR: " + dir);
		}
		ObjectMap.Entries<String, AssetData> it = fileMap.iterator();
		while (it.hasNext()) {
			ObjectMap.Entry<String, AssetData> next = it.next();
			String path = fixPath(next.key);

			TeaResourceLoader pathFileHandle = new TeaResourceLoader(this, path, FileType.Internal);
			TeaResourceLoader parent = pathFileHandle.parent();
			String parentPath = fixPath(parent.path());

			boolean isChildParentRoot = isRootFolder(parent.path());

			if (isRoot) {
				if (isChildParentRoot) {
					if (debug) {
						System.out.println("LIST ROOD ADD: " + path);
					}
					tmpPaths.add(path);
				}
			} else {
				if (equals) {
					if (parentPath.equals(dir)) {
						if (debug) {
							System.out.println("LIST EQUAL ADD: PATH: " + path + " --- PARENT: " + parentPath);
						}
						tmpPaths.add(path);
					}
				} else {
					if (parentPath.startsWith(dir)) {
						if (debug) {
							System.out.println("LIST STARTWITH ADD: PATH: " + path + " --- PARENT: " + parentPath);
						}
						tmpPaths.add(path);
					}
				}
			}
		}
		if (debug) {
			System.out.println("********** END LIST ***");
		}
		String[] str = new String[tmpPaths.size];
		for (int i = 0; i < tmpPaths.size; i++) {
			String s = tmpPaths.get(i);
			if (s.startsWith("/")) {
				s = s.substring(1);
			}
			if (debug) {
				System.out.println(getClass().getSimpleName() + " LIST[" + i + "]: " + s);
			}
			str[i] = s;
		}
		tmpPaths.clear();
		return str;
	}

	final public AssetData removeInternal(String path) {
		return removeInternal(path, true);
	}

	final public AssetData removeInternal(String path, boolean callMethod) {
		AssetData fileData = fileMap.remove(path);
		if (debug) {
			String pathStr = "\"" + path + "\"";
			String type = fileData != null && fileData.isDirectory() ? " REMOVE FOLDER: " : " REMOVE FILE: ";
			System.out.println(getClass().getSimpleName() + type + (fileData != null) + " Path: " + pathStr);
		}
		return fileData;
	}

	public final AssetData getInternal(String path) {
		path = fixPath(path);

		AssetData fileData = fileMap.get(path);
		
		if (debug) {
			path = "\"" + path + "\"";
			String type = fileData != null && fileData.isDirectory() ? " GET FOLDER: " : " GET FILE: ";
			System.out.println(getClass().getSimpleName() + type + (fileData != null) + " Size: "
					+ (fileData != null ? fileData.getBytesSize() : 0) + " Path: " + path);
		}
		return fileData;
	}

	public InputStream read(String url) {
		String path = fixPath(url);
		AssetData data = getInternal(path);
		if (data == null) {
			return null;
		}
		byte[] byteArray = data.getBytes();
		try {
			return new ByteArrayInputStream(byteArray);
		} catch (RuntimeException e) {
			removeInternal(path);
			throw new RuntimeException(getClass().getSimpleName() + " Error: " + path, e);
		}
	}
}

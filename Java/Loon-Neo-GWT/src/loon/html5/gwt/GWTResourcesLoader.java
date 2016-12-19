/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import loon.html5.gwt.GWTResources.FileType;
import loon.html5.gwt.preloader.Preloader;

public class GWTResourcesLoader {
	public final Preloader preloader;
	private final String file;
	private final FileType type;

	public GWTResourcesLoader(Preloader preloader, String fileName,
			FileType type) {
		if (type != FileType.Internal && type != FileType.Classpath) {
			throw new RuntimeException("FileType '" + type
					+ "' Not supported in GWT backend");
		}
		this.preloader = preloader;
		this.file = fixSlashes(fileName);
		this.type = type;
	}

	public GWTResourcesLoader(String path) {
		this.type = FileType.Internal;
		this.preloader = Loon.self.getPreloader();
		this.file = fixSlashes(path);
	}

	public String path() {
		return file;
	}

	public String name() {
		int index = file.lastIndexOf('/');
		if (index < 0)
			return file;
		return file.substring(index + 1);
	}

	public String extension() {
		String name = name();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return "";
		return name.substring(dotIndex + 1);
	}

	public String nameWithoutExtension() {
		String name = name();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return name;
		return name.substring(0, dotIndex);
	}

	public String pathWithoutExtension() {
		String path = file;
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1)
			return path;
		return path.substring(0, dotIndex);
	}

	public FileType type() {
		return type;
	}

	public File file() {
		throw new RuntimeException("Not supported in GWT backend");
	}

	public InputStream read() {
		InputStream in = preloader.read(file);
		if (in == null) {
			throw new RuntimeException(file + " does not exist");
		}
		return in;
	}

	public BufferedInputStream read(int bufferSize) {
		return new BufferedInputStream(read(), bufferSize);
	}

	public Reader reader() {
		return new InputStreamReader(read());
	}

	public Reader reader(String charset) {
		try {
			return new InputStreamReader(read(), charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Encoding '" + charset
					+ "' not supported", e);
		}
	}

	public BufferedReader reader(int bufferSize) {
		return new BufferedReader(reader(), bufferSize);
	}

	public BufferedReader reader(int bufferSize, String charset) {
		return new BufferedReader(reader(charset), bufferSize);
	}

	public String readString() {
		return readString(null);
	}

	public String readString(String charset) {
		if (preloader.isText(file)) {
			return preloader.texts.get(file);
		}
		try {
			return new String(readBytes(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	public byte[] readBytes() {
		int length = (int) length();
		if (length == 0)
			length = 512;
		byte[] buffer = new byte[length];
		int position = 0;
		InputStream input = read();
		try {
			while (true) {
				int count = input.read(buffer, position, buffer.length
						- position);
				if (count == -1)
					break;
				position += count;
				if (position == buffer.length) {
					byte[] newBuffer = new byte[buffer.length * 2];
					System.arraycopy(buffer, 0, newBuffer, 0, position);
					buffer = newBuffer;
				}
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error reading file: " + this, ex);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}
		}
		if (position < buffer.length) {
			byte[] newBuffer = new byte[position];
			System.arraycopy(buffer, 0, newBuffer, 0, position);
			buffer = newBuffer;
		}
		return buffer;
	}

	public int readBytes(byte[] bytes, int offset, int size) {
		InputStream input = read();
		int position = 0;
		try {
			while (true) {
				int count = input.read(bytes, offset + position, size
						- position);
				if (count <= 0)
					break;
				position += count;
			}
		} catch (IOException ex) {
			throw new RuntimeException("Error reading file: " + this, ex);
		} finally {
			try {
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}
		}
		return position - offset;
	}

	public GWTResourcesLoader[] list() {
		return preloader.list(file);
	}

	public GWTResourcesLoader[] list(FileFilter filter) {
		return preloader.list(file, filter);
	}

	public GWTResourcesLoader[] list(FilenameFilter filter) {
		return preloader.list(file, filter);
	}

	public GWTResourcesLoader[] list(String suffix) {
		return preloader.list(file, suffix);
	}

	public boolean isDirectory() {
		return preloader.isDirectory(file);
	}

	public GWTResourcesLoader child(String name) {
		return new GWTResourcesLoader(preloader, (file.isEmpty() ? ""
				: (file + (file.endsWith("/") ? "" : "/"))) + name,
				FileType.Internal);
	}

	public GWTResourcesLoader parent() {
		int index = file.lastIndexOf("/");
		String dir = "";
		if (index > 0)
			dir = file.substring(0, index);
		return new GWTResourcesLoader(preloader, dir, type);
	}

	public GWTResourcesLoader sibling(String name) {
		return parent().child(fixSlashes(name));
	}

	public boolean exists() {
		return preloader.contains(file);
	}

	public long length() {
		return preloader.length(file);
	}

	public long lastModified() {
		return 0;
	}

	public String toString() {
		return file;
	}

	private static String fixSlashes(String path) {
		path = path.replace('\\', '/');
		if (path.endsWith("/")) {
			path = path.substring(0, path.length() - 1);
		}
		return path;
	}

}

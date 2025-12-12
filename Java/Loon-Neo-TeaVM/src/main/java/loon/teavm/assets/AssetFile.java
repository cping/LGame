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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import loon.teavm.builder.AssetFilter;
import loon.teavm.utils.StreamUtils;

public class AssetFile {

	public static AssetFile createHandle(String fileName) {
		return new AssetFile(fileName);
	}

	public static AssetFile createHandle(File file) {
		return new AssetFile(file);
	}

	public static AssetFile createCopyHandle(String fileName, String assetsChildDir) {
		return createCopyHandle(fileName, assetsChildDir, null);
	}

	public static AssetFile createCopyHandle(String fileName, String assetsChildDir, AssetFilter filter) {
		AssetFile assetFileHandle = new AssetFile(fileName);
		assetFileHandle.assetsChildDir = assetsChildDir;
		assetFileHandle.filter = filter;
		return assetFileHandle;
	}

	public String assetsChildDir = "";
	public AssetFilter filter;
	public File file;

	public AssetFile(File f) {
		this.file = f;
	}

	public AssetFile(String fileName) {
		this(new File(fileName));
	}

	public String path() {
		String path = file.getPath().replace('\\', '/');
		if (path.startsWith("/")) {
			path = path.substring(path.indexOf('/') + 1, path.length());
		}
		return path;
	}

	public String name() {
		return file.getName();
	}

	public String extension() {
		String name = file.getName();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return "";
		return name.substring(dotIndex + 1);
	}

	public String nameWithoutExtension() {
		String name = file.getName();
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return name;
		return name.substring(0, dotIndex);
	}

	public String pathWithoutExtension() {
		String path = file.getPath().replace('\\', '/');
		int dotIndex = path.lastIndexOf('.');
		if (dotIndex == -1)
			return path;
		return path.substring(0, dotIndex);
	}

	public File file() {
		return file;
	}

	public InputStream read() {
		try {
			return new FileInputStream(file());
		} catch (Exception ex) {
			if (file().isDirectory()) {
				throw new RuntimeException("Cannot open a stream to a directory: " + file, ex);
			}
			throw new RuntimeException("Error reading file: " + file, ex);
		}
	}

	public BufferedInputStream read(int bufferSize) {
		return new BufferedInputStream(read(), bufferSize);
	}

	public Reader reader() {
		return new InputStreamReader(read());
	}

	public AssetFile[] list() {
		String[] relativePaths = file().list();
		if (relativePaths == null) {
			return new AssetFile[0];
		}
		AssetFile[] handles = new AssetFile[relativePaths.length];
		for (int i = 0, n = relativePaths.length; i < n; i++)
			handles[i] = child(relativePaths[i]);
		return handles;
	}

	public AssetFile[] list(FileFilter filter) {
		File file = file();
		String[] relativePaths = file.list();
		if (relativePaths == null) {
			return new AssetFile[0];
		}
		AssetFile[] handles = new AssetFile[relativePaths.length];
		int count = 0;
		for (int i = 0, n = relativePaths.length; i < n; i++) {
			String path = relativePaths[i];
			AssetFile child = child(path);
			if (!filter.accept(child.file()))
				continue;
			handles[count] = child;
			count++;
		}
		if (count < relativePaths.length) {
			AssetFile[] newHandles = new AssetFile[count];
			System.arraycopy(handles, 0, newHandles, 0, count);
			handles = newHandles;
		}
		return handles;
	}

	public AssetFile[] list(FilenameFilter filter) {
		File file = file();
		String[] relativePaths = file.list();
		if (relativePaths == null) {
			return new AssetFile[0];
		}
		AssetFile[] handles = new AssetFile[relativePaths.length];
		int count = 0;
		for (int i = 0, n = relativePaths.length; i < n; i++) {
			String path = relativePaths[i];
			if (!filter.accept(file, path))
				continue;
			handles[count] = child(path);
			count++;
		}
		if (count < relativePaths.length) {
			AssetFile[] newHandles = new AssetFile[count];
			System.arraycopy(handles, 0, newHandles, 0, count);
			handles = newHandles;
		}
		return handles;
	}

	public AssetFile[] list(String suffix) {
		String[] relativePaths = file().list();
		if (relativePaths == null) {
			return new AssetFile[0];
		}
		AssetFile[] handles = new AssetFile[relativePaths.length];
		int count = 0;
		for (int i = 0, n = relativePaths.length; i < n; i++) {
			String path = relativePaths[i];
			if (!path.endsWith(suffix)) {
				continue;
			}
			handles[count] = child(path);
			count++;
		}
		if (count < relativePaths.length) {
			AssetFile[] newHandles = new AssetFile[count];
			System.arraycopy(handles, 0, newHandles, 0, count);
			handles = newHandles;
		}
		return handles;
	}

	public boolean isDirectory() {
		return file().isDirectory();
	}

	public AssetFile child(String name) {
		if (file.getPath().length() == 0) {
			return new AssetFile(new File(name));
		}
		return new AssetFile(new File(file, name));
	}

	public AssetFile sibling(String name) {
		return new AssetFile(new File(file.getParent(), name));
	}

	public AssetFile parent() {
		File parent = file.getParentFile();
		if (parent == null) {
			parent = new File("/");
		}
		return new AssetFile(parent);
	}

	public boolean delete() {
		return file().delete();
	}

	public void mkdirs() {
		file().mkdirs();
	}

	public boolean exists() {
		return file().exists();
	}

	public void writeString(String string, boolean append) {
		writeString(string, append, null);
	}

	public void writeString(String string, boolean append, String charset) {
		Writer writer = null;
		try {
			writer = writer(append, charset);
			writer.write(string);
		} catch (Exception ex) {
			throw new RuntimeException("Error writing file: " + file, ex);
		} finally {
			StreamUtils.closeQuietly(writer);
		}
	}

	public OutputStream write(boolean append, int bufferSize) {
		return new BufferedOutputStream(write(append), bufferSize);
	}

	public void write(InputStream input, boolean append) {
		OutputStream output = null;
		try {
			output = write(append);
			StreamUtils.copyStream(input, output);
		} catch (Exception ex) {
			throw new RuntimeException("Error stream writing to file: " + file, ex);
		} finally {
			StreamUtils.closeQuietly(input);
			StreamUtils.closeQuietly(output);
		}

	}

	public Writer writer(boolean append) {
		return writer(append, null);
	}

	public Writer writer(boolean append, String charset) {
		parent().mkdirs();
		try {
			FileOutputStream output = new FileOutputStream(file(), append);
			if (charset == null)
				return new OutputStreamWriter(output);
			else
				return new OutputStreamWriter(output, charset);
		} catch (IOException ex) {
			if (file().isDirectory())
				throw new RuntimeException("Cannot open a stream to a directory: " + file, ex);
			throw new RuntimeException("Error writing file: " + file, ex);
		}
	}

	public void writeBytes(byte[] bytes, boolean append) {
		OutputStream output = write(append);
		try {
			output.write(bytes);
		} catch (IOException ex) {
			throw new RuntimeException("Error writing file: " + file, ex);
		} finally {
			StreamUtils.closeQuietly(output);
		}
	}

	public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
		OutputStream output = write(append);
		try {
			output.write(bytes, offset, length);
		} catch (IOException ex) {
			throw new RuntimeException("Error writing file: " + file, ex);
		} finally {
			StreamUtils.closeQuietly(output);
		}
	}

	public OutputStream write(boolean append) {
		parent().mkdirs();
		try {
			return new FileOutputStream(file(), append);
		} catch (Exception ex) {
			if (file().isDirectory()) {
				throw new RuntimeException("Cannot open a stream to a directory: " + file, ex);
			}
			throw new RuntimeException("Error writing file: " + file, ex);
		}
	}

	static private void copyFile(AssetFile source, AssetFile dest) {
		try {
			dest.write(source.read(), false);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Error copying source file: " + source.file + "\n" + "To destination: " + dest.file, ex);
		}
	}

	static private void copyDirectory(AssetFile sourceDir, AssetFile destDir) {
		destDir.mkdirs();
		AssetFile[] files = sourceDir.list();
		for (int i = 0, n = files.length; i < n; i++) {
			AssetFile srcFile = files[i];
			AssetFile destFile = destDir.child(srcFile.name());
			if (srcFile.isDirectory()) {
				copyDirectory(srcFile, destFile);
			} else {
				copyFile(srcFile, destFile);
			}
		}
	}

	public void copyTo(AssetFile dest) {
		if (!isDirectory()) {
			if (dest.isDirectory()) {
				dest = dest.child(name());
			}
			copyFile(this, dest);
			return;
		}
		if (dest.exists()) {
			if (!dest.isDirectory())
				throw new RuntimeException("Destination exists but is not a directory: " + dest);
		} else {
			dest.mkdirs();
			if (!dest.isDirectory())
				throw new RuntimeException("Destination directory cannot be created: " + dest);
		}
		copyDirectory(this, dest.child(name()));
	}

	public void moveTo(AssetFile dest) {

		if (file().renameTo(dest.file()))
			return;
		copyTo(dest);
		delete();
		if (exists() && isDirectory())
			deleteDirectory();
	}

	public long length() {
		return file().length();
	}

	public boolean deleteDirectory() {
		return deleteDirectory(file());
	}

	static public AssetFile tempFile(String prefix) {
		try {
			return new AssetFile(File.createTempFile(prefix, null));
		} catch (IOException ex) {
			throw new RuntimeException("Unable to create temp file.", ex);
		}
	}

	static public AssetFile tempDirectory(String prefix) {
		try {
			File file = File.createTempFile(prefix, null);
			if (!file.delete())
				throw new IOException("Unable to delete temp file: " + file);
			if (!file.mkdir())
				throw new IOException("Unable to create temp directory: " + file);
			return new AssetFile(file);
		} catch (IOException ex) {
			throw new RuntimeException("Unable to create temp file.", ex);
		}
	}

	static private void emptyDirectory(File file, boolean preserveTree) {
		if (file.exists()) {
			File[] files = file.listFiles();
			if (files != null) {
				for (int i = 0, n = files.length; i < n; i++) {
					if (!files[i].isDirectory())
						files[i].delete();
					else if (preserveTree)
						emptyDirectory(files[i], true);
					else
						deleteDirectory(files[i]);
				}
			}
		}
	}

	static private boolean deleteDirectory(File file) {
		emptyDirectory(file, false);
		return file.delete();
	}
}

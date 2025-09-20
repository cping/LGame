/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.build.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;

final public class FileUtils {

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context) throws IOException {
		write(fileName, context, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(File file, String context, String coding) throws IOException {
		write(file, context.getBytes(coding), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context, boolean append) throws IOException {
		write(new File(fileName), context.getBytes("UTF-8"), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param bytes
	 * @throws IOException
	 */
	public static void write(File file, byte[] bytes) throws IOException {
		write(file, new ByteArrayInputStream(bytes), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param bytes
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, byte[] bytes, boolean append) throws IOException {
		write(file, new ByteArrayInputStream(bytes), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param input
	 * @throws IOException
	 */
	public static void write(File file, InputStream input) throws IOException {
		write(file, input, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param input
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, InputStream input, boolean append) throws IOException {
		makedirs(file);
		BufferedOutputStream output = null;
		try {
			int contentLength = input.available();
			output = new BufferedOutputStream(new FileOutputStream(file, append));
			while (contentLength-- > 0) {
				output.write(input.read());
			}
		} finally {
			close(input);
			close(output);
		}
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param chars
	 * @throws IOException
	 */
	public static void write(File file, char[] chars) throws IOException {
		write(file, new CharArrayReader(chars), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param chars
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, char[] chars, boolean append) throws IOException {
		write(file, new CharArrayReader(chars), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param string
	 * @throws IOException
	 */
	public static void write(File file, String string) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param string
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, String string, boolean append) throws IOException {
		write(file, new CharArrayReader(string.toCharArray()), append);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param reader
	 * @throws IOException
	 */
	public static void write(File file, Reader reader) throws IOException {
		write(file, reader, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param reader
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, Reader reader, boolean append) throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			int i = -1;
			while ((i = reader.read()) != -1) {
				writer.write(i);
			}

		} finally {
			close(reader);
			close(writer);
		}
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param records
	 * @throws IOException
	 */
	public static void write(File file, ArrayList<String> records) throws IOException {
		write(file, records, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param file
	 * @param records
	 * @param append
	 * @throws IOException
	 */
	public static void write(File file, ArrayList<String> records, boolean append) throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			for (Iterator<String> it = records.iterator(); it.hasNext();) {
				writer.write(it.next());
				writer.write(System.getProperty("line.separator", "\n"));
			}
		} finally {
			close(writer);
		}
	}

	/**
	 * 创建目录
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public static void makedirs(String fileName) throws IOException {
		makedirs(new File(fileName));

	}

	/**
	 * 创建目录
	 * 
	 * @param path
	 * @return
	 */
	public static boolean makedirs(File file) throws IOException {
		boolean isFile = checkFile(file);
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				File parent = file.getAbsoluteFile();
				if (!parent.isDirectory())
					parent = file.getParentFile();

				if (parent == null) {
					return isFile;
				}
				parent.mkdirs();
			}
		}
		return isFile;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param file
	 * @throws IOException
	 */
	private static boolean checkFile(File file) throws IOException {
		boolean exists = file.exists();
		if (exists && !file.isFile()) {
			return false;
		}
		return true;
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param input
	 * @param file
	 */
	public static void close(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param output
	 * @param file
	 */
	public static void close(OutputStream output) {
		if (output != null) {
			try {
				output.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param reader
	 * @param file
	 */
	public static void close(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象
	 * 
	 * @param writer
	 * @param file
	 */
	public static void close(Writer writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				closingFailed(e);
			}
		}
	}

	/**
	 * 关闭指定对象产生异常
	 * 
	 * @param file
	 * @param e
	 */
	public static void closingFailed(IOException e) {
		throw new RuntimeException(e.getMessage());
	}

	/**
	 * 拷贝指定长度数据流
	 * 
	 * @param is
	 * @param os
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream is, OutputStream os, long len) throws IOException {
		byte[] buf = new byte[1024];
		long copied = 0;
		int read;
		while ((read = is.read(buf)) != 0 && copied < len) {
			long leftToCopy = len - copied;
			int toWrite = read < leftToCopy ? read : (int) leftToCopy;
			os.write(buf, 0, toWrite);
			copied += toWrite;
		}
		return copied;
	}

	/**
	 * 拷贝指定数据流
	 * 
	 * @param in
	 * @param out
	 * @return
	 * @throws IOException
	 */
	public static long copy(InputStream in, OutputStream out) throws IOException {
		long written = 0;
		byte[] buffer = new byte[4096];
		while (true) {
			int len = in.read(buffer);
			if (len < 0) {
				break;
			}
			out.write(buffer, 0, len);
			written += len;
		}
		return written;
	}

	/**
	 * 读取指定长度数据流
	 * 
	 * @param is
	 * @param len
	 * @return
	 * @throws IOException
	 */
	public static byte[] read(InputStream is, long len) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(is, out, len);
		return out.toByteArray();
	}

	/**
	 * 获得指定文件大小
	 * 
	 * @param file
	 * @return
	 */
	public static long getKB(File file) {
		return getKB(file.length());
	}

	/**
	 * 将指定长度转化为KB显示
	 * 
	 * @param size
	 * @return
	 */
	public static long getKB(long size) {
		size /= 1000L;
		if (size == 0L) {
			size = 1L;
		}
		return size;
	}

	/**
	 * 删除指定目录下全部文件
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteAll(File dir) {
		String fileNames[] = dir.list();
		if (fileNames == null)
			return false;
		for (int i = 0; i < fileNames.length; i++) {
			File file = new File(dir, fileNames[i]);
			if (file.isFile())
				file.delete();
			else if (file.isDirectory())
				deleteAll(file);
		}

		return dir.delete();
	}

	/**
	 * 读取file文件,转为byte[]
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		InputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			extracted(file);
		}
		is.close();
		return bytes;
	}

	private static void extracted(File file) throws IOException {
		throw new IOException("Could not completely read file " + file.getName());
	}

	/**
	 * 读取指定文件为Byte[]
	 * 
	 * @param fileName
	 * @return
	 */
	public static byte[] readBytesFromFile(String fileName) {
		try {
			return readBytesFromFile(new File(fileName));
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * 读取file
	 * 
	 * @param file
	 * @return
	 */
	public static final InputStream read(File file) {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	/**
	 * 以指定全路径名读取文件
	 * 
	 * @param fileName
	 * @return
	 */
	public static final InputStream read(String fileName) {
		return read(new File(fileName));
	}

	/**
	 * 获得指定路径下的所有文件名(包含全路径)
	 * 
	 * @param path String 指定目录
	 * @return ArrayList 所有文件名(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllFiles(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);
				if (tempfile.isDirectory()) {
					ArrayList<String> arr = getAllFiles(tempfile.getPath());
					ret.addAll(arr);
					arr.clear();
					arr = null;
				} else {
					ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径下的所有目录(包含全路径)
	 * 
	 * @param path String 指定目录
	 * @return ArrayList 所有目录(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);
				if (tempfile.isDirectory()) {
					ret.add(tempfile.getAbsolutePath());
					ArrayList<String> arr = getAllDir(tempfile.getPath());
					ret.addAll(arr);
					arr.clear();
					arr = null;

				}
			}
		}
		return ret;

	}

	/**
	 * 获得指定路径下指定扩展名的所有文件(包含全路径)
	 * 
	 * @param path String 指定路径
	 * @param ext  String 扩展名
	 * @return ArrayList 所有文件(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllFiles(String path, String ext) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] exts = ext.split(",");
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);
				if (tempfile.isDirectory()) {
					ArrayList<String> arr = getAllFiles(tempfile.getPath(), ext);
					ret.addAll(arr);
					arr.clear();
					arr = null;
				} else {
					for (int j = 0; j < exts.length; j++) {
						if (getExtension(tempfile.getAbsolutePath()).equalsIgnoreCase(exts[j])) {
							ret.add(tempfile.getAbsolutePath());
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径下的文件列表(包含全路径),仅包含一级目录
	 * 
	 * @param path String 指定路径
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> Ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);

				if (!tempfile.isDirectory()) {
					Ret.add(tempfile.getAbsolutePath());

				}

			}
		}
		return Ret;
	}

	/**
	 * 获得指定路径下的子目录(包含全路径),仅包含一级目录
	 * 
	 * @param path String 指定路径
	 * @return ArrayList 子目录
	 * @throws IOException
	 */
	public static ArrayList<String> getDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);

				if (tempfile.isDirectory()) {
					ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径下指定扩展名的文件(包含全路径),仅包含一级目录
	 * 
	 * @param path String 指定路径
	 * @param ext  String 扩展名
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path, String ext) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);

				if (!tempfile.isDirectory()) {
					if (getExtension(tempfile.getAbsolutePath()).equalsIgnoreCase(ext))
						ret.add(tempfile.getAbsolutePath());

				}
			}
		}
		return ret;
	}

	/**
	 * 获得指定路径的目录名
	 * 
	 * @param name
	 * @return
	 */
	public static String getFileName(String name) {
		if (name == null) {
			return "";
		}
		int length = name.length();
		int size = name.lastIndexOf(System.getProperty("file.separator", "\\")) + 1;
		if (size < length) {
			return name.substring(size, length);
		} else {
			return "";
		}
	}

	/**
	 * 获得指定文件扩展名
	 * 
	 * @param name
	 * @return
	 */
	public static String getExtension(String name) {
		if (name == null) {
			return "";
		}
		int index = name.lastIndexOf(".");
		if (index == -1) {
			return "";
		} else {
			return name.substring(index + 1);
		}
	}

	/**
	 * 删除指定目录下的所有文件
	 * 
	 * @param path String 指定目录
	 * @throws Exception
	 */
	public static void deleteFile(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);
				// 如果是目录
				if (tempfile.isDirectory()) {
					deleteFile(tempfile.getPath());
				} else { // 如果不是
					tempfile.delete();

				}
			}
		}
	}

	/**
	 * 删除指定目录下的所有目录
	 * 
	 * @param path String 指定目录
	 * @throws Exception
	 */
	public static void deleteDir(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + System.getProperty("file.separator", "\\") + listFile[i]);
				if (tempfile.isDirectory()) {
					deleteDir(tempfile.getPath());
					tempfile.delete();

				}
			}
		}
	}

	public static byte[] readData(String file) {
		byte[] result = null;

		File f = new File(file);
		if (!f.exists())
			return null;

		int length = (int) f.length();

		try (FileInputStream out = new FileInputStream(f);) {
			result = new byte[length];
			out.read(result, 0, length);
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		}
	}

	public static String readFile(String file) {
		StringBuilder b = new StringBuilder();
		try {
			File f = new File(file);
			if (!f.exists())
				return null;

			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				b.append(line);
				b.append("\n");
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return b.toString();
	}

	public static ArrayList<String> readLines(File file) {
		ArrayList<String> lines = new ArrayList<>();
		try {
			if (!file.exists()) {
				return null;
			}
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return lines;
	}

	public static ArrayList<String> readLines(String file) {
		return readLines(new File(file));
	}

	public static void writeFile(String file, byte[] bytes) {
		File f = new File(file);
		try {
			FileUtils.makedirs(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try (FileOutputStream out = new FileOutputStream(f)) {
			out.write(bytes);
			out.flush();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void copyDir(String from, String to) {
		try {

			DirCopy copy = new DirCopy(Paths.get(from), Paths.get(to));
			Files.walkFileTree(Paths.get(from), copy);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void rmDir(String target) {
		try {
			Files.walkFileTree(Paths.get(target), new DirRemove());
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	public static void mkDir(String newPath, Path dir) {
		try {
			if (!Files.exists(dir))
				Files.createDirectories(dir);

			if (!Files.isDirectory(dir))
				throw new RuntimeException("Can not create path inside non-directory " + dir);

			Path toMake = Paths.get(dir.toString(), newPath);
			Files.createDirectories(toMake);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Path[] list(String from) throws IOException {
		try {
			DirList list = new DirList();
			Files.walkFileTree(Paths.get(from), list);
			System.out.println(list.toString());

			return list.getPaths();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}

	}

	public static void delete(String name) {
		Path x = Paths.get(name);
		if (!Files.exists(x))
			return;

		if (Files.isDirectory(x))
			return;

		try {
			Files.delete(x);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String print(String from) {
		try {
			DirList list = new DirList();
			Files.walkFileTree(Paths.get(from), list);
			return list.toString();
		} catch (IOException e) {
			return e.getMessage();
		}
	}

	public static class DirList extends SimpleFileVisitor<Path> {
		ArrayList<Path> paths = new ArrayList<>();

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
			paths.add(dir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
			paths.add(file);
			return FileVisitResult.CONTINUE;
		}

		public synchronized Path[] getPaths() {
			Path[] result = new Path[paths.size()];

			for (int i = 0; i < paths.size(); i++)
				result[i] = paths.get(i);

			return result;
		}

		public String toString() {
			StringBuilder build = new StringBuilder();

			for (Path p : paths) {
				if (Files.isDirectory(p))
					build.append(p.toAbsolutePath());
				else {
					build.append("\t");
					build.append(p.toAbsolutePath());
				}

				build.append("\n");
			}

			return build.toString();
		}
	}

	public static class DirRemove extends SimpleFileVisitor<Path> {
		@Override
		public FileVisitResult postVisitDirectory(Path dir, IOException e) throws IOException {
			if (e != null)
				throw e;

			Files.delete(dir);
			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
			Files.delete(file);
			return FileVisitResult.CONTINUE;
		}
	}

	public static class DirCopy extends SimpleFileVisitor<Path> {
		private Path from;
		private Path to;

		public DirCopy(Path from, Path to) {
			this.from = from;
			this.to = to;
		}

		@Override
		public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attr) throws IOException {
			Path target = to.resolve(from.relativize(dir));
			if (!Files.exists(target))
				Files.createDirectories(target);

			return FileVisitResult.CONTINUE;
		}

		@Override
		public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws IOException {
			Files.copy(file, to.resolve(from.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
			return FileVisitResult.CONTINUE;
		}
	}

	public static <T extends Serializable> void serialize(File f, T t) {
		try {
			makedirs(f);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try (FileOutputStream fo = new FileOutputStream(f); ObjectOutputStream o = new ObjectOutputStream(fo);) {
			o.writeObject(t);
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static Object deserialize(File f) {
		if (!f.exists())
			return null;
		try (FileInputStream fo = new FileInputStream(f); ObjectInputStream in = new ObjectInputStream(fo);) {
			return in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static ArrayList<String> copyDirectory(File source, File target) throws IOException {
		return copyDirectory(source, target, null, null);
	}

	public static ArrayList<String> copyDirectory(File source, File target, String limitExt,
			ArrayList<String> limitPaths) throws IOException {
		if (source == null) {
			return null;
		}
		if (!target.exists()) {
			target.mkdirs();
		}

		final String[] files = source.list();
		if (files != null) {
			for (String fileName : source.list()) {
				if (!StringUtils.isEmpty(limitExt) && limitExt.equalsIgnoreCase(getExtension(fileName))) {
					if (limitPaths != null && !limitPaths.contains(fileName)) {
						limitPaths.add(fileName);
					}
					continue;
				}
				File s = new File(source, fileName);
				File t = new File(target, fileName);
				if (s.isDirectory()) {
					copyDirectory(s, t, limitExt, limitPaths);
				} else {
					copy(s, t);
				}
			}
		}
		return limitPaths;
	}

	public static void copy(File source, File target) throws IOException {
		try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(target)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}

	public static boolean deleteDirectory(File directory) {
		if (directory.exists()) {
			File[] files = directory.listFiles();
			if (null != files) {
				for (File file : files) {
					if (file.isDirectory()) {
						deleteDirectory(file);
					} else {
						file.delete();
					}
				}
			}
		}
		return directory.delete();
	}
}

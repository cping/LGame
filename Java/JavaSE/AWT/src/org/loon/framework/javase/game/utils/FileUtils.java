package org.loon.framework.javase.game.utils;

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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

import org.loon.framework.javase.game.core.LSystem;
import org.loon.framework.javase.game.core.resource.Resources;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
final public class FileUtils {

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context)
			throws IOException {
		write(fileName, context, false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(File file, String context, String coding)
			throws IOException {
		write(file, context.getBytes(coding), false);
	}

	/**
	 * 写入数据到指定目标
	 * 
	 * @param fileName
	 * @param context
	 * @throws IOException
	 */
	public static void write(String fileName, String context, boolean append)
			throws IOException {
		write(new File(fileName), context.getBytes(LSystem.encoding), append);
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
	public static void write(File file, byte[] bytes, boolean append)
			throws IOException {
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
	public static void write(File file, InputStream input, boolean append)
			throws IOException {
		makedirs(file);
		BufferedOutputStream output = null;
		try {
			int contentLength = input.available();
			output = new BufferedOutputStream(
					new FileOutputStream(file, append));
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
	public static void write(File file, char[] chars, boolean append)
			throws IOException {
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
	public static void write(File file, String string, boolean append)
			throws IOException {
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
	public static void write(File file, Reader reader, boolean append)
			throws IOException {
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
	public static void write(File file, ArrayList <String> records) throws IOException {
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
	public static void write(File file, ArrayList<String> records, boolean append)
			throws IOException {
		makedirs(file);
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(file, append));
			for (Iterator<String> it = records.iterator(); it.hasNext();) {
				writer.write((String) it.next());
				writer.write(LSystem.LS);
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
	public static void makedirs(File file) throws IOException {
		checkFile(file);
		File parentFile = file.getParentFile();
		if (parentFile != null) {
			if (!parentFile.exists() && !parentFile.mkdirs()) {
				throw new IOException("Creating directories "
						+ parentFile.getPath() + " failed.");
			}
		}
	}

	/**
	 * 剪切掉地址内反斜杆
	 * 
	 * @param str
	 * @return
	 */
	public static String cutFileName(String str) {
		if (str == null)
			return null;
		str = cutDC(str);
		int idx;
		idx = str.lastIndexOf("\\");
		if ((idx + 1) < str.length()) {
			str = str.substring(idx + 1);
		}
		idx = str.lastIndexOf("/");
		if ((idx + 1) < str.length()) {
			str = str.substring(idx + 1);
		}
		return str;
	}

	/**
	 * 剪切反斜杆
	 * 
	 * @param str
	 * @return
	 */
	private static String cutDC(String str) {
		if (str == null)
			return null;
		if (str.equals("\"\""))
			return "";
		if (str.length() > 1) {
			if (str.substring(0, 1).equals("\"")) {
				str = str.substring(1);
			}
		}
		if (str.length() > 1) {
			if (str.substring(str.length() - 1).equals("\"")) {
				str = str.substring(0, str.length() - 1);
			}
		}
		return str;
	}

	/**
	 * 检查文件是否存在
	 * 
	 * @param file
	 * @throws IOException
	 */
	private static void checkFile(File file) throws IOException {
		boolean exists = file.exists();
		if (exists && !file.isFile()) {
			throw new IOException("File " + file.getPath()
					+ " is actually not a file.");
		}
	}

	/**
	 * 更改文件名
	 * 
	 * @param oldName
	 * @param newName
	 */
	public static void reName(String oldName, String newName) {
		FileUtils.reName(new File(oldName), new File(newName));
	}

	/**
	 * 更改文件名
	 * 
	 * @param oldName
	 * @param newName
	 */
	public static void reName(File oldFile, File newFile) {
		try {
			checkFile(oldFile);
			checkFile(newFile);
			oldFile.renameTo(newFile);
		} catch (IOException e) {
			throw new RuntimeException("Change the file name fails!", e);
		}
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
	public static long copy(InputStream is, OutputStream os, long len)
			throws IOException {
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
	public static long copy(InputStream in, OutputStream out)
			throws IOException {
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
	 * 跳过指定字节后读取指定数据流
	 * 
	 * @param is
	 * @param len
	 * @throws IOException
	 */
	public static void skipForSure(InputStream is, long len) throws IOException {
		long leftToSkip = len;
		while (leftToSkip > 0) {
			long skiped = is.skip(leftToSkip);
			leftToSkip -= skiped;
		}
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
	 * 拼装文件名
	 * 
	 * @param name
	 * @param ext
	 * @return
	 */
	public static String getName(String name, String ext) {
		return (name + "." + ext).intern();
	}

	/**
	 * 消除文件扩展名
	 * 
	 * @param name
	 * @return
	 */
	public static String getNoExtensionName(String name) {
		if (name.indexOf(".") == -1)
			return name;
		else
			return name.substring(0, name.lastIndexOf(getExtension(name)) - 1);
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
	 * 以指定编码格式，copy指定文件内容
	 * 
	 * @param sSource
	 * @param sDest
	 * @param srcEncoding
	 * @param destEncoding
	 * @return
	 */
	public static final boolean copyFile(final String sSource,
			final String sDest, final String srcEncoding,
			final String destEncoding) {
		try {
			File src = new File(sSource);
			if (!src.exists()) {
				return false;
			}
			File dest = new File(sDest);
			String reuslt = FileUtils.readFile(src, srcEncoding);
			FileUtils.write(dest, reuslt, destEncoding);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * copy指定文件内容
	 * 
	 * @param sSource
	 * @param sDest
	 * @return
	 */
	public static final boolean copyFile(final String sSource,
			final String sDest) {
		try {
			File src = new File(sSource);
			if (!src.exists()) {
				return false;
			}
			File dest = new File(sDest);
			FileUtils.makedirs(dest);
			InputStream in = new BufferedInputStream(new FileInputStream(src));
			OutputStream os = new BufferedOutputStream(new FileOutputStream(
					dest));
			byte[] buffer = new byte[8 * 1024];
			int len;
			while ((len = in.read(buffer)) > 0) {
				os.write(buffer, 0, len);
			}
			in.close();
			os.close();
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * copy指定文件架下所有内容
	 * 
	 * @param listFile
	 * @param targetFloder
	 */
	public static void copyFiles(String listFile, String targetFloder) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(listFile));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				copyFile(tempString, targetFloder);
			}
			reader.close();
		} catch (IOException e) {
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
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
	 * String方式以指定编码读取文件
	 * 
	 * @param filePath
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public static String readFile(String filePath, String encode)
			throws IOException {
		return readFile(new File(filePath), encode);
	}

	/**
	 * file方式以指定编码读取文件
	 * 
	 * @param file
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public static String readFile(File file, String encode) throws IOException {
		BufferedReader reader = null;
		InputStream in = null;
		String s;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			InputStreamReader isr = new InputStreamReader(in, encode);
			reader = new BufferedReader(isr);
			s = read(reader);
		} finally {
			try {
				if (in != null) {
					in.close();
					in = null;
				}
			} catch (IOException ioexception) {
			}
			try {
				if (reader != null) {
					reader.close();
					reader = null;
				}
			} catch (IOException ioexception1) {
			}
		}
		return s;
	}

	/**
	 * 读取file文件,转为byte[]
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] readBytesFromFile(File file) throws IOException {
		InputStream is = new DataInputStream(new BufferedInputStream(
				new FileInputStream(file)));
		long length = file.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			extracted(file);
		}
		is.close();
		return bytes;
	}

	private static void extracted(File file) throws IOException {
		throw new IOException("Could not completely read file "
				+ file.getName());
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
	 * 读取inputStream为string,并转为指定编码
	 * 
	 * @param inputStream
	 * @param encoding
	 * @return
	 */
	public static final String readToString(InputStream inputStream,
			String encoding) {
		try {
			byte[] buffer = Resources.getDataSource(inputStream);
			return new String(buffer, encoding);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 读取inputStream为string
	 * 
	 * @param inputStream
	 * @return
	 */
	public static final String readToString(InputStream inputStream) {
		return readToString(inputStream, LSystem.encoding);
	}


	/**
	 * 读取文件
	 * 
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	public static final String read(Reader reader) throws IOException {
		StringWriter writer = new StringWriter(8192);
		copy(reader, writer);
		return writer.getBuffer().toString();
	}

	/**
	 * 以BufferedReader方式读取数据
	 * 
	 * @param buffer
	 * @return
	 * @throws IOException
	 */
	public static String read(BufferedReader buffer) throws IOException {
		StringBuffer sb = new StringBuffer();
		for (String line = ""; (line = buffer.readLine()) != null;) {
			sb.append(line + LSystem.LS);
		}
		return sb.toString();
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

	public static final void copy(Reader from, Writer to) throws IOException {
		char buffer[] = new char[8192];
		int charsRead;
		while ((charsRead = from.read(buffer)) != -1) {
			to.write(buffer, 0, charsRead);
			to.flush();
		}
	}

	/**
	 * 从文件路径得到文件名。
	 * 
	 * @param filePath
	 *            文件的路径，可以是相对路径也可以是绝对路径
	 * @return 对应的文件名
	 * @version 0.1
	 */
	public static String toFileName(String filePath) {
		return new File(filePath).getName();
	}

	/**
	 * 从文件名得到文件绝对路径。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 对应的文件路径
	 * @version 0.1
	 */
	public static String toFilePath(String fileName) {
		return new File(fileName).getAbsolutePath();
	}

	/**
	 * 取得格式化的绝对路径
	 * 
	 * @param fileName
	 * @return
	 */
	final static public String toformatPath(String fileName) {
		return new File(fileName).getAbsolutePath();
	}

	/**
	 * @function 得到文件的类型。
	 * @param fileName
	 *            文件名
	 * @return 文件名中的类型部分
	 * @version 0.1
	 */
	public static String toTypePart(String fileName) {
		int point = fileName.lastIndexOf('.');
		int length = fileName.length();
		return (point == -1 || point == length - 1) ? "" : fileName.substring(
				point + 1, length);
	}

	/**
	 * 得到文件的类型。
	 * 
	 * @param file
	 *            文件
	 * @return 文件名中的类型部分
	 * @version 0.1
	 */
	public static String toFileType(File file) {
		return toTypePart(file.getName());
	}

	/**
	 * 得到文件的名字部分。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 文件名中的名字部分
	 * @version 0.1
	 */
	public static String toNamePart(String fileName) {
		int point = toPathLsatIndex(fileName);
		int length = fileName.length();
		if (point == -1) {
			return fileName;
		} else if (point == length - 1) {
			int secondPoint = toPathLsatIndex(fileName, point - 1);
			if (secondPoint == -1) {
				if (length == 1) {
					return fileName;
				} else {
					return fileName.substring(0, point);
				}
			} else {
				return fileName.substring(secondPoint + 1, point);
			}
		} else {
			return fileName.substring(point + 1);
		}
	}

	/**
	 * 得到文件名中的父路径部分。
	 * 
	 * @param fileName
	 *            文件名
	 * @return 父路径，不存在或者已经是父目录时返回""
	 * @version 0.1
	 */
	public static String toPathPart(String fileName) {
		int point = toPathLsatIndex(fileName);
		int length = fileName.length();
		if (point == -1) {
			return "";
		} else if (point == length - 1) {
			int secondPoint = toPathLsatIndex(fileName, point - 1);
			if (secondPoint == -1) {
				return "";
			} else {
				return fileName.substring(0, secondPoint);
			}
		} else {
			return fileName.substring(0, point);
		}
	}

	/**
	 * 得到路径分隔符在文件路径中首次出现的位置。
	 * 
	 * @param fileName
	 *            文件路径
	 * @return 路径分隔符在路径中首次出现的位置，没有出现时返回-1。
	 * @version 0.5
	 */
	public static int toPathIndex(String fileName) {
		int point = fileName.indexOf('/');
		if (point == -1) {
			point = fileName.indexOf('\\');
		}
		return point;
	}

	/**
	 * 得到路径分隔符在文件路径中指定位置后首次出现的位置。
	 * 
	 * @param fileName
	 *            文件路径
	 * @param fromIndex
	 *            开始查找的位置
	 * @return 路径分隔符在路径中指定位置后首次出现的位置，没有出现时返回-1。
	 * @version 0.1
	 */
	public static int toPathIndex(String fileName, int fromIndex) {
		int point = fileName.indexOf('/', fromIndex);
		if (point == -1) {
			point = fileName.indexOf('\\', fromIndex);
		}
		return point;
	}

	/**
	 * 得到路径分隔符在文件路径中最后出现的位置。
	 * 
	 * @param fileName
	 *            文件路径
	 * @return 路径分隔符在路径中最后出现的位置，没有出现时返回-1。
	 * @version 0.1
	 */
	public static int toPathLsatIndex(String fileName) {
		int point = fileName.lastIndexOf('/');
		if (point == -1) {
			point = fileName.lastIndexOf('\\');
		}
		return point;
	}

	/**
	 * 得到路径分隔符在文件路径中指定位置前最后出现的位置。
	 * 
	 * @param fileName
	 *            文件路径
	 * @param fromIndex
	 *            开始查找的位置
	 * @return 路径分隔符在路径中指定位置前最后出现的位置，没有出现时返回-1。
	 * @version 0.1
	 */
	public static int toPathLsatIndex(String fileName, int fromIndex) {
		int point = fileName.lastIndexOf('/', fromIndex);
		if (point == -1) {
			point = fileName.lastIndexOf('\\', fromIndex);
		}
		return point;
	}

	/**
	 * 得到相对路径,文件名不是目录名的子节点时返回文件名。
	 * 
	 * @param pathName
	 *            目录名
	 * @param fileName
	 *            文件名
	 * @return 得到文件名相对于目录名的相对路径，目录下不存在该文件时返回文件名
	 * @version 0.1
	 */
	public static String toSubpath(String pathName, String fileName) {
		return (fileName.indexOf(pathName) != -1) ? fileName.substring(fileName
				.indexOf(pathName)
				+ pathName.length() + 1) : fileName;
	}

	/**
	 * 返回系统所在路径
	 * 
	 * @return
	 */
	public static final String toFileRoot() { // 返回所有系统目录
		File[] dvs = File.listRoots();
		String[] rootname = new String[dvs.length];
		StringBuffer path = new StringBuffer();
		for (int i = 0; i < rootname.length; i++) {
			rootname[i] = dvs[i].getPath();

			path.append(rootname[i].toString());
		}
		return path.toString();
	}

	/**
	 * 获得文件当前所在目录
	 * 
	 * @param 无值
	 * @return 当前文件路径
	 * @version 0.1
	 */
	public static String toNonceDir() {
		return new File("").getAbsoluteFile().getAbsolutePath();
	}

	/**
	 * 获得指定路径目录列表
	 * 
	 * @param path
	 * @return
	 * @version 0.1
	 */
	public static String[] toDirList(File path) {
		String pathName = path.getPath();
		String[] fileList;
		if ("".equals(pathName))
			path = new File(".");
		else
			path = new File(pathName);
		// 获得目录文件列表
		if (path.isDirectory())
			fileList = path.list();
		else
			return null;
		return fileList;
	}

	/**
	 * 获得指定路径目录列表
	 * 
	 * @param path
	 * @return
	 * @version 0.1
	 */
	public static String[] toDirList(String pathName) {
		return toDirList(new File(pathName));
	}


	/**
	 * 获得指定路径下的所有文件名(包含全路径)
	 * 
	 * @param path
	 *            String 指定目录
	 * @return ArrayList 所有文件名(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllFiles(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
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
	 * @param path
	 *            String 指定目录
	 * @return ArrayList 所有目录(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
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
	 * @param path
	 *            String 指定路径
	 * @param ext
	 *            String 扩展名
	 * @return ArrayList 所有文件(包含全路径)
	 * @throws IOException
	 */
	public static ArrayList<String> getAllFiles(String path, String ext)
			throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] exts = ext.split(",");
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					ArrayList<String> arr = getAllFiles(tempfile.getPath(), ext);
					ret.addAll(arr);
					arr.clear();
					arr = null;
				} else {
					for (int j = 0; j < exts.length; j++) {
						if (getExtension(tempfile.getAbsolutePath())
								.equalsIgnoreCase(exts[j])) {
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
	 * @param path
	 *            String 指定路径
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> Ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

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
	 * @param path
	 *            String 指定路径
	 * @return ArrayList 子目录
	 * @throws IOException
	 */
	public static ArrayList<String> getDir(String path) throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

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
	 * @param path
	 *            String 指定路径
	 * @param ext
	 *            String 扩展名
	 * @return ArrayList 文件名
	 * @throws IOException
	 */
	public static ArrayList<String> getFiles(String path, String ext)
			throws IOException {
		File file = new File(path);
		ArrayList<String> ret = new ArrayList<String>();
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);

				if (!tempfile.isDirectory()) {
					if (getExtension(tempfile.getAbsolutePath())
							.equalsIgnoreCase(ext))
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
		int size = name.lastIndexOf(LSystem.FS) + 1;
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
	 * @param path
	 *            String 指定目录
	 * @throws Exception
	 */
	public static void deleteFile(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
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
	 * @param path
	 *            String 指定目录
	 * @throws Exception
	 */
	public static void deleteDir(String path) throws Exception {
		File file = new File(path);
		String[] listFile = file.list();
		if (listFile != null) {
			for (int i = 0; i < listFile.length; i++) {
				File tempfile = new File(path + LSystem.FS + listFile[i]);
				if (tempfile.isDirectory()) {
					deleteDir(tempfile.getPath());
					tempfile.delete();

				}
			}
		}

	}

	/**
	 * 返回文件长度
	 * 
	 * @param filePath
	 * @return
	 */
	public static long getFileSize(String filePath) {
		return new File(filePath).length();
	}

}
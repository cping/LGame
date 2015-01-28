/**
 * 
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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon.core.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import loon.LSystem;
import loon.utils.StringUtils;
import loon.utils.collection.ArrayByte;

public abstract class Resources {

	static ClassLoader classLoader;

	static {
		try {
			classLoader = Resources.class.getClassLoader();
		} catch (Exception e) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
	}

	// 以下为0.3.2版中新增资源读取方法(为避免冲突，同时保留了原始读取方式)
	/**
	 * 从类中读取资源
	 * 
	 * @param path
	 * @return
	 */
	public static Resource classRes(String path) {
		return new ClassRes(path);
	}

	/**
	 * 从文件中读取资源
	 * 
	 * @param path
	 * @return
	 */
	public static Resource fileRes(String path) {
		return new FileRes(path);
	}

	/**
	 * 从远程地址读取资源
	 * 
	 * @param path
	 * @return
	 */
	public static Resource remoteRes(String path) {
		return new RemoteRes(path);
	}

	/**
	 * 从SD卡中读取资源
	 * 
	 * @param path
	 * @return
	 */
	public static Resource sdRes(String path) {
		return new SDRes(path);
	}

	/**
	 * 以字符串命令，加载任意类型资源
	 * 
	 * @param path
	 * @return
	 */
	public final static InputStream strRes(final String path) {
		if (path == null) {
			return null;
		}
		InputStream in = null;
		if (path.indexOf("->") == -1) {
			if (path.startsWith("sd:")) {
				in = sdRes(path.substring(3, path.length())).getInputStream();
			} else if (path.startsWith("class:")) {
				in = classRes(path.substring(6, path.length()))
						.getInputStream();
			} else if (path.startsWith("path:")) {
				in = fileRes(path.substring(5, path.length())).getInputStream();
			} else if (path.startsWith("url:")) {
				in = remoteRes(path.substring(4, path.length()))
						.getInputStream();
			}
		} else {
			String[] newPath = StringUtils.split(path, "->");
			in = new ByteArrayInputStream(LPKResource.openResource(
					newPath[0].trim(), newPath[1].trim()));
		}
		return in;
	}

	final static private Object LOCK = new Object();

	private final static HashMap<String, Object> lazyResources = new HashMap<String, Object>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	private Resources() {
	}

	/**
	 * 获得资源名迭代器
	 * 
	 * @return
	 */
	public static Iterator<String> getNames() {
		synchronized (LOCK) {
			return lazyResources.keySet().iterator();
		}
	}

	/**
	 * 检查指定资源名是否存在
	 * 
	 * @param resName
	 * @return
	 */
	public static boolean contains(String resName) {
		synchronized (LOCK) {
			return (lazyResources.get(resName) != null);
		}
	}

	/**
	 * 删除指定名称的资源
	 * 
	 * @param resName
	 */
	public static void remove(String resName) {
		synchronized (LOCK) {
			lazyResources.remove(resName);
		}
	}

	/**
	 * 读取指定资源为InputStream
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getResourceAsStream(final String fileName) {
		if ((fileName.indexOf("file:") >= 0) || (fileName.indexOf(":/") > 0)) {
			try {
				URL url = new URL(fileName);
				return new BufferedInputStream(url.openStream());
			} catch (Exception e) {
				return null;
			}
		}
		return new ByteArrayInputStream(getResource(fileName).getData());
	}

	public static InputStream openResource(final String resName)
			throws IOException {
		InputStream resource = strRes(resName);
		if (resource != null) {
			return resource;
		}
		File file = new File(resName);
		if (file.exists()) {
			try {
				return new BufferedInputStream(new FileInputStream(file));
			} catch (FileNotFoundException e) {
				throw new IOException(resName + " file not found !");
			}
		} else {
			if (classLoader != null) {
				InputStream in = null;
				try {
					in = classLoader.getResourceAsStream(resName);
				} catch (Exception e) {
					throw new RuntimeException(resName + " not found!");
				}
				return in;
			} else {
				try {
					return new FileInputStream(file);
				} catch (FileNotFoundException e) {
					throw new IOException(resName + " not found!");
				}
			}
		}
	}

	private static boolean isExists(String fileName) {
		return new File(fileName).exists();
	}

	/**
	 * 将指定文件转为ArrayByte
	 * 
	 * @param fileName
	 * @return
	 */
	public static ArrayByte getResource(final String fileName) {
		InputStream resource = strRes(fileName);
		if (resource != null) {
			ArrayByte result = new ArrayByte();
			try {
				result.write(resource);
				resource.close();
				result.reset();
			} catch (IOException ex) {
				result = null;
			}
			return result;
		}
		String innerName = fileName;
		String keyName = innerName.replaceAll(" ", "").toLowerCase();
		synchronized (LOCK) {
			if (lazyResources.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
				lazyResources.clear();
				System.gc();
			}
			byte[] data = (byte[]) lazyResources.get(keyName);
			if (data != null) {
				return new ArrayByte(data);
			}
		}
		BufferedInputStream in = null;
		boolean canInner = StringUtils.startsWith(innerName, '.')
				|| (StringUtils.startsWith(innerName, '/') && LSystem
						.isWindows());
		if (!isExists(innerName) && !canInner) {
			innerName = ("/" + innerName).intern();
			canInner = true;
		}
		if (canInner) {
			if (StringUtils.startsWith(innerName, '.')) {
				innerName = innerName.substring(1, innerName.length());
			}
			innerName = StringUtils.replaceIgnoreCase(innerName, "\\", "/");
			innerName = innerName.substring(1, innerName.length());
		} else {
			if (StringUtils.startsWith(innerName, '\\')) {
				innerName = innerName.substring(1, innerName.length());
			}
		}
		if (!canInner) {
			try {
				in = new BufferedInputStream(new FileInputStream(new File(
						innerName)));
			} catch (FileNotFoundException ex) {
				throw new RuntimeException(ex);
			}
		}
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			in.close();
			byteArray.reset();
			lazyResources.put(keyName, byteArray.getData());
			return byteArray;
		} catch (IOException ex) {
			throw new RuntimeException(fileName + " file not found !");
		}
	}

	/**
	 * 将InputStream转为byte[]
	 * 
	 * @param is
	 * @return
	 */
	final static public byte[] getDataSource(InputStream is) {
		if (is == null) {
			return null;
		}
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] bytes = new byte[8192];
		try {
			int read;
			while ((read = is.read(bytes)) >= 0) {
				byteArrayOutputStream.write(bytes, 0, read);
			}
			bytes = byteArrayOutputStream.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (byteArrayOutputStream != null) {
					byteArrayOutputStream.flush();
					byteArrayOutputStream = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}
		return bytes;
	}

	public static void destroy() {
		lazyResources.clear();
	}

	public void finalize() {
		destroy();
	}

}

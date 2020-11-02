package org.loon.framework.android.game.core.resource;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.loon.framework.android.game.core.LSystem;
import org.loon.framework.android.game.utils.collection.ArrayByte;

import android.content.res.AssetManager;

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
 * @project loonframework
 * @author chenpeng
 * @email ceponline@yahoo.com.cn
 * @version 0.1.0
 */
public abstract class Resources {

	private static ClassLoader classLoader;

	private final static Object lock = new Object();

	private final static Map<String, Object> lazyResources = new HashMap<String, Object>(
			LSystem.DEFAULT_MAX_CACHE_SIZE);

	static {
		try {
			// 在Android中Thread.currentThread()方式等于被废|||……
			// classLoader = Thread.currentThread().getContextClassLoader();
			classLoader = Resources.class.getClassLoader();
		} catch (Throwable ex) {
			classLoader = null;
		}
	}

	/**
	 * 获得资源名迭代器
	 * 
	 * @return
	 */
	public static Iterator<String> getNames() {
		synchronized (lock) {
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
		synchronized (lock) {
			return (lazyResources.get(resName) != null);
		}
	}

	/**
	 * 删除指定名称的资源
	 * 
	 * @param resName
	 */
	public static void remove(String resName) {
		synchronized (lock) {
			lazyResources.remove(resName);
		}
	}

	public static void destroy() {
		lazyResources.clear();
	}

	public void finalize() {
		destroy();
	}

	/**
	 * 获得当前系统的ClassLoader
	 * 
	 * @return
	 */
	public final static ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * 获得指定类的ClassLoader
	 * 
	 * @param clazz
	 * @return
	 */
	public final static ClassLoader getClassLoader(Class<?> clazz) {
		return clazz.getClassLoader();
	}

	/**
	 * 返回针对当前游戏应用的资源管理器
	 * 
	 * @return
	 */
	public final static android.content.res.Resources getResources() {
		return LSystem.getActivity().getResources();
	}

	/**
	 * 打开一个指定的ClassLoader资源
	 * 
	 * @param resName
	 * @param cl
	 * @return
	 * @throws IOException
	 */
	public static InputStream openResource(String resName, final ClassLoader c)
			throws IOException {
		final InputStream result = c.getResourceAsStream(resName);
		if (result == null) {
			throw new IOException("Exception to load resource [" + resName
					+ "] .");
		}
		return result;
	}

	private final static String assetsFlag = "assets";

	/**
	 * 打开当前类加载器下的资源文件
	 * 
	 * @param resName
	 * @return
	 * @throws IOException
	 */
	public static InputStream openResource(String resName) throws IOException {
		String fileName = resName.toLowerCase();
		if (fileName.startsWith(assetsFlag)
				|| fileName.startsWith(LSystem.FS + assetsFlag)) {
			boolean flag = resName.startsWith(LSystem.FS);
			AssetManager asset = LSystem.getActivity().getAssets();
			String file;
			if (flag) {
				file = resName.substring(1);
			} else {
				file = resName;
			}
			int index = file.indexOf(LSystem.FS) + 1;
			if (index != -1) {
				file = resName.substring(index);
			} else {
				int length = file.length();
				int size = file.lastIndexOf(LSystem.FS, 0) + 1;
				if (size < length) {
					file = file.substring(size, length);
				}
			}
			return asset.open(file);
		}
		if (classLoader != null) {
			InputStream in = null;
			try {
				in = classLoader.getResourceAsStream(resName);
			} catch (Exception e) {
				try {
					in = LSystem.getResourceAsStream(resName);
				} catch (Exception ex) {
					throw new RuntimeException(resName + " not found!");
				}
			}
			if (in == null) {
				in = LSystem.getResourceAsStream(resName);
			}
			return in;
		} else {
			return LSystem.getActivity().getAssets().open(resName);
		}
	}

	/**
	 * 加载资源文件
	 * 
	 * @param resName
	 * @return
	 */
	public final static ArrayByte getResource(String resName) {
		if (resName == null) {
			return null;
		}
		resName = resName.startsWith(LSystem.FS) ? resName.substring(1)
				: resName;
		String innerName = resName;
		String keyName = innerName.replaceAll(" ", "").toLowerCase();
		synchronized (lock) {
			if (lazyResources.size() > LSystem.DEFAULT_MAX_CACHE_SIZE) {
				lazyResources.clear();
			}
			byte[] data = (byte[]) lazyResources.get(keyName);
			if (data != null) {
				return new ArrayByte(data);
			}
		}
		InputStream in = null;
		// 外部文件标志
		boolean filePath = innerName.startsWith("$");
		if (filePath || isExists(resName)) {
			try {
				innerName = innerName.substring(1, innerName.length());
				in = new BufferedInputStream(new FileInputStream(new File(
						innerName)));
			} catch (Exception ex) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				throw new RuntimeException(resName + " file not found !");
			}
		} else {
			try {
				in = openResource(innerName);
			} catch (IOException e) {
				throw new RuntimeException(resName + " file not found !");
			}
		}
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			in.close();
			byteArray.reset();
			lazyResources.put(keyName, byteArray.getData());
		} catch (IOException ex) {
			byteArray = null;
		}
		if (byteArray == null) {
			throw new RuntimeException(resName + " file not found !");
		}
		return byteArray;
	}

	/**
	 * 加载资源文件(无缓存)
	 * 
	 * @param resName
	 * @return
	 */
	public final static ArrayByte getNotCacheResource(String resName) {
		if (resName == null) {
			return null;
		}
		resName = resName.startsWith(LSystem.FS) ? resName.substring(1)
				: resName;
		InputStream in = null;
		// 外部文件标志
		boolean filePath = resName.startsWith("$");
		if (filePath || isExists(resName)) {
			try {
				resName = resName.substring(1, resName.length());
				in = new BufferedInputStream(new FileInputStream(new File(
						resName)));
			} catch (Exception ex) {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
					}
				}
				throw new RuntimeException(resName + " file not found !");
			}
		} else {
			try {
				in = openResource(resName);
			} catch (IOException e) {
				throw new RuntimeException(resName + " file not found !");
			}
		}
		ArrayByte byteArray = new ArrayByte();
		try {
			byteArray.write(in);
			in.close();
			byteArray.reset();
		} catch (IOException ex) {
			byteArray = null;
		}
		if (byteArray == null) {
			throw new RuntimeException(resName + " file not found !");
		}
		return byteArray;
	}

	/**
	 * 加载资源文件为InputStream格式
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

	/**
	 * 加载资源文件为InputStream格式(无缓存)
	 * 
	 * @param fileName
	 * @return
	 */
	public static InputStream getNotCacheResourceAsStream(final String fileName) {
		if ((fileName.indexOf("file:") >= 0) || (fileName.indexOf(":/") > 0)) {
			try {
				URL url = new URL(fileName);
				return new BufferedInputStream(url.openStream());
			} catch (Exception e) {
				return null;
			}
		}
		return new ByteArrayInputStream(getNotCacheResource(fileName).getData());
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

	public final static InputStream getResource(Class<?> clazz, String resName) {
		return clazz.getResourceAsStream(resName);
	}

	private static boolean isExists(String fileName) {
		return new File(fileName).exists();
	}

	/**
	 * 通过url读取网络文件流
	 * 
	 * @param uri
	 * @return
	 */
	final static public byte[] getHttpStream(final String uri) {
		URL url;
		try {
			url = new URL(uri);
		} catch (Exception e) {
			return null;
		}
		InputStream is = null;
		try {
			is = url.openStream();
		} catch (Exception e) {
			return null;
		}
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] arrayByte = null;
		try {
			arrayByte = new byte[4096];
			int read;
			while ((read = is.read(arrayByte)) >= 0) {
				os.write(arrayByte, 0, read);
			}
			arrayByte = os.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (os != null) {
					os.close();
					os = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (IOException e) {
			}
		}

		return arrayByte;
	}

}

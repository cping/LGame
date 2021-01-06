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
package loon;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

/**
 * 多jar打包加载用类（修改自org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader）
 * 
 * Manifest-Version: 1.0 Rsrc-Class-Path: ./ LGame-JavaSE-0.3.3.jar lwjgl.jar
 * Class-Path: . Rsrc-Main-Class: com.mianfilename Main-Class: loon.JarInternal
 */
public class JarInternal {

	static final String REDIRECTED_CLASS_PATH_MANIFEST_NAME = "Rsrc-Class-Path"; 
	static final String REDIRECTED_MAIN_CLASS_MANIFEST_NAME = "Rsrc-Main-Class"; 
	static final String DEFAULT_REDIRECTED_CLASSPATH = ""; 
	static final String MAIN_METHOD_NAME = "main"; 
	static final String JAR_INTERNAL_URL_PROTOCOL_WITH_COLON = "jar:rsrc:"; 
	static final String JAR_INTERNAL_SEPARATOR = "!/"; 
	static final String INTERNAL_URL_PROTOCOL_WITH_COLON = "rsrc:"; 
	static final String INTERNAL_URL_PROTOCOL = "rsrc"; 
	static final String PATH_SEPARATOR = "/"; 
	static final String CURRENT_DIR = "./"; 
	static final String UTF8_ENCODING = "UTF-8"; 
	static final String RUNTIME = "#runtime"; 

	public static class RsrcURLStreamHandlerFactory implements URLStreamHandlerFactory {

		private ClassLoader classLoader;
		private URLStreamHandlerFactory chainFac;

		public RsrcURLStreamHandlerFactory(ClassLoader cl) {
			this.classLoader = cl;
		}

		@Override
		public URLStreamHandler createURLStreamHandler(String protocol) {
			if (INTERNAL_URL_PROTOCOL.equals(protocol)) {
				return new RsrcURLStreamHandler(classLoader);
			}
			if (chainFac != null) {
				return chainFac.createURLStreamHandler(protocol);
			}
			return null;
		}

		public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
			chainFac = fac;
		}

	}

	public static class RsrcURLConnection extends URLConnection {

		private ClassLoader classLoader;

		public RsrcURLConnection(URL url, ClassLoader classLoader) {
			super(url);
			this.classLoader = classLoader;
		}

		@Override
		public void connect() throws IOException {
		}

		@Override
		public InputStream getInputStream() throws IOException {
			String file = URLDecoder.decode(url.getFile(), UTF8_ENCODING);
			InputStream result = classLoader.getResourceAsStream(file);
			if (result == null) {
				throw new MalformedURLException("Could not open InputStream for URL '" + url + "'");
			}
			return result;
		}

	}

	public static class RsrcURLStreamHandler extends URLStreamHandler {

		private ClassLoader classLoader;

		public RsrcURLStreamHandler(ClassLoader c) {
			this.classLoader = c;
		}

		@Override
		protected URLConnection openConnection(URL u) throws IOException {
			return new RsrcURLConnection(u, this.classLoader);
		}

		@Override
		protected void parseURL(URL url, String spec, int start, int limit) {
			String file;
			if (spec.startsWith(INTERNAL_URL_PROTOCOL_WITH_COLON)) {
				file = spec.substring(5);
			}
			else if (url.getFile().equals(CURRENT_DIR)) {
				file = spec;
			}
			else if (url.getFile().endsWith(PATH_SEPARATOR)) {
				file = url.getFile() + spec;
			}
			else if (RUNTIME.equals(spec)) {
				file = url.getFile();
			}
			else {
				file = spec;
			}
			setURL(url, INTERNAL_URL_PROTOCOL, "", -1, null, null, file, null, null);  }
		}

		private static ManifestInfo getManifestInfo() throws IOException {
			Enumeration<?> resEnum;
			resEnum = Thread.currentThread().getContextClassLoader().getResources(JarFile.MANIFEST_NAME);
			while (resEnum.hasMoreElements()) {
				try {
					URL url = (URL) resEnum.nextElement();
					InputStream is = url.openStream();
					if (is != null) {
						ManifestInfo result = new ManifestInfo();
						Manifest manifest = new Manifest(is);
						Attributes mainAttribs = manifest.getMainAttributes();
						result.rsrcMainClass = mainAttribs.getValue(REDIRECTED_MAIN_CLASS_MANIFEST_NAME);
						String rsrcCP = mainAttribs.getValue(REDIRECTED_CLASS_PATH_MANIFEST_NAME);
						if (rsrcCP == null)
							rsrcCP = DEFAULT_REDIRECTED_CLASSPATH;
						result.rsrcClassPath = splitSpaces(rsrcCP);
						if ((result.rsrcMainClass != null) && !result.rsrcMainClass.trim().isEmpty())
							return result;
					}
				} catch (Exception e) {
				}
			}
			System.err.println("Missing attributes for JarRsrcLoader in Manifest (" 
					+ REDIRECTED_MAIN_CLASS_MANIFEST_NAME + ", " + REDIRECTED_CLASS_PATH_MANIFEST_NAME + ")");  
			return null;
		}

		private static String[] splitSpaces(String line) {
			if (line == null) {
				return null;
			}
			ArrayList<String> result = new ArrayList<String>();
			int firstPos = 0;
			while (firstPos < line.length()) {
				int lastPos = line.indexOf(' ', firstPos);
				if (lastPos == -1) {
					lastPos = line.length();
				}
				if (lastPos > firstPos) {
					result.add(line.substring(firstPos, lastPos));
				}
				firstPos = lastPos + 1;
			}
			return result.toArray(new String[result.size()]);
		}

		private static class ManifestInfo {
			String rsrcMainClass;
			String[] rsrcClassPath;
		}

		public static void main(String[] args)
				throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
				InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
			ManifestInfo mi = getManifestInfo();
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			URL.setURLStreamHandlerFactory(new RsrcURLStreamHandlerFactory(cl));
			URL[] rsrcUrls = new URL[mi.rsrcClassPath.length];
			for (int i = 0; i < mi.rsrcClassPath.length; i++) {
				String rsrcPath = mi.rsrcClassPath[i];
				if (rsrcPath.endsWith(PATH_SEPARATOR)) {
					rsrcUrls[i] = new URL(INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath);
				} else {
					rsrcUrls[i] = new URL(JAR_INTERNAL_URL_PROTOCOL_WITH_COLON + rsrcPath + JAR_INTERNAL_SEPARATOR);
				}
			}
			ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, getParentClassLoader());
			Thread.currentThread().setContextClassLoader(jceClassLoader);
			Class<?> c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
			Method main = c.getMethod(MAIN_METHOD_NAME, new Class[] { args.getClass() });
			main.invoke(null, new Object[] { args });
		}

		public static float getJavaVersion() {
			String version = System.getProperty("java.version", null);
			if (version != null) {
				int lastIdx = version.lastIndexOf('.');
				if (lastIdx == -1) {
					return Float.parseFloat(version.replace("_", ""));
				}
				String ver = version.substring(0, lastIdx);
				return Float.parseFloat(ver.replace("_", ""));
			}
			return -1f;
		}

		/**
		 * 获得getPlatformClassLoader，以满足java9以上版本的必须依赖getPlatformClassLoader加载限制
		 * 
		 * @return
		 */
		private static ClassLoader getParentClassLoader() throws InvocationTargetException, IllegalAccessException {
			float ver = getJavaVersion();
			if (ver != -1 && ver <= 1.8f) {
				return null;
			}
			try {
				Method platformClassLoader = ClassLoader.class.getMethod("getPlatformClassLoader", (Class[]) null);
				return (ClassLoader) platformClassLoader.invoke(null, (Object[]) null);
			} catch (NoSuchMethodException e) {
				return null;
			}
		}
	

}

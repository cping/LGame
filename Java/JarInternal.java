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
import java.util.jar.Manifest;

/**
 * 多jar打包加载用类（修改自org.eclipse.jdt.internal.jarinjarloader.JarRsrcLoader）
 * 
 * Manifest-Version: 1.0
 * Rsrc-Class-Path: ./ LGame-JavaSE-0.3.3.jar lwjgl.jar 
 * Class-Path: .
 * Rsrc-Main-Class: com.mianfilename
 * Main-Class: loon.JarInternal
 */
public class JarInternal {

	public static class RsrcURLStreamHandlerFactory implements
			URLStreamHandlerFactory {
		private ClassLoader classLoader;
		private URLStreamHandlerFactory chainFac;

		public RsrcURLStreamHandlerFactory(ClassLoader cl) {
			this.classLoader = cl;
		}

		public URLStreamHandler createURLStreamHandler(String protocol) {
			if ("rsrc".equals(protocol)) {
				return new RsrcURLStreamHandler(this.classLoader);
			}
			if (this.chainFac != null) {
				return this.chainFac.createURLStreamHandler(protocol);
			}
			return null;
		}

		public void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
			this.chainFac = fac;
		}
	}

	public static class RsrcURLConnection extends URLConnection {
		private ClassLoader classLoader;

		public RsrcURLConnection(URL url, ClassLoader classLoader) {
			super(url);
			this.classLoader = classLoader;
		}

		public void connect() throws IOException {
		}

		public InputStream getInputStream() throws IOException {
			String file = URLDecoder.decode(this.url.getFile(), "UTF-8");
			InputStream result = this.classLoader.getResourceAsStream(file);
			if (result == null) {
				throw new MalformedURLException(
						"Could not open InputStream for URL '" + this.url + "'");
			}
			return result;
		}
	}

	public static class RsrcURLStreamHandler extends URLStreamHandler {

		private ClassLoader classLoader;

		public RsrcURLStreamHandler(ClassLoader c) {
			this.classLoader = c;
		}

		protected URLConnection openConnection(URL u) throws IOException {
			return new RsrcURLConnection(u, this.classLoader);
		}

		protected void parseURL(URL url, String spec, int start, int limit) {
			String file = null;
			if (spec.startsWith(INTERNAL_URL_PROTOCOL_WITH_COLON)) {
				file = spec.substring(5);
			} else {
				if (url.getFile().equals(CURRENT_DIR)) {
					file = spec;
				} else {
					if (url.getFile().endsWith(PATH_SEPARATOR)) {
						file = url.getFile() + spec;
					} else {
						file = spec;
					}
				}
			}
			setURL(url,INTERNAL_URL_PROTOCOL, "", -1, null, null, file, null, null);
		}
	}

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

	private static ManifestInfo getManifestInfo() throws IOException {
		Enumeration<URL> resEnum = Thread.currentThread()
				.getContextClassLoader().getResources("META-INF/MANIFEST.MF");
		while (resEnum.hasMoreElements()) {
			try {
				URL url = resEnum.nextElement();
				InputStream is = url.openStream();
				if (is != null) {
					ManifestInfo result = new ManifestInfo(null);
					Manifest manifest = new Manifest(is);
					Attributes mainAttribs = manifest.getMainAttributes();
					result.rsrcMainClass = mainAttribs
							.getValue(REDIRECTED_MAIN_CLASS_MANIFEST_NAME);
					String rsrcCP = mainAttribs.getValue(REDIRECTED_CLASS_PATH_MANIFEST_NAME);
					if (rsrcCP == null) {
						rsrcCP = "";
					}
					result.rsrcClassPath = splitSpaces(rsrcCP);
					if ((result.rsrcMainClass != null)
							&& (!result.rsrcMainClass.trim().equals(""))) {
						return result;
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		System.err
				.println("Missing attributes for JarRsrcLoader in Manifest (Rsrc-Main-Class, Rsrc-Class-Path)");
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

		private ManifestInfo() {
		}

		ManifestInfo(ManifestInfo paramManifestInfo) {
			this();
		}
	}

	public static void main(String[] args) throws ClassNotFoundException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, SecurityException,
			NoSuchMethodException, IOException {
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
		ClassLoader jceClassLoader = new URLClassLoader(rsrcUrls, null);
		Thread.currentThread().setContextClassLoader(jceClassLoader);
		Class<?> c = Class.forName(mi.rsrcMainClass, true, jceClassLoader);
		Method main = c.getMethod(MAIN_METHOD_NAME, new Class[] { args.getClass() });
		main.invoke(null, new Object[] { args });
	}

}

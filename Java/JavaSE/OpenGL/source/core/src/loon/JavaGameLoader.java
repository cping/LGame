package loon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import loon.core.LSystem;


/**
 * Copyright 2008 - 2011
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
public class JavaGameLoader {

	private static File nativesDir = new File(System
			.getProperty("java.io.tmpdir")
			+ "/loon/");

	public static boolean loadLibrary(String nativeFile32, String nativeFile64) {
		String path = extractLibrary(nativeFile32, nativeFile64);
		if (path != null) {
			System.load(path);
		}
		return path != null;
	}

	private static String extractLibrary(final String native32,
			final String native64) {
		return (String) AccessController
				.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						String nativeFileName = LSystem.isBit64() ? native64
								: native32;
						File nativeFile = new File(nativesDir, nativeFileName);
						try {
							InputStream input = LSystem
									.getResourceAsStream("/" + nativeFileName);
							if (input == null) {
								return null;
							}
							nativesDir.mkdirs();
							FileOutputStream output = new FileOutputStream(
									nativeFile);
							byte[] buffer = new byte[4096];
							while (true) {
								int length = input.read(buffer);
								if (length == -1)
									break;
								output.write(buffer, 0, length);
							}
							input.close();
							output.close();
						} catch (IOException localIOException) {
						}
						return nativeFile.exists() ? nativeFile
								.getAbsolutePath() : null;
					}
				});
	}

}

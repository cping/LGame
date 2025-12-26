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
package loon.cport.builder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class TeaClassLoader extends URLClassLoader {

	public TeaClassLoader(URL[] classPaths, ClassLoader parent) {
		super(classPaths, parent);
	}

	public ArrayList<String> getAllClasses(List<String> classOrPackageNames) {
		ArrayList<String> classes = new ArrayList<String>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		for (String name : classOrPackageNames) {
			if (name.matches(".*\\.[A-Z].*")) {
				try {
					Class<?> clazz = Class.forName(name);
					classes.add(clazz.getName());
				} catch (ClassNotFoundException e) {
					addClassesFromPackage(name, classes, classLoader);
				}
			} else {
				addClassesFromPackage(name, classes, classLoader);
			}
		}
		return classes;
	}

	private static void addClassesFromPackage(String packageName, ArrayList<String> classes, ClassLoader classLoader) {
		String path = packageName.replace('.', '/');
		try {
			Enumeration<URL> resources = classLoader.getResources(path);
			for (; resources.hasMoreElements();) {
				URL resource = resources.nextElement();
				File directory = new File(resource.getFile());
				if (directory.exists() && directory.isDirectory()) {
					File[] files = directory.listFiles();
					if (files != null) {
						for (File file : files) {
							String fileName = file.getName().toLowerCase();
							if (file.isFile() && fileName.endsWith(".class")) {
								String className = packageName + '.'
										+ file.getName().substring(0, file.getName().length() - 6);
								classes.add(Class.forName(className).getName());
							}
						}
					}
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
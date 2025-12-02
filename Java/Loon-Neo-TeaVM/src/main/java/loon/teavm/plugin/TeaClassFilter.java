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
package loon.teavm.plugin;

import java.util.ArrayList;
import org.teavm.model.FieldReference;
import org.teavm.model.MethodReference;
import org.teavm.vm.spi.ElementFilter;

import loon.teavm.builder.TeaBuilder;

public class TeaClassFilter implements ElementFilter {
	private static final ArrayList<String> classesToExclude = new ArrayList<>();
	private static final ArrayList<Pair> methodsToExclude = new ArrayList<>();
	private static final ArrayList<Pair> fieldsToExclude = new ArrayList<>();

	private static final ArrayList<String> ALLOWED_CLASSES = new ArrayList<>();
	private static final ArrayList<String> EXCLUDED_CLASSES = new ArrayList<>();

	public static void addClassToExclude(String className) {
		classesToExclude.add(className);
	}

	public static void addMethodsToExclude(String className, String methodName) {
		methodsToExclude.add(new Pair(className, methodName));
	}

	public static void addFieldsToExclude(String className, String fieldName) {
		fieldsToExclude.add(new Pair(className, fieldName));
	}

	public static void printAllowedClasses() {
		TeaBuilder.begin("EXCLUDED CLASSES: " + ALLOWED_CLASSES.size());
		for (String allowedClass : ALLOWED_CLASSES) {
			TeaBuilder.println(allowedClass);
		}
		TeaBuilder.end();
	}

	public static void printExcludedClasses() {
		TeaBuilder.begin("ALLOWED CLASES: " + EXCLUDED_CLASSES.size());
		for (String excludedClass : EXCLUDED_CLASSES) {
			TeaBuilder.println(excludedClass);
		}
		TeaBuilder.end();
	}

	private static boolean containsClass(ArrayList<String> list, String className) {
		for (int i = 0; i < list.size(); i++) {
			String excludedClass = list.get(i);
			if (className.matches(excludedClass) || className.contains(excludedClass + "$"))
				return true;
		}
		return false;
	}

	private static boolean contains(ArrayList<Pair> list, String className, String methodOrFieldName) {
		for (int i = 0; i < list.size(); i++) {
			Pair pair = list.get(i);
			if (className.contains(pair.key)) {
				if (methodOrFieldName.equals(pair.value)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean acceptClass(String fullClassName) {
		boolean accceptClass = true;
		if (containsClass(classesToExclude, fullClassName)) {
			accceptClass = false;
		}
		if (accceptClass) {
			ALLOWED_CLASSES.add(fullClassName);
		} else {
			EXCLUDED_CLASSES.add(fullClassName);
		}
		return accceptClass;
	}

	@Override
	public boolean acceptMethod(MethodReference method) {
		String fullClassName = method.getClassName();
		String name = method.getName();
		if (contains(methodsToExclude, fullClassName, name)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean acceptField(FieldReference field) {
		String fullClassName = field.getClassName();
		String fieldName = field.getFieldName();
		if (contains(fieldsToExclude, fullClassName, fieldName)) {
			return false;
		}
		return true;
	}

	public static class Pair {
		public String key;
		public String value;

		public Pair(String key, String value) {
			this.key = key;
			this.value = value;
		}
	}
}
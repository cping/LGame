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
package loon.fx;

import loon.LSysException;
import loon.LazyLoading;

public abstract class Loon {

	private static Loon register() {
		Class<? extends Loon> appClass = newClass(Loon.class, "register");
		return newInstance(appClass);
	}

	public static void register(Loon app, JavaFXSetting setting, LazyLoading.Data lazy) {
		register(app, setting, lazy, new String[0]);
	}

	private static void register(Loon app, JavaFXSetting setting, LazyLoading.Data lazy, String[] args) {
		JavaFXApplication.launchFX(app, setting, lazy, args);
	}

	public static void register(Class<? extends Loon> appClass, JavaFXSetting setting, LazyLoading.Data lazy) {
		register(appClass, setting, lazy, new String[0]);
	}

	public static void register(Class<? extends Loon> appClass, JavaFXSetting setting, LazyLoading.Data lazy,
			String[] args) {
		try {
			
			Loon app = newInstance(appClass);
			register(app, setting, lazy, args);
		} catch (Throwable e) {
			
			e.printStackTrace();
		}
	}

	public static void register(JavaFXSetting setting, LazyLoading.Data lazy) {
		register(setting, lazy, new String[0]);
	}

	public static void register(JavaFXSetting setting, LazyLoading.Data lazy, String[] args) {
		try {
			Loon app = register();
			register(app, setting, lazy, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	protected static <T> T newInstance(Class<T> type) {
		try {
			return type.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	protected static <T> Class<? extends T> newClass(Class<T> superType, String callMethodName) {
		try {
			StackTraceElement[] cause = Thread.currentThread().getStackTrace();
			boolean foundMethod = false;
			String callClassName = null;
			for (StackTraceElement se : cause) {
				String className = se.getClassName();
				String methodName = se.getMethodName();
				if (foundMethod) {
					callClassName = className;
					break;
				} else if (superType.getName().equals(className) && callMethodName.equals(methodName)) {
					foundMethod = true;
				}
			}
			if (callClassName == null) {
				throw new LSysException("Unable to determine calling class name");
			}
			Class<?> theClass = Class.forName(callClassName, false, Thread.currentThread().getContextClassLoader());
			if (!superType.isAssignableFrom(theClass)) {
				throw new LSysException(theClass + " is not a subclass of " + superType);
			}
			return (Class<? extends T>) theClass;
		} catch (Exception e) {
			throw new LSysException(e.getMessage());
		}
	}

}
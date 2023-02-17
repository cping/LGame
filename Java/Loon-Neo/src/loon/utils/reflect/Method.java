/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.utils.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

@SuppressWarnings("rawtypes")
public final class Method {

	private final java.lang.reflect.Method method;

	Method(java.lang.reflect.Method method) {
		this.method = method;
	}

	public String getName() {
		return method.getName();
	}

	public Class<?> getReturnType() {
		return method.getReturnType();
	}

	public Class[] getParameterTypes() {
		return method.getParameterTypes();
	}

	public Class getDeclaringClass() {
		return method.getDeclaringClass();
	}

	@SuppressWarnings("deprecation")
	public boolean isAccessible() {
		return method.isAccessible();
	}

	public void setAccessible(boolean accessible) {
		method.setAccessible(accessible);
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(method.getModifiers());
	}

	public boolean isDefaultAccess() {
		return !isPrivate() && !isProtected() && !isPublic();
	}

	public boolean isFinal() {
		return Modifier.isFinal(method.getModifiers());
	}

	public boolean isPrivate() {
		return Modifier.isPrivate(method.getModifiers());
	}

	public boolean isProtected() {
		return Modifier.isProtected(method.getModifiers());
	}

	public boolean isPublic() {
		return Modifier.isPublic(method.getModifiers());
	}

	public boolean isNative() {
		return Modifier.isNative(method.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(method.getModifiers());
	}

	public boolean isVarArgs() {
		return method.isVarArgs();
	}

	public Object invoke(Object obj, Object... args) throws ReflectionException {
		try {
			return method.invoke(obj, args);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException(
					"Illegal argument(s) supplied to method: " + getName(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Illegal access to method: "
					+ getName(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException("Exception occurred in method: "
					+ getName(), e);
		}
	}

}

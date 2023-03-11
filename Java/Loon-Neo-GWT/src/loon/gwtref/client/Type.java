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
package loon.gwtref.client;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class Type {

	private static final Field[] EMPTY_FIELDS = new Field[0];
	private static final Method[] EMPTY_METHODS = new Method[0];
	private static final Constructor[] EMPTY_CONSTRUCTORS = new Constructor[0];
	private static final Annotation[] EMPTY_ANNOTATIONS = new Annotation[0];
	private static final Set<Class> EMPTY_ASSIGNABLES = Collections.unmodifiableSet(new HashSet<Class>());
	private static final Set<Class> EMPTY_INTERFACES = Collections.unmodifiableSet(new HashSet<Class>());

	final String name;
	final int id;
	final Class clazz;
	final CachedTypeLookup superClass;
	final Set<Class> assignables;
	final Set<Class> interfaces;
	boolean isAbstract;
	boolean isInterface;
	boolean isPrimitive;
	boolean isEnum;
	boolean isArray;
	boolean isMemberClass;
	boolean isStatic;
	boolean isAnnotation;

	Field[] fields = EMPTY_FIELDS;
	Method[] methods = EMPTY_METHODS;
	Constructor[] constructors = EMPTY_CONSTRUCTORS;
	Annotation[] annotations = EMPTY_ANNOTATIONS;

	Class componentType;
	Object[] enumConstants;

	private Field[] allFields;
	private Method[] allMethods;

	public Type(String name, int id, Class clazz, Class superClass, Set<Class> assignables, Set<Class> interfaces) {
		this.name = name;
		this.id = id;
		this.clazz = clazz;
		this.superClass = new CachedTypeLookup(superClass);
		this.assignables = assignables != null ? assignables : EMPTY_ASSIGNABLES;
		this.interfaces = interfaces != null ? interfaces : EMPTY_INTERFACES;
	}

	public Object newInstance() throws NoSuchMethodException {
		return getConstructor().newInstance();
	}

	public String getName() {
		return name;
	}

	public Class getClassOfType() {
		return clazz;
	}

	public Type getSuperclass() {
		return superClass.getType();
	}

	public boolean isAssignableFrom(Type otherType) {
		return clazz == otherType.clazz || (clazz == Object.class && !otherType.isPrimitive)
				|| otherType.assignables.contains(clazz);
	}

	public Class[] getInterfaces() {
		return interfaces.toArray(new Class[this.interfaces.size()]);
	}

	public Field getField(String name) {
		for (Field f : getFields()) {
			if (f.name.equals(name))
				return f;
		}
		return null;
	}

	public Field[] getFields() {
		if (allFields == null) {
			ArrayList<Field> allFieldsList = new ArrayList<Field>();
			Type t = this;
			while (t != null) {
				for (Field f : t.fields) {
					if (f.isPublic)
						allFieldsList.add(f);
				}
				t = t.getSuperclass();
			}
			allFields = allFieldsList.toArray(new Field[allFieldsList.size()]);
		}
		return allFields;
	}

	public Field[] getDeclaredFields() {
		return fields;
	}

	public Method getMethod(String name, Class... parameterTypes) throws NoSuchMethodException {
		for (Method m : getMethods()) {
			if (m.match(name, parameterTypes))
				return m;
		}
		throw new NoSuchMethodException();
	}

	public Method[] getMethods() {
		if (allMethods == null) {
			ArrayList<Method> allMethodsList = new ArrayList<Method>();
			Type t = this;
			while (t != null) {
				for (Method m : t.methods) {
					if (m.isPublic())
						allMethodsList.add(m);
				}
				t = t.getSuperclass();
			}
			allMethods = allMethodsList.toArray(new Method[allMethodsList.size()]);
		}
		return allMethods;
	}

	public Method[] getDeclaredMethods() {
		return methods;
	}

	public Constructor[] getConstructors() {
		return constructors;
	}

	public Constructor getDeclaredConstructor(Class... parameterTypes) throws NoSuchMethodException {
		return getConstructor(parameterTypes);
	}

	public Constructor getConstructor(Class... parameterTypes) throws NoSuchMethodException {
		for (Constructor c : constructors) {
			if (c.isPublic() && c.match(parameterTypes))
				return c;
		}
		throw new NoSuchMethodException();
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public boolean isPrimitive() {
		return isPrimitive;
	}

	public boolean isEnum() {
		return isEnum;
	}

	public boolean isArray() {
		return isArray;
	}

	public boolean isMemberClass() {
		return isMemberClass;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isAnnotation() {
		return isAnnotation;
	}

	public Class getComponentType() {
		return componentType;
	}

	public int getArrayLength(Object obj) {
		return ReflectionCache.getArrayLength(this, obj);
	}

	public Object getArrayElement(Object obj, int i) {
		return ReflectionCache.getArrayElement(this, obj, i);
	}

	public void setArrayElement(Object obj, int i, Object value) {
		ReflectionCache.setArrayElement(this, obj, i, value);
	}

	public Object[] getEnumConstants() {
		return enumConstants;
	}

	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}

	public Annotation getDeclaredAnnotation(Class<? extends java.lang.annotation.Annotation> annotationType) {
		for (Annotation annotation : annotations) {
			if (annotation.annotationType().equals(annotationType))
				return annotation;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Type [name=" + name + ",\n clazz=" + clazz + ",\n superClass=" + superClass + ",\n assignables="
				+ assignables + ",\n isAbstract=" + isAbstract + ",\n isInterface=" + isInterface + ",\n isPrimitive="
				+ isPrimitive + ",\n isEnum=" + isEnum + ",\n isArray=" + isArray + ",\n isMemberClass=" + isMemberClass
				+ ",\n isStatic=" + isStatic + ",\n isAnnotation=" + isAnnotation + ",\n fields="
				+ Arrays.toString(fields) + ",\n methods=" + Arrays.toString(methods) + ",\n constructors="
				+ Arrays.toString(constructors) + ",\n annotations=" + Arrays.toString(annotations)
				+ ",\n componentType=" + componentType + ",\n enumConstants=" + Arrays.toString(enumConstants) + "]";
	}
}

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

@SuppressWarnings("rawtypes")
public class Constructor extends Method {
	Constructor(String name, Class enclosingType, Class returnType, Parameter[] parameters, boolean isAbstract,
			boolean isFinal, boolean isStatic, boolean isDefaultAccess, boolean isPrivate, boolean isProtected,
			boolean isPublic, boolean isNative, boolean isVarArgs, boolean isMethod, boolean isConstructor,
			int methodId, Annotation[] annotations) {
		super(name, enclosingType, returnType, parameters, isAbstract, isFinal, isStatic, isDefaultAccess, isPrivate,
				isProtected, isPublic, isNative, isVarArgs, isMethod, isConstructor, methodId, annotations);
	}

	public Object newInstance(Object... params) {
		return super.invoke(null, params);
	}
}

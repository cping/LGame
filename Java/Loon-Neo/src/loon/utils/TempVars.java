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
package loon.utils;

import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.geom.Vector4f;

public class TempVars {

	private static TempVars instance;

	public static final TempVars get() {
		synchronized (TempVars.class) {
			if (instance == null) {
				instance = new TempVars();
			}
			return instance;
		}
	}

	public static final TempVars getClean2f() {
		return get().clean2f();
	}

	public static final TempVars getClean3f() {
		return get().clean3f();
	}

	public static final TempVars getClean4f() {
		return get().clean4f();
	}

	public static final TempVars getClean() {
		return get().cleanAll();
	}

	public static void freeStatic() {
		instance = null;
	}

	public final Vector2f vec2f1 = new Vector2f();
	public final Vector2f vec2f2 = new Vector2f();
	public final Vector2f vec2f3 = new Vector2f();
	public final Vector2f vec2f4 = new Vector2f();
	public final Vector3f vec3f1 = new Vector3f();
	public final Vector3f vec3f2 = new Vector3f();
	public final Vector3f vec3f3 = new Vector3f();
	public final Vector3f vec3f4 = new Vector3f();
	public final Vector3f vec3f5 = new Vector3f();
	public final Vector3f vec3f6 = new Vector3f();
	public final Vector3f vec3f7 = new Vector3f();
	public final Vector3f vec3f8 = new Vector3f();
	public final Vector3f vec3f9 = new Vector3f();
	public final Vector3f vec3f10 = new Vector3f();
	public final Vector4f vec4f1 = new Vector4f();
	public final Vector4f vec4f2 = new Vector4f();

	public TempVars clean2f() {
		vec2f1.setEmpty();
		vec2f2.setEmpty();
		vec2f3.setEmpty();
		vec2f4.setEmpty();
		return this;
	}

	public TempVars clean3f() {
		vec3f1.setEmpty();
		vec3f2.setEmpty();
		vec3f3.setEmpty();
		vec3f4.setEmpty();
		vec3f5.setEmpty();
		vec3f6.setEmpty();
		vec3f7.setEmpty();
		vec3f8.setEmpty();
		vec3f9.setEmpty();
		vec3f10.setEmpty();
		return this;
	}

	public TempVars clean4f() {
		vec4f1.setEmpty();
		vec4f2.setEmpty();
		return this;
	}

	public TempVars cleanAll() {
		clean2f();
		clean3f();
		clean4f();
		return this;
	}

}

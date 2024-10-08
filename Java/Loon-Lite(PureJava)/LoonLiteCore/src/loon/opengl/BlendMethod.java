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
package loon.opengl;

public class BlendMethod {

	public static final int MODE_NORMAL = 1;

	public static final int MODE_ALPHA_MAP = 2;

	public static final int MODE_ALPHA_BLEND = 3;

	public static final int MODE_COLOR_MULTIPLY = 4;

	public static final int MODE_ADD = 5;

	public static final int MODE_SCREEN = 6;

	public static final int MODE_ALPHA = 7;

	public static final int MODE_SPEED = 8;

	public static final int MODE_ALPHA_ONE = 9;

	public static final int MODE_NONE = 10;

	public static final int MODE_MASK = 11;

	public static final int MODE_LIGHT = 12;

	public static final int MODE_ALPHA_ADD = 13;

	public static final int MODE_MULTIPLY = 14;

	protected int _GL_BLEND;

	public BlendMethod(int b) {
		this._GL_BLEND = b;
	}

	public BlendMethod() {
		this(BlendMethod.MODE_NORMAL);
	}

	public void blendNormal() {
		_GL_BLEND = BlendMethod.MODE_NORMAL;
	}

	public void blendSpeed() {
		_GL_BLEND = BlendMethod.MODE_SPEED;
	}

	public void blendAdd() {
		_GL_BLEND = BlendMethod.MODE_ALPHA_ADD;
	}

	public void blendMultiply() {
		_GL_BLEND = BlendMethod.MODE_MULTIPLY;
	}

	public void blendLight() {
		_GL_BLEND = BlendMethod.MODE_LIGHT;
	}

	public void blendMask() {
		_GL_BLEND = BlendMethod.MODE_MASK;
	}

	public int getBlend() {
		return _GL_BLEND;
	}

	public BlendMethod setBlend(int b) {
		this._GL_BLEND = b;
		return this;
	}
}

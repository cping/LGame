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

import loon.LSystem;

public class BlendMode {

	protected int _blend = LSystem.MODE_NORMAL;

	public void blendNormal() {
		_blend = LSystem.MODE_NORMAL;
	}

	public void blendSpeed() {
		_blend = LSystem.MODE_SPEED;
	}

	public void blendAdd() {
		_blend = LSystem.MODE_ALPHA_ADD;
	}

	public void blendMultiply() {
		_blend = LSystem.MODE_MULTIPLY;
	}

	public void blendLight() {
		_blend = LSystem.MODE_LIGHT;
	}
	
	public void blendMask() {
		_blend = LSystem.MODE_MASK;
	}

	public int getBlend() {
		return _blend;
	}

	public void setBlend(int b) {
		this._blend = b;
	}
}

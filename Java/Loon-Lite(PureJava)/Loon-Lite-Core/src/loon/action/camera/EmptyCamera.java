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
package loon.action.camera;

import loon.LSystem;
import loon.geom.Matrix4;

public class EmptyCamera extends BaseCamera {

	protected Matrix4 _projMatrix4;

	protected Matrix4 _viewMatrix4;

	public EmptyCamera() {
		this(LSystem.viewSize.getMatrix().cpy(), new Matrix4());
	}

	public EmptyCamera(Matrix4 p, Matrix4 v) {
		_projMatrix4 = p;
		_viewMatrix4 = v;
	}

	@Override
	public Matrix4 getView() {
		return _viewMatrix4;
	}

	@Override
	public Matrix4 getProjection() {
		return _projMatrix4;
	}

	@Override
	public Matrix4 getCombine() {
		return _projMatrix4.mul(_viewMatrix4);
	}

}

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
import loon.geom.Transforms;
import loon.geom.Vector2f;
import loon.geom.Vector3f;

public class OrthographicCamera extends EmptyCamera {

	private final Vector3f _tempVector3f = new Vector3f();
	private float width;
	private float height;

	public OrthographicCamera() {
		this(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public OrthographicCamera(float width, float height) {
		this(0, width, height, 0);
	}

	public OrthographicCamera(float left, float right, float bottom, float top) {
		this.width = right - left;
		this.height = bottom - top;
		this._viewMatrix4 = Transforms.createOrtho2d(left, right, bottom, top, 0, 100f);
	}

	public OrthographicCamera translate(Vector2f v) {
		_viewMatrix4.mul(Transforms.createTranslation(new Vector3f(v, 0)));
		return this;
	}

	public OrthographicCamera translateTo(float x, float y) {
		_viewMatrix4.idt().mul(Transforms.createTranslation(new Vector3f(x, y, 0)));
		return this;
	}

	public OrthographicCamera translateTo(Vector2f v) {
		_viewMatrix4.idt().mul(Transforms.createTranslation(new Vector3f(v, 0)));
		return this;
	}

	public OrthographicCamera center(float x1, float y1) {
		_viewMatrix4.idt();
		float x = (width / 2) - x1;
		float y = (height / 2) - y1;
		return translate(x, y);
	}

	public OrthographicCamera center(Vector2f v) {
		if (v == null) {
			return this;
		}
		return center(v.x, v.y);
	}

	public OrthographicCamera translate(float x, float y) {
		_viewMatrix4.mul(Transforms.createTranslation(new Vector3f(x, y, 0)));
		return this;
	}

	public OrthographicCamera rotate(Vector3f axis, float angle) {
		_viewMatrix4.mul(Transforms.createRotation(axis, angle));
		return this;
	}

	public OrthographicCamera initProjection(float width, float height) {
		return initProjection(0, width, height, 0);
	}

	public OrthographicCamera initProjection(float left, float right, float bottom, float top) {
		width = right - left;
		height = bottom - top;
		Transforms.createOrtho2d(left, right, bottom, top, 0, 100, _projMatrix4);
		return this;
	}

	public Vector2f project(Vector2f worldCoords) {
		return project(worldCoords.x, worldCoords.y, worldCoords);
	}

	public Vector3f project(Vector3f worldCoords) {
		project(worldCoords, 0, 0, width, height);
		return worldCoords;
	}

	public Vector3f project(Vector3f worldCoords, float viewportX, float viewportY, float viewportWidth,
			float viewportHeight) {
		worldCoords.prjSelf(_viewMatrix4);
		worldCoords.x = viewportWidth * (worldCoords.x + 1f) / 2f + viewportX;
		worldCoords.y = viewportHeight * (worldCoords.y + 1f) / 2f + viewportY;
		worldCoords.z = (worldCoords.z + 1f) / 2f;
		return worldCoords;
	}

	public Vector2f project(float x, float y, Vector2f dst) {
		_tempVector3f.set(x, y, 0);
		project(_tempVector3f);
		return dst.set(_tempVector3f.x, _tempVector3f.y);
	}

	public Vector3f unproject(Vector3f screenCoords, float viewportX, float viewportY, float viewportWidth,
			float viewportHeight) {
		float x = screenCoords.x - viewportX, y = screenCoords.y - viewportY;
		screenCoords.x = (2f * x) / viewportWidth - 1f;
		screenCoords.y = (2f * y) / viewportHeight - 1f;
		screenCoords.z = 2f * screenCoords.z - 1f;
		screenCoords.prjSelf(_viewMatrix4);
		return screenCoords;
	}

	public Vector3f unproject(Vector3f screenCoords) {
		unproject(screenCoords, 0, 0, width, height);
		return screenCoords;
	}

	public Vector2f unproject(Vector2f screenCoords) {
		return unproject(screenCoords.x, screenCoords.y, screenCoords);
	}

	public Vector2f unproject(float x, float y, Vector2f dst) {
		_tempVector3f.set(x, y, 0);
		unproject(_tempVector3f);
		return dst.set(_tempVector3f.x, _tempVector3f.y);
	}
}

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
package loon;

import loon.geom.Affine2f;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.StringUtils;

/**
 * State是一个简单的界面渲染器(也是简单的FSM状态机实现),用于同Stage配合实现轻量级的画面转换,既单纯替换用户自定义的渲染内容.
 * 
 * 它不同于标准Screen,它的替换本质上只是画布渲染部分的替换,不涉及组件精灵之类其它对象，转化它并不能改变Screen除GLEx渲染外的任何内容.
 * 换句话说,它可以在保持组件精灵以及其它Screen中对象设置不变的情况下,单独转换用户自定义渲染部分.
 * 
 * 一个Stage中,可以存在复数的State,使用addState添加,使用playState转换用户需要的State对象进行显示,removeState删除.
 */
public abstract class State implements LRelease {

	protected StateManager _stateManager;

	protected String _stateName;

	protected Affine2f _camera;

	protected boolean _isLoaded;

	protected boolean _isScalePos;

	protected boolean _syncCamera;

	public State() {
		this(LSystem.UNKNOWN);
	}

	public State(String name) {
		this._stateName = name;
		this._syncCamera = true;
		this._isScalePos = false;
		this._camera = new Affine2f();
	}

	protected void setName(String name) {
		if (!StringUtils.isEmpty(name)) {
			this._stateName = name;
		}
	}

	public String getName() {
		return this._stateName;
	}

	public Affine2f getCamera() {
		return this._camera;
	}

	public State setCameraX(float x) {
		_camera.tx = x;
		_isScalePos = false;
		return this;
	}

	public State setCameraY(float y) {
		_camera.ty = y;
		_isScalePos = false;
		return this;
	}

	public State camera(float x, float y) {
		return camera(x, y, LSystem.getScaleWidth(), LSystem.getScaleHeight());
	}

	public State camera(float x, float y, float scaleX, float scaleY) {
		posCamera(-(x * scaleX), -(y * scaleY));
		_isScalePos = true;
		return this;
	}

	public State posCamera(float x, float y) {
		setCameraX(x);
		setCameraY(y);
		return this;
	}

	public State scaleCamera(float scale) {
		return scaleCamera(scale, scale);
	}

	public State scaleCamera(float sx, float sy) {
		_camera.scale(sx, sy);
		return this;
	}

	public State rotateCamera(float angle, float x, float y) {
		_camera.rotate(angle, x, y);
		return this;
	}

	public float getCameraX() {
		return MathUtils.abs(_isScalePos ? (_camera.tx / _camera.scaleX()) : _camera.tx);
	}

	public float getCameraY() {
		return MathUtils.abs(_isScalePos ? (_camera.ty / _camera.scaleY()) : _camera.ty);
	}

	protected void setStateManager(StateManager smr) {
		this._stateManager = smr;
	}

	public void begin() {
	}

	public void end() {
	}

	public abstract void load();

	public abstract void update(float delta);

	public abstract void paint(GLEx g);

	public boolean isSyncCamera() {
		return _syncCamera;
	}

	public void setSyncCamera(boolean sync) {
		this._syncCamera = sync;
	}

}

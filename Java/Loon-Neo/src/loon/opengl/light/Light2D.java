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
package loon.opengl.light;

import loon.LRelease;
import loon.LSystem;
import loon.geom.FloatValue;
import loon.geom.Vector2f;
import loon.geom.Vector4f;
import loon.opengl.BaseBatch;
import loon.opengl.BlendMethod;
import loon.opengl.GLEx;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.opengl.TrilateralBatch;
import loon.utils.MathUtils;

public class Light2D implements LRelease {

	private static class LightShader extends ShaderSource {

		private final FloatValue _timer = new FloatValue(1f);

		private final Vector2f _touch = new Vector2f();

		private final Vector4f _ambientData = new Vector4f(0.3f, 0.3f, 0.8f, 0.3f);

		private final Vector4f _lightData = new Vector4f(1.0f, 0.8f, 0.2f, 2f);

		private final Vector2f _lightSize = new Vector2f(0.3f, 0.2f);

		private final Vector2f _size = new Vector2f();

		private boolean _sway;

		public LightShader() {
			super(LSystem.getGLExVertexShader(), LSystem.getGLExLightFragmentShader());
			updateSize();
		}

		public void reset() {
			_timer.set(1f);
			_ambientData.set(0.3f, 0.3f, 0.8f, 0.3f);
			_lightData.set(1.0f, 0.8f, 0.2f, 2f);
			_lightSize.set(0.3f, 0.2f);
		}

		public void updateSize() {
			setSize(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}

		public void setSway(boolean s) {
			this._sway = s;
		}

		public void setAmbientData(float r, float g, float b, float a) {
			_ambientData.set(r, g, b, a);
		}

		public void setLightData(float r, float g, float b, float a) {
			_lightData.set(r, g, b, a);
		}

		public void setLightSize(float w, float h) {
			_size.set(w, h);
		}

		public void setSize(float w, float h) {
			_size.set(w, h);
		}

		public void setTimer(float d) {
			_timer.set(d);
		}

		public void setTouch(float x, float y) {
			_touch.set(x, y);
		}

		@Override
		public void setupShader(ShaderProgram program) {
			float scaleX = LSystem.getScaleHeight();
			float scaleY = LSystem.getScaleHeight();
			program.setUniformf("touch", _touch.x * scaleX, (_size.y - _touch.y) * scaleY);
			program.setUniformf("time", _timer.get() * (_sway ? MathUtils.nextInt(5, 10) : 10), 0);
			program.setUniformf("resolution", _size.x * scaleX, _size.y * scaleY);
			program.setUniformf("ambientData", _ambientData);
			program.setUniformf("lightData", _lightData);
			program.setUniformf("lightSize", _lightSize);
		}

	}

	private LightShader _shader;

	private boolean _closed;

	private boolean _inited, _autoTouchMove;

	private BaseBatch _baseBatch;

	private int _blend;

	public Light2D() {
		this(BlendMethod.MODE_ALPHA);
	}

	public Light2D(int b) {
		this._blend = b;
		this._autoTouchMove = true;
	}

	public BaseBatch getGlBaseBatch() {
		if (_closed) {
			return null;
		}
		if (_baseBatch == null) {
			_baseBatch = createGLExBatch();
		}
		_baseBatch.setBlendMode(_blend);
		return _baseBatch;
	}

	public BaseBatch createGLExBatch() {
		if (_closed) {
			return null;
		}
		loadShaderSource();
		return new TrilateralBatch(LSystem.base().graphics().gl, _shader);
	}

	private void loadShaderSource() {
		if (!_inited || _shader == null) {
			_shader = new LightShader();
			_inited = true;
		}
		_shader.updateSize();
	}

	public boolean isAutoTouchMove() {
		return _autoTouchMove;
	}

	public Light2D setAutoTouchMove(boolean a) {
		this._autoTouchMove = a;
		return this;
	}

	public Light2D reset() {
		loadShaderSource();
		_shader.reset();
		_closed = false;
		return this;
	}

	public Light2D setSway(boolean b) {
		loadShaderSource();
		_shader.setSway(b);
		return this;
	}

	public Light2D setAmbientData(float r, float g, float b, float a) {
		loadShaderSource();
		_shader.setAmbientData(r, g, b, a);
		return this;
	}

	public Light2D setLightData(float r, float g, float b, float a) {
		loadShaderSource();
		_shader.setLightData(r, g, b, a);
		return this;
	}

	public Light2D setLightSize(float w, float h) {
		loadShaderSource();
		_shader.setLightSize(w, h);
		return this;
	}

	public Light2D setSize(float w, float h) {
		loadShaderSource();
		_shader.setSize(w, h);
		return this;
	}

	public Light2D setAutoTouchTimer(float x, float y, float timer) {
		loadShaderSource();
		if (_autoTouchMove) {
			_shader.setTouch(x, y);
			_shader.setTimer(timer);
		}
		return this;
	}

	public Light2D setTimer(float d) {
		loadShaderSource();
		_shader.setTimer(d);
		return this;
	}

	public Light2D setTouch(float x, float y) {
		loadShaderSource();
		_shader.setTouch(x, y);
		return this;
	}

	public void draw(GLEx g) {
		loadShaderSource();
	}

	public int getBlend() {
		return _blend;
	}

	public Light2D setBlend(int blend) {
		this._blend = blend;
		return this;
	}

	public boolean isClosed() {
		return _closed;
	}

	@Override
	public void close() {
		if (_baseBatch != null) {
			_baseBatch.close();
			_baseBatch = null;
		}
		_closed = true;
	}

}

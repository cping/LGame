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
import loon.canvas.LColor;
import loon.events.EventActionN;
import loon.geom.FloatValue;
import loon.geom.Vector2f;
import loon.geom.Vector4f;
import loon.opengl.BlendMethod;
import loon.opengl.ShaderMask;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * loon提供的2d单光源/多光源类管理用类
 */
public class Light2D implements EventActionN, LRelease {

	public enum LightType {
		Singleton, Multiple;
	}

	public static class LightMultipleShader extends ShaderSource {

		private static final int LIGHT_COUNT = 32;

		private final float[] _tmpLightPositions = new float[2 * LIGHT_COUNT];

		private final float[] _tmpLightColors = new float[4 * LIGHT_COUNT];

		private final float[] _tmpLightSizes = new float[2 * LIGHT_COUNT];

		private final float[] _tmpLightIntensitys = new float[1 * LIGHT_COUNT];

		private final float[] _tmpLightAttenuations = new float[1 * LIGHT_COUNT];

		private final TArray<PointLight> _lights = new TArray<PointLight>();

		private final Vector4f _ambientData = new Vector4f(0.3f, 0.3f, 0.8f, 0.3f);

		private final FloatValue _timer = new FloatValue(1f);

		private final Vector2f _size = new Vector2f();

		private boolean _dirty = true;

		private boolean _sway;

		public LightMultipleShader() {
			super(LSystem.getGLExVertexShader(), LSystem.getGLExMLightFragmentShader());
			updateSize();
		}

		public void addLight(PointLight light) {
			_lights.add(light);
			_dirty = true;
		}

		public void addLights(PointLight... light) {
			_lights.addAll(light);
			_dirty = true;
		}

		public PointLight removeLight(int idx) {
			PointLight light = _lights.removeIndex(idx);
			if (light != null) {
				_dirty = true;
			}
			return light;
		}

		public boolean removeLightValue(PointLight l) {
			boolean result = _lights.removeValue(l);
			if (result) {
				_dirty = true;
			}
			return result;
		}

		public PointLight updateLightValue(int idx, PointLight l) {
			PointLight light = _lights.get(idx);
			if (light != null && l != null) {
				light.set(l);
				_dirty = true;
			}
			return light;
		}

		public void updateLight() {
			this._dirty = true;
		}

		public void reset() {
			_timer.set(1f);
			_ambientData.set(0.3f, 0.3f, 0.8f, 0.3f);
			_dirty = true;
		}

		public void updateSize() {
			setLightSize(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
		}

		public void setSway(boolean s) {
			this._sway = s;
		}

		public void setAmbientData(float r, float g, float b, float a) {
			_ambientData.set(r, g, b, a);
		}

		public void setLightSize(float w, float h) {
			_size.set(w, h);
		}

		public void setTimer(float d) {
			_timer.set(d);
		}

		private void update(float scaleX, float scaleY) {
			if (_dirty) {
				int floatIndex = 0;
				int vec2Index = 0;
				int vec4Index = 0;
				for (int i = _lights.size - 1; i > -1; i--) {
					PointLight light = _lights.get(i);
					_tmpLightIntensitys[floatIndex] = light.intensity;
					_tmpLightAttenuations[floatIndex] = light.attenuation;
					_tmpLightPositions[vec2Index + 0] = light.position.x * scaleX;
					_tmpLightPositions[vec2Index + 1] = (_size.y - light.position.y) * scaleY;
					_tmpLightSizes[vec2Index + 0] = light.radius * scaleX;
					_tmpLightSizes[vec2Index + 1] = light.radius * scaleY;
					_tmpLightColors[vec4Index + 0] = light.color.r;
					_tmpLightColors[vec4Index + 1] = light.color.g;
					_tmpLightColors[vec4Index + 2] = light.color.b;
					_tmpLightColors[vec4Index + 3] = light.color.a;
					floatIndex++;
					vec2Index += 2;
					vec4Index += 4;
				}
				_dirty = false;
			}
		}

		@Override
		public void setupShader(ShaderProgram program) {
			float scaleX = LSystem.getScaleHeight();
			float scaleY = LSystem.getScaleHeight();
			update(scaleX, scaleY);
			program.setUniformf("time", _timer.get() * (_sway ? MathUtils.nextInt(5, 10) : 10), 0);
			program.setUniformf("resolution", _size.x * scaleX, _size.y * scaleY);
			program.setUniformi("lightCount", _lights.size);
			program.setUniformf("ambientData", _ambientData);
			program.setUniform2fv("lightPos", _tmpLightPositions, 0, _tmpLightPositions.length);
			program.setUniform4fv("lightColor", _tmpLightColors, 0, _tmpLightColors.length);
			program.setUniform2fv("lightSize", _tmpLightSizes, 0, _tmpLightSizes.length);
			program.setUniform1fv("lightIntensity", _tmpLightIntensitys, 0, _tmpLightIntensitys.length);
			program.setUniform1fv("lightAttenuation", _tmpLightAttenuations, 0, _tmpLightAttenuations.length);
		}

	}

	public static class LightSingletonShader extends ShaderSource {

		private final FloatValue _timer = new FloatValue(1f);

		private final Vector2f _touch = new Vector2f();

		private final Vector4f _ambientData = new Vector4f(0.3f, 0.3f, 0.8f, 0.3f);

		private final Vector4f _lightData = new Vector4f(1.0f, 0.8f, 0.2f, 2f);

		private final Vector2f _lightSize = new Vector2f(0.3f, 0.2f);

		private final Vector2f _size = new Vector2f();

		private boolean _sway;

		public LightSingletonShader() {
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
			setLightSize(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
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

	private boolean _inited, _autoTouchMove;

	private LightSingletonShader _lsshader;

	private LightMultipleShader _lmshader;

	private ShaderMask _mask;

	private LightType _model;

	private boolean _dirty;

	public Light2D(LightType lt) {
		this(BlendMethod.MODE_ALPHA, lt);
	}

	public Light2D(int b, LightType lt) {
		this._mask = new ShaderMask(b, this);
		this._autoTouchMove = _dirty = true;
		this._model = lt;
	}

	public ShaderMask getMask() {
		update();
		return this._mask;
	}

	public LightType getLightType() {
		return this._model;
	}

	public Light2D updateLightType(LightType lt) {
		this._model = lt;
		this._dirty = true;
		return this;
	}

	@Override
	public void update() {
		if (!_inited || (_lsshader == null && _lmshader == null) || _dirty) {
			if (_lsshader == null) {
				_lsshader = new LightSingletonShader();
			}
			if (_lmshader == null) {
				_lmshader = new LightMultipleShader();
			}
			switch (_model) {
			case Singleton:
				_mask.setShaderSource(_lsshader);
				break;
			default:
				_mask.setShaderSource(_lmshader);
				break;
			}
			_inited = true;
			_dirty = false;
		}
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.updateSize();
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.updateSize();
			}
			break;
		}
	}

	public boolean isAutoTouchMove() {
		return _autoTouchMove;
	}

	public Light2D setAutoTouchMove(boolean a) {
		this._autoTouchMove = a;
		return this;
	}

	public Light2D reset() {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.reset();
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.reset();
			}
			break;
		}
		_mask.reset();
		return this;
	}

	public Light2D setSway(boolean b) {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.setSway(b);
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.setSway(b);
			}
			break;
		}
		return this;
	}

	public Light2D setAmbientData(LColor c) {
		if (c == null) {
			return this;
		}
		return setAmbientData(c.r, c.g, c.b, c.a);
	}

	public Light2D setAmbientData(float r, float g, float b, float a) {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.setAmbientData(r, g, b, a);
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.setAmbientData(r, g, b, a);
			}
			break;
		}
		return this;
	}

	public Light2D setLightData(float r, float g, float b, float a) {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.setLightData(r, g, b, a);
			}
			break;
		default:
			break;
		}
		return this;
	}

	public Light2D setLightSize(float w, float h) {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.setLightSize(w, h);
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.setLightSize(w, h);
			}
			break;
		}
		return this;
	}

	public Light2D setAutoTouchTimer(float x, float y, float timer) {
		update();
		if (_autoTouchMove && _lsshader != null) {
			_lsshader.setTouch(x, y);
			_lsshader.setTimer(timer);
		}
		return this;
	}

	public Light2D setTimer(float d) {
		update();
		switch (_model) {
		case Singleton:
			if (_lsshader != null) {
				_lsshader.setTimer(d);
			}
			break;
		default:
			if (_lmshader != null) {
				_lmshader.setTimer(d);
			}
			break;
		}
		return this;
	}

	public Light2D setTouch(float x, float y) {
		update();
		if (_lsshader != null) {
			_lsshader.setTouch(x, y);
		}
		return this;
	}

	public Light2D addLight(PointLight light) {
		update();
		if (_lmshader != null) {
			_lmshader.addLight(light);
		}
		return this;
	}

	public Light2D addLights(PointLight... light) {
		update();
		if (_lmshader != null) {
			_lmshader.addLights(light);
		}
		return this;
	}

	public PointLight removeLight(int idx) {
		update();
		if (_lmshader != null) {
			return _lmshader.removeLight(idx);
		}
		return null;
	}

	public boolean removeLightValue(PointLight l) {
		update();
		if (_lmshader != null) {
			return _lmshader.removeLightValue(l);
		}
		return false;
	}

	public PointLight updateLightValue(int idx, PointLight l) {
		update();
		if (_lmshader != null) {
			return _lmshader.updateLightValue(idx, l);
		}
		return null;
	}

	public Light2D updateLight() {
		update();
		if (_lmshader != null) {
			_lmshader.updateLight();
		}
		return this;
	}

	public boolean isClosed() {
		return _mask.isClosed();
	}

	@Override
	public void close() {
		_mask.close();
		_dirty = true;
		_inited = false;
	}

}

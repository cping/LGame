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
package loon.opengl.mask;

import loon.LSystem;
import loon.geom.Vector2f;
import loon.opengl.BlendMethod;
import loon.opengl.ShaderMask;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;

/**
 * 需要配合FBO类使用，可以实现整个画面的干扰波风格显示
 */
public class NoiseMask implements FBOMask {

	public final static class NoiseShader extends ShaderSource {

		private final Vector2f _viewSize;

		private final Vector2f _mouse;

		private float _time;

		private boolean _autoViewResize;

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n" + "#define LOWP lowp\r\n"
				+ "precision mediump float;\r\n" + "#else\r\n" + "#define LOWP \r\n" + "#endif\r\n" + "\r\n"
				+ "varying LOWP vec4 v_color;\r\n" + "varying vec2 v_texCoords;\r\n" + "\r\n"
				+ "uniform sampler2D u_texture;\r\n" + "uniform vec2 resolution;\r\n" + "uniform vec2 mouse;\r\n"
				+ "uniform float time;\r\n" + "uniform float frame;\r\n" + "\r\n"
				+ "highp float random1d(float dt) {\r\n" + "    highp float c = 43758.5453;\r\n"
				+ "    highp float sn = mod(dt, 3.14);\r\n" + "    return fract(sin(sn) * c);\r\n" + "}\r\n" + "\r\n"
				+ "highp float noise1d(float value) {\r\n" + "	highp float i = floor(value);\r\n"
				+ "	highp float f = fract(value);\r\n"
				+ "	return mix(random1d(i), random1d(i + 1.0), smoothstep(0.0, 1.0, f));\r\n" + "}\r\n" + "\r\n"
				+ "highp float random2d(vec2 co) {\r\n" + "    highp float a = 12.9898;\r\n"
				+ "    highp float b = 78.233;\r\n" + "    highp float c = 43758.5453;\r\n"
				+ "    highp float dt = dot(co.xy, vec2(a, b));\r\n" + "    highp float sn = mod(dt, 3.14);\r\n"
				+ "    return fract(sin(sn) * c);\r\n" + "}\r\n" + "\r\n" + "void main() {\r\n" + "\r\n"
				+ "	float strength = (0.3 + 0.7 * noise1d(0.3 * time)) * mouse.x / resolution.x;\r\n" + "\r\n"
				+ "	float jump = 500.0 * floor(0.3 * (mouse.x / resolution.x) * (time + noise1d(time)));\r\n" + "\r\n"
				+ "	vec2 uv = v_texCoords;\r\n"
				+ "	uv.y += 0.2 * strength * (noise1d(5.0 * v_texCoords.y + 2.0 * time + jump) - 0.5);\r\n"
				+ "	uv.x += 0.1 * strength * (noise1d(100.0 * strength * uv.y + 3.0 * time + jump) - 0.5);\r\n" + "\r\n"
				+ "	vec3 pixel_color = texture2D(u_texture, uv).rgb;\r\n" + "\r\n"
				+ "	pixel_color += vec3(5.0 * strength * (random2d(v_texCoords + 1.133001 * vec2(time, 1.13)) - 0.5));\r\n"
				+ "\r\n" + "	gl_FragColor = vec4(pixel_color, 1.0);\r\n" + "}";

		public NoiseShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
			_autoViewResize = autoResize;
			_viewSize = new Vector2f(w, h);
			_mouse = new Vector2f(64);
			_time = 64;
		}

		public void setMouse(float x, float y) {
			_mouse.set(x, y);
		}

		public float getMouseX() {
			return _mouse.x;
		}

		public float getMouseY() {
			return _mouse.y;
		}

		public void setTime(float t) {
			_time = t;
		}

		public float getTime() {
			return _time;
		}

		public void setViewSize(float w, float h) {
			_viewSize.set(w, h);
		}

		public void updateToScreen() {
			if (_autoViewResize) {
				_viewSize.set(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
			}
		}

		@Override
		public void setupShader(ShaderProgram program) {
			float scaleX = LSystem.getScaleWidth();
			float scaleY = LSystem.getScaleHeight();
			program.setUniformf("resolution", _viewSize.x * scaleX, _viewSize.y * scaleY);
			program.setUniformf("time", _time);
			program.setUniformf("mouse", _mouse);
		}
	}

	private boolean _shaderInited, _shaderDirty;
	private final ShaderMask _shaderMask;
	private final NoiseShader _noiseShader;

	public NoiseMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public NoiseMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public NoiseMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._noiseShader = new NoiseShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	public NoiseShader getNoiawShader() {
		return _noiseShader;
	}

	@Override
	public ShaderSource getShader() {
		return getNoiawShader();
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	public void setMouse(float x, float y) {
		_noiseShader.setMouse(x, y);
	}

	public float getMouseX() {
		return _noiseShader.getMouseX();
	}

	public float getMouseY() {
		return _noiseShader.getMouseY();
	}

	public void setTime(float f) {
		_noiseShader.setTime(f);
	}

	public float getTime() {
		return _noiseShader.getTime();
	}

	@Override
	public void setViewSize(float w, float h) {
		_noiseShader.setViewSize(w, h);
	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_noiseShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
		_noiseShader.updateToScreen();
	}

	@Override
	public void close() {

	}

}
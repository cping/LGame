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

public class ShockwaveMask implements FBOMask {

	public final static class ShockwaveShader extends ShaderSource {

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n" + "    #define PRECISION mediump\r\n"
				+ "    #define LOWP lowp\r\n" + "    precision PRECISION float;\r\n" + "#else\r\n"
				+ "    #define PRECISION\r\n" + "    #define LOWP \r\n" + "#endif\r\n" + "\r\n"
				+ "uniform sampler2D u_texture;\r\n" + "\r\n" + "uniform vec2 u_center;     \r\n"
				+ "uniform float u_time;      \r\n" + "uniform float u_diffusion;\r\n"
				+ "uniform float u_diffusionp;\r\n" + "uniform float u_thickness;\r\n" + "\r\n"
				+ "varying LOWP vec4 v_color;\r\n" + "varying vec2 v_texCoords;\r\n" + "\r\n" + "void main() {\r\n"
				+ "    vec2 uv = v_texCoords.xy;\r\n" + "    vec2 texCoord = uv;\r\n"
				+ "    float dist = distance(uv, u_center);\r\n" + "    float diff = dist - u_time; \r\n"
				+ "    if ((diff <= u_thickness) && (diff >= -u_thickness)) \r\n" + "    {\r\n"
				+ "        float powDiff = 1.0 - pow(abs(diff * u_diffusion), u_diffusionp); \r\n"
				+ "        float diffTime = diff * powDiff;\r\n"
				+ "        vec2 diffUV = normalize(uv - u_center); \r\n"
				+ "        texCoord = uv + (diffUV * diffTime);\r\n" + "    }\r\n"
				+ "    gl_FragColor = texture2D(u_texture, texCoord);\r\n" + "}\r\n";

		private final Vector2f _viewCenter;

		private final Vector2f _viewSize;

		private float _time;

		private float _thickness;

		private float _diffusiond;

		private float _diffusionp;

		private boolean _autoViewResize;

		public ShockwaveShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
			_autoViewResize = autoResize;
			_viewSize = new Vector2f(w, h);
			_viewCenter = new Vector2f(w / 2f, h / 2f);
			_diffusiond = 10f;
			_diffusionp = 0.7f;
			_thickness = 1f;
		}

		public void setViewSize(float w, float h) {
			_viewSize.set(w, h);
		}

		public void updateToScreen() {
			if (_autoViewResize) {
				_viewSize.set(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
			}
		}

		public void setViewCenter(float x, float y) {
			_viewCenter.set(x, y);
		}

		public void setTime(float t) {
			_time = t;
		}

		public float getTime() {
			return _time;
		}

		public void setThickness(float t) {
			_thickness = t;
		}

		public float getThickness() {
			return _thickness;
		}

		public float getDiffusion() {
			return _diffusiond;
		}

		public void setDiffusion(float d) {
			_diffusiond = d;
		}

		public float getDiffusionPower() {
			return _diffusionp;
		}

		public void setDiffusionPower(float d) {
			_diffusionp = d;
		}

		@Override
		public void setupShader(ShaderProgram program) {
			final float scaleX = LSystem.getScaleWidth();
			final float scaleY = LSystem.getScaleHeight();
			final float viewWidth = LSystem.viewSize.getWidth() * scaleX;
			final float viewHeight = LSystem.viewSize.getHeight() * scaleY;
			program.setUniformf("u_time", _time);
			program.setUniformf("u_center", ((_viewCenter.x * scaleX) / viewWidth),
					((_viewCenter.y * scaleY) / viewHeight));
			program.setUniformf("u_diffusion", _diffusiond);
			program.setUniformf("u_diffusionp", _diffusionp);
			program.setUniformf("u_thickness", _thickness);
		}
	}

	private boolean _shaderInited, _shaderDirty;

	private final ShaderMask _shaderMask;

	private final ShockwaveShader _shockwaveShader;

	public ShockwaveMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public ShockwaveMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public ShockwaveMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._shockwaveShader = new ShockwaveShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	public ShockwaveShader getShockwaveShader() {
		return _shockwaveShader;
	}

	@Override
	public ShaderSource getShader() {
		return getShockwaveShader();
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	public void setViewCenter(float x, float y) {
		_shockwaveShader.setViewCenter(x, y);
	}

	public void setTime(float t) {
		_shockwaveShader.setTime(t);
	}

	public float getTime() {
		return _shockwaveShader.getTime();
	}

	public void setThickness(float t) {
		_shockwaveShader.setThickness(t);
	}

	public float getThickness() {
		return _shockwaveShader.getThickness();
	}

	public float getDiffusion() {
		return _shockwaveShader.getDiffusion();
	}

	public void setDiffusion(float d) {
		_shockwaveShader.setDiffusion(d);
	}

	public float getDiffusionPower() {
		return _shockwaveShader.getDiffusionPower();
	}

	public void setDiffusionPower(float d) {
		_shockwaveShader.setDiffusionPower(d);
	}

	@Override
	public void setViewSize(float w, float h) {
		_shockwaveShader.setViewSize(w, h);
		_shockwaveShader.setViewCenter(w / 2f, h / 2f);
	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_shockwaveShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
	}

	@Override
	public void close() {

	}

}
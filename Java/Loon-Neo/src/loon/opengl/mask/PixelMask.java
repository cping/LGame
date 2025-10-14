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
 * 需要配合FBO类使用，可以实现整个画面的像素风格显示
 */
public class PixelMask implements FBOMask {

	public final static class PixelShader extends ShaderSource {

		private final Vector2f _viewSize;

		private final Vector2f _mouse;

		private float _frame;

		private boolean _autoViewResize;

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n" + "#define LOWP lowp\r\n"
				+ "precision mediump float;\r\n" + "#else\r\n" + "#define LOWP \r\n" + "#endif\r\n" + "\r\n"
				+ "varying LOWP vec4 v_color;\r\n" + "varying vec2 v_texCoords;\r\n" + "\r\n"
				+ "uniform sampler2D u_texture;\r\n" + "uniform vec2 resolution;\r\n" + "uniform vec2 mouse;\r\n"
				+ "uniform float time;\r\n" + "uniform float frame;\r\n" + "\r\n" + "void main() {\r\n"
				+ "	float square_size = floor(2.0 + frame * (mouse.x / resolution.x));\r\n" + "\r\n"
				+ "	vec2 center = square_size * floor(v_texCoords * resolution / square_size) + square_size * vec2(0.5, 0.5);\r\n"
				+ "	vec2 corner1 = center + square_size * vec2(-0.5, -0.5);\r\n"
				+ "	vec2 corner2 = center + square_size * vec2(+0.5, -0.5);\r\n"
				+ "	vec2 corner3 = center + square_size * vec2(+0.5, +0.5);\r\n"
				+ "	vec2 corner4 = center + square_size * vec2(-0.5, +0.5);\r\n" + "\r\n"
				+ "	vec3 pixel_color = 0.4 * texture2D(u_texture, center / resolution).rgb;\r\n"
				+ "	pixel_color += 0.15 * texture2D(u_texture, corner1 / resolution).rgb;\r\n"
				+ "	pixel_color += 0.15 * texture2D(u_texture, corner2 / resolution).rgb;\r\n"
				+ "	pixel_color += 0.15 * texture2D(u_texture, corner3 / resolution).rgb;\r\n"
				+ "	pixel_color += 0.15 * texture2D(u_texture, corner4 / resolution).rgb;\r\n" + "\r\n"
				+ "	gl_FragColor = vec4(pixel_color,1.0);\r\n" + "}";

		public PixelShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
			_autoViewResize = autoResize;
			_viewSize = new Vector2f(w, h);
			_mouse = new Vector2f(2);
			_frame = 30;
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

		public void setFrame(float f) {
			_frame = f;
		}

		public float getFrame() {
			return _frame;
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
			float scaleX = LSystem.getScaleHeight();
			float scaleY = LSystem.getScaleHeight();
			program.setUniformf("resolution", _viewSize.x * scaleX, _viewSize.y * scaleY);
			program.setUniformf("mouse", _mouse);
			program.setUniformf("frame", _frame);
		}
	}

	private boolean _shaderInited, _shaderDirty;
	private final ShaderMask _shaderMask;
	private final PixelShader _pixelShader;

	public PixelMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public PixelMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public PixelMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._pixelShader = new PixelShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	public PixelShader getPixelShader() {
		return _pixelShader;
	}

	@Override
	public ShaderSource getShader() {
		return getPixelShader();
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	public void setMouse(float x, float y) {
		_pixelShader.setMouse(x, y);
	}

	public float getMouseX() {
		return _pixelShader.getMouseX();
	}

	public float getMouseY() {
		return _pixelShader.getMouseY();
	}

	public void setFrame(float f) {
		_pixelShader.setFrame(f);
	}

	public float getFrame() {
		return _pixelShader.getFrame();
	}

	@Override
	public void setViewSize(float w, float h) {
		_pixelShader.setViewSize(w, h);
	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_pixelShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
		_pixelShader.updateToScreen();
	}

	@Override
	public void close() {

	}

}
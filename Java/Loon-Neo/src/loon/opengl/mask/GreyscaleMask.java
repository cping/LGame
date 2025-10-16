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
import loon.opengl.BlendMethod;
import loon.opengl.ShaderMask;
import loon.opengl.ShaderProgram;
import loon.opengl.ShaderSource;

/**
 * 需要配合FBO类使用，可以实现整个画面的灰度黑白风格显示
 */
public class GreyscaleMask implements FBOMask {

	public final static class GreyscaleShader extends ShaderSource {

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n"
				+ "#define LOWP lowp\r\n"
				+ "precision mediump float;\r\n"
				+ "#else\r\n"
				+ "#define LOWP \r\n"
				+ "#endif\r\n"
				+ "\r\n"
				+ "varying LOWP vec4 v_color;\r\n"
				+ "varying vec2 v_texCoords;\r\n"
				+ "\r\n"
				+ "uniform sampler2D u_texture;\r\n"
				+ "\r\n"
				+ "void main()\r\n"
				+ "{\r\n"
				+ "vec4 c = texture2D(u_texture, v_texCoords);\r\n"
				+ "float v = c.r * 0.2989 + c.g * 0.5870 + c.b * 0.1140;\r\n"
				+ "gl_FragColor.r = v;\r\n"
				+ "gl_FragColor.g = v;\r\n"
				+ "gl_FragColor.b = v;\r\n"
				+ "gl_FragColor.a = c.a;\r\n"
				+ "}";

		public GreyscaleShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
		}

		@Override
		public void setupShader(ShaderProgram program) {

		}
	}

	private boolean _shaderInited, _shaderDirty;
	private final ShaderMask _shaderMask;
	private final GreyscaleShader _greyscaleShader;

	public GreyscaleMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public GreyscaleMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public GreyscaleMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._greyscaleShader = new GreyscaleShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	public GreyscaleShader getGreyscaleShader() {
		return _greyscaleShader;
	}

	@Override
	public ShaderSource getShader() {
		return getGreyscaleShader();
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	@Override
	public void setViewSize(float w, float h) {

	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_greyscaleShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
	}

	@Override
	public void close() {

	}

}
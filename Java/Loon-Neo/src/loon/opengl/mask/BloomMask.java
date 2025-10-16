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
 * 需要配合FBO类使用，可以实现整个画面的扩散风格显示
 */
public class BloomMask implements FBOMask {

	public final static class BloomShader extends ShaderSource {

		private final Vector2f _viewSize;

		private final Vector2f _mouse;

		private float _frame;

		private int _blurDirection;

		private boolean _autoViewResize;

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n" + "#define LOWP lowp\r\n"
				+ "precision mediump float;\r\n" + "#else\r\n" + "#define LOWP \r\n" + "#endif\r\n" + "\r\n"
				+ "varying LOWP vec4 v_color;\r\n" + "varying vec2 v_texCoords;\r\n" + "\r\n"
				+ "uniform sampler2D u_texture;\r\n" + "uniform int blurDirection;\r\n" + "uniform vec2 resolution;\r\n"
				+ "uniform vec2 mouse;\r\n" + "uniform float frame;\r\n" + "\r\n"
				+ "float gaussianFunction(float x)\r\n" + "{\r\n" + "	float variance = 0.15; \r\n"
				+ "	float alpha = -(x*x / (2.0*variance));\r\n" + "	return exp(alpha);\r\n" + "}\r\n" + "\r\n"
				+ "float gaussianFunction2D(float x, float y)\r\n" + "{\r\n" + "	float variance = 0.25; \r\n"
				+ "	float alpha = -( (x*x+y*y) / (2.0*variance));\r\n" + "	return exp(alpha);\r\n" + "}\r\n" + "\r\n"
				+ "void main()\r\n" + "{\r\n" + "	float textureW = resolution.x;\r\n"
				+ "	float textureH = resolution.y;\r\n" + "\r\n" + "	float radiusSize = mouse.x;\r\n"
				+ "	float skip = frame;\r\n" + "	float totalWeight = mouse.y;\r\n" + "\r\n"
				+ "	vec4 accumulatedColor;\r\n" + "\r\n" + "	if(blurDirection == 0) \r\n" + "	{\r\n"
				+ "		float u = v_texCoords.x;\r\n" + "		float y;\r\n"
				+ "		for(y=-radiusSize; y<=radiusSize; y+=skip)\r\n" + "		{\r\n"
				+ "			float v = v_texCoords.y + y/textureH;\r\n" + "			\r\n"
				+ "			if(v>=0.0 && v<=1.0)\r\n" + "			{\r\n"
				+ "				float weight = gaussianFunction(y/radiusSize);\r\n"
				+ "				accumulatedColor += texture2D(u_texture, vec2(u,v)) * weight;\r\n"
				+ "				totalWeight += weight;\r\n" + "			}\r\n" + "		}\r\n" + "		\r\n"
				+ "		gl_FragColor = accumulatedColor / totalWeight;\r\n" + "	}\r\n"
				+ "	else if(blurDirection == 1) \r\n" + "	{\r\n" + "		float v = v_texCoords.y;\r\n"
				+ "		float x;\r\n" + "		for(x=-radiusSize; x<=radiusSize; x+=skip)\r\n" + "		{\r\n"
				+ "			float u = v_texCoords.x + x/textureW;\r\n" + "			if(u>=0.0 && u<=1.0)\r\n"
				+ "			{\r\n" + "				float weight = gaussianFunction(x/radiusSize);\r\n"
				+ "				accumulatedColor += texture2D(u_texture, vec2(u,v)) * weight;\r\n"
				+ "				totalWeight += weight;\r\n" + "			}\r\n" + "		}\r\n"
				+ "		gl_FragColor = accumulatedColor / totalWeight;\r\n" + "	}\r\n"
				+ "	else if(blurDirection == 2)\r\n" + "	{\r\n"
				+ "		vec4 testColor = texture2D(u_texture,v_texCoords);\r\n"
				+ "		float averageColor = (testColor.r + testColor.g + testColor.b) / 3.0;\r\n"
				+ "		if(averageColor > 0.6)\r\n" + "		{\r\n" + "			gl_FragColor = testColor;\r\n"
				+ "		}\r\n" + "		else\r\n" + "		{\r\n"
				+ "			gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\r\n" + "		}\r\n" + "	}\r\n" + "	else\r\n"
				+ "	{\r\n" + "		gl_FragColor = texture2D(u_texture,v_texCoords);\r\n" + "	}\r\n" + "}\r\n";

		public BloomShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
			_autoViewResize = autoResize;
			_viewSize = new Vector2f(w, h);
			_mouse = new Vector2f(8, 0);
			_frame = 4;
			_blurDirection = 0;
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

		public void setBlurDirection(int b) {
			_blurDirection = b;
		}

		public float getBlurDirection() {
			return _blurDirection;
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
			program.setUniformf("mouse", _mouse);
			program.setUniformf("frame", _frame);
			program.setUniformi("blurDirection", _blurDirection);
		}
	}

	private boolean _shaderInited, _shaderDirty;
	private final ShaderMask _shaderMask;
	private final BloomShader _bloomShader;

	public BloomMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BloomMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public BloomMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._bloomShader = new BloomShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	public BloomShader getBloomShader() {
		return _bloomShader;
	}

	@Override
	public ShaderSource getShader() {
		return getBloomShader();
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	public void setMouse(float x, float y) {
		_bloomShader.setMouse(x, y);
	}

	public float getMouseX() {
		return _bloomShader.getMouseX();
	}

	public float getMouseY() {
		return _bloomShader.getMouseY();
	}

	public void setFrame(float f) {
		_bloomShader.setFrame(f);
	}

	public float getFrame() {
		return _bloomShader.getFrame();
	}

	@Override
	public void setViewSize(float w, float h) {
		_bloomShader.setViewSize(w, h);
	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_bloomShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
		_bloomShader.updateToScreen();
	}

	@Override
	public void close() {

	}

}
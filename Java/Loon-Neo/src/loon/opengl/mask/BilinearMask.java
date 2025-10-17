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
import loon.utils.MathUtils;

/**
 * 画面映射扭曲用遮罩，可以将纹理8方向UV参数扭曲为理想角度，从而实现2D图片的伪3D变形。
 * 配合FBO类使用，则可以实现整个画面的变形显示，最常见的做法是2D游戏画面转到斜角透视画面。 嗯，再直白点说就是游戏王那类牌桌倾斜效果.
 * (loon中显示类提供有saveToFrameBuffer函数，激活则显示对象画面会自动保存到FrameBuffer纹理，
 * 然后直接getFrameBuffer再获得texture就行了)。
 */
public final class BilinearMask implements FBOMask {

	public final static class BilinearShader extends ShaderSource {

		private final Vector2f _topleft = new Vector2f();

		private final Vector2f _topright = new Vector2f();

		private final Vector2f _bottomleft = new Vector2f();

		private final Vector2f _bottomright = new Vector2f();

		private final Vector2f _viewSize;

		private boolean _autoViewResize;

		private final static String _fragmentShaderSource = "#ifdef GL_ES\r\n" + "#define LOWP lowp\r\n"
				+ "precision mediump float;\r\n" + "#else\r\n" + "#define LOWP \r\n" + "#endif\r\n"
				+ "uniform sampler2D u_texture;\r\n" + "varying LOWP vec4 v_color;\r\n"
				+ "varying vec2 v_texCoords;\r\n" + "uniform vec2 resolution;\r\n" + "\r\n"
				+ "uniform vec2 topleft;\r\n" + "uniform vec2 topright;\r\n" + "uniform vec2 bottomleft;\r\n"
				+ "uniform vec2 bottomright;\r\n" + "\r\n"
				+ "float crossvec( in vec2 a, in vec2 b ) { return a.x*b.y - a.y*b.x; }\r\n" + "\r\n"
				+ "vec2 invBilinear( in vec2 p, in vec2 a, in vec2 b, in vec2 c, in vec2 d ) {\r\n"
				+ "	vec2 res = vec2(-1.0);\r\n" + "\r\n" + "	vec2 e = b-a;\r\n" + "	vec2 f = d-a;\r\n"
				+ "	vec2 g = a-b+c-d;\r\n" + "	vec2 h = p-a;\r\n" + "\r\n" + "	float k2 = crossvec( g, f );\r\n"
				+ "	float k1 = crossvec( e, f ) + crossvec( h, g );\r\n" + "	float k0 = crossvec( h, e );\r\n"
				+ "\r\n" + "	if( abs(k2)<0.001 ) {\r\n"
				+ "		res = vec2( (h.x*k1+f.x*k0)/(e.x*k1-g.x*k0), -k0/k1 );\r\n" + "	}else {\r\n"
				+ "		float w = k1*k1 - 4.0*k0*k2;\r\n" + "		if( w<0.0 ) return vec2(-1.0);\r\n"
				+ "		w = sqrt( w );\r\n" + "\r\n" + "		float ik2 = 0.5/k2;\r\n"
				+ "		float v = (-k1 - w)*ik2;\r\n" + "		float u = (h.x - f.x*v)/(e.x + g.x*v);\r\n"
				+ "		\r\n" + "		if( u<0.0 || u>1.0 || v<0.0 || v>1.0 ) {\r\n" + "		v = (-k1 + w)*ik2;\r\n"
				+ "		   u = (h.x - f.x*v)/(e.x + g.x*v);\r\n" + "		}\r\n"
				+ "		res = vec2( u, 1.0 - v );\r\n" + "	}	\r\n" + "	return res;\r\n" + "}\r\n" + "\r\n"
				+ "void main()\r\n" + "{\r\n" + "  vec2 topleftUV = topleft / resolution;\r\n"
				+ "  vec2 toprightUV = vec2(1.0,0.0)+topright / resolution;\r\n"
				+ "  vec2 bottomrightUV = vec2(1.0,1.0)+bottomright / resolution;\r\n"
				+ "  vec2 bottomleftUV =vec2(0.0,1.0)+ bottomleft / resolution;\r\n"
				+ "  vec2 newUV = invBilinear(v_texCoords, topleftUV, toprightUV, bottomrightUV, bottomleftUV);\r\n"
				+ "  if (topleft.x == 0.0 || topright.x == 0.0) {\r\n"
				+ "      gl_FragColor = v_color * texture2D(u_texture, newUV);\r\n" + "  }else{\r\n"
				+ "      if (newUV == vec2(-1.0)){\r\n"
				+ "        gl_FragColor = v_color * texture2D(u_texture, v_texCoords);\r\n" + "      }else{\r\n"
				+ "        gl_FragColor = v_color * texture2D(u_texture, newUV);\r\n" + "      }\r\n" + "  }\r\n" + "}";

		public BilinearShader(boolean autoResize, float w, float h) {
			super(LSystem.getGLExVertexShader(), _fragmentShaderSource);
			_autoViewResize = autoResize;
			_viewSize = new Vector2f(w, h);
		}

		public float getTopLeftX() {
			return _topleft.x;
		}

		public float getTopLeftY() {
			return _topleft.y;
		}

		public float getTopRightX() {
			return _topright.x;
		}

		public float getTopRightY() {
			return _topright.y;
		}

		public float getBottomLeftX() {
			return _bottomleft.x;
		}

		public float getBottomLeftY() {
			return _bottomleft.y;
		}

		public float getBottomRightX() {
			return _bottomright.x;
		}

		public float getBottomRightY() {
			return _bottomright.y;
		}

		public void setXTopLeftRight(float x, float y) {
			_topleft.set(x, _topleft.y);
			_topright.set(y, _topright.y);
		}

		public void setYTopLeftRight(float x, float y) {
			_topleft.set(_topleft.x, x);
			_topright.set(_topright.x, y);
		}

		public void setXBottomLeftRight(float x, float y) {
			_bottomleft.set(x, _bottomleft.y);
			_bottomright.set(y, _bottomright.y);
		}

		public void setYBottomLeftRight(float x, float y) {
			_bottomleft.set(_bottomleft.x, x);
			_bottomright.set(_bottomleft.x, y);
		}

		public void setTopLeft(float x, float y) {
			_topleft.set(x, y);
		}

		public void setTopRight(float x, float y) {
			_topright.set(x, y);
		}

		public void setBottomLeft(float x, float y) {
			_bottomleft.set(x, y);
		}

		public void setBottomRight(float x, float y) {
			_bottomright.set(x, y);
		}

		public void setViewSize(float w, float h) {
			_viewSize.set(w, h);
		}

		public void updateToScreen() {
			if (_autoViewResize) {
				_viewSize.set(LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
			}
		}

		public Vector2f convertToUV(Vector2f v) {
			final Vector2f topleftUV = _topleft.div(_viewSize);
			final Vector2f toprightUV = Vector2f.at(1f, 0f).addSelf(_topright).div(_viewSize);
			final Vector2f bottomrightUV = Vector2f.at(1f, 1f).addSelf(_bottomright).div(_viewSize);
			final Vector2f bottomleftUV = Vector2f.at(0f, 1f).addSelf(_bottomleft).div(_viewSize);
			final Vector2f newUV = Vector2f.invBilinear(v, topleftUV, toprightUV, bottomrightUV, bottomleftUV);
			float vx = MathUtils.convertDecimalPlaces(newUV.x, 5);
			float vy = MathUtils.convertDecimalPlaces(newUV.y, 5);
			vx = MathUtils.clamp(vx, -MathUtils.abs(_topleft.x / 2f), _viewSize.x);
			vy = MathUtils.clamp(vy, -MathUtils.abs(_topleft.y / 2f), _viewSize.y);
			return newUV.set(vx, vy);
		}

		@Override
		public void setupShader(ShaderProgram program) {
			final float scaleX = LSystem.getScaleWidth();
			final float scaleY = LSystem.getScaleHeight();
			program.setUniformf("resolution", _viewSize.x * scaleX, _viewSize.y * scaleY);
			program.setUniformf("topleft", _topleft);
			program.setUniformf("topright", _topright);
			program.setUniformf("bottomleft", _bottomleft);
			program.setUniformf("bottomright", _bottomright);
		}
	}

	private boolean _shaderInited, _shaderDirty;
	
	private final ShaderMask _shaderMask;
	
	private final BilinearShader _bilinearShader;

	public BilinearMask() {
		this(true, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public BilinearMask(boolean a, float w, float h) {
		this(BlendMethod.MODE_ALPHA, a, w, h);
	}

	public BilinearMask(int b, boolean a, float w, float h) {
		this._shaderMask = new ShaderMask(b, this);
		this._bilinearShader = new BilinearShader(a, w, h);
		this._shaderDirty = true;
		this.update();
	}

	@Override
	public ShaderSource getShader() {
		return getBilinearShader();
	}

	public BilinearShader getBilinearShader() {
		return _bilinearShader;
	}

	public ShaderMask getMask() {
		return _shaderMask;
	}

	public float getTopLeftX() {
		return _bilinearShader.getTopLeftX();
	}

	public float getTopLeftY() {
		return _bilinearShader.getTopLeftY();
	}

	public float getTopRightX() {
		return _bilinearShader.getTopRightX();
	}

	public float getTopRightY() {
		return _bilinearShader.getTopRightY();
	}

	public void setXTopLeftRight(float x, float y) {
		_bilinearShader.setXTopLeftRight(x, y);
	}

	public void setYTopLeftRight(float x, float y) {
		_bilinearShader.setYTopLeftRight(x, y);
	}

	public void setXBottomLeftRight(float x, float y) {
		_bilinearShader.setXBottomLeftRight(x, y);
	}

	public void setYBottomLeftRight(float x, float y) {
		_bilinearShader.setYBottomLeftRight(x, y);
	}

	public void setViewSize(float w, float h) {
		_bilinearShader.setViewSize(w, h);
	}

	public Vector2f convertTouchToUV(Vector2f v) {
		final Vector2f result = _bilinearShader.convertToUV(v);
		return result.set(v.x - result.x, v.y - result.y);
	}

	@Override
	public void update() {
		if (!_shaderInited || _shaderDirty) {
			_shaderMask.setShaderSource(_bilinearShader);
			_shaderInited = true;
			_shaderDirty = false;
		}
		_bilinearShader.updateToScreen();
	}

	@Override
	public void close() {

	}

}

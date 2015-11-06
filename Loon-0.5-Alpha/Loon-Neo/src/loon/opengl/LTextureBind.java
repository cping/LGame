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
package loon.opengl;

import loon.LTexture;
import loon.utils.GLUtils;

public class LTextureBind extends GLBase {

	public static abstract class Source {

		public String fragment() {
			StringBuilder str = new StringBuilder(FRAGMENT_PREAMBLE);
			str.append(textureUniforms());
			str.append(textureVaryings());
			str.append("void main(void) {\n");
			str.append(textureColor());
			str.append(textureTint());
			str.append(textureAlpha());
			str.append("  gl_FragColor = textureColor;\n" + "}");
			return str.toString();
		}

		protected String textureUniforms() {
			return "uniform lowp sampler2D u_Texture;\n";
		}

		protected String textureVaryings() {
			return ("varying mediump vec2 v_TexCoord;\n"
					+ "varying lowp vec4 v_Color;\n");
		}

		protected String textureColor() {
			return "  vec4 textureColor = texture2D(u_Texture, v_TexCoord);\n";
		}

		protected String textureTint() {
			return "  textureColor.rgb *= v_Color.rgb;\n";
		}

		protected String textureAlpha() {
			return "  textureColor *= v_Color.a;\n";
		}

		protected static final String FRAGMENT_PREAMBLE = "#ifdef GL_ES\n"
				+ "precision lowp float;\n" + "#else\n" + "#define lowp\n"
				+ "#define mediump\n" + "#define highp\n" + "#endif\n";
	}

	public final GL20 gl;
	protected int curTexId;

	public void setTexture(LTexture texture) {
		int id = texture.getID();
		if (curTexId != 0 && curTexId != id) {
			flush();
		}
		this.curTexId = id;
	}

	@Override
	public void end() {
		super.end();
		curTexId = 0;
	}

	protected LTextureBind(GL20 gl) {
		this.gl = gl;
	}

	protected void bindTexture() {
		GLUtils.bindTexture(gl, curTexId);
	}

	@Override
	public void init() {

	}

	@Override
	public void freeBuffer() {

	}
}

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
package org.test;

import loon.Stage;
import loon.action.sprite.Entity;
import loon.opengl.GlobalSource;

public class ShaderTest extends Stage {

	private String green="#ifdef GL_ES\n"+
			"#define LOWP lowp\n"+
			"precision mediump float;\n"+
			"#else\n"+
			"#define LOWP \n"+
			"#endif\n"+
			"uniform sampler2D u_texture;\n"+
			"varying LOWP vec4 v_color;\n"+
			"varying vec2 v_texCoords;\n"+
			"void main() {\n"+
			"vec4 o =  texture2D(u_texture, v_texCoords); \n"+
			"float L = (17.8824 * o.r) + (43.5161 * o.g) + (4.11935 * o.b); \n"+
			"float M = (3.45565 * o.r) + (27.1554 * o.g) + (3.86714 * o.b); \n"+
			"float S = (0.0299566 * o.r) + (0.184309 * o.g) + (1.46709 * o.b); \n"+
		    "float l = 1.0 * L + 0.0 * M + 0.0 * S; \n"+
		    "float m = 0.0 * L + 1.0 * M + 0.0 * S; \n"+
		    "float s = -0.395913 * L + 0.801109 * M + 0.0 * S; \n"+
			"vec4 error;\n"+
			"error.r = (0.0809444479 * l) + (-0.130504409 * m) + (0.116721066 * s);\n"+
			"error.g = (-0.0102485335 * l) + (0.0540193266 * m) + (-0.113614708 * s);\n"+
			"error.b = (-0.000365296938 * l) + (-0.00412161469 * m) + (0.693511405 * s);\n"+
			"error.a = 1.0;\n"+
			"gl_FragColor = error.rgba;\n"+
			"};";
	@Override
	public void create() {
		GlobalSource source = new GlobalSource();

		source.setFragmentShader(green);
		//注入Shader
		setShaderSource(source);

		addPadding(Entity.make("bird.png"), 100, 150);
		addRow(Entity.make("ccc.png"), 100);

	}
	
	@Override
	public void close(){
		super.close();
		//还原成默认的Shader
		setShaderSource(new GlobalSource());
	}

}

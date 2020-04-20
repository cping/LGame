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
package loon.opengl;

import loon.LSystem;
import loon.utils.StringKeyValue;

public abstract class ShaderSource {

	private String _vertexShader;

	private String _framentShader;

	public ShaderSource() {
		this(LSystem.getGLExVertexShader(), LSystem.getGLExFragmentShader());
	}
	
	public ShaderSource(String vertex, String frament) {
		this._vertexShader = vertex;
		this._framentShader = frament;
	}

	/**
	 * 于此动态设置着色器参数
	 */
	public abstract void setupShader(ShaderProgram program);

	public String vertexShader() {
		return _vertexShader;
	}

	public String fragmentShader() {
		return _framentShader;
	}

	public void setVertexShader(String vertex) {
		this._vertexShader = vertex;
	}

	public void setFragmentShader(String fragment) {
		this._framentShader = fragment;
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) {
			return false;
		}
		if (o == this) {
			return true;
		}
		if (o instanceof ShaderSource) {
			ShaderSource other = (ShaderSource) o;
			if (other._vertexShader == null) {
				return false;
			}
			if (other._framentShader == null) {
				return false;
			}
			if (_vertexShader.length() != other._vertexShader.length()) {
				return false;
			}
			if (_framentShader.length() != other._framentShader.length()) {
				return false;
			}
			if (other._vertexShader.equals(_vertexShader) && other._framentShader.equals(_framentShader)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("ShaderSource");
		builder.newLine().kv("vertexShader", _vertexShader).newLine().kv("framentShader", _framentShader).newLine();
		return builder.toString();
	}

}

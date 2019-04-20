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
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class ShaderCmd {

	private final static ObjectMap<String, ShaderCmd> _instance = new ObjectMap<String, ShaderCmd>(100);

	public final static ShaderCmd getCmd(String name) {
		ShaderCmd cmd = _instance.get(name);
		if (cmd == null) {
			synchronized (_instance) {
				cmd = new ShaderCmd(10);
				_instance.put(name, cmd);
			}
		}
		return cmd;
	}

	public enum VarType {
		Attribute, Uniform, Varying;
	}

	private String command;

	private String cacheCommand;

	private final TArray<String> attributeList;

	private final TArray<String> uniformList;

	private final TArray<String> varyingList;

	private final String space = " ";

	private final String end = ";\n";

	protected final String FLOAT = "float";

	protected final String VEC2 = "vec2";

	protected final String VEC3 = "vec3";

	protected final String VEC4 = "vec4";

	protected final String MAT2 = "mat2";

	protected final String MAT3 = "mat3";

	protected final String MAT4 = "mat4";

	protected final String IF_DEF_GLES = "#ifdef GL_ES\n" //
			+ "#define LOWP lowp\n" //
			+ "precision mediump float;\n" //
			+ "#else\n" //
			+ "#define LOWP \n" //
			+ "#endif\n";

	private String define = null;

	private boolean flag = false;

	public ShaderCmd(int size) {
		this.define = IF_DEF_GLES;
		this.attributeList = new TArray<String>(size);
		this.uniformList = new TArray<String>(size);
		this.varyingList = new TArray<String>(size);
	}

	public ShaderCmd putAttributeFloat(String name) {
		return putAttribute(FLOAT, name);
	}

	public ShaderCmd putAttributeVec2(String name) {
		return putAttribute(VEC2, name);
	}

	public ShaderCmd putAttributeVec3(String name) {
		return putAttribute(VEC3, name);
	}

	public ShaderCmd putAttributeVec4(String name) {
		return putAttribute(VEC4, name);
	}

	public ShaderCmd putVaringFloat(String name) {
		return putVarying(FLOAT, name);
	}

	public ShaderCmd putVaryingVec2(String name) {
		return putVarying(VEC2, name);
	}

	public ShaderCmd putVaryingVec3(String name) {
		return putVarying(VEC3, name);
	}

	public ShaderCmd putVaryingVec4(String name) {
		return putVarying(VEC4, name);
	}

	public ShaderCmd putUniformVec2(String name) {
		return putUniform(VEC2, name);
	}

	public ShaderCmd putUniformVec3(String name) {
		return putUniform(VEC3, name);
	}

	public ShaderCmd putUniformVec4(String name) {
		return putUniform(VEC4, name);
	}

	public ShaderCmd putUniformMat2(String name) {
		return putUniform(MAT2, name);
	}

	public ShaderCmd putUniformMat3(String name) {
		return putUniform(MAT3, name);
	}

	public ShaderCmd putUniformMat4(String name) {
		return putUniform(MAT4, name);
	}

	public ShaderCmd putAttribute(String var, String name) {
		return putVar(VarType.Attribute, var, name);
	}

	public ShaderCmd putUniform(String var, String name) {
		return putVar(VarType.Uniform, var, name);
	}

	public ShaderCmd putVarying(String var, String name) {
		return putVar(VarType.Varying, var, name);
	}

	protected ShaderCmd putVar(VarType type, String var, String name) {
		clearCache();
		String typeString = null;
		switch (type) {
		case Attribute:
			typeString = "attribute";
			attributeList.add(typeString + space + var + space + name + end);
			break;
		case Uniform:
			typeString = "uniform";
			uniformList.add(typeString + space + var + space + name + end);
			break;
		case Varying:
			typeString = "varying";
			varyingList.add(typeString + space + var + space + name + end);
			break;
		default:
			break;
		}
		return this;
	}

	public String getAttributes() {
		StringBuilder cmds = new StringBuilder();
		for (String s : attributeList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getVaryings() {
		StringBuilder cmds = new StringBuilder();
		for (String s : varyingList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getUniforms() {
		StringBuilder cmds = new StringBuilder();
		for (String s : uniformList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getVarShader() {
		StringBuilder sbr = new StringBuilder();
		sbr.append(getAttributes());
		sbr.append(getUniforms());
		sbr.append(getVaryings());
		return sbr.toString();
	}

	public ShaderCmd putMainCmd(String cmd) {
		clearCache();
		command = null;
		if (!StringUtils.isEmpty(cmd)) {
			command = "void main()\n{\n" + cmd + "\n}\n";
		} else {
			command = "void main()\n" + "{\n\n}\n";
		}
		return this;
	}

	public ShaderCmd putMainLowpCmd(String cmd) {
		this.putMainCmd(cmd);
		this.flag = true;
		return this;
	}

	private void set(final String cache, final String cmd, final String def, final boolean f) {
		this.flag = f;
		this.define = def;
		this.cacheCommand = cache;
		this.command = cmd;
	}

	public ShaderCmd putDefine(String d) {
		this.clearCache();
		this.define = d;
		this.flag = true;
		return this;
	}

	public void clearCache() {
		if (StringUtils.isEmpty(define)) {
			this.flag = false;
		}
		this.cacheCommand = null;
	}

	public boolean isCache() {
		return !StringUtils.isEmpty(cacheCommand);
	}

	public String getShader() {
		if (StringUtils.isEmpty(cacheCommand)) {
			if (flag) {
				cacheCommand = (StringUtils.isEmpty(define) ? IF_DEF_GLES : define)
						+ (getVarShader() + (command == null ? LSystem.EMPTY : command));
			} else {
				cacheCommand = (getVarShader() + (command == null ? LSystem.EMPTY : command));
			}
		}
		return cacheCommand;
	}

	public ShaderCmd cpy() {
		ShaderCmd cmd = new ShaderCmd(10);
		cmd.attributeList.addAll(this.attributeList);
		cmd.uniformList.addAll(this.uniformList);
		cmd.varyingList.addAll(this.varyingList);
		cmd.set(cacheCommand, command, define, flag);
		return cmd;
	}

	@Override
	public String toString() {
		return getShader();
	}

}

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
import loon.utils.CollectionUtils;
import loon.utils.ObjectMap;
import loon.utils.StrBuilder;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

public final class ShaderCmd {

	private final static ObjectMap<String, ShaderCmd> _instance = new ObjectMap<String, ShaderCmd>();

	public final static ShaderCmd at(String name) {
		return getCmd(name);
	}

	public final static ShaderCmd getCmd(String name) {
		ShaderCmd cmd = _instance.get(name);
		if (cmd == null) {
			synchronized (_instance) {
				cmd = new ShaderCmd();
				_instance.put(name, cmd);
			}
		}
		return cmd;
	}

	public enum VarType {
		Attribute, Uniform, Varying, Struct, Const
	}

	private String command;

	private String cacheCommand;

	private final TArray<String> attributeList;

	private final TArray<String> uniformList;

	private final TArray<String> varyingList;

	private final TArray<String> constList;

	private final TArray<StringKeyValue> structList;

	private final String space = " ";

	private final String end = ";\n";

	protected final String INTEGER = "int";

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

	public ShaderCmd() {
		this(CollectionUtils.INITIAL_CAPACITY);
	}

	public ShaderCmd(int size) {
		this.define = IF_DEF_GLES;
		this.structList = new TArray<StringKeyValue>(size);
		this.constList = new TArray<String>(size);
		this.attributeList = new TArray<String>(size);
		this.uniformList = new TArray<String>(size);
		this.varyingList = new TArray<String>(size);
	}

	public ShaderCmd putConstFloat(String name) {
		return putConst(FLOAT, name);
	}

	public ShaderCmd putConstInt(String name) {
		return putConst(INTEGER, name);
	}

	public ShaderCmd putConstFloat(String name, String context) {
		return putConst(FLOAT, name, context);
	}

	public ShaderCmd putConstInt(String name, String context) {
		return putConst(INTEGER, name, context);
	}

	public ShaderCmd putAttributeFloat(String name) {
		return putAttribute(FLOAT, name);
	}

	public ShaderCmd putAttributeInt(String name) {
		return putAttribute(INTEGER, name);
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

	public ShaderCmd putVaringInt(String name) {
		return putVarying(INTEGER, name);
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

	public ShaderCmd putVaringFloat(String name, String context) {
		return putVarying(FLOAT, name, context);
	}

	public ShaderCmd putVaringInt(String name, String context) {
		return putVarying(INTEGER, name, context);
	}

	public ShaderCmd putVaryingVec2(String name, String context) {
		return putVarying(VEC2, name, context);
	}

	public ShaderCmd putVaryingVec3(String name, String context) {
		return putVarying(VEC3, name, context);
	}

	public ShaderCmd putVaryingVec4(String name, String context) {
		return putVarying(VEC4, name, context);
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

	public ShaderCmd putAttribute(String varName, String name) {
		return putVar(VarType.Attribute, varName, name);
	}

	public ShaderCmd putUniform(String varName, String name) {
		return putVar(VarType.Uniform, varName, name);
	}

	public ShaderCmd putVarying(String varName, String name) {
		return putVar(VarType.Varying, varName, name);
	}

	public ShaderCmd putConst(String varName, String name) {
		return putVar(VarType.Const, varName, name);
	}

	public ShaderCmd putAttribute(String varName, String name, String context) {
		return putVar(VarType.Attribute, varName, name, context);
	}

	public ShaderCmd putUniform(String varName, String name, String context) {
		return putVar(VarType.Uniform, varName, name, context);
	}

	public ShaderCmd putVarying(String varName, String name, String context) {
		return putVar(VarType.Varying, varName, name, context);
	}

	public ShaderCmd putConst(String varName, String name, String context) {
		return putVar(VarType.Const, varName, name, context);
	}

	public ShaderCmd putStruct(String structName, String context) {
		return putVar(VarType.Struct, null, structName, context);
	}

	public ShaderCmd putStruct(String structName, StrBuilder context) {
		return putStruct(structName, context.toString());
	}

	protected ShaderCmd putVar(VarType type, String varName, String name) {
		return putVar(type, varName, name, null);
	}

	protected ShaderCmd putVar(VarType type, String varName, String name, String context) {
		clearCache();
		final boolean isContext = !StringUtils.isEmpty(context);
		String typeString = null;
		switch (type) {
		case Attribute:
			typeString = "attribute";
			if (isContext) {
				attributeList.add(typeString + space + varName + space + name + " = " + context + end);
			} else {
				attributeList.add(typeString + space + varName + space + name + end);
			}
			break;
		case Uniform:
			typeString = "uniform";
			if (isContext) {
				uniformList.add(typeString + space + varName + space + name + end + " = " + context);
			} else {
				uniformList.add(typeString + space + varName + space + name + end);
			}
			break;
		case Varying:
			typeString = "varying";
			if (isContext) {
				varyingList.add(typeString + space + varName + space + name + " = " + context + end);
			} else {
				varyingList.add(typeString + space + varName + space + name + end);
			}
			break;
		case Const:
			typeString = "const";
			if (isContext) {
				constList.add(typeString + space + varName + space + name + " = " + context + end);
			} else {
				constList.add(typeString + space + varName + space + name + end);
			}
			break;
		case Struct:
			typeString = "struct";
			StringKeyValue skv = new StringKeyValue(typeString);
			skv.space().addValue(name).newLine().pushBrace().newLine();
			if (isContext) {
				skv.addValue(context);
			}
			skv.newLine().popBrace().branch();
			structList.add(skv);
			break;
		default:
			break;
		}
		return this;
	}

	public String getStruct() {
		StrBuilder cmds = new StrBuilder();
		for (int i = 0; i < structList.size; i++) {
			cmds.append(structList.get(i).toData());
			if (i == structList.size - 1) {
				cmds.append(LSystem.LF);
			}
		}
		return cmds.toString();
	}

	public String getConst() {
		StrBuilder cmds = new StrBuilder();
		for (String s : constList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getAttributes() {
		StrBuilder cmds = new StrBuilder();
		for (String s : attributeList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getVaryings() {
		StrBuilder cmds = new StrBuilder();
		for (String s : varyingList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getUniforms() {
		StrBuilder cmds = new StrBuilder();
		for (String s : uniformList) {
			cmds.append(s);
		}
		return cmds.toString();
	}

	public String getVarShader() {
		StrBuilder sbr = new StrBuilder();
		sbr.append(getStruct());
		sbr.append(getConst());
		sbr.append(getAttributes());
		sbr.append(getUniforms());
		sbr.append(getVaryings());
		return sbr.toString();
	}

	public ShaderCmd putMainCmd(StrBuilder cmd) {
		return putMainCmd(cmd.toString());
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
		ShaderCmd cmd = new ShaderCmd();
		cmd.constList.addAll(this.constList);
		cmd.attributeList.addAll(this.attributeList);
		cmd.uniformList.addAll(this.uniformList);
		cmd.varyingList.addAll(this.varyingList);
		cmd.structList.addAll(this.structList);
		cmd.set(cacheCommand, command, define, flag);
		return cmd;
	}

	@Override
	public String toString() {
		return getShader();
	}

}

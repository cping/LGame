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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;


import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.action.collision.CollisionHelper;
import loon.canvas.LColor;
import loon.canvas.LColorList;
import loon.canvas.LGradation;
import loon.geom.Affine2f;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.ShaderCmd;
import loon.opengl.ShaderProgram;
import loon.utils.ARC4;
import loon.utils.Array;
import loon.utils.ArrayByte;
import loon.utils.ArrayMap;
import loon.utils.BoolArray;
import loon.utils.Calculator;
import loon.utils.CharUtils;
import loon.utils.CollectionUtils;
import loon.utils.Easing;
import loon.utils.FloatArray;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.ListMap;
import loon.utils.MathUtils;
import loon.utils.ObjectBundle;
import loon.utils.ObjectMap;
import loon.utils.OrderedMap;
import loon.utils.OrderedSet;
import loon.utils.PathUtils;
import loon.utils.Random;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.TimeUtils;
import loon.utils.UUID;
import loon.utils.timer.Duration;

public class Test444 {
	public static String createVertexShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "attribute vec4 " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasNormals ? "attribute vec3 " + ShaderProgram.NORMAL_ATTRIBUTE + ";\n" : "")
				+ (hasColors ? "attribute vec4 " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "attribute vec2 " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}

		shader += "uniform mat4 u_projModelView;\n";
		shader += (hasColors ? "varying vec4 v_col;\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
		}

		shader += "void main()\n{\n" + "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
				+ (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");

		for (int i = 0; i < numTexCoords; i++) {
			shader += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
		}
		shader += "   gl_PointSize = 1.0;\n";
		shader += "}\n";
		return shader;
	}

	public static String createFragmentShader(boolean hasNormals, boolean hasColors, int numTexCoords) {
		String shader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n";

		for (int i = 0; i < numTexCoords; i++) {
			shader += "uniform sampler2D u_sampler" + i + ";\n";
		}

		if (hasColors)
			shader += "varying vec4 v_col;\n";
		for (int i = 0; i < numTexCoords; i++) {
			shader += "varying vec2 v_tex" + i + ";\n";
		}
		shader += "void main()\n{\n" + "  gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");

		if (numTexCoords > 0)
			shader += " * ";

		for (int i = 0; i < numTexCoords; i++) {
			if (i == numTexCoords - 1) {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
			} else {
				shader += " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
			}
		}

		shader += ";\n}";
		return shader;
	}

	public static String createVertexShader2(boolean hasNormals, boolean hasColors, int numTexCoords) {
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, hasNormals);
		hashCode = LSystem.unite(hashCode, hasColors);
		hashCode = LSystem.unite(hashCode, numTexCoords);
		ShaderCmd cmd = ShaderCmd.getCmd("gvshader" + hashCode);
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putAttributeVec4(ShaderProgram.POSITION_ATTRIBUTE);
			if (hasNormals) {
				cmd.putAttributeVec3(ShaderProgram.NORMAL_ATTRIBUTE);
			}
			if (hasColors) {
				cmd.putAttributeVec4(ShaderProgram.COLOR_ATTRIBUTE);
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putAttributeVec2(ShaderProgram.TEXCOORD_ATTRIBUTE + i);
			}
			cmd.putUniformMat4("u_projModelView");
			if (hasColors) {
				cmd.putVaryingVec4("v_col");
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putVaryingVec2("v_tex" + i);
			}
			String mainCmd = "   gl_Position = u_projModelView * " + ShaderProgram.POSITION_ATTRIBUTE + ";\n"
					+ (hasColors ? "   v_col = " + ShaderProgram.COLOR_ATTRIBUTE + ";\n" : "");
			for (int i = 0; i < numTexCoords; i++) {
				mainCmd += "   v_tex" + i + " = " + ShaderProgram.TEXCOORD_ATTRIBUTE + i + ";\n";
			}
			mainCmd += "   gl_PointSize = 1.0;";
			cmd.putMainCmd(mainCmd);
			return cmd.getShader();
		}
	}

	public static String createFragmentShader2(boolean hasNormals, boolean hasColors, int numTexCoords) {

		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, hasNormals);
		hashCode = LSystem.unite(hashCode, hasColors);
		hashCode = LSystem.unite(hashCode, numTexCoords);
		ShaderCmd cmd = ShaderCmd.getCmd("gfshader" + hashCode);
		if (cmd.isCache()) {
			return cmd.getShader();
		} else {
			cmd.putDefine("#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n");
			if (hasColors) {
				cmd.putVaryingVec4("v_col");
			}
			for (int i = 0; i < numTexCoords; i++) {
				cmd.putVaryingVec2("v_tex" + i);
				cmd.putUniform("sampler2D", "u_sampler" + i);
			}

			String mainCmd = "  gl_FragColor = " + (hasColors ? "v_col" : "vec4(1, 1, 1, 1)");
			if (numTexCoords > 0) {
				mainCmd += " * ";
			}
			for (int i = 0; i < numTexCoords; i++) {
				if (i == numTexCoords - 1) {
					mainCmd += " texture2D(u_sampler" + i + ",  v_tex" + i + ")";
				} else {
					mainCmd += " texture2D(u_sampler" + i + ",  v_tex" + i + ") *";
				}
			}
			mainCmd += ";";
			cmd.putMainCmd(mainCmd);
			return cmd.getShader();
		}
	}

	public static void main(String[] args) throws IOException {
		//ff0000ff

TArray<String> s=new TArray<String>(32);
s.add("65");
s.add("SB");
s.add("96");
s.unshift("666");
s.unshift("434666");
System.out.println(s.size);
System.out.println(s);
		
		//System.out.println(LColorList.get().find(LColorList.get().find("red")));
//System.out.println(Duration.at(0.5438964655f).toMillisLong());
		//System.out.println("我就是传说中的帝王".length());
		//System.out.println("我就是传说中的帝王".substring(0, 9));
		/*String test="dsdsffs'abcdefg'";
		System.out.println(Integer.toHexString(4454545));
		System.out.println(CharUtils.toHex(4454545));
		System.out.println(Integer.parseInt("4454545"));
		System.out.println(Duration.atMinute(4).formatTime());*/
		//float f=0.4568f;
		//System.out.println(Duration.at(1000).toSeconds());
		/*int size=300;
		long start = System.currentTimeMillis();
		FastHashMap<String, String> list=new FastHashMap<>();
		for(int i=0;i<size;i++){
			list.put(i+"A", i+"1546");
		}
		for(Entry<String, String> v:list){
			System.out.println(v.key);
		}*/
	/*
		long start = System.currentTimeMillis();
		int size = 100000;
		ObjectMap<String,Object> list = new ObjectMap<>();
		for(int i=0;i<size;i++){
			list.put(i+"A", i+"1546");
		}
		list.put("CNDY", "天地无极");
		System.out.println(list.hashCode());
		Array<String> sb=new Array<String>();
		sb.add("dsbd");
		sb.add("sdsdff");
		System.out.println(sb.hashCode());
		System.out.println(sb.get(0));*/
	  /*  float v=0.76f;  
		float vv= 0.5f * (1 - MathUtils.cos(MathUtils.PI * v));
		System.out.println(vv);
		System.out.println(MathUtils.PI / 2f);
		ObjectBundle b = new ObjectBundle();
		//添加一个数值a,数值1234,+128,+325,乘3,除69,返回a结果
		System.out.println(b.add("a","1234").inc("a", 128).inc("a", "325").mul("a", 3).div("a", 69).get("a"));*/
		/*
		int size = 100000;
		long start = System.currentTimeMillis();
		HashMap<String, Object> hashMap  =new HashMap<>();
		for(int i=0;i<size;i++){
			hashMap.put(String.valueOf(i), String.valueOf(i+1));
			
		}
		for(int i=0;i<size;i++){
			hashMap.remove(String.valueOf(i));
			
		}
		System.out.println("hashMap:"+(System.currentTimeMillis()-start));
		
		

		 start = System.currentTimeMillis();
		ObjectMap<String, Object> objMap  =new ObjectMap<>();
		for(int i=0;i<size;i++){
			objMap.put(String.valueOf(i), String.valueOf(i+1));
			
		}
		for(int i=0;i<size;i++){
			objMap.remove(String.valueOf(i));
			
		}

		System.out.println("objMap:"+(System.currentTimeMillis()-start));
		 start = System.currentTimeMillis();
		IntHashMap arrayMap  =new IntHashMap();
		for(int i=0;i<size;i++){
			arrayMap.put(i, String.valueOf(i+1));
			
		}	/*for(int i=0;i<size;i++){
			arrayMap.remove(i);
			
		}
		System.out.println(arrayMap.get(2667));
		
		System.out.println("arrayMap:"+(System.currentTimeMillis()-start));
 /*
		Object[] list=CollectionUtils.sendToBack(new String[]{"ASDAD","SAS","AA","BB","CC"},"SAS");
		for(int i=0;i<list.length;i++){
			System.out.println(list[i]);
		}*/
	}

}

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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import loon.LRelease;
import loon.LSystem;
import loon.LGame;
import loon.canvas.LColor;
import loon.geom.Matrix3;
import loon.geom.Matrix4;
import loon.geom.Vector2f;
import loon.geom.Vector3f;
import loon.utils.IntMap;
import loon.utils.ObjectMap;
import loon.utils.TArray;

public class ShaderProgram implements LRelease {

	public static final String POSITION_ATTRIBUTE = "a_position";

	public static final String NORMAL_ATTRIBUTE = "a_normal";

	public static final String COLOR_ATTRIBUTE = "a_color";

	public static final String TEXCOORD_ATTRIBUTE = "a_texCoord";

	public static final String TANGENT_ATTRIBUTE = "a_tangent";

	public static final String BINORMAL_ATTRIBUTE = "a_binormal";

	public static boolean pedantic = true;

	private final static ObjectMap<LGame, TArray<ShaderProgram>> shaders = new ObjectMap<LGame, TArray<ShaderProgram>>();

	private String log = "";

	private boolean isCompiled;

	private final IntMap<Integer> uniforms = new IntMap<Integer>();

	private final IntMap<Integer> uniformTypes = new IntMap<Integer>();

	private final IntMap<Integer> uniformSizes = new IntMap<Integer>();

	private String[] uniformNames;

	private final IntMap<Integer> attributes = new IntMap<Integer>();

	private final IntMap<Integer> attributeTypes = new IntMap<Integer>();

	private final IntMap<Integer> attributeSizes = new IntMap<Integer>();

	private String[] attributeNames;

	private int program;

	private int vertexShaderHandle;

	private int fragmentShaderHandle;

	private final String vertexShaderSource;

	private final String fragmentShaderSource;

	private boolean invalidated;

	private final int[] params = new int[2];

	private final int[] length = new int[1];

	private final int[] size = new int[1];

	private final int[] type = new int[1];

	private byte[] namebytes;

	final IntBuffer intbuf;

	public ShaderProgram(String vertexShader, String fragmentShader) {
		if (vertexShader == null) {
			throw new IllegalArgumentException("vertex shader must not be null");
		}
		if (fragmentShader == null) {
			throw new IllegalArgumentException("fragment shader must not be null");
		}
		String glslVersion = "#version 100\n";
		if (LSystem.base() != null && LSystem.base().graphics() != null) {
			glslVersion = "#version " + LSystem.base().graphics().gl.getGlslVersion() + "\n";
		}
		this.intbuf = LSystem.base().support().newIntBuffer(1);
		this.vertexShaderSource = vertexShader;
		this.fragmentShaderSource = fragmentShader;
		compileShaders(glslVersion + vertexShader, glslVersion + fragmentShader);
		if (isCompiled()) {
			fetchAttributesAndUniforms();
			addManagedShader(LSystem.base(), this);
		}
	}

	private void compileShaders(String vertexShader, String fragmentShader) {
		vertexShaderHandle = loadShader(GL20.GL_VERTEX_SHADER, vertexShader);
		fragmentShaderHandle = loadShader(GL20.GL_FRAGMENT_SHADER, fragmentShader);
		if (vertexShaderHandle == -1 || fragmentShaderHandle == -1) {
			isCompiled = false;
			return;
		}
		program = linkProgram(createProgram());
		if (program == -1) {
			isCompiled = false;
			return;
		}

		isCompiled = true;
	}

	private int loadShader(int type, String source) {
		GL20 gl = LSystem.base().graphics().gl;
		IntBuffer intbuf = LSystem.base().support().newIntBuffer(1);

		int shader = gl.glCreateShader(type);
		if (shader == 0) {
			return -1;
		}

		gl.glShaderSource(shader, source);
		gl.glCompileShader(shader);
		gl.glGetShaderiv(shader, GL20.GL_COMPILE_STATUS, intbuf);

		int compiled = intbuf.get(0);
		if (compiled == 0) {
			String infoLog = gl.glGetShaderInfoLog(shader);
			log += infoLog;
			return -1;
		}
		return shader;
	}

	protected int createProgram() {
		GL20 gl = LSystem.base().graphics().gl;
		int program = gl.glCreateProgram();
		return program != 0 ? program : -1;
	}

	private int linkProgram(int program) {
		GL20 gl = LSystem.base().graphics().gl;
		if (program == -1) {
			return -1;
		}

		gl.glAttachShader(program, vertexShaderHandle);
		gl.glAttachShader(program, fragmentShaderHandle);
		gl.glLinkProgram(program);

		ByteBuffer tmp = ByteBuffer.allocateDirect(4);
		tmp.order(ByteOrder.nativeOrder());
		IntBuffer intbuf = tmp.asIntBuffer();

		gl.glGetProgramiv(program, GL20.GL_LINK_STATUS, intbuf);
		int linked = intbuf.get(0);
		if (linked == 0) {
			log = LSystem.base().graphics().gl.glGetProgramInfoLog(program);
			return -1;
		}
		return program;
	}

	public String getLog() {
		if (isCompiled) {
			log = LSystem.base().graphics().gl.glGetProgramInfoLog(program);
			return log;
		} else {
			return log;
		}
	}

	public boolean isCompiled() {
		return isCompiled;
	}

	private int fetchAttributeLocation(String name) {
		GL20 gl = LSystem.base().graphics().gl;
		int location;
		if ((location = attributes.get(name, -2)) == -2) {
			location = gl.glGetAttribLocation(program, name);
			attributes.put(name, location);
		}
		return location;
	}

	private int fetchUniformLocation(String name) {
		return fetchUniformLocation(name, pedantic);
	}

	public int fetchUniformLocation(String name, boolean pedantic) {
		GL20 gl = LSystem.base().graphics().gl;
		int location;
		if ((location = uniforms.get(name, -2)) == -2) {
			location = gl.glGetUniformLocation(program, name);
			if (location == -1 && pedantic) {
				throw new IllegalArgumentException("no uniform with name '" + name + "' in shader");
			}
			uniforms.put(name, location);
		}
		return location;
	}

	public void setUniformi(String name, int value) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform1i(location, value);
	}

	public void setUniformi(int location, int value) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform1i(location, value);
	}

	public void setUniformi(String name, int value1, int value2) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform2i(location, value1, value2);
	}

	public void setUniformi(int location, int value1, int value2) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform2i(location, value1, value2);
	}

	public void setUniformi(String name, int value1, int value2, int value3) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform3i(location, value1, value2, value3);
	}

	public void setUniformi(int location, int value1, int value2, int value3) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform3i(location, value1, value2, value3);
	}

	public void setUniformi(String name, int value1, int value2, int value3, int value4) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform4i(location, value1, value2, value3, value4);
	}

	public void setUniformi(int location, int value1, int value2, int value3, int value4) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform4i(location, value1, value2, value3, value4);
	}

	public void setUniformf(String name, float value) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform1f(location, value);
	}

	public void setUniformf(String name, LColor values) {
		setUniformf(name, values.r, values.g, values.b, values.a);
	}

	public void setUniformf(int location, LColor values) {
		setUniformf(location, values.r, values.g, values.b, values.a);
	}

	public void setUniformf(int location, float value) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform1f(location, value);
	}

	public void setUniformf(String name, float value1, float value2) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform2f(location, value1, value2);
	}

	public void setUniformf(int location, float value1, float value2) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform2f(location, value1, value2);
	}

	public void setUniformf(String name, float value1, float value2, float value3) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform3f(location, value1, value2, value3);
	}

	public void setUniformf(int location, float value1, float value2, float value3) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform3f(location, value1, value2, value3);
	}

	public void setUniformf(String name, float value1, float value2, float value3, float value4) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform4f(location, value1, value2, value3, value4);
	}

	public void setUniformf(int location, float value1, float value2, float value3, float value4) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform4f(location, value1, value2, value3, value4);
	}

	public void setUniform1fv(String name, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform1fv(location, length, values, offset);
	}

	public void setUniform1fv(int location, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform1fv(location, length, values, offset);
	}

	public void setUniform2fv(String name, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform2fv(location, length / 2, values, offset);
	}

	public void setUniform2fv(int location, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform2fv(location, length / 2, values, offset);
	}

	public void setUniform3fv(String name, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform3fv(location, length / 3, values, offset);
	}

	public void setUniform3fv(int location, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform3fv(location, length / 3, values, offset);
	}

	public void setUniform4fv(String name, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchUniformLocation(name);
		gl.glUniform4fv(location, length / 4, values, offset);
	}

	public void setUniform4fv(int location, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniform4fv(location, length / 4, values, offset);
	}

	public void setUniformMatrix(String name, Matrix4 matrix) {
		setUniformMatrix(name, matrix, false);
	}

	public void setUniformMatrix(String name, Matrix4 matrix, boolean transpose) {
		setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
	}

	public void setUniformMatrix(int location, Matrix4 matrix) {
		setUniformMatrix(location, matrix, false);
	}

	public void setUniformMatrix(int location, Matrix4 matrix, boolean transpose) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniformMatrix4fv(location, 1, transpose, matrix.val, 0);
	}

	public void setUniformMatrix(String name, Matrix3 matrix) {
		setUniformMatrix(name, matrix, false);
	}

	public void setUniformMatrix(String name, Matrix3 matrix, boolean transpose) {
		setUniformMatrix(fetchUniformLocation(name), matrix, transpose);
	}

	public void setUniformMatrix(int location, Matrix3 matrix) {
		setUniformMatrix(location, matrix, false);
	}

	public void setUniformMatrix(int location, Matrix3 matrix, boolean transpose) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniformMatrix3fv(location, 1, transpose, matrix.val, 0);
	}

	public void setUniformMatrix3fv(String name, FloatBuffer buffer, int count, boolean transpose) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		buffer.position(0);
		int location = fetchUniformLocation(name);
		gl.glUniformMatrix3fv(location, count, transpose, buffer);
	}

	public void setUniformMatrix4fv(String name, FloatBuffer buffer, int count, boolean transpose) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		buffer.position(0);
		int location = fetchUniformLocation(name);
		gl.glUniformMatrix4fv(location, count, transpose, buffer);
	}

	public void setUniformMatrix4fv(int location, float[] values, int offset, int length) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUniformMatrix4fv(location, length / 16, false, values, offset);
	}

	public void setUniformMatrix4fv(String name, float[] values, int offset, int length) {
		setUniformMatrix4fv(fetchUniformLocation(name), values, offset, length);
	}

	public void setUniformf(String name, Vector2f values) {
		setUniformf(name, values.x, values.y);
	}

	public void setUniformf(int location, Vector2f values) {
		setUniformf(location, values.x, values.y);
	}

	public void setUniformf(String name, Vector3f values) {
		setUniformf(name, values.x, values.y, values.z);
	}

	public void setUniformf(int location, Vector3f values) {
		setUniformf(location, values.x, values.y, values.z);
	}

	public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, Buffer buffer) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchAttributeLocation(name);
		if (location == -1) {
			return;
		}
		gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
	}

	public void setVertexAttribute(int location, int size, int type, boolean normalize, int stride, Buffer buffer) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glVertexAttribPointer(location, size, type, normalize, stride, buffer);
	}

	public void setVertexAttribute(String name, int size, int type, boolean normalize, int stride, int offset) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchAttributeLocation(name);
		if (location == -1)
			return;
		gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
	}

	public void setVertexAttribute(int location, int size, int type, boolean normalize, int stride, int offset) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glVertexAttribPointer(location, size, type, normalize, stride, offset);
	}

	public void begin() {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glUseProgram(program);
	}

	public void glUseProgramBind() {
		GL20 gl = LSystem.base().graphics().gl;
		gl.glUseProgram(program);
	}

	public void glUseProgramUnBind() {
		if (!LSystem.mainDrawRunning()) {
			GL20 gl = LSystem.base().graphics().gl;
			gl.glUseProgram(0);
		}
	}

	public void end() {
		if (!LSystem.mainDrawRunning()) {
			GL20 gl = LSystem.base().graphics().gl;
			gl.glUseProgram(0);
		}
	}

	public void close() {
		GL20 gl = LSystem.base().graphics().gl;
		if (gl != null) {
			if (!LSystem.mainDrawRunning()) {
				gl.glUseProgram(0);
			}
			gl.glDeleteShader(vertexShaderHandle);
			gl.glDeleteShader(fragmentShaderHandle);
			gl.glDeleteProgram(program);
			if (shaders.get(LSystem.base()) != null) {
				shaders.get(LSystem.base()).removeValue(this, true);
			}
		}
	}

	public void disableVertexAttribute(String name) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchAttributeLocation(name);
		if (location == -1) {
			return;
		}
		gl.glDisableVertexAttribArray(location);
	}

	public void disableVertexAttribute(int location) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glDisableVertexAttribArray(location);
	}

	public void enableVertexAttribute(String name) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		int location = fetchAttributeLocation(name);
		if (location == -1) {
			return;
		}
		gl.glEnableVertexAttribArray(location);
	}

	public void enableVertexAttribute(int location) {
		GL20 gl = LSystem.base().graphics().gl;
		checkManaged();
		gl.glEnableVertexAttribArray(location);
	}

	private void checkManaged() {
		if (invalidated) {
			compileShaders(vertexShaderSource, fragmentShaderSource);
			invalidated = false;
		}
	}

	private void addManagedShader(LGame self, ShaderProgram shaderProgram) {
		TArray<ShaderProgram> managedResources = shaders.get(self);
		if (managedResources == null) {
			managedResources = new TArray<ShaderProgram>();
		}
		managedResources.add(shaderProgram);
		shaders.put(self, managedResources);
	}

	public static void invalidateAllShaderPrograms(LGame self) {
		if (shaders == null || shaders.size == 0) {
			return;
		}
		if (LSystem.base().graphics().gl == null) {
			return;
		}
		TArray<ShaderProgram> shaderArray = shaders.get(self);
		if (shaderArray == null) {
			return;
		}
		for (int i = 0; i < shaderArray.size; i++) {
			shaderArray.get(i).invalidated = true;
			shaderArray.get(i).checkManaged();
		}
	}

	public static void clearAllShaderPrograms(LGame self) {
		shaders.remove(self);
	}

	public static String getManagedStatus() {
		StringBuilder builder = new StringBuilder();
		builder.append("Managed shaders/self: { ");
		for (LGame self : shaders.keys()) {
			builder.append(shaders.get(self).size);
			builder.append(" ");
		}
		builder.append("}");
		return builder.toString();
	}

	public void setAttributef(String name, float value1, float value2, float value3, float value4) {
		GL20 gl = LSystem.base().graphics().gl;
		int location = fetchAttributeLocation(name);
		gl.glVertexAttrib4f(location, value1, value2, value3, value4);
	}

	public void setAttributef(String name, LColor color) {
		GL20 gl = LSystem.base().graphics().gl;
		int location = fetchAttributeLocation(name);
		gl.glVertexAttrib4f(location, color.r, color.g, color.b, color.a);
	}

	private void fetchAttributesAndUniforms() {
		GL20 gl = LSystem.base().graphics().gl;

		if (gl instanceof GLExt) {

			IntBuffer params = LSystem.base().support().newIntBuffer(1);
			IntBuffer type = LSystem.base().support().newIntBuffer(1);

			params.clear();
			gl.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params);
			int numAttributes = params.get(0);

			attributeNames = new String[numAttributes];

			GLExt ext = (GLExt) gl;

			for (int i = 0; i < numAttributes; i++) {
				params.clear();
				params.put(0, 1);
				type.clear();
				String name = ext.glGetActiveAttrib(program, i, params, type);
				int location = gl.glGetAttribLocation(program, name);
				attributes.put(name, location);
				attributeTypes.put(name, type.get(0));
				attributeSizes.put(name, params.get(0));
				attributeNames[i] = name;
			}

			params.clear();
			gl.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, params);
			int numUniforms = params.get(0);

			uniformNames = new String[numUniforms];

			for (int i = 0; i < numUniforms; i++) {
				params.clear();
				params.put(0, 1);
				type.clear();
				String name = ext.glGetActiveUniform(program, i, params, type);
				int location = gl.glGetUniformLocation(program, name);
				uniforms.put(name, location);
				uniformTypes.put(name, type.get(0));
				uniformSizes.put(name, params.get(0));
				uniformNames[i] = name;
			}
		} else {

			gl.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTES, params, 0);
			gl.glGetProgramiv(program, GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH, params, 1);
			int numAttributes = params[0];
			int maxAttributeLength = params[1];
			namebytes = new byte[maxAttributeLength];
			attributeNames = new String[numAttributes];
			for (int i = 0; i < numAttributes; i++) {
				gl.glGetActiveAttrib(program, i, maxAttributeLength, length, 0, size, 0, type, 0, namebytes, 0);
				String name = new String(namebytes, 0, length[0]);
				attributes.put(name, gl.glGetAttribLocation(program, name));
				attributeTypes.put(name, type[0]);
				attributeSizes.put(name, params[0]);
				attributeNames[i] = name;
			}
			gl.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORMS, params, 0);
			gl.glGetProgramiv(program, GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH, params, 1);
			int numUniforms = params[0];
			int maxUniformLength = params[1];
			namebytes = new byte[maxUniformLength];
			uniformNames = new String[numUniforms];
			for (int i = 0; i < numUniforms; i++) {
				gl.glGetActiveUniform(program, i, maxUniformLength, length, 0, size, 0, type, 0, namebytes, 0);
				String name = new String(namebytes, 0, length[0]);
				uniforms.put(name, gl.glGetUniformLocation(program, name));
				uniformTypes.put(name, type[0]);
				uniformSizes.put(name, params[0]);
				uniformNames[i] = name;
			}
		}
	}

	public boolean hasAttribute(String name) {
		return attributes.containsKey(name);
	}

	public int getAttributeType(String name) {
		return attributeTypes.get(name, 0);
	}

	public int getAttributeLocation(String name) {
		return attributes.get(name, -1);
	}

	public int getAttributeSize(String name) {
		return attributeSizes.get(name, 0);
	}

	public boolean hasUniform(String name) {
		return uniforms.containsKey(name);
	}

	public int getUniformType(String name) {
		return uniformTypes.get(name, 0);
	}

	public int getUniformLocation(String name) {
		return uniforms.get(name, -1);
	}

	public int getUniformSize(String name) {
		return uniformSizes.get(name, 0);
	}

	public String[] getAttributes() {
		return attributeNames;
	}

	public String[] getUniforms() {
		return uniformNames;
	}

	public String getVertexShaderSource() {
		return vertexShaderSource;
	}

	public String getFragmentShaderSource() {
		return fragmentShaderSource;
	}
}

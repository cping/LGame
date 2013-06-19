package loon.core.graphics.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * 
 * Copyright 2008 - 2011
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
 * @email javachenpeng@yahoo.com
 * @version 0.1
 */

public interface GL10 extends GLBase {

	public void glAlphaFunc(int func, float ref);

	public void glClientActiveTexture(int texture);

	public void glColor4f(float red, float green, float blue, float alpha);

	public void glColorPointer(int size, int type, int stride, Buffer pointer);

	public void glDeleteTextures(int n, int[] textures, int offset);

	public void glDisableClientState(int array);

	public void glEnableClientState(int array);

	public void glFogf(int pname, float param);

	public void glFogfv(int pname, float[] params, int offset);

	public void glFogfv(int pname, FloatBuffer params);

	public void glFrustumf(float left, float right, float bottom, float top,
			float zNear, float zFar);

	public void glGenTextures(int n, int[] textures, int offset);

	@Override
	public void glGenTextures(int n, IntBuffer textures);

	public int glGenTextures();

	public void glGetIntegerv(int pname, int[] params, int offset);

	public void glLightModelf(int pname, float param);

	public void glLightModelfv(int pname, float[] params, int offset);

	public void glLightModelfv(int pname, FloatBuffer params);

	public void glLightf(int light, int pname, float param);

	public void glLightfv(int light, int pname, float[] params, int offset);

	public void glLightfv(int light, int pname, FloatBuffer params);

	public void glLoadIdentity();

	public void glLoadMatrixf(float[] m, int offset);

	public void glLoadMatrixf(FloatBuffer m);

	public void glLogicOp(int opcode);

	public void glMaterialf(int face, int pname, float param);

	public void glMaterialfv(int face, int pname, float[] params, int offset);

	public void glMaterialfv(int face, int pname, FloatBuffer params);

	public void glMatrixMode(int mode);

	public void glMultMatrixf(float[] m, int offset);

	public void glMultMatrixf(FloatBuffer m);

	public void glMultiTexCoord4f(int target, float s, float t, float r, float q);

	public void glNormal3f(float nx, float ny, float nz);

	public void glNormalPointer(int type, int stride, Buffer pointer);

	public void glOrthof(float left, float right, float bottom, float top,
			float zNear, float zFar);

	public void glOrthox(int left, int right, int bottom,int top,
			int zNear, int zFar);
	
	public void glPointSize(float size);

	public void glPopMatrix();

	public void glPushMatrix();

	@Override
	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels);

	public void glRotatef(float angle, float x, float y, float z);

	public void glSampleCoverage(float value, boolean invert);

	public void glScalef(float x, float y, float z);

	public void glShadeModel(int mode);

	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer);

	public void glTexEnvf(int target, int pname, float param);

	public void glTexEnvfv(int target, int pname, float[] params, int offset);

	public void glTexEnvfv(int target, int pname, FloatBuffer params);

	public void glTranslatef(float x, float y, float z);

	public void glVertexPointer(int size, int type, int stride, Buffer pointer);

}

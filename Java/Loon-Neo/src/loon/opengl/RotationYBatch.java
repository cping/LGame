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

public class RotationYBatch extends TrilateralBatch {
	
	public float angle;

	public RotationYBatch(GL20 gl, final float eyeX, final float eyeY,
			final float zScale) {
		super(gl, new Source() {
			@Override
			public String vertex() {
				return RotationYBatch.vertex(eyeX, eyeY, zScale);
			}
		});
		uAngle = program.getUniformLocation("u_Angle");
	}

	@Override
	public void begin(float fbufWidth, float fbufHeight, boolean flip) {
		super.begin(fbufWidth, fbufHeight, flip);
		program.activate();
		gl.glUniform1f(uAngle, angle);
	}

	private final int uAngle;

	protected static String vertex(float eyeX, float eyeY, float zScale) {
		return TrilateralBatch.Source.VERT_UNIFS
				+ "uniform float u_Angle;\n"
				+ TrilateralBatch.Source.VERT_ATTRS
				+ TrilateralBatch.Source.PER_VERT_ATTRS
				+ TrilateralBatch.Source.VERT_VARS
				+
				"void main(void) {\n"
				+
				"  mat4 transform = mat4(\n"
				+ "    a_Matrix[0],      a_Matrix[1],      0, 0,\n"
				+ "    a_Matrix[2],      a_Matrix[3],      0, 0,\n"
				+ "    0,                0,                1, 0,\n"
				+ "    a_Translation[0], a_Translation[1], 0, 1);\n"
				+ "  vec4 pos = transform * vec4(a_Position, 0, 1);\n"
				+
				"  float cosa = cos(u_Angle);\n"
				+ "  float sina = sin(u_Angle);\n"
				+ "  mat4 rotmat = mat4(\n"
				+ "    cosa, 0, sina, 0,\n"
				+ "    0,    1, 0,    0,\n"
				+ "   -sina, 0, cosa, 0,\n"
				+ "    0,    0, 0,    1);\n"
				+ "  pos = rotmat * vec4(pos.x - "
				+ format(eyeX)
				+ " * 2.*u_HScreenSize.x,\n"
				+ "                      pos.y - "
				+ format(eyeY)
				+ " * 2.*u_HScreenSize.y,\n"
				+ "                      0, 1);\n"
				+
				"  mat4 persp = mat4(\n" + "    1, 0, 0, 0,\n"
				+ "    0, 1, 0, 0,\n" + "    0, 0, 1, -1.0/2000.0,\n"
				+ "    0, 0, 0, 1);\n" + "  pos = persp * pos;\n"
				+ "  pos += vec4(" + format(eyeX)
				+ " * 2.*u_HScreenSize.x,\n"
				+ "              "
				+ format(eyeY)
				+ " * 2.*u_HScreenSize.y, 0, 0);\n"
				+
				"  pos.xy /= u_HScreenSize.xy;\n"
				+ "  pos.z  /= (u_HScreenSize.x * " + format(zScale) + ");\n"
				+ "  pos.xy -= 1.0;\n"
				+
				"  pos.y  *= u_Flip;\n" + "  gl_Position = pos;\n" +

				TrilateralBatch.Source.VERT_SETTEX
				+ TrilateralBatch.Source.VERT_SETCOLOR + "}";
	}

	public static String format(float value) {
		String fmt = String.valueOf(value);
		return fmt.indexOf('.') == -1 ? (fmt + ".0") : fmt;
	}
}

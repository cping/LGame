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
package loon.canvas;

import loon.geom.Affine2f;
import loon.utils.MathUtils;

public class PixmapMatrixTransform implements PixmapTransform {

	final float m00, m01, m02;
	final float m10, m11, m12;

	public PixmapMatrixTransform(Affine2f aff) {
		this(aff.m00, aff.m01, aff.tx, aff.m10, aff.m11, aff.ty);
	}

	public PixmapMatrixTransform(float m00, float m01, float m02, float m10, float m11, float m12) {
		this.m00 = m00;
		this.m01 = m01;
		this.m02 = m02;
		this.m10 = m10;
		this.m11 = m11;
		this.m12 = m12;
	}

	@Override
	public void transform(float x, float y, float[] out) {
		out[0] = m00 * x + m01 * y + m02;
		out[1] = m10 * x + m11 * y + m12;
	}

	public static PixmapMatrixTransform rotation(float radians) {
		float cos = MathUtils.cos(radians);
		float sin = MathUtils.sin(radians);
		return new PixmapMatrixTransform(cos, sin, 0, -sin, cos, 0);
	}

	public static PixmapMatrixTransform scale(float sx, float sy) {
		return new PixmapMatrixTransform(sx, 0, 0, 0, sy, 0);
	}

	public static PixmapMatrixTransform shear(float shx, float shy) {
		return new PixmapMatrixTransform(1, shx, 0, shy, 1, 0);
	}

	public static PixmapMatrixTransform translate(float tx, float ty) {
		return new PixmapMatrixTransform(1, 0, tx, 0, 1, ty);
	}

	public PixmapMatrixTransform combine(PixmapMatrixTransform other) {
		return new PixmapMatrixTransform(m00 * other.m00 + m01 * other.m10, m00 * other.m01 + m01 * other.m11,
				m00 * other.m02 + m01 * other.m12 + m02, m10 * other.m00 + m11 * other.m10,
				m10 * other.m01 + m11 * other.m11, m10 * other.m02 + m11 * other.m12 + m12);
	}
}
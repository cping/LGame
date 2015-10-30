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
package loon.geom;

public class Transforms {

	public static <T extends Transform> T multiply(Affine2f a, Affine2f b,
			T into) {
		return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, b.m00, b.m01,
				b.m10, b.m11, b.tx, b.ty, into);
	}

	public static <T extends Transform> T multiply(Affine2f a, float m00,
			float m01, float m10, float m11, float tx, float ty, T into) {
		return multiply(a.m00, a.m01, a.m10, a.m11, a.tx, a.ty, m00, m01, m10,
				m11, tx, ty, into);
	}

	public static <T extends Transform> T multiply(float m00, float m01,
			float m10, float m11, float tx, float ty, Affine2f b, T into) {
		return multiply(m00, m01, m10, m11, tx, ty, b.m00, b.m01, b.m10, b.m11,
				b.tx, b.ty, into);
	}

	public static <T extends Transform> T multiply(float am00, float am01,
			float am10, float am11, float atx, float aty, float bm00,
			float bm01, float bm10, float bm11, float btx, float bty, T into) {
		into.setTransform(am00 * bm00 + am10 * bm01, am01 * bm00 + am11 * bm01,
				am00 * bm10 + am10 * bm11, am01 * bm10 + am11 * bm11, am00
						* btx + am10 * bty + atx, am01 * btx + am11 * bty + aty);
		return into;
	}
}

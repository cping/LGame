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

import loon.utils.MathUtils;

/**
 * 渲染三角矩阵时合规性检测用类
 */
public class GLTriangle {

	public float tx1 = 0f;

	public float ty1 = 0f;

	public float uvx1 = 0f;

	public float uvy1 = 0f;

	public float tx2 = 0f;

	public float ty2 = 0f;

	public float uvx2 = 0f;

	public float uvy2 = 0f;

	public float tx3 = 0f;

	public float ty3 = 0f;

	public float uvx3 = 0f;

	public float uvy3 = 0f;

	public float color = 0f;

	public float alpha = 0f;

	protected GLTriangle _next = null;

	public float getX1() {
		return MathUtils.min(tx1, tx2, tx3);
	}

	public float getX2() {
		return MathUtils.max(tx1, tx2, tx3);
	}

	public float getY1() {
		return MathUtils.min(ty1, ty2, ty3);
	}

	public float getY2() {
		return MathUtils.max(ty1, ty2, ty3);
	}

	public static float cross(float ux, float uy, float vx, float vy) {
		return ux * vy - uy * vx;
	}

	public static boolean linesIntersect(float x11, float y11, float x12, float y12, float x21, float y21, float x22,
			float y22) {
		float d = ((y22 - y21) * (x12 - x11)) - ((x22 - x21) * (y12 - y11));
		if (d != 0) {
			final float ua = (((x22 - x21) * (y11 - y21)) - ((y22 - y21) * (x11 - x21))) / d;
			final float ub = (((x12 - x11) * (y11 - y21)) - ((y12 - y11) * (x11 - x21))) / d;
			if (ua >= 0 && ua <= 1 && ub >= 0 && ub <= 1) {
				d = 0;
			}
		}
		return d == 0;
	}

	public static boolean triangleContains(float x1, float y1, float x2, float y2, float x3, float y3, float px,
			float py) {
		float v0x = x3 - x1, v0y = y3 - y1, v1x = x2 - x1, v1y = y2 - y1, v2x = px - x1, v2y = py - y1;
		float u = cross(v2x, v2y, v0x, v0y), v = cross(v1x, v1y, v2x, v2y), d = cross(v1x, v1y, v0x, v0y);
		if (d < 0) {
			u = -u;
			v = -v;
			d = -d;
		}
		return u >= 0 && v >= 0 && (u + v) <= d;
	}

	public boolean intersectsTriangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		return (linesIntersect(x1, y1, x2, y2, tx1, ty1, tx2, ty2) || linesIntersect(x2, y2, x3, y3, tx1, ty1, tx2, ty2)
				|| linesIntersect(x1, y1, x3, y3, tx1, ty1, tx2, ty2)
				|| linesIntersect(x1, y1, x2, y2, tx2, ty2, tx3, ty3)
				|| linesIntersect(x2, y2, x3, y3, tx2, ty2, tx3, ty3)
				|| linesIntersect(x1, y1, x3, y3, tx2, ty2, tx3, ty3)
				|| linesIntersect(x1, y1, x2, y2, tx1, ty1, tx3, ty3)
				|| linesIntersect(x2, y2, x3, y3, tx1, ty1, tx3, ty3)
				|| linesIntersect(x1, y1, x3, y3, tx1, ty1, tx3, ty3)
				|| triangleContains(x1, y1, x2, y2, x3, y3, tx1, ty1)
				|| triangleContains(x1, y1, x2, y2, x3, y3, tx2, ty2)
				|| triangleContains(x1, y1, x2, y2, x3, y3, tx3, ty3)
				|| triangleContains(tx1, ty1, tx2, ty2, tx3, ty3, x1, y1)
				|| triangleContains(tx1, ty1, tx2, ty2, tx3, ty3, x2, y2)
				|| triangleContains(tx1, ty1, tx2, ty2, tx3, ty3, x3, y3));
	}

}

package org.loon.framework.android.game.core.geom;

/**
 * Copyright 2008 - 2010
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class Triangle {

	public float[] xpoints;

	public float[] ypoints;

	public Triangle(float x1, float y1, float x2, float y2, float x3, float y3) {
		this();
		float dx1 = x2 - x1;
		float dx2 = x3 - x1;
		float dy1 = y2 - y1;
		float dy2 = y3 - y1;
		float cross = dx1 * dy2 - dx2 * dy1;
		boolean ccw = (cross > 0);
		if (ccw) {
			xpoints[0] = x1;
			xpoints[1] = x2;
			xpoints[2] = x3;
			ypoints[0] = y1;
			ypoints[1] = y2;
			ypoints[2] = y3;
		} else {
			xpoints[0] = x1;
			xpoints[1] = x3;
			xpoints[2] = x2;
			ypoints[0] = y1;
			ypoints[1] = y3;
			ypoints[2] = y2;
		}
	}

	public Triangle() {
		xpoints = new float[3];
		ypoints = new float[3];
	}

	public float[] getVertexs() {
		int vertice_size = xpoints.length * 2;
		float[] verts = new float[vertice_size];
		for (int i = 0, j = 0; i < vertice_size; i += 2, j++) {
			verts[i] = xpoints[j];
			verts[i + 1] = ypoints[j];
		}
		return verts;
	}

	public void set(int w, int h) {
		set(w / 2 - 1, h / 2 - 1, w - 1, h - 1);
	}

	public void set(int x, int y, int w, int h) {
		int halfWidth = w / 2;
		int halfHeight = h / 2;
		float top = -halfWidth;
		float bottom = halfHeight;
		float left = -halfHeight;
		float center = 0;
		float right = halfWidth;

		xpoints[0] = x + center;
		xpoints[1] = x + right;
		xpoints[2] = x + left;
		ypoints[0] = y + top;
		ypoints[1] = y + bottom;
		ypoints[2] = y + bottom;
	}

	public void set(Triangle t) {
		xpoints[0] = t.xpoints[0];
		xpoints[1] = t.xpoints[1];
		xpoints[2] = t.xpoints[2];
		ypoints[0] = t.ypoints[0];
		ypoints[1] = t.ypoints[1];
		ypoints[2] = t.ypoints[2];
	}

	public boolean isInside(float nx, float ny) {
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u > 0) && (v > 0) && (u + v < 1));
	}

	public boolean containsPoint(float nx, float ny) {
		float vx2 = nx - xpoints[0];
		float vy2 = ny - ypoints[0];
		float vx1 = xpoints[1] - xpoints[0];
		float vy1 = ypoints[1] - ypoints[0];
		float vx0 = xpoints[2] - xpoints[0];
		float vy0 = ypoints[2] - ypoints[0];

		float dot00 = vx0 * vx0 + vy0 * vy0;
		float dot01 = vx0 * vx1 + vy0 * vy1;
		float dot02 = vx0 * vx2 + vy0 * vy2;
		float dot11 = vx1 * vx1 + vy1 * vy1;
		float dot12 = vx1 * vx2 + vy1 * vy2;
		float invDenom = 1.0f / (dot00 * dot11 - dot01 * dot01);
		float u = (dot11 * dot02 - dot01 * dot12) * invDenom;
		float v = (dot00 * dot12 - dot01 * dot02) * invDenom;

		return ((u >= 0) && (v >= 0) && (u + v <= 1));
	}

}

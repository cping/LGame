/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.core.graphics.opengl;

import loon.core.graphics.LColor;
import loon.utils.MathUtils;

public class GLRenderer {

	private GLBatch _renderer;

	private LColor _color = new LColor(1f, 1f, 1f, 1f);

	private GLType _currType = null;

	public GLRenderer() {
		this(5000);
	}

	public GLRenderer(int maxVertices) {
		_renderer = new GLBatch(maxVertices);
	}

	public void begin(GLType type) {
		if (_currType != null) {
			throw new RuntimeException(
					"Call end() before beginning a new shape batch !");
		}
		_currType = type;
		_renderer.begin(_currType.glType);
	}

	public void setColor(LColor color) {
		this._color.setColor(color);
	}

	public void setColor(float r, float g, float b, float a) {
		this._color.setColor(r, g, b, a);
	}

	public void point(float x, float y) {
		point(x, y, 1);
	}

	public void point(float x, float y, float z) {
		if (_currType != GLType.Point) {
			throw new RuntimeException("Must call begin(GLType.Point)");
		}
		checkDirty();
		checkFlush(1);
		_renderer.color(_color);
		_renderer.vertex(x, y, z);
	}

	public void line(float x, float y, float z, float x2, float y2, float z2) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(2);
		_renderer.color(_color);
		_renderer.vertex(x, y, z);
		_renderer.color(_color);
		_renderer.vertex(x2, y2, z2);
	}

	public void line(float x, float y, float x2, float y2) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(2);
		_renderer.color(_color);
		_renderer.vertex(x, y, 0);
		_renderer.color(_color);
		_renderer.vertex(x2, y2, 0);
	}

	public void curve(float x1, float y1, float cx1, float cy1, float cx2,
			float cy2, float x2, float y2, int segments) {
		if (_currType != GLType.Line) {
			throw new RuntimeException("Must call begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(segments * 2 + 2);
		float subdiv_step = 1f / segments;
		float subdiv_step2 = subdiv_step * subdiv_step;
		float subdiv_step3 = subdiv_step * subdiv_step * subdiv_step;

		float pre1 = 3 * subdiv_step;
		float pre2 = 3 * subdiv_step2;
		float pre4 = 6 * subdiv_step2;
		float pre5 = 6 * subdiv_step3;

		float tmp1x = x1 - cx1 * 2 + cx2;
		float tmp1y = y1 - cy1 * 2 + cy2;

		float tmp2x = (cx1 - cx2) * 3 - x1 + x2;
		float tmp2y = (cy1 - cy2) * 3 - y1 + y2;

		float fx = x1;
		float fy = y1;

		float dfx = (cx1 - x1) * pre1 + tmp1x * pre2 + tmp2x * subdiv_step3;
		float dfy = (cy1 - y1) * pre1 + tmp1y * pre2 + tmp2y * subdiv_step3;

		float ddfx = tmp1x * pre4 + tmp2x * pre5;
		float ddfy = tmp1y * pre4 + tmp2y * pre5;

		float dddfx = tmp2x * pre5;
		float dddfy = tmp2y * pre5;

		for (; segments-- > 0;) {
			_renderer.color(_color);
			_renderer.vertex(fx, fy, 0);
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			_renderer.color(_color);
			_renderer.vertex(fx, fy, 0);
		}
		_renderer.color(_color);
		_renderer.vertex(fx, fy, 0);
		_renderer.color(_color);
		_renderer.vertex(x2, y2, 0);
	}

	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(6);
		if (_currType == GLType.Line) {
			_renderer.color(_color);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(_color);
			_renderer.vertex(x2, y2, 0);

			_renderer.color(_color);
			_renderer.vertex(x2, y2, 0);
			_renderer.color(_color);
			_renderer.vertex(x3, y3, 0);

			_renderer.color(_color);
			_renderer.vertex(x3, y3, 0);
			_renderer.color(_color);
			_renderer.vertex(x1, y1, 0);
		} else {
			_renderer.color(_color);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(_color);
			_renderer.vertex(x2, y2, 0);
			_renderer.color(_color);
			_renderer.vertex(x3, y3, 0);
		}
	}

	public void rect(float x, float y, float width, float height) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}

		checkDirty();
		checkFlush(8);

		if (_currType == GLType.Line) {
			_renderer.color(_color);
			_renderer.vertex(x, y, 0);
			_renderer.color(_color);
			_renderer.vertex(x + width, y, 0);

			_renderer.color(_color);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(_color);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(_color);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(_color);
			_renderer.vertex(x, y + height, 0);

			_renderer.color(_color);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(_color);
			_renderer.vertex(x, y, 0);
		} else {
			_renderer.color(_color);
			_renderer.vertex(x, y, 0);
			_renderer.color(_color);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(_color);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(_color);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(_color);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(_color);
			_renderer.vertex(x, y, 0);
		}
	}

	public void rect(float x, float y, float width, float height, LColor col1,
			LColor col2, LColor col3, LColor col4) {
		if (_currType != GLType.Filled && _currType != GLType.Line) {
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		}
		checkDirty();
		checkFlush(8);

		if (_currType == GLType.Line) {
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);

			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);

			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
		} else {
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
			_renderer.color(col2.r, col2.g, col2.b, col2.a);
			_renderer.vertex(x + width, y, 0);
			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);

			_renderer.color(col3.r, col3.g, col3.b, col3.a);
			_renderer.vertex(x + width, y + height, 0);
			_renderer.color(col4.r, col4.g, col4.b, col4.a);
			_renderer.vertex(x, y + height, 0);
			_renderer.color(col1.r, col1.g, col1.b, col1.a);
			_renderer.vertex(x, y, 0);
		}
	}

	public void oval(float x, float y, float radius) {
		oval(x, y, radius, (int) (6 * (float) Math.cbrt(radius)));
	}

	public void oval(float x, float y, float radius, int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be >= 0.");
		if (_currType != GLType.Filled && _currType != GLType.Line)
			throw new RuntimeException(
					"Must call begin(GLType.Filled) or begin(GLType.Line)");
		checkDirty();
		checkFlush(segments * 2 + 2);
		float angle = 2 * 3.1415926f / segments;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = radius, cy = 0;
		if (_currType == GLType.Line) {
			for (int i = 0; i < segments; i++) {
				_renderer.color(_color);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(_color);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(_color);
			_renderer.vertex(x + cx, y + cy, 0);
		} else {
			segments--;
			for (int i = 0; i < segments; i++) {
				_renderer.color(_color);
				_renderer.vertex(x, y, 0);
				_renderer.color(_color);
				_renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				_renderer.color(_color);
				_renderer.vertex(x + cx, y + cy, 0);
			}
			_renderer.color(_color);
			_renderer.vertex(x, y, 0);
			_renderer.color(_color);
			_renderer.vertex(x + cx, y + cy, 0);
		}
		cx = radius;
		cy = 0;
		_renderer.color(_color);
		_renderer.vertex(x + cx, y + cy, 0);
	}

	public void polygon(float[] vertices) {
		if (_currType != GLType.Line)
			throw new RuntimeException("Must call begin(GLType.Line)");
		if (vertices.length < 6)
			throw new IllegalArgumentException(
					"Polygons must contain at least 3 points.");
		if (vertices.length % 2 != 0)
			throw new IllegalArgumentException(
					"Polygons must have a pair number of vertices.");
		final int numFloats = vertices.length;

		checkDirty();
		checkFlush(numFloats);

		float firstX = vertices[0];
		float firstY = vertices[1];

		for (int i = 0; i < numFloats; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			if (i + 2 >= numFloats) {
				x2 = firstX;
				y2 = firstY;
			} else {
				x2 = vertices[i + 2];
				y2 = vertices[i + 3];
			}

			_renderer.color(_color);
			_renderer.vertex(x1, y1, 0);
			_renderer.color(_color);
			_renderer.vertex(x2, y2, 0);
		}
	}

	private void checkDirty() {
		GLType type = _currType;
		end();
		begin(type);
	}

	private void checkFlush(int newVertices) {
		if (_renderer.getMaxVertices() - _renderer.getNumVertices() >= newVertices) {
			return;
		}
		GLType type = _currType;
		end();
		begin(type);
	}

	public void end() {
		if (_renderer != null) {
			_renderer.end();
			_currType = null;
		}
	}

	public void flush() {
		GLType type = _currType;
		end();
		begin(type);
	}

	public GLType getCurrentType() {
		return _currType;
	}

	public void dispose() {
		if (_renderer != null) {
			_renderer.dispose();
		}
	}

	public final static class GLType {
		
		public static final GLType Point = new GLType(GL10.GL_POINTS);
		
		public static final GLType Line = new GLType(GL10.GL_LINES);
		
		public static final GLType Filled = new GLType(GL10.GL_TRIANGLES);
		
		final int glType;

		GLType(int glType) {
			this.glType = glType;
		}

	}

}

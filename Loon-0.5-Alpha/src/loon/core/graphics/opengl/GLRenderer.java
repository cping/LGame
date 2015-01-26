package loon.core.graphics.opengl;

import loon.core.LRelease;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.math.Transform4;
import loon.core.graphics.opengl.math.Location2;
import loon.core.graphics.opengl.math.Location3;
import loon.jni.NativeSupport;
import loon.utils.MathUtils;

public class GLRenderer implements LRelease {

	public enum ShapeType {
		Point(GL20.GL_POINTS), Line(GL20.GL_LINES), Filled(GL20.GL_TRIANGLES);

		private final int glType;

		ShapeType(int glType) {
			this.glType = glType;
		}

		public int getGlType() {
			return glType;
		}
	}

	private final GLBatch renderer;
	private boolean matrixDirty = false;
	private final Transform4 projectionMatrix = new Transform4();
	private final Transform4 transformMatrix = new Transform4();
	private final Transform4 combinedMatrix = new Transform4();
	private final Location2 tmp = new Location2();
	private final LColor color = new LColor(1, 1, 1, 1);
	private ShapeType shapeType;
	private boolean autoShapeType;
	private float defaultRectLineWidth = 0.75f;

	public GLRenderer() {
		this(5000);
	}

	public GLRenderer(int maxVertices) {
		renderer = new GLBatch(maxVertices, false, true, 0);
		projectionMatrix.setToOrtho2D(0, 0, GLEx.width(),
				GLEx.height());
		matrixDirty = true;
	}

	public void setColor(LColor color) {
		this.color.setColor(color);
	}

	public void setColor(float r, float g, float b, float a) {
		this.color.setColor(r, g, b, a);
	}

	public LColor getColor() {
		return color;
	}

	public void updateMatrices() {
		matrixDirty = true;
	}

	public void setProjectionMatrix(Transform4 matrix) {
		projectionMatrix.set(matrix);
		matrixDirty = true;
	}

	public Transform4 getProjectionMatrix() {
		return projectionMatrix;
	}

	public void setTransformMatrix(Transform4 matrix) {
		transformMatrix.set(matrix);
		matrixDirty = true;
	}

	public Transform4 getTransformMatrix() {
		return transformMatrix;
	}

	public void identity() {
		transformMatrix.idt();
		matrixDirty = true;
	}

	public void translate(float x, float y, float z) {
		transformMatrix.translate(x, y, z);
		matrixDirty = true;
	}

	public void rotate(float axisX, float axisY, float axisZ, float degrees) {
		transformMatrix.rotate(axisX, axisY, axisZ, degrees);
		matrixDirty = true;
	}

	public void scale(float scaleX, float scaleY, float scaleZ) {
		transformMatrix.scale(scaleX, scaleY, scaleZ);
		matrixDirty = true;
	}

	public void setAutoShapeType(boolean autoShapeType) {
		this.autoShapeType = autoShapeType;
	}

	public void begin() {
		if (!autoShapeType){
			throw new IllegalStateException(
					"autoShapeType must be true to use this method.");
		}
		begin(ShapeType.Line);
	}

	public void begin(ShapeType type) {
		if (shapeType != null)
			throw new IllegalStateException(
					"Call end() before beginning a new shape batch.");
		shapeType = type;
		if (matrixDirty) {
			combinedMatrix.set(projectionMatrix);
			NativeSupport.mul(combinedMatrix.val, transformMatrix.val);
			matrixDirty = false;
		}
		renderer.begin(combinedMatrix, shapeType.getGlType());
	}

	public void set(ShapeType type) {
		if (shapeType == type)
			return;
		if (shapeType == null)
			throw new IllegalStateException("begin must be called first.");
		if (!autoShapeType)
			throw new IllegalStateException("autoShapeType must be enabled.");
		end();
		begin(type);
	}

	public void point(float x, float y, float z) {
		if (shapeType == ShapeType.Line) {
			float size = defaultRectLineWidth * 0.5f;
			line(x - size, y - size, z, x + size, y + size, z);
			return;
		} else if (shapeType == ShapeType.Filled) {
			float size = defaultRectLineWidth * 0.5f;
			box(x - size, y - size, z - size, defaultRectLineWidth,
					defaultRectLineWidth, defaultRectLineWidth);
			return;
		}
		check(ShapeType.Point, null, 1);
		renderer.color(color);
		renderer.vertex(x, y, z);
	}

	public final void line(float x, float y, float z, float x2, float y2,
			float z2) {
		line(x, y, z, x2, y2, z2, color, color);
	}

	public final void line(Location3 v0, Location3 v1) {
		line(v0.x, v0.y, v0.z, v1.x, v1.y, v1.z, color, color);
	}

	public final void line(float x, float y, float x2, float y2) {
		line(x, y, 0.0f, x2, y2, 0.0f, color, color);
	}

	public final void line(Location2 v0, Location2 v1) {
		line(v0.x, v0.y, 0.0f, v1.x, v1.y, 0.0f, color, color);
	}

	public final void line(float x, float y, float x2, float y2, LColor c1,
			LColor c2) {
		line(x, y, 0.0f, x2, y2, 0.0f, c1, c2);
	}

	public void line(float x, float y, float z, float x2, float y2, float z2,
			LColor c1, LColor c2) {
		if (shapeType == ShapeType.Filled) {
			rectLine(x, y, x2, y2, defaultRectLineWidth);
			return;
		}
		check(ShapeType.Line, null, 2);
		renderer.color(c1.r, c1.g, c1.b, c1.a);
		renderer.vertex(x, y, z);
		renderer.color(c2.r, c2.g, c2.b, c2.a);
		renderer.vertex(x2, y2, z2);
	}

	public void curve(float x1, float y1, float cx1, float cy1, float cx2,
			float cy2, float x2, float y2, int segments) {
		check(ShapeType.Line, null, segments * 2 + 2);
		
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

		while (segments-- > 0) {
			renderer.color(color);
			renderer.vertex(fx, fy, 0);
			fx += dfx;
			fy += dfy;
			dfx += ddfx;
			dfy += ddfy;
			ddfx += dddfx;
			ddfy += dddfy;
			renderer.color(color);
			renderer.vertex(fx, fy, 0);
		}
		renderer.color(color);
		renderer.vertex(fx, fy, 0);
		renderer.color(color);
		renderer.vertex(x2, y2, 0);
	}

	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3) {
		check(ShapeType.Line, ShapeType.Filled, 6);
		if (shapeType == ShapeType.Line) {
			renderer.color(color);
			renderer.vertex(x1, y1, 0);
			renderer.color(color);
			renderer.vertex(x2, y2, 0);

			renderer.color(color);
			renderer.vertex(x2, y2, 0);
			renderer.color(color);
			renderer.vertex(x3, y3, 0);

			renderer.color(color);
			renderer.vertex(x3, y3, 0);
			renderer.color(color);
			renderer.vertex(x1, y1, 0);
		} else {
			renderer.color(color);
			renderer.vertex(x1, y1, 0);
			renderer.color(color);
			renderer.vertex(x2, y2, 0);
			renderer.color(color);
			renderer.vertex(x3, y3, 0);
		}
	}

	public void triangle(float x1, float y1, float x2, float y2, float x3,
			float y3, LColor col1, LColor col2, LColor col3) {
		check(ShapeType.Line, ShapeType.Filled, 6);
		if (shapeType == ShapeType.Line) {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);

			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);

			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
		} else {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);
		}
	}

	public void rect(float x, float y, float width, float height) {
		check(ShapeType.Line, ShapeType.Filled, 8);

		if (shapeType == ShapeType.Line) {
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + width, y, 0);

			renderer.color(color);
			renderer.vertex(x + width, y, 0);
			renderer.color(color);
			renderer.vertex(x + width, y + height, 0);

			renderer.color(color);
			renderer.vertex(x + width, y + height, 0);
			renderer.color(color);
			renderer.vertex(x, y + height, 0);

			renderer.color(color);
			renderer.vertex(x, y + height, 0);
			renderer.color(color);
			renderer.vertex(x, y, 0);
		} else {
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + width, y, 0);
			renderer.color(color);
			renderer.vertex(x + width, y + height, 0);

			renderer.color(color);
			renderer.vertex(x + width, y + height, 0);
			renderer.color(color);
			renderer.vertex(x, y + height, 0);
			renderer.color(color);
			renderer.vertex(x, y, 0);
		}
	}

	public void rect(float x, float y, float width, float height, LColor col1,
			LColor col2, LColor col3, LColor col4) {
		check(ShapeType.Line, ShapeType.Filled, 8);

		if (shapeType == ShapeType.Line) {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x, y, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x + width, y, 0);

			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x + width, y, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x + width, y + height, 0);

			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x + width, y + height, 0);
			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x, y + height, 0);

			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x, y + height, 0);
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x, y, 0);
		} else {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x, y, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x + width, y, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x + width, y + height, 0);

			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x + width, y + height, 0);
			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x, y + height, 0);
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x, y, 0);
		}
	}

	public void rect(float x, float y, float originX, float originY,
			float width, float height, float scaleX, float scaleY, float degrees) {
		rect(x, y, originX, originY, width, height, scaleX, scaleY, degrees,
				color, color, color, color);
	}

	public void rect(float x, float y, float originX, float originY,
			float width, float height, float scaleX, float scaleY,
			float degrees, LColor col1, LColor col2, LColor col3, LColor col4) {
		check(ShapeType.Line, ShapeType.Filled, 8);

		float cos = MathUtils.cosDeg(degrees);
		float sin = MathUtils.sinDeg(degrees);
		float fx = -originX;
		float fy = -originY;
		float fx2 = width - originX;
		float fy2 = height - originY;

		if (scaleX != 1 || scaleY != 1) {
			fx *= scaleX;
			fy *= scaleY;
			fx2 *= scaleX;
			fy2 *= scaleY;
		}

		float worldOriginX = x + originX;
		float worldOriginY = y + originY;

		float x1 = cos * fx - sin * fy + worldOriginX;
		float y1 = sin * fx + cos * fy + worldOriginY;

		float x2 = cos * fx2 - sin * fy + worldOriginX;
		float y2 = sin * fx2 + cos * fy + worldOriginY;

		float x3 = cos * fx2 - sin * fy2 + worldOriginX;
		float y3 = sin * fx2 + cos * fy2 + worldOriginY;

		float x4 = x1 + (x3 - x2);
		float y4 = y3 - (y2 - y1);

		if (shapeType == ShapeType.Line) {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);

			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);

			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);
			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x4, y4, 0);

			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x4, y4, 0);
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
		} else {
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
			renderer.color(col2.r, col2.g, col2.b, col2.a);
			renderer.vertex(x2, y2, 0);
			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);

			renderer.color(col3.r, col3.g, col3.b, col3.a);
			renderer.vertex(x3, y3, 0);
			renderer.color(col4.r, col4.g, col4.b, col4.a);
			renderer.vertex(x4, y4, 0);
			renderer.color(col1.r, col1.g, col1.b, col1.a);
			renderer.vertex(x1, y1, 0);
		}

	}

	public void rectLine(float x1, float y1, float x2, float y2, float width) {
		check(ShapeType.Line, ShapeType.Filled, 8);

		Location2 t = tmp.set(y2 - y1, x1 - x2).nor();
		width *= 0.5f;
		float tx = t.x * width;
		float ty = t.y * width;
		if (shapeType == ShapeType.Line) {
			renderer.color(color);
			renderer.vertex(x1 + tx, y1 + ty, 0);
			renderer.color(color);
			renderer.vertex(x1 - tx, y1 - ty, 0);

			renderer.color(color);
			renderer.vertex(x2 + tx, y2 + ty, 0);
			renderer.color(color);
			renderer.vertex(x2 - tx, y2 - ty, 0);

			renderer.color(color);
			renderer.vertex(x2 + tx, y2 + ty, 0);
			renderer.color(color);
			renderer.vertex(x1 + tx, y1 + ty, 0);

			renderer.color(color);
			renderer.vertex(x2 - tx, y2 - ty, 0);
			renderer.color(color);
			renderer.vertex(x1 - tx, y1 - ty, 0);
		} else {
			renderer.color(color);
			renderer.vertex(x1 + tx, y1 + ty, 0);
			renderer.color(color);
			renderer.vertex(x1 - tx, y1 - ty, 0);
			renderer.color(color);
			renderer.vertex(x2 + tx, y2 + ty, 0);

			renderer.color(color);
			renderer.vertex(x2 - tx, y2 - ty, 0);
			renderer.color(color);
			renderer.vertex(x2 + tx, y2 + ty, 0);
			renderer.color(color);
			renderer.vertex(x1 - tx, y1 - ty, 0);
		}
	}

	public void rectLine(Location2 p1, Location2 p2, float width) {
		rectLine(p1.x, p1.y, p2.x, p2.y, width);
	}

	public void box(float x, float y, float z, float width, float height,
			float depth) {
		depth = -depth;

		if (shapeType == ShapeType.Line) {
			check(ShapeType.Line, ShapeType.Filled, 24);

			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y, z);

			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);

			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y, z + depth);

			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y, z);

			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x, y + height, z);

			renderer.color(color);
			renderer.vertex(x, y + height, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);

			renderer.color(color);
			renderer.vertex(x + width, y + height, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);
			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);
			renderer.color(color);
			renderer.vertex(x, y + height, z);

			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);

			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);
		} else {
			check(ShapeType.Line, ShapeType.Filled, 36);

			// Front
			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);

			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);
			renderer.color(color);
			renderer.vertex(x, y + height, z);

			// Back
			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);
			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			// Left
			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x, y + height, z);

			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x, y + height, z);
			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);

			// Right
			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);

			// Top
			renderer.color(color);
			renderer.vertex(x, y + height, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);

			renderer.color(color);
			renderer.vertex(x, y + height, z);
			renderer.color(color);
			renderer.vertex(x + width, y + height, z + depth);
			renderer.color(color);
			renderer.vertex(x, y + height, z + depth);

			// Bottom
			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y, z);

			renderer.color(color);
			renderer.vertex(x, y, z + depth);
			renderer.color(color);
			renderer.vertex(x + width, y, z);
			renderer.color(color);
			renderer.vertex(x, y, z);
		}

	}

	public void x(float x, float y, float size) {
		line(x - size, y - size, x + size, y + size);
		line(x - size, y + size, x + size, y - size);
	}

	public void x(Location2 p, float size) {
		x(p.x, p.y, size);
	}

	public void arc(float x, float y, float radius, float start, float degrees) {
		arc(x, y, radius, start, degrees, Math.max(1,
				(int) (6 * (float) Math.cbrt(radius) * (degrees / 360.0f))));
	}

	public void arc(float x, float y, float radius, float start, float degrees,
			int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be > 0.");

		float theta = (2 * MathUtils.PI * (degrees / 360.0f)) / segments;
		float cos = MathUtils.cos(theta);
		float sin = MathUtils.sin(theta);
		float cx = radius * MathUtils.cos(start * MathUtils.DEG_TO_RAD);
		float cy = radius * MathUtils.sin(start * MathUtils.DEG_TO_RAD);

		if (shapeType == ShapeType.Line) {
			check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);

			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		} else {
			check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);

			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x, y, 0);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		}

		cx = 0;
		cy = 0;
		renderer.color(color);
		renderer.vertex(x + cx, y + cy, 0);
	}

	public void circle(float x, float y, float radius) {
		circle(x, y, radius, Math.max(1, (int) (6 * (float) Math.cbrt(radius))));
	}

	public void circle(float x, float y, float radius, int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be > 0.");

		float angle = 2 * MathUtils.PI / segments;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = radius, cy = 0;
		if (shapeType == ShapeType.Line) {
			check(ShapeType.Line, ShapeType.Filled, segments * 2 + 2);
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		} else {
			check(ShapeType.Line, ShapeType.Filled, segments * 3 + 3);
			segments--;
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x, y, 0);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, 0);
			}
			renderer.color(color);
			renderer.vertex(x, y, 0);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, 0);
		}
		cx = radius;
		cy = 0;
		renderer.color(color);
		renderer.vertex(x + cx, y + cy, 0);
	}

	public void ellipse(float x, float y, float width, float height) {
		ellipse(x, y, width, height, Math.max(1, (int) (12 * (float) Math
				.cbrt(Math.max(width * 0.5f, height * 0.5f)))));
	}

	public void ellipse(float x, float y, float width, float height,
			int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be > 0.");
		check(ShapeType.Line, ShapeType.Filled, segments * 3);

		float angle = 2 * MathUtils.PI / segments;

		float cx = x + width / 2, cy = y + height / 2;
		if (shapeType == ShapeType.Line) {
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(cx + (width * 0.5f * MathUtils.cos(i * angle)),
						cy + (height * 0.5f * MathUtils.sin(i * angle)), 0);

				renderer.color(color);
				renderer.vertex(
						cx + (width * 0.5f * MathUtils.cos((i + 1) * angle)),
						cy + (height * 0.5f * MathUtils.sin((i + 1) * angle)),
						0);
			}
		} else {
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(cx + (width * 0.5f * MathUtils.cos(i * angle)),
						cy + (height * 0.5f * MathUtils.sin(i * angle)), 0);

				renderer.color(color);
				renderer.vertex(cx, cy, 0);

				renderer.color(color);
				renderer.vertex(
						cx + (width * 0.5f * MathUtils.cos((i + 1) * angle)),
						cy + (height * 0.5f * MathUtils.sin((i + 1) * angle)),
						0);
			}
		}
	}

	public void cone(float x, float y, float z, float radius, float height) {
		cone(x, y, z, radius, height,
				Math.max(1, (int) (4 * (float) Math.sqrt(radius))));
	}

	public void cone(float x, float y, float z, float radius, float height,
			int segments) {
		if (segments <= 0)
			throw new IllegalArgumentException("segments must be > 0.");
		check(ShapeType.Line, ShapeType.Filled, segments * 4 + 2);

		float angle = 2 * MathUtils.PI / segments;
		float cos = MathUtils.cos(angle);
		float sin = MathUtils.sin(angle);
		float cx = radius, cy = 0;
		if (shapeType == ShapeType.Line) {
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);
				renderer.color(color);
				renderer.vertex(x, y, z + height);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);
			}
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, z);
		} else {
			segments--;
			for (int i = 0; i < segments; i++) {
				renderer.color(color);
				renderer.vertex(x, y, z);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);
				float temp = cx;
				float temp2 = cy;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);

				renderer.color(color);
				renderer.vertex(x + temp, y + temp2, z);
				renderer.color(color);
				renderer.vertex(x + cx, y + cy, z);
				renderer.color(color);
				renderer.vertex(x, y, z + height);
			}
			renderer.color(color);
			renderer.vertex(x, y, z);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, z);
		}
		float temp = cx;
		float temp2 = cy;
		cx = radius;
		cy = 0;
		renderer.color(color);
		renderer.vertex(x + cx, y + cy, z);
		if (shapeType != ShapeType.Line) {
			renderer.color(color);
			renderer.vertex(x + temp, y + temp2, z);
			renderer.color(color);
			renderer.vertex(x + cx, y + cy, z);
			renderer.color(color);
			renderer.vertex(x, y, z + height);
		}
	}

	public void polygon(float[] vertices, int offset, int count) {
		if (count < 6)
			throw new IllegalArgumentException(
					"Polygons must contain at least 3 points.");
		if (count % 2 != 0)
			throw new IllegalArgumentException(
					"Polygons must have an even number of vertices.");

		check(ShapeType.Line, null, count);

		float firstX = vertices[0];
		float firstY = vertices[1];

		for (int i = offset, n = offset + count; i < n; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			if (i + 2 >= count) {
				x2 = firstX;
				y2 = firstY;
			} else {
				x2 = vertices[i + 2];
				y2 = vertices[i + 3];
			}

			renderer.color(color);
			renderer.vertex(x1, y1, 0);
			renderer.color(color);
			renderer.vertex(x2, y2, 0);
		}
	}

	public void polygon(float[] vertices) {
		polygon(vertices, 0, vertices.length);
	}

	public void polyline(float[] vertices, int offset, int count) {
		if (count < 4)
			throw new IllegalArgumentException(
					"Polylines must contain at least 2 points.");
		if (count % 2 != 0)
			throw new IllegalArgumentException(
					"Polylines must have an even number of vertices.");

		check(ShapeType.Line, null, count);

		for (int i = offset, n = offset + count - 2; i < n; i += 2) {
			float x1 = vertices[i];
			float y1 = vertices[i + 1];

			float x2;
			float y2;

			x2 = vertices[i + 2];
			y2 = vertices[i + 3];

			renderer.color(color);
			renderer.vertex(x1, y1, 0);
			renderer.color(color);
			renderer.vertex(x2, y2, 0);
		}
	}

	public void polyline(float[] vertices) {
		polyline(vertices, 0, vertices.length);
	}

	private void check(ShapeType preferred, ShapeType other, int newVertices) {
		if (shapeType == null){
			throw new IllegalStateException("begin must be called first.");
		}

		if (shapeType != preferred && shapeType != other) {
			if (!autoShapeType) {
				if (other == null)
					throw new IllegalStateException(
							"Must call begin(ShapeType." + preferred + ").");
				else
					throw new IllegalStateException(
							"Must call begin(ShapeType." + preferred
									+ ") or begin(ShapeType." + other + ").");
			}
			end();
			begin(preferred);
		} else if (matrixDirty) {
			ShapeType type = shapeType;
			end();
			begin(type);
		} else if (renderer.getMaxVertices() - renderer.getNumVertices() < newVertices) {
			ShapeType type = shapeType;
			end();
			begin(type);
		}
	}

	public void end() {
		renderer.end();
		shapeType = null;
	}

	public void flush() {
		ShapeType type = shapeType;
		end();
		begin(type);
	}

	public ShapeType getCurrentType() {
		return shapeType;
	}

	public GLBatch getRenderer() {
		return renderer;
	}

	public boolean isDrawing() {
		return shapeType != null;
	}

	public void dispose() {
		renderer.dispose();
	}
}

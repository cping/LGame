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

import loon.LRelease;
import loon.LSysException;
import loon.LSystem;
import loon.action.ActionBind;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 一个最基础的矩形碰撞器
 */
public class AABB implements XY, XYZW, BoxSize, LRelease {

	public static void createfromPoints(Vector3f[] points, AABB o) {
		if (points == null) {
			throw new LSysException("points is null !");
		}
		Vector3f min = o.min();
		Vector3f max = o.max();
		min.x = Float.MAX_VALUE;
		min.y = Float.MAX_VALUE;
		min.z = Float.MAX_VALUE;
		max.x = -Float.MAX_VALUE;
		max.y = -Float.MAX_VALUE;
		max.z = -Float.MAX_VALUE;
		for (int i = 0, n = points.length; i < n; ++i) {
			Vector3f.min(min, points[i], min);
			Vector3f.max(max, points[i], max);
		}
	}

	public final static void checkMinMax(Vector3f min, Vector3f max, Vector3f point) {
		if (point.x < min.x) {
			min.x = point.x;
		}
		if (point.x > max.x) {
			max.x = point.x;
		}
		if (point.y < min.y) {
			min.y = point.y;
		}
		if (point.y > max.y) {
			max.y = point.y;
		}
		if (point.z < min.z) {
			min.z = point.z;
		}
		if (point.z > max.z) {
			max.z = point.z;
		}
	}

	public final static AABB at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new AABB();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 3) {
			if (len == 4) {
				try {
					float x = Float.parseFloat(result[0].trim());
					float y = Float.parseFloat(result[1].trim());
					float width = Float.parseFloat(result[2].trim());
					float height = Float.parseFloat(result[3].trim());
					return new AABB(x, y, width, height);
				} catch (Exception ex) {
				}
			} else if (len == 6) {
				try {
					float minx = Float.parseFloat(result[0].trim());
					float miny = Float.parseFloat(result[1].trim());
					float minz = Float.parseFloat(result[2].trim());
					float maxx = Float.parseFloat(result[3].trim());
					float maxy = Float.parseFloat(result[4].trim());
					float maxz = Float.parseFloat(result[5].trim());
					return new AABB(minx, miny, minz, maxx, maxy, maxz);
				} catch (Exception ex) {
				}
			}
		}
		return new AABB();
	}

	public final static AABB at(int x, int y, int w, int h) {
		return new AABB(x, y, w, h);
	}

	public final static AABB at(float x, float y, float w, float h) {
		return new AABB(x, y, w, h);
	}

	public final static AABB fromActor(ActionBind bind) {
		return new AABB(bind.getX(), bind.getY(), bind.getWidth(), bind.getHeight());
	}

	private final Vector3f _min = new Vector3f();

	private final Vector3f _max = new Vector3f();

	public float minX, minY, minZ;

	public float maxX, maxY, maxZ;

	public AABB() {
		this(0f, 0f, 0f, 0f);
	}

	public AABB(float radius) {
		this(null, radius);
	}

	public AABB(XYZ center, float radius) {
		radius = MathUtils.max(0f, radius);
		if (center == null) {
			this.minX = -radius;
			this.minY = -radius;
			this.minZ = -radius;
			this.maxX = radius;
			this.maxY = radius;
			this.maxZ = radius;
		} else {
			this.minX = center.getX() - radius;
			this.minY = center.getY() - radius;
			this.minZ = center.getZ() - radius;
			this.maxX = center.getX() + radius;
			this.maxY = center.getY() + radius;
			this.maxZ = center.getZ() + radius;
		}
	}

	public AABB(AABB aabb) {
		this(aabb.minX, aabb.minY, aabb.maxX, aabb.maxY);
	}

	public AABB(float minX, float minY, float maxX, float maxY) {
		this(minX, minY, 0f, maxX, maxY, 0f);
	}

	public AABB(XY min, XY max) {
		this(min.getX(), min.getY(), max.getX(), max.getY());
	}

	public AABB(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;
		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	public AABB setCentered(float x, float y, float size) {
		return set(x - size / 2f, y - size / 2f, size, size);
	}

	public AABB setCentered(float x, float y, float width, float height) {
		return set(x - width / 2f, y - height / 2f, width, height);
	}

	public AABB setTopLeft(AABB other) {
		if (other == null) {
			return this;
		}
		this.minX = other.minX;
		this.maxX = (other.minX + other.maxX) / 2f;
		this.minY = other.minY;
		this.maxY = (other.minY + other.maxY) / 2f;
		return this;
	}

	public AABB setTopRight(AABB other) {
		if (other == null) {
			return this;
		}
		this.minX = (other.minX + other.maxX) / 2f;
		this.maxX = other.maxX;
		this.minY = other.minY;
		this.maxY = (other.minY + other.maxY) / 2f;
		return this;
	}

	public AABB setBottomLeft(AABB other) {
		if (other == null) {
			return this;
		}
		this.minX = other.minX;
		this.maxX = (other.minX + other.maxX) / 2f;
		this.minY = (other.minY + other.maxY) / 2f;
		this.maxY = other.maxY;
		return this;
	}

	public AABB setBottomRight(AABB other) {
		if (other == null) {
			return this;
		}
		this.minX = (other.minX + other.maxX) / 2f;
		this.maxX = other.maxX;
		this.minY = (other.minY + other.maxY) / 2f;
		this.maxY = other.maxY;
		return this;
	}

	public int AABBX() {
		return MathUtils.iceil(maxX + minX) / 2;
	}

	public int AABBY() {
		return MathUtils.iceil(maxY + minY) / 2;
	}

	public int AABBZ() {
		return MathUtils.iceil(maxZ + minZ) / 2;
	}

	public Vector3f min() {
		return _min.set(minX, minY, minZ);
	}

	public Vector3f max() {
		return _max.set(maxX, maxY, maxZ);
	}

	public int width() {
		return (int) getWidth();
	}

	public int height() {
		return (int) getHeight();
	}

	@Override
	public float getWidth() {
		return this.maxX - this.minX;
	}

	@Override
	public float getHeight() {
		return this.maxY - this.minY;
	}

	public float depth() {
		return this.maxZ - this.minZ;
	}

	public AABB cpy() {
		return new AABB(this.minX, this.minY, this.maxX, this.maxY);
	}

	public boolean isHit(AABB b) {
		return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY && b.minY < this.maxY;
	}

	public boolean isOutside(AABB b) {
		return maxX - b.minX < 0 || minX - b.maxX > 0 || maxY - b.minY < 0 || minY - b.maxY > 0 || maxZ - b.minZ < 0
				|| minZ - b.maxZ > 0;
	}

	public AABB set(AABB b, XYZ p) {
		this.minX = MathUtils.ifloor(b.minX + p.getX());
		this.minY = MathUtils.ifloor(b.minY + p.getY());
		this.minZ = MathUtils.ifloor(b.minZ + p.getZ());
		this.maxX = MathUtils.iceil(b.maxX + p.getX());
		this.maxY = MathUtils.iceil(b.maxY + p.getY());
		this.maxZ = MathUtils.iceil(b.maxZ + p.getZ());
		return this;
	}

	public AABB set(float x1, float y1, float x2, float y2) {
		return set(x1, y1, 0f, x2, y2, 0f);
	}

	public AABB set(float x1, float y1, float z1, float x2, float y2, float z2) {
		this.minX = x1;
		this.minY = y1;
		this.minZ = z1;
		this.maxX = x2;
		this.maxY = y2;
		this.maxZ = z2;
		return this;
	}

	public AABB scale(float sx, float sy) {
		return new AABB(this.minX * sx, this.minY * sy, this.maxX * sx, this.maxY * sy);
	}

	public AABB moveMin(float cx, float cy) {
		this.minX += cx;
		this.minY += cy;
		return this;
	}

	public AABB moveMax(float cx, float cy) {
		this.maxX += cx;
		this.maxY += cy;
		return this;
	}

	public AABB move(float cx, float cy) {
		this.moveMin(cx, cy);
		this.moveMax(cx, cy);
		return this;
	}

	public boolean moveOut(AABB other) {
		if (other == null) {
			return false;
		}
		if (!intersects(other)) {
			return false;
		}
		final float right = other.minX - this.maxX;
		final float left = this.minX - other.maxX;
		final float up = other.minY - this.maxY;
		final float down = this.minY - other.maxY;
		final float lr = right > left ? -right : left;
		final float ud = up > down ? up : -down;
		if (MathUtils.abs(lr) < MathUtils.abs(ud)) {
			move(lr, 0f);
		} else {
			move(0f, ud);
		}
		return true;
	}

	public boolean intersects(float x, float y) {
		return x > this.minX && x < this.maxX && y > this.minY && y < this.maxY;
	}

	public boolean intersects(XY pos) {
		if (pos == null) {
			return false;
		}
		return pos.getX() > this.minX && pos.getX() < this.maxX && pos.getY() > this.minY && pos.getY() < this.maxY;
	}

	public boolean intersects(AABB other) {
		if (other == null) {
			return false;
		}
		if (this.minX > other.maxX || other.minX > this.maxX) {
			return false;
		}
		if (this.minY > other.maxY || other.minY > this.maxY) {
			return false;
		}
		return !(this.minZ > other.maxZ || other.minZ > this.maxZ);
	}

	public boolean contains(XY pos) {
		if (pos == null) {
			return false;
		}
		return pos.getX() >= this.minX && pos.getX() <= this.maxX && pos.getY() >= this.minY && pos.getY() <= this.maxY;
	}

	public boolean overlaps(AABB other) {
		if (other == null) {
			return false;
		}
		return this.minX <= other.maxX && this.maxX >= other.minX && this.maxY >= other.minY && this.minY <= other.maxY
				&& !(other.maxZ > maxZ || other.minZ < minZ);
	}

	public boolean overlaps(XYZ point) {
		if (point == null) {
			return false;
		}
		if (point.getX() > maxX || point.getX() < minX) {
			return false;
		}
		if (point.getY() > maxY || point.getY() < minY) {
			return false;
		}
		if (point.getZ() > maxZ || point.getZ() < minZ) {
			return false;
		}
		return true;
	}

	public float distance(Vector2f other) {
		float dx = getX() - other.x;
		float dy = getY() - other.y;
		return MathUtils.sqrt(dx * dx + dy * dy);
	}

	public AABB merge(AABB other) {
		float minx = MathUtils.min(this.minX, other.minX);
		float miny = MathUtils.min(this.minY, other.minY);
		float minz = MathUtils.min(this.minZ, other.minZ);
		float maxx = MathUtils.max(this.maxX, other.maxX);
		float maxy = MathUtils.max(this.maxY, other.maxY);
		float maxz = MathUtils.max(this.maxY, other.maxY);
		return new AABB(minx, miny, minz, maxx, maxy, maxz);
	}

	public Vector2f getPosition(Vector2f pos) {
		return pos.set(getX(), getY());
	}

	public AABB setPosition(XY pos) {
		if (pos == null) {
			return this;
		}
		setPosition(pos.getX(), pos.getY());
		return this;
	}

	public AABB rotate(float rotate) {
		final int[] rect = MathUtils.getLimit(this.minX, this.minY, this.maxX, this.maxY, rotate);
		return set(rect[0], rect[1], rect[2], rect[3]);
	}

	public AABB setPosition(float x, float y) {
		setX(x);
		setY(y);
		return this;
	}

	public AABB setVertices(TArray<Vector2f> vertices, XY velocity) {
		this.minX = Float.MIN_VALUE;
		this.maxX = -Float.MIN_VALUE;
		this.minY = Float.MIN_VALUE;
		this.maxY = -Float.MIN_VALUE;
		for (int i = 0; i < vertices.size; i++) {
			Vector2f vertex = vertices.get(i);
			if (vertex.x > this.maxX) {
				this.maxX = vertex.x;
			}
			if (vertex.x < this.minX) {
				this.minX = vertex.x;
			}
			if (vertex.y > this.maxY) {
				this.maxY = vertex.y;
			}
			if (vertex.y < this.minY) {
				this.minY = vertex.y;
			}
		}
		if (velocity != null) {
			if (velocity.getX() > 0) {
				this.maxX += velocity.getX();
			} else {
				this.minX += velocity.getX();
			}
			if (velocity.getY() > 0) {
				this.maxY += velocity.getY();
			} else {
				this.minY += velocity.getY();
			}
		}
		return this;
	}

	public AABB setSize(float width, float height) {
		setWidth(width);
		setHeight(height);
		return this;
	}

	public float getAspectRatio() {
		return (getHeight() == 0) ? MathUtils.NaN : getWidth() / getHeight();
	}

	public Vector2f[] getBoxCoordinates() {
		return new Vector2f[] { new Vector2f(this.minX, this.maxY), new Vector2f(this.maxX, this.maxY),
				new Vector2f(this.maxX, this.minY), new Vector2f(this.minX, this.minY) };
	}

	public float getPerimeter() {
		return 2 * (this.maxX - this.minX + this.maxY - this.minY);
	}

	public float getArea() {
		return (this.maxX - this.minX) * (this.maxY - this.minY);
	}

	public Vector3f getCenter(Vector3f pos) {
		final float centerX = maxX + minX;
		final float centerY = maxY + minY;
		final float centerZ = maxZ + minZ;
		return new Vector3f((centerX < 0 ? 0 : centerX * 0.5f), (centerY < 0 ? 0 : centerY * 0.5f),
				(centerZ < 0 ? 0 : centerZ * 0.5f));
	}

	public Vector2f getCenter(Vector2f pos) {
		pos.x = getX() + getWidth() / 2;
		pos.y = getY() + getHeight() / 2;
		return pos;
	}

	public AABB setCenter(float x, float y) {
		setPosition(x - getWidth() / 2, y - getHeight() / 2);
		return this;
	}

	public AABB setCenter(XY pos) {
		setPosition(pos.getX() - getWidth() / 2, pos.getY() - getHeight() / 2);
		return this;
	}

	public AABB i(AABB rect) {
		float ratio = getAspectRatio();
		if (ratio > rect.getAspectRatio()) {
			setSize(rect.getHeight() * ratio, rect.getHeight());
		} else {
			setSize(rect.getWidth(), rect.getWidth() / ratio);
		}
		setPosition((rect.getX() + rect.getWidth() / 2) - getWidth() / 2,
				(rect.getY() + rect.getHeight() / 2) - getHeight() / 2);
		return this;
	}

	public AABB fitInside(AABB rect) {
		float ratio = getAspectRatio();
		if (ratio < rect.getAspectRatio()) {
			setSize(rect.getHeight() * ratio, rect.getHeight());
		} else {
			setSize(rect.getWidth(), rect.getWidth() / ratio);
		}
		setPosition((rect.getX() + rect.getWidth() / 2) - getWidth() / 2,
				(rect.getY() + rect.getHeight() / 2) - getHeight() / 2);
		return this;
	}

	public AABB addQuad(Matrix3 mat) {
		return addQuad(mat.val);
	}

	public AABB addQuad(float[] vertices) {
		float x0 = this.minX;
		float y0 = this.minY;
		float x1 = this.maxX;
		float y1 = this.maxY;

		float x = vertices[0];
		float y = vertices[1];

		x0 = x < x0 ? x : x0;
		y0 = y < y0 ? y : y0;
		x1 = x > x1 ? x : x1;
		y1 = y > y1 ? y : y1;

		x = vertices[2];
		y = vertices[3];
		x0 = x < x0 ? x : x0;
		y0 = y < y0 ? y : y0;
		x1 = x > x1 ? x : x1;
		y1 = y > y1 ? y : y1;

		x = vertices[4];
		y = vertices[5];
		x0 = x < x0 ? x : x0;
		y0 = y < y0 ? y : y0;
		x1 = x > x1 ? x : x1;
		y1 = y > y1 ? y : y1;

		x = vertices[6];
		y = vertices[7];
		x0 = x < x0 ? x : x0;
		y0 = y < y0 ? y : y0;
		x1 = x > x1 ? x : x1;
		y1 = y > y1 ? y : y1;

		this.minX = x0;
		this.minY = y0;
		this.maxX = x1;
		this.maxY = y1;
		return this;
	}

	public AABB addFrame(Affine2f aff, float x0, float y0, float x1, float y1) {
		float a = aff.m00;
		float b = aff.m01;
		float c = aff.m10;
		float d = aff.m11;
		float tx = aff.tx;
		float ty = aff.ty;

		float minX1 = this.minX;
		float minY1 = this.minY;
		float maxX1 = this.maxX;
		float maxY1 = this.maxY;

		float x = (a * x0) + (c * y0) + tx;
		float y = (b * x0) + (d * y0) + ty;

		minX1 = x < minX1 ? x : minX1;
		minY1 = y < minY1 ? y : minY1;
		maxX1 = x > maxX1 ? x : maxX1;
		maxY1 = y > maxY1 ? y : maxY1;

		x = (a * x1) + (c * y0) + tx;
		y = (b * x1) + (d * y0) + ty;
		minX1 = x < minX1 ? x : minX1;
		minY1 = y < minY1 ? y : minY1;
		maxX1 = x > maxX1 ? x : maxX1;
		maxY1 = y > maxY1 ? y : maxY1;

		x = (a * x0) + (c * y1) + tx;
		y = (b * x0) + (d * y1) + ty;
		minX1 = x < minX1 ? x : minX1;
		minY1 = y < minY1 ? y : minY1;
		maxX1 = x > maxX1 ? x : maxX1;
		maxY1 = y > maxY1 ? y : maxY1;

		x = (a * x1) + (c * y1) + tx;
		y = (b * x1) + (d * y1) + ty;
		minX1 = x < minX1 ? x : minX1;
		minY1 = y < minY1 ? y : minY1;
		maxX1 = x > maxX1 ? x : maxX1;
		maxY1 = y > maxY1 ? y : maxY1;

		this.minX = minX1;
		this.minY = minY1;
		this.maxX = maxX1;
		this.maxY = maxY1;
		return this;
	}

	public AABB addVertexData(float[] vertexData, int begin, int end) {
		float minX1 = this.minX;
		float minY1 = this.minY;
		float maxX1 = this.maxX;
		float maxY1 = this.maxY;

		for (int i = begin; i < end; i += 2) {
			float x = vertexData[i];
			float y = vertexData[i + 1];

			minX1 = x < minX1 ? x : minX1;
			minY1 = y < minY1 ? y : minY1;
			maxX1 = x > maxX1 ? x : maxX1;
			maxY1 = y > maxY1 ? y : maxY1;
		}

		this.minX = minX1;
		this.minY = minY1;
		this.maxX = maxX1;
		this.maxY = maxY1;
		return this;
	}

	public AABB clear() {
		return setZero();
	}

	public AABB setZero() {
		this.minX = 0f;
		this.maxX = 0f;
		this.minY = 0f;
		this.maxY = 0f;
		this.minZ = 0f;
		this.maxZ = 0f;
		return this;
	}

	public void setMinZ(float z) {
		this.minZ = z;
	}

	public void setMaxZ(float z) {
		this.maxZ = z;
	}

	@Override
	public void setX(float x) {
		this.minX = x;
	}

	@Override
	public void setY(float y) {
		this.minY = y;
	}

	@Override
	public void setWidth(float w) {
		this.maxX = w;
	}

	@Override
	public void setHeight(float h) {
		this.maxY = h;
	}

	@Override
	public float getX() {
		return this.minX;
	}

	@Override
	public float getY() {
		return this.minY;
	}

	@Override
	public float getZ() {
		return this.maxX;
	}

	@Override
	public float getW() {
		return this.maxY;
	}

	public boolean contains(Circle circle) {
		float xmin = circle.x - circle.getRadius();
		float xmax = xmin + 2f * circle.getRadius();

		float ymin = circle.y - circle.getRadius();
		float ymax = ymin + 2f * circle.getRadius();

		return ((xmin > this.minX && xmin < this.minX + this.maxX)
				&& (xmax > this.minX && xmax < this.minX + this.maxX))
				&& ((ymin > this.minY && ymin < this.minY + this.maxY)
						&& (ymax > this.minY && ymax < this.minY + this.maxY));
	}

	public boolean isEmpty() {
		return minX >= maxX || minY >= maxY || minZ >= maxZ;
	}

	public AABB random() {
		this.minX = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.minY = MathUtils.random(0f, LSystem.viewSize.getHeight());
		this.maxX = MathUtils.random(0f, LSystem.viewSize.getWidth());
		this.maxY = MathUtils.random(0f, LSystem.viewSize.getHeight());
		return this;
	}

	@Override
	public float getCenterX() {
		return this.minX + this.maxX / 2f;
	}

	@Override
	public float getCenterY() {
		return this.minY + this.maxY / 2f;
	}

	public RectBox getRectBox(RectBox rect) {
		if (this.minX > this.maxX || this.minY > this.maxY) {
			return new RectBox();
		}

		if (rect == null) {
			rect = new RectBox();
		}

		rect.x = this.minX;
		rect.y = this.minY;
		rect.width = MathUtils.floor(this.maxX - this.minX);
		rect.height = MathUtils.floor(this.maxY - this.minY);

		return rect;
	}

	public AABB translate(XYZ pos) {
		if (pos == null) {
			return this;
		}
		this.minX += pos.getX();
		this.maxX += pos.getX();
		this.minY += pos.getY();
		this.maxY += pos.getY();
		this.minZ += pos.getZ();
		this.maxZ += pos.getZ();
		return this;
	}

	public AABB shift(XYZ pos) {
		if (pos == null) {
			return this;
		}
		float deltaX = this.maxX - this.minX;
		float deltaY = this.maxY - this.minY;
		this.minX = pos.getX();
		this.maxX = pos.getX() + deltaX;
		this.minY = pos.getY();
		this.maxY = pos.getY() + deltaY;
		this.minZ = pos.getZ();
		this.maxZ = pos.getZ() + deltaY;
		return this;
	}

	public RectBox toRectBox() {
		return getRectBox(null);
	}

	public AABB add(XYZ pos) {
		this.minX = MathUtils.min(this.minX, pos.getX());
		this.maxX = MathUtils.max(this.maxX, pos.getX());
		this.minY = MathUtils.min(this.minY, pos.getY());
		this.maxY = MathUtils.max(this.maxY, pos.getY());
		this.minZ = MathUtils.min(this.minZ, pos.getZ());
		this.maxZ = MathUtils.max(this.maxZ, pos.getZ());
		return this;
	}

	public AABB add(AABB rect) {
		final float minX1 = this.minX;
		final float minY1 = this.minY;
		final float minZ1 = this.minZ;
		final float maxX1 = this.maxX;
		final float maxY1 = this.maxY;
		final float maxZ1 = this.maxZ;
		this.minX = rect.minX < minX1 ? rect.minX : minX1;
		this.minY = rect.minY < minY1 ? rect.minY : minY1;
		this.minZ = rect.minZ < minZ1 ? rect.minZ : minZ1;
		this.maxX = rect.maxX > maxX1 ? rect.maxX : maxX1;
		this.maxY = rect.maxY > maxY1 ? rect.maxY : maxY1;
		this.maxZ = rect.maxZ > maxZ1 ? rect.maxZ : maxZ1;
		return this;
	}

	public AABB add(Affine2f tx, XY pos) {
		final float x = (tx.m00 * pos.getX()) + (tx.m10 * pos.getY()) + tx.tx;
		final float y = (tx.m01 * pos.getX()) + (tx.m11 * pos.getY()) + tx.ty;

		this.minX = MathUtils.min(this.minX, x);
		this.maxX = MathUtils.max(this.maxX, x);
		this.minY = MathUtils.min(this.minY, y);
		this.maxY = MathUtils.max(this.maxY, y);
		return this;
	}

	public AABB add(AABB aabb, RectBox area) {
		final float aminX = aabb.minX > area.x ? aabb.minX : area.x;
		final float bminY = aabb.minY > area.y ? aabb.minY : area.y;
		final float cmaxX = aabb.maxX < area.x + area.width ? aabb.maxX : (area.x + area.width);
		final float dmaxY = aabb.maxY < area.y + area.height ? aabb.maxY : (area.y + area.height);

		if (aminX <= cmaxX && bminY <= dmaxY) {
			final float minX1 = this.minX;
			final float minY1 = this.minY;
			final float maxX1 = this.maxX;
			final float maxY1 = this.maxY;

			this.minX = aminX < minX1 ? aminX : minX1;
			this.minY = bminY < minY1 ? bminY : minY1;
			this.maxX = cmaxX > maxX1 ? cmaxX : maxX1;
			this.maxY = dmaxY > maxY1 ? dmaxY : maxY1;
		}
		return this;
	}

	public boolean isDegenerate() {
		return this.minX == this.maxX || this.minY == this.maxY || this.minZ == this.maxZ;
	}

	public boolean isDegenerate(float error) {
		return MathUtils.abs(this.maxX - this.minX) <= error || MathUtils.abs(this.maxY - this.minY) <= error
				|| MathUtils.abs(this.maxZ - this.minZ) <= error;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, this.minX);
		hashCode = prime * LSystem.unite(hashCode, this.minY);
		hashCode = prime * LSystem.unite(hashCode, this.minZ);
		hashCode = prime * LSystem.unite(hashCode, this.maxX);
		hashCode = prime * LSystem.unite(hashCode, this.maxY);
		hashCode = prime * LSystem.unite(hashCode, this.maxZ);
		return hashCode;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("AABB");
		builder.kv("minX", this.minX).comma().kv("minY", this.minY).kv("minZ", this.minZ).comma().kv("maxX", this.maxX)
				.comma().kv("maxY", this.maxY).comma().kv("maxZ", this.maxZ);
		return builder.toString();
	}

	@Override
	public void close() {
		this.minX = this.minY = this.minZ = this.maxX = this.maxY = this.maxZ = 0f;
	}

}

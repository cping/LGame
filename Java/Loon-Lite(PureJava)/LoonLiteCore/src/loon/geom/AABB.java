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
import loon.LSystem;
import loon.action.ActionBind;
import loon.utils.MathUtils;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;

/**
 * 一个最基础的矩形碰撞器
 */
public class AABB implements XY, XYZW, BoxSize, LRelease {

	public final static AABB at(String v) {
		if (StringUtils.isEmpty(v)) {
			return new AABB();
		}
		String[] result = StringUtils.split(v, LSystem.COMMA);
		int len = result.length;
		if (len > 3) {
			try {
				float x = Float.parseFloat(result[0].trim());
				float y = Float.parseFloat(result[1].trim());
				float width = Float.parseFloat(result[2].trim());
				float height = Float.parseFloat(result[3].trim());
				return new AABB(x, y, width, height);
			} catch (Exception ex) {
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

	public float minX, minY;

	public float maxX, maxY;

	public AABB() {
		this(0.0F, 0.0F, 0.0F, 0.0F);
	}

	public AABB(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
	}

	public AABB setCentered(float x, float y, float size) {
		return set(x - size / 2f, y - size / 2f, size, size);
	}

	public AABB setCentered(float x, float y, float width, float height) {
		return set(x - width / 2f, y - height / 2f, width, height);
	}

	public int width() {
		return (int) this.maxX;
	}

	public int height() {
		return (int) this.maxY;
	}

	@Override
	public float getWidth() {
		return this.maxX;
	}

	@Override
	public float getHeight() {
		return this.maxY;
	}

	public AABB cpy() {
		return new AABB(this.minX, this.minY, this.maxX, this.maxY);
	}

	public boolean isHit(AABB b) {
		return this.minX < b.maxX && b.minX < this.maxX && this.minY < b.maxY && b.minY < this.maxY;
	}

	public AABB set(float minX, float minY, float maxX, float maxY) {
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	public AABB scale(float sx, float sy) {
		return new AABB(minX * sx, minY * sy, maxX * sx, maxY * sy);
	}

	public AABB move(float cx, float cy) {
		this.minX += cx;
		this.minY += cy;
		return this;
	}

	public float distance(Vector2f other) {
		float dx = getX() - other.x;
		float dy = getY() - other.y;
		return MathUtils.sqrt(dx * dx + dy * dy);
	}

	public AABB merge(AABB other) {
		float minX = MathUtils.min(this.getX(), other.getX());
		float minY = MathUtils.min(this.getY(), other.getY());

		float maxW = MathUtils.max(this.getWidth(), other.getWidth());
		float maxH = MathUtils.max(this.getHeight(), other.getHeight());

		return new AABB(minX, minY, maxW, maxH);
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
		final int[] rect = MathUtils.getLimit(minX, minY, maxX, maxY, rotate);
		return set(rect[0], rect[1], rect[2], rect[3]);
	}

	public AABB setPosition(float x, float y) {
		setX(x);
		setY(y);
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

	public Vector2f getCenter(Vector2f pos) {
		pos.x = getX() + getWidth() / 2f;
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

	public AABB fitOutside(AABB rect) {
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
		float minX = this.minX;
		float minY = this.minY;
		float maxX = this.maxX;
		float maxY = this.maxY;

		float x = vertices[0];
		float y = vertices[1];

		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = vertices[2];
		y = vertices[3];
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = vertices[4];
		y = vertices[5];
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = vertices[6];
		y = vertices[7];
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	public AABB addFrame(Affine2f aff, float x0, float y0, float x1, float y1) {
		float a = aff.m00;
		float b = aff.m01;
		float c = aff.m10;
		float d = aff.m11;
		float tx = aff.tx;
		float ty = aff.ty;

		float minX = this.minX;
		float minY = this.minY;
		float maxX = this.maxX;
		float maxY = this.maxY;

		float x = (a * x0) + (c * y0) + tx;
		float y = (b * x0) + (d * y0) + ty;

		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = (a * x1) + (c * y0) + tx;
		y = (b * x1) + (d * y0) + ty;
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = (a * x0) + (c * y1) + tx;
		y = (b * x0) + (d * y1) + ty;
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		x = (a * x1) + (c * y1) + tx;
		y = (b * x1) + (d * y1) + ty;
		minX = x < minX ? x : minX;
		minY = y < minY ? y : minY;
		maxX = x > maxX ? x : maxX;
		maxY = y > maxY ? y : maxY;

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
	}

	public AABB addVertexData(float[] vertexData, int begin, int end) {
		float minX = this.minX;
		float minY = this.minY;
		float maxX = this.maxX;
		float maxY = this.maxY;

		for (int i = begin; i < end; i += 2) {
			float x = vertexData[i];
			float y = vertexData[i + 1];

			minX = x < minX ? x : minX;
			minY = y < minY ? y : minY;
			maxX = x > maxX ? x : maxX;
			maxY = y > maxY ? y : maxY;
		}

		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		return this;
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
		return minX;
	}

	@Override
	public float getY() {
		return minY;
	}

	@Override
	public float getZ() {
		return maxX;
	}

	@Override
	public float getW() {
		return maxY;
	}

	public boolean contains(Circle circle) {
		float xmin = circle.x - circle.getRadius();
		float xmax = xmin + 2f * circle.getRadius();

		float ymin = circle.y - circle.getRadius();
		float ymax = ymin + 2f * circle.getRadius();

		return ((xmin > minX && xmin < minX + maxX) && (xmax > minX && xmax < minX + maxX))
				&& ((ymin > minY && ymin < minY + maxY) && (ymax > minY && ymax < minY + maxY));
	}

	public boolean isEmpty() {
		return this.maxX <= 0 && this.maxY <= 0;
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

	public RectBox toRectBox() {
		return getRectBox(null);
	}

	public AABB add(XY pos) {
		this.minX = MathUtils.min(this.minX, pos.getX());
		this.maxX = MathUtils.max(this.maxX, pos.getX());
		this.minY = MathUtils.min(this.minY, pos.getY());
		this.maxY = MathUtils.max(this.maxY, pos.getY());
		return this;
	}

	public AABB add(AABB rect) {
		final float minX = this.minX;
		final float minY = this.minY;
		final float maxX = this.maxX;
		final float maxY = this.maxY;

		this.minX = rect.minX < minX ? rect.minX : minX;
		this.minY = rect.minY < minY ? rect.minY : minY;
		this.maxX = rect.maxX > maxX ? rect.maxX : maxX;
		this.maxY = rect.maxY > maxY ? rect.maxY : maxY;
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
		final float _minX = aabb.minX > area.x ? aabb.minX : area.x;
		final float _minY = aabb.minY > area.y ? aabb.minY : area.y;
		final float _maxX = aabb.maxX < area.x + area.width ? aabb.maxX : (area.x + area.width);
		final float _maxY = aabb.maxY < area.y + area.height ? aabb.maxY : (area.y + area.height);

		if (_minX <= _maxX && _minY <= _maxY) {
			final float minX = this.minX;
			final float minY = this.minY;
			final float maxX = this.maxX;
			final float maxY = this.maxY;

			this.minX = _minX < minX ? _minX : minX;
			this.minY = _minY < minY ? _minY : minY;
			this.maxX = _maxX > maxX ? _maxX : maxX;
			this.maxY = _maxY > maxY ? _maxY : maxY;
		}
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 1;
		hashCode = prime * LSystem.unite(hashCode, minX);
		hashCode = prime * LSystem.unite(hashCode, minY);
		hashCode = prime * LSystem.unite(hashCode, maxX);
		hashCode = prime * LSystem.unite(hashCode, maxY);
		return hashCode;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("AABB");
		builder.kv("minX", minX).comma().kv("minY", minY).comma().kv("maxX", maxX).comma().kv("maxY", maxY);
		return builder.toString();
	}

	@Override
	public void close() {
		this.minX = this.minY = this.maxX = this.maxY = 0;
	}

}

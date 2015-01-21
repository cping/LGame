/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package loon.core;

import java.io.Serializable;
import java.util.List;

import loon.core.graphics.opengl.math.Transform4;
import loon.core.graphics.opengl.math.Location3;


/**
 * Encapsulates an axis aligned bounding box represented by a minimum and a
 * maximum Vector. Additionally you can query for the bounding box's center,
 * dimensions and corner points.
 * 
 * @author badlogicgames@gmail.com, Xoppa
 */
public class BoundingBox implements Serializable {
	private static final long serialVersionUID = -1286036817192127343L;

	private final static Location3 tmpVector = new Location3();

	public final Location3 min = new Location3();
	public final Location3 max = new Location3();

	private final Location3 cnt = new Location3();
	private final Location3 dim = new Location3();

	private Location3[] corners;

	public Location3 getCenter() {
		return cnt;
	}

	/**
	 * @param out
	 *            The {@link Location3} to receive the center of the bounding box.
	 * @return The vector specified with the out argument.
	 */
	public Location3 getCenter(Location3 out) {
		return out.set(cnt);
	}

	public float getCenterX() {
		return cnt.x;
	}

	public float getCenterY() {
		return cnt.y;
	}

	public float getCenterZ() {
		return cnt.z;
	}

	public Location3[] getCorners() {
		if (corners == null) {
			corners = new Location3[8];
			for (int i = 0; i < 8; i++)
				corners[i] = new Location3();
		}
		corners[0].set(min.x, min.y, min.z);
		corners[1].set(max.x, min.y, min.z);
		corners[2].set(max.x, max.y, min.z);
		corners[3].set(min.x, max.y, min.z);
		corners[4].set(min.x, min.y, max.z);
		corners[5].set(max.x, min.y, max.z);
		corners[6].set(max.x, max.y, max.z);
		corners[7].set(min.x, max.y, max.z);
		return corners;
	}

	public Location3 getCorner000(final Location3 out) {
		return out.set(min.x, min.y, min.z);
	}

	public Location3 getCorner001(final Location3 out) {
		return out.set(min.x, min.y, max.z);
	}

	public Location3 getCorner010(final Location3 out) {
		return out.set(min.x, max.y, min.z);
	}

	public Location3 getCorner011(final Location3 out) {
		return out.set(min.x, max.y, max.z);
	}

	public Location3 getCorner100(final Location3 out) {
		return out.set(max.x, min.y, min.z);
	}

	public Location3 getCorner101(final Location3 out) {
		return out.set(max.x, min.y, max.z);
	}

	public Location3 getCorner110(final Location3 out) {
		return out.set(max.x, max.y, min.z);
	}

	public Location3 getCorner111(final Location3 out) {
		return out.set(max.x, max.y, max.z);
	}

	/**
	 * @deprecated Use {@link #getDimensions(Location3)} instead
	 * @return The dimensions of this bounding box on all three axis
	 */
	@Deprecated
	public Location3 getDimensions() {
		return dim;
	}

	/**
	 * @param out
	 *            The {@link Location3} to receive the dimensions of this bounding
	 *            box on all three axis.
	 * @return The vector specified with the out argument
	 */
	public Location3 getDimensions(final Location3 out) {
		return out.set(dim);
	}

	public float getWidth() {
		return dim.x;
	}

	public float getHeight() {
		return dim.y;
	}

	public float getDepth() {
		return dim.z;
	}

	public Location3 getMin(final Location3 out) {
		return out.set(min);
	}

	public Location3 getMax(final Location3 out) {
		return out.set(max);
	}

	public BoundingBox() {
		clr();
	}

	public BoundingBox(BoundingBox bounds) {
		this.set(bounds);
	}

	public BoundingBox(Location3 minimum, Location3 maximum) {
		this.set(minimum, maximum);
	}

	/**
	 * Sets the given bounding box.
	 * 
	 * @param bounds
	 *            The bounds.
	 * @return This bounding box for chaining.
	 */
	public BoundingBox set(BoundingBox bounds) {
		return this.set(bounds.min, bounds.max);
	}

	/**
	 * Sets the given minimum and maximum vector.
	 * 
	 * @param minimum
	 *            The minimum vector
	 * @param maximum
	 *            The maximum vector
	 * @return This bounding box for chaining.
	 */
	public BoundingBox set(Location3 minimum, Location3 maximum) {
		min.set(minimum.x < maximum.x ? minimum.x : maximum.x,
				minimum.y < maximum.y ? minimum.y : maximum.y,
				minimum.z < maximum.z ? minimum.z : maximum.z);
		max.set(minimum.x > maximum.x ? minimum.x : maximum.x,
				minimum.y > maximum.y ? minimum.y : maximum.y,
				minimum.z > maximum.z ? minimum.z : maximum.z);
		cnt.set(min).add(max).scl(0.5f);
		dim.set(max).sub(min);
		return this;
	}

	/**
	 * Sets the bounding box minimum and maximum vector from the given points.
	 * 
	 * @param points
	 *            The points.
	 * @return This bounding box for chaining.
	 */
	public BoundingBox set(Location3[] points) {
		this.inf();
		for (Location3 l_point : points)
			this.ext(l_point);
		return this;
	}

	/**
	 * Sets the bounding box minimum and maximum vector from the given points.
	 * 
	 * @param points
	 *            The points.
	 * @return This bounding box for chaining.
	 */
	public BoundingBox set(List<Location3> points) {
		this.inf();
		for (Location3 l_point : points)
			this.ext(l_point);
		return this;
	}

	/**
	 * Sets the minimum and maximum vector to positive and negative infinity.
	 * 
	 * @return This bounding box for chaining.
	 */
	public BoundingBox inf() {
		min.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY,
				Float.POSITIVE_INFINITY);
		max.set(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY,
				Float.NEGATIVE_INFINITY);
		cnt.set(0, 0, 0);
		dim.set(0, 0, 0);
		return this;
	}

	/**
	 * Extends the bounding box to incorporate the given {@link Location3}.
	 * 
	 * @param point
	 *            The vector
	 * @return This bounding box for chaining.
	 */
	public BoundingBox ext(Location3 point) {
		return this.set(min.set(min(min.x, point.x), min(min.y, point.y),
				min(min.z, point.z)), max.set(Math.max(max.x, point.x),
				Math.max(max.y, point.y), Math.max(max.z, point.z)));
	}

	/**
	 * Sets the minimum and maximum vector to zeros.
	 * 
	 * @return This bounding box for chaining.
	 */
	public BoundingBox clr() {
		return this.set(min.set(0, 0, 0), max.set(0, 0, 0));
	}

	/**
	 * Returns whether this bounding box is valid. This means that {@link #max}
	 * is greater than {@link #min}.
	 * 
	 * @return True in case the bounding box is valid, false otherwise
	 */
	public boolean isValid() {
		return min.x < max.x && min.y < max.y && min.z < max.z;
	}

	/**
	 * Extends this bounding box by the given bounding box.
	 * 
	 * @param a_bounds
	 *            The bounding box
	 * @return This bounding box for chaining.
	 */
	public BoundingBox ext(BoundingBox a_bounds) {
		return this.set(min.set(min(min.x, a_bounds.min.x),
				min(min.y, a_bounds.min.y), min(min.z, a_bounds.min.z)), max
				.set(max(max.x, a_bounds.max.x), max(max.y, a_bounds.max.y),
						max(max.z, a_bounds.max.z)));
	}

	/**
	 * Extends this bounding box by the given transformed bounding box.
	 * 
	 * @param bounds
	 *            The bounding box
	 * @param transform
	 *            The transformation matrix to apply to bounds, before using it
	 *            to extend this bounding box.
	 * @return This bounding box for chaining.
	 */
	public BoundingBox ext(BoundingBox bounds, Transform4 transform) {
		ext(tmpVector.set(bounds.min.x, bounds.min.y, bounds.min.z).mul(
				transform));
		ext(tmpVector.set(bounds.min.x, bounds.min.y, bounds.max.z).mul(
				transform));
		ext(tmpVector.set(bounds.min.x, bounds.max.y, bounds.min.z).mul(
				transform));
		ext(tmpVector.set(bounds.min.x, bounds.max.y, bounds.max.z).mul(
				transform));
		ext(tmpVector.set(bounds.max.x, bounds.min.y, bounds.min.z).mul(
				transform));
		ext(tmpVector.set(bounds.max.x, bounds.min.y, bounds.max.z).mul(
				transform));
		ext(tmpVector.set(bounds.max.x, bounds.max.y, bounds.min.z).mul(
				transform));
		ext(tmpVector.set(bounds.max.x, bounds.max.y, bounds.max.z).mul(
				transform));
		return this;
	}

	/**
	 * Multiplies the bounding box by the given matrix. This is achieved by
	 * multiplying the 8 corner points and then calculating the minimum and
	 * maximum vectors from the transformed points.
	 * 
	 * @param transform
	 *            The matrix
	 * @return This bounding box for chaining.
	 */
	public BoundingBox mul(Transform4 transform) {
		final float x0 = min.x, y0 = min.y, z0 = min.z, x1 = max.x, y1 = max.y, z1 = max.z;
		inf();
		ext(tmpVector.set(x0, y0, z0).mul(transform));
		ext(tmpVector.set(x0, y0, z1).mul(transform));
		ext(tmpVector.set(x0, y1, z0).mul(transform));
		ext(tmpVector.set(x0, y1, z1).mul(transform));
		ext(tmpVector.set(x1, y0, z0).mul(transform));
		ext(tmpVector.set(x1, y0, z1).mul(transform));
		ext(tmpVector.set(x1, y1, z0).mul(transform));
		ext(tmpVector.set(x1, y1, z1).mul(transform));
		return this;
	}

	/**
	 * Returns whether the given bounding box is contained in this bounding box.
	 * 
	 * @param b
	 *            The bounding box
	 * @return Whether the given bounding box is contained
	 */
	public boolean contains(BoundingBox b) {
		return !isValid()
				|| (min.x <= b.min.x && min.y <= b.min.y && min.z <= b.min.z
						&& max.x >= b.max.x && max.y >= b.max.y && max.z >= b.max.z);
	}

	/**
	 * Returns whether the given bounding box is intersecting this bounding box
	 * (at least one point in).
	 * 
	 * @param b
	 *            The bounding box
	 * @return Whether the given bounding box is intersected
	 */
	public boolean intersects(BoundingBox b) {
		if (!isValid()){
			return false;
		}

		float lx = Math.abs(this.cnt.x - b.cnt.x);
		float sumx = (this.dim.x / 2.0f) + (b.dim.x / 2.0f);

		float ly = Math.abs(this.cnt.y - b.cnt.y);
		float sumy = (this.dim.y / 2.0f) + (b.dim.y / 2.0f);

		float lz = Math.abs(this.cnt.z - b.cnt.z);
		float sumz = (this.dim.z / 2.0f) + (b.dim.z / 2.0f);

		return (lx <= sumx && ly <= sumy && lz <= sumz);

	}

	public boolean contains(Location3 v) {
		return min.x <= v.x && max.x >= v.x && min.y <= v.y && max.y >= v.y
				&& min.z <= v.z && max.z >= v.z;
	}

	@Override
	public String toString() {
		return "[" + min + "|" + max + "]";
	}

	/**
	 * Extends the bounding box by the given vector.
	 * 
	 * @param x
	 *            The x-coordinate
	 * @param y
	 *            The y-coordinate
	 * @param z
	 *            The z-coordinate
	 * @return This bounding box for chaining.
	 */
	public BoundingBox ext(float x, float y, float z) {
		return this.set(min.set(min(min.x, x), min(min.y, y), min(min.z, z)),
				max.set(max(max.x, x), max(max.y, y), max(max.z, z)));
	}

	static final float min(final float a, final float b) {
		return a > b ? b : a;
	}

	static final float max(final float a, final float b) {
		return a > b ? a : b;
	}
}

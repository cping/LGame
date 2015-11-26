package loon.action.collision.c2d;

import loon.geom.Vector2f;
import loon.utils.NumberUtils;

public class Rectangle2D extends Polygon2D {

	private float width, height;

	private Vector2f v1;
	private Vector2f v2;
	private Vector2f v3;
	private Vector2f v4;

	private Vector2f min;
	private Vector2f max;

	public Rectangle2D() {
		this(0, 0, 0, 0);
	}

	public Rectangle2D(float x, float y, float width, float height) {
		this.width = width;
		this.height = height;

		v1 = new Vector2f();
		v2 = new Vector2f();
		v3 = new Vector2f();
		v4 = new Vector2f();

		min = new Vector2f();
		max = new Vector2f();

		setPosition(new Vector2f(x, y));

		updateVertices();
	}

	public Rectangle2D(float width, float height) {
		this(0, 0, width, height);
	}

	public Rectangle2D(Vector2f min, Vector2f max) {
		this(min.x, min.y, max.x - min.x, max.y - min.y);
	}

	private void updateVertices() {
		clearVertices();

		addVertex(v1.set(0, 0));
		addVertex(v2.set(width, 0));
		addVertex(v3.set(width, height));
		addVertex(v4.set(0, height));
	}

	public boolean intersect(Polygon2D p) {
		if (p instanceof Rectangle2D && p.getRotation() == 0
				&& getRotation() == 0) {
			Rectangle2D r = (Rectangle2D) p;

			float x = getPosition().getX();
			float y = getPosition().getY();

			float rx = r.getX();
			float ry = r.getY();

			return (x < rx + r.width) && (rx < x + width)
					&& (y < ry + r.height) && (ry < y + height);
		} else{
			return super.intersects(p);
		}
	}

	@Override
	public Rectangle2D copy() {
		return new Rectangle2D(getX(), getY(), width, height);
	}

	@Override
	public int hashCode() {
		int result = (getX() != +0.0f ? NumberUtils.floatToIntBits(getX()) : 0);
		result = 31 * result
				+ (getY() != +0.0f ? NumberUtils.floatToIntBits(getY()) : 0);
		result = 31 * result
				+ (width != +0.0f ? NumberUtils.floatToIntBits(width) : 0);
		result = 31 * result
				+ (height != +0.0f ? NumberUtils.floatToIntBits(height) : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Rectangle2D rectangle = (Rectangle2D) o;

		return NumberUtils.compare(rectangle.height, height) == 0
				&& NumberUtils.compare(rectangle.width, width) == 0
				&& NumberUtils.compare(rectangle.getX(), getX()) == 0
				&& NumberUtils.compare(rectangle.getY(), getY()) == 0;
	}

	public float getIntersectionWidth(Rectangle2D aabb) {
		if (aabb.getRotation() != 0){
			aabb = aabb.getBounds();
		}

		Rectangle2D self = getRotation() == 0 ? this : getBounds();

		float tx1 = self.getX();
		float rx1 = aabb.getX();

		float tx2 = tx1 + self.getWidth();
		float rx2 = rx1 + aabb.getWidth();

		return tx2 > rx2 ? rx2 - tx1 : tx2 - rx1;
	}

	public float getIntersectionHeight(Rectangle2D aabb) {
		if (aabb.getRotation() != 0){
			aabb = aabb.getBounds();
		}
		
		Rectangle2D self = getRotation() == 0 ? this : getBounds();

		float ty1 = self.getY();
		float ry1 = aabb.getY();

		float ty2 = ty1 + self.getHeight();
		float ry2 = ry1 + aabb.getHeight();

		return ty2 > ry2 ? ry2 - ty1 : ty2 - ry1;
	}

	public float getX() {
		return getPosition().getX();
	}

	public void setX(float x) {
		Vector2f position = getPosition();
		position.setX(x);
		setPosition(position);
	}

	public float getY() {
		return getPosition().getY();
	}

	public void setY(float y) {
		Vector2f position = getPosition();
		position.setY(y);
		setPosition(position);
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
		updateVertices();
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
		updateVertices();
	}

	public void set(float x, float y, float width, float height) {
		setPosition(x, y);

		this.width = width;
		this.height = height;

		float rotation = getRotation();
		updateVertices();

		setRotation(rotation);
	}

	public Vector2f getMin() {
		return min.set(getPosition()).addSelf(v1);
	}

	public Vector2f getMax() {
		return max.set(min).addSelf(v3);
	}

	@Override
	public String toString() {
		return "Rectangle2D{" + "width=" + width + ", height=" + height + '}';
	}

}

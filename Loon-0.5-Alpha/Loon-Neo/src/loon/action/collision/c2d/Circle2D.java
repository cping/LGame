package loon.action.collision.c2d;

import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.NumberUtils;

public class Circle2D extends Polygon2D {

	public Circle2D(float r) {
		this(0, 0, r);
	}

	public Circle2D(float x, float y, float r) {
		this(new Vector2f(x, y), r);
	}

	public Circle2D(Vector2f center, float r) {
		updateVertices(r);
		setCenter(center);
	}

	private void updateVertices(float r) {
		clearVertices();

		float x = getPosition().x;
		float y = getPosition().y;

		for (int i = 0; i < 360; i++) {
			addVertex(new Vector2f(x + r + MathUtils.cos(i) * r, y + r
					+ MathUtils.sin(i) * r));
		}
	}

	@Override
	public boolean contains(Vector2f p) {
		return (((getX() - p.getX()) * (getX() - p.getX())) + ((getY() - p
				.getY()) * (getY() - p.getY()))) < getRadius() * getRadius();
	}

	@Override
	public int hashCode() {
		int result = (getRadius() != +0.0f ? NumberUtils.floatToIntBits(getRadius())
				: 0);
		result = 31 * result
				+ (getX() != +0.0f ? NumberUtils.floatToIntBits(getX()) : 0);
		result = 31 * result
				+ (getY() != +0.0f ? NumberUtils.floatToIntBits(getY()) : 0);
		return result;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o){
			return true;
		}
		if (o == null || getClass() != o.getClass()){
			return false;
		}

		Circle2D circle =(Circle2D) o;

		return getRadius() == circle.getRadius() && getX() == circle.getX()
				&& getY() == circle.getY();
	}

	@Override
	public String toString() {
		return "Circle{" + "x=" + getX() + ", y=" + getY() + ", r="
				+ getRadius() + '}';
	}

	public float getX() {
		return getCenter().getX();
	}

	public void setX(float x) {
		Vector2f center = getCenter();
		center.setX(x);
		setCenter(center);
	}

	public float getY() {
		return getCenter().getY();
	}

	public void setY(float y) {
		Vector2f center = getCenter();
		center.setY(y);
		setCenter(center);
	}

	public float getRadius() {
		return getBounds().getWidth() / 2;
	}

	public void setRadius(float radius) {
		updateVertices(radius);
	}

}
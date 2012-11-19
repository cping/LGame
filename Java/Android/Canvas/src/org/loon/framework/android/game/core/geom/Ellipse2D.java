package org.loon.framework.android.game.core.geom;


public class Ellipse2D {

	public double x;

	public double y;

	public double width;

	public double height;

	public Ellipse2D(double x, double y, double w, double h) {
		set(x, y, w, h);
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getWidth() {
		return width;
	}

	public double getHeight() {
		return height;
	}

	public boolean isEmpty() {
		return (width <= 0.0 || height <= 0.0);
	}

	public void set(double x, double y, double w, double h) {
		this.x = x;
		this.y = y;
		this.width = w;
		this.height = h;
	}

	public RectBox getBounds2D() {
		return new RectBox(x, y, width, height);
	}

	public boolean contains(double x, double y) {
		double ellw = getWidth();
		if (ellw <= 0.0) {
			return false;
		}
		double normx = (x - getX()) / ellw - 0.5;
		double ellh = getHeight();
		if (ellh <= 0.0) {
			return false;
		}
		double normy = (y - getY()) / ellh - 0.5;
		return (normx * normx + normy * normy) < 0.25;
	}

	public boolean intersects(double x, double y, double w, double h) {
		if (w <= 0.0 || h <= 0.0) {
			return false;
		}
		double ellw = getWidth();
		if (ellw <= 0.0) {
			return false;
		}
		double normx0 = (x - getX()) / ellw - 0.5;
		double normx1 = normx0 + w / ellw;
		double ellh = getHeight();
		if (ellh <= 0.0) {
			return false;
		}
		double normy0 = (y - getY()) / ellh - 0.5;
		double normy1 = normy0 + h / ellh;
		double nearx, neary;
		if (normx0 > 0.0) {
			nearx = normx0;
		} else if (normx1 < 0.0) {
			nearx = normx1;
		} else {
			nearx = 0.0;
		}
		if (normy0 > 0.0) {
			neary = normy0;
		} else if (normy1 < 0.0) {
			neary = normy1;
		} else {
			neary = 0.0;
		}
		return (nearx * nearx + neary * neary) < 0.25;
	}

	public boolean contains(double x, double y, double w, double h) {
		return (contains(x, y) && contains(x + w, y) && contains(x, y + h) && contains(
				x + w, y + h));
	}

	public int hashCode() {
		long bits = java.lang.Double.doubleToLongBits(getX());
		bits += java.lang.Double.doubleToLongBits(getY()) * 37;
		bits += java.lang.Double.doubleToLongBits(getWidth()) * 43;
		bits += java.lang.Double.doubleToLongBits(getHeight()) * 47;
		return (((int) bits) ^ ((int) (bits >> 32)));
	}

	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Ellipse2D) {
			Ellipse2D e2d = (Ellipse2D) obj;
			return ((getX() == e2d.getX()) && (getY() == e2d.getY())
					&& (getWidth() == e2d.getWidth()) && (getHeight() == e2d
					.getHeight()));
		}
		return false;
	}
}

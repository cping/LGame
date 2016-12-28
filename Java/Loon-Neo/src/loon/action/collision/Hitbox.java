package loon.action.collision;

import loon.canvas.LColor;
import loon.geom.Polygon;
import loon.geom.Shape;
import loon.opengl.GLEx;
import loon.utils.TArray;

public class Hitbox {

	private TArray<Polygon> shapes;

	public Hitbox() {
		shapes = new TArray<Polygon>();
	}

	public Hitbox(Polygon shape) {
		shapes = new TArray<Polygon>();
		shapes.add(shape);
	}

	public void addShape(Polygon shape) {
		shapes.add(shape);
	}

	public boolean contains(Hitbox other) {
		for (Shape s : shapes) {
			for (Shape o : other.shapes) {
				if (s.contains(o)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean intersects(Hitbox other) {
		for (Shape s : shapes) {
			for (Shape o : other.shapes) {
				if (s.intersects(o)) {
					return true;
				}
			}
		}
		return false;
	}

	public void moveX(float d) {
		for (Shape s : shapes) {
			s.setX((float) (s.getX() + d));
		}
	}

	public void moveY(float d) {
		for (Shape s : shapes) {
			s.setY((float) (s.getY() + d));
		}
	}

	public void draw(GLEx g, LColor color) {
		int current = g.color();
		g.setColor(color);
		for (Shape s : shapes) {
			g.fill(s);
			g.draw(s);
		}
		g.setColor(current);
	}

	public void setX(float x) {
		for (Shape s : shapes) {
			s.setX(x);
		}
	}

	public void setY(float y) {
		for (Shape s : shapes) {
			s.setY(y);
		}
	}

	public void setCenterX(float x) {
		for (Shape s : shapes) {
			s.setCenterX(x);
		}
	}

	public void setCenterY(float y) {
		for (Polygon s : shapes) {
			s.setCenterY(y);
		}
	}

	public Hitbox copy(float dx, float dy) {
		Hitbox copy = new Hitbox();
		for (Polygon s : shapes) {
			Polygon shapeCopy = s.cpy();
			copy.addShape(shapeCopy);
		}
		copy.moveX(dx);
		copy.moveY(dy);
		return copy;
	}

}
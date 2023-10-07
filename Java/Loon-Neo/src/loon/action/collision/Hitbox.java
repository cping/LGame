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
package loon.action.collision;

import loon.LRelease;
import loon.canvas.LColor;
import loon.geom.Circle;
import loon.geom.Curve;
import loon.geom.Ellipse;
import loon.geom.Line;
import loon.geom.Point;
import loon.geom.Polygon;
import loon.geom.RectBox;
import loon.geom.Shape;
import loon.geom.Triangle2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.TArray;

/**
 * 一个碰撞盒子,可以检测任意不规则(多边形)碰撞
 */
public class Hitbox implements LRelease {

	private final TArray<Shape> _shapes;

	public Hitbox() {
		_shapes = new TArray<Shape>();
	}

	public Hitbox(Shape shape) {
		_shapes = new TArray<Shape>();
		_shapes.add(shape);
	}

	public Hitbox addShape(Shape shape) {
		_shapes.add(shape);
		return this;
	}

	public boolean removeShape(Shape shape) {
		return _shapes.remove(shape);
	}

	private boolean checkCollided(Shape src, Shape dst) {
		return src.collided(dst);
	}

	private boolean checkContainsCollision(Shape src, XY pos) {
		if (src instanceof Line) {
			return ((Line) src).contains(pos);
		} else if (src instanceof RectBox) {
			return ((RectBox) src).contains(pos);
		} else if (src instanceof Circle) {
			return ((Circle) src).contains(pos);
		} else if (src instanceof Ellipse) {
			return ((Ellipse) src).contains(pos);
		} else if (src instanceof Point) {
			return ((Point) src).contains(pos);
		} else if (src instanceof Polygon) {
			return ((Polygon) src).contains(pos);
		} else if (src instanceof Triangle2f) {
			return ((Triangle2f) src).contains(pos);
		} else if (src instanceof Curve) {
			return ((Curve) src).intersects(pos);
		}
		return src.contains(pos);
	}

	public boolean contains(XY pos) {
		for (Shape s : _shapes) {
			if (checkContainsCollision(s, pos)) {
				return true;
			}
		}
		return false;
	}

	public Shape containsResult(XY pos) {
		for (Shape s : _shapes) {
			if (checkContainsCollision(s, pos)) {
				return s;
			}
		}
		return null;
	}

	public boolean contains(Hitbox other) {
		for (Shape s : _shapes) {
			for (Shape o : other._shapes) {
				if (s.contains(o)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean collided(Shape shape) {
		for (Shape s : _shapes) {
			if (checkCollided(s, shape)) {
				return true;
			}
		}
		return false;
	}

	public Shape collidedResult(Shape shape) {
		for (Shape s : _shapes) {
			if (checkCollided(s, shape)) {
				return s;
			}
		}
		return null;
	}

	public Shape containsResult(Hitbox other) {
		for (Shape s : _shapes) {
			for (Shape o : other._shapes) {
				if (s.contains(o)) {
					return s;
				}
			}
		}
		return null;
	}

	public boolean intersects(Hitbox other) {
		for (Shape s : _shapes) {
			for (Shape o : other._shapes) {
				if (s.intersects(o)) {
					return true;
				}
			}
		}
		return false;
	}

	public Shape intersectsResult(Hitbox other) {
		for (Shape s : _shapes) {
			for (Shape o : other._shapes) {
				if (s.intersects(o)) {
					return s;
				}
			}
		}
		return null;
	}

	public Hitbox move(float x, float y) {
		for (Shape s : _shapes) {
			s.setLocation(s.getX() + x, s.getY() + y);
		}
		return this;
	}

	public Hitbox moveX(float x) {
		for (Shape s : _shapes) {
			s.setX(s.getX() + x);
		}
		return this;
	}

	public Hitbox moveY(float y) {
		for (Shape s : _shapes) {
			s.setY(s.getY() + y);
		}
		return this;
	}

	public void draw(GLEx g, LColor c) {
		this.draw(g, c);
	}

	public void draw(GLEx g, LColor fillColor, LColor drawColor) {
		final int current = g.color();
		for (Shape s : _shapes) {
			g.setColor(fillColor);
			g.fill(s);
			g.setColor(drawColor);
			g.draw(s);
		}
		g.setColor(current);
	}

	public Hitbox setX(float x) {
		for (Shape s : _shapes) {
			s.setX(x);
		}
		return this;
	}

	public Hitbox setY(float y) {
		for (Shape s : _shapes) {
			s.setY(y);
		}
		return this;
	}

	public Hitbox setCenterX(float x) {
		for (Shape s : _shapes) {
			s.setCenterX(x);
		}
		return this;
	}

	public Hitbox setCenterY(float y) {
		for (Shape s : _shapes) {
			s.setCenterY(y);
		}
		return this;
	}

	public Hitbox copy(float dx, float dy) {
		Hitbox copy = new Hitbox();
		for (Shape s : _shapes) {
			Shape shapeCopy = s.cpy();
			copy.addShape(shapeCopy);
		}
		copy.moveX(dx);
		copy.moveY(dy);
		return copy;
	}

	@Override
	public void close() {
		_shapes.clear();
	}

}
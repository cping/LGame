package loon.action.sprite;

import loon.canvas.LColor;
import loon.geom.Shape;
import loon.opengl.GLEx;

public class ShapeEntity extends Entity {

	private Shape _shape;

	private boolean _fill;

	public ShapeEntity(Shape shape, LColor c, boolean fill) {
		this.setLocation(shape.getX(), shape.getY());
		this.setSize(shape.getWidth(), shape.getHeight());
		this.setColor(c == null ? LColor.white : c);
		this.setRepaint(true);
		this._fill = fill;
		this._shape = shape;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		int color = g.color();
		g.setColor(_baseColor);
		if (_fill) {
			g.fill(_shape, offsetX, offsetY);
		} else {
			g.draw(_shape, offsetX, offsetY);
		}
		g.setColor(color);
	}

	public void setShape(Shape s) {
		this._shape = s;
	}

	public Shape getShape() {
		return _shape;
	}

}

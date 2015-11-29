package loon.opengl;

import loon.canvas.LColor;

public class Paint {

	public enum Gradient {
		LINEAR_TOP_TO_BOTTOM, LINEAR_LEFT_TO_RIGHT, DIAGONAL_LEFT_TO_RIGHT, DIAGONAL_RIGHT_TO_LEFT
	}

	private LColor topLeft;
	private LColor topRight;
	private LColor bottomLeft;
	private LColor bottomRight;

	public Paint(Paint paint) {
		this();
		setColor(paint);
	}

	public Paint(LColor topLeft, LColor topRight, LColor bottomLeft,
			LColor bottomRight) {
		this.topLeft = topLeft.cpy();
		this.topRight = topRight.cpy();
		this.bottomLeft = bottomLeft.cpy();
		this.bottomRight = bottomRight.cpy();
	}

	public Paint(LColor color) {
		this(color, color, color, color);
	}

	public Paint() {
		this(LColor.white);
	}

	public Paint(LColor c1, LColor c2, Gradient gradient) {
		this();

		switch (gradient) {
		case LINEAR_TOP_TO_BOTTOM:
			setTopLeftColor(c1);
			setTopRightColor(c1);
			setBottomLeftColor(c2);
			setBottomRightColor(c2);
			break;

		case LINEAR_LEFT_TO_RIGHT:
			setTopLeftColor(c1);
			setTopRightColor(c2);
			setBottomLeftColor(c1);
			setBottomRightColor(c2);
			break;

		case DIAGONAL_LEFT_TO_RIGHT:
			setTopLeftColor(c1);
			setTopRightColor(c2);
			setBottomLeftColor(c2);
			setBottomRightColor(c1);
			break;

		case DIAGONAL_RIGHT_TO_LEFT:
			setTopLeftColor(c2);
			setTopRightColor(c1);
			setBottomLeftColor(c1);
			setBottomRightColor(c2);
			break;
		}
	}

	public LColor getTopLeftColor() {
		return topLeft;
	}

	public void setTopLeftColor(LColor topLeft) {
		this.topLeft.setColor(topLeft);
	}

	public LColor getTopRightColor() {
		return topRight;
	}

	public void setTopRightColor(LColor topRight) {
		this.topRight.setColor(topRight);
	}

	public LColor getBottomLeftColor() {
		return bottomLeft;
	}

	public void setBottomLeftColor(LColor bottomLeft) {
		this.bottomLeft.setColor(bottomLeft);
	}

	public LColor getBottomRightColor() {
		return bottomRight;
	}

	public void setBottomRightColor(LColor bottomRight) {
		this.bottomRight.setColor(bottomRight);
	}

	public void setColor(LColor color) {
		setTopLeftColor(color);
		setTopRightColor(color);
		setBottomLeftColor(color);
		setBottomRightColor(color);
	}

	public void setColor(Paint paint) {
		setTopLeftColor(paint.getTopLeftColor());
		setTopRightColor(paint.getTopRightColor());
		setBottomLeftColor(paint.getBottomLeftColor());
		setBottomRightColor(paint.getBottomRightColor());
	}

}

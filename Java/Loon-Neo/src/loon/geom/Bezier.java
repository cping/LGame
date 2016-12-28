package loon.geom;

import loon.geom.Bezier;

public class Bezier {

	public Vector2f endPosition = new Vector2f();

	public Vector2f controlPoint1 = new Vector2f();

	public Vector2f controlPoint2 = new Vector2f();

	public Bezier() {

	}

	public Bezier(float cp1x, float cp1y, float cp2x, float cp2y, float endx,
			float endy) {
		this(Vector2f.at(cp1x, cp1y), Vector2f.at(cp2x, cp2y), Vector2f.at(
				endx, endy));
	}

	public Bezier(Vector2f controlPos1, Vector2f controlPos2, Vector2f endPos) {
		controlPoint1.set(controlPos1);
		controlPoint2.set(controlPos2);
		endPosition.set(endPos);
	}

	public Vector2f getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(float x, float y) {
		setEndPosition(Vector2f.at(x, y));
	}

	public void setEndPosition(Vector2f endPosition) {
		this.endPosition = endPosition;
	}

	public Vector2f getControlPoint1() {
		return controlPoint1;
	}

	public void setControlPoint1(float x, float y) {
		setControlPoint1(Vector2f.at(x, y));
	}

	public void setControlPoint1(Vector2f controlPoint1) {
		this.controlPoint1 = controlPoint1;
	}

	public Vector2f getControlPoint2() {
		return controlPoint2;
	}

	public void setControlPoint2(float x, float y) {
		setControlPoint2(Vector2f.at(x, y));
	}

	public void setControlPoint2(Vector2f controlPoint2) {
		this.controlPoint2 = controlPoint2;
	}

	public Bezier cpy() {
		return new Bezier(controlPoint1, controlPoint2, endPosition);
	}
}

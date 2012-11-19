package org.test;

import loon.core.geom.Vector2f;
import loon.utils.MathUtils;

public final class Trigonometry {
	public static float getAngle(Vector2f vector1) {
		return MathUtils.atan2(vector1.y, vector1.x);
	}

	public static Vector2f getDirection(float angle) {
		return new Vector2f(MathUtils.cos(angle), MathUtils.sin(angle));
	}
}
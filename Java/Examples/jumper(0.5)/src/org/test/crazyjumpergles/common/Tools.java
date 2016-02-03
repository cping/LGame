package org.test.crazyjumpergles.common;

import loon.geom.RectBox;
import loon.utils.MathUtils;
import loon.utils.RefObject;

public final class Tools {
	
	public static float MAX_FLOAT = 3.4E+28f;

	public static void ClampAngle(RefObject<Float> angle)
	{
		if (angle.argvalue < 0f)
		{
			angle.argvalue += 360f;
		}
		else if (angle.argvalue > 360f)
		{
			angle.argvalue -= 360f;
		}
		else if (angle.argvalue == 360f)
		{
			angle.argvalue = 0f;
		}
	}
	
	public static int getRand(int min, int max) {
		return MathUtils.random(min, max);
	}

	public static float getRandF(float min, float max) {
		return MathUtils.random(min, max);
	}

	public static boolean isCircleIntersectingRect(float cX, float cY,
			float cRadius, float rcLeft, float rcRight, float rcTop,
			float rcBottom) {
		float num = MathUtils.clamp(cX, rcLeft, rcRight);
		float num2 = MathUtils.clamp(cY, rcTop, rcBottom);
		float num3 = cX - num;
		float num4 = cY - num2;
		float num5 = (num3 * num3) + (num4 * num4);
		return (num5 < (cRadius * cRadius));
	}

	public static boolean isIntersectingRect(float ax, float ay, float aw,
			float ah, float bx, float by, float bw, float bh) {
		return (((by <= (ay + ah)) && ((by + bh) >= ay)) && (((bx + bw) >= ax) && (bx <= (ax + aw))));
	}

	public static boolean IsInvalidRect(RectBox rc) {
		return (rc == null)
				|| (((rc.x == 0) && (rc.y == 0)) && ((rc.width == 0) && (rc.height == 0)));
	}

	public static RectBox MakeInvalidRect() {
		return new RectBox();
	}
}
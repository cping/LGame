package loon.action;

import loon.utils.MathUtils;

public class ActionSmooth implements ActionPath {
	
	@Override
	public float compute(float t, float[] points, int pointsSize) {
		int segment = MathUtils.floor((pointsSize-1) * t);
		segment = MathUtils.max(segment, 0);
		segment = MathUtils.min(segment, pointsSize-2);

		t = t * (pointsSize-1) - segment;

		if (segment == 0) {
			return catmullRomSpline(points[0], points[0], points[1], points[2], t);
		}

		if (segment == pointsSize-2) {
			return catmullRomSpline(points[pointsSize-3], points[pointsSize-2], points[pointsSize-1], points[pointsSize-1], t);
		}

		return catmullRomSpline(points[segment-1], points[segment], points[segment+1], points[segment+2], t);
	}

	private float catmullRomSpline(float a, float b, float c, float d, float t) {
		float t1 = (c - a) * 0.5f;
		float t2 = (d - b) * 0.5f;

		float h1 = +2 * t * t * t - 3 * t * t + 1;
		float h2 = -2 * t * t * t + 3 * t * t;
		float h3 = t * t * t - 2 * t * t + t;
		float h4 = t * t * t - t * t;

		return b * h1 + c * h2 + t1 * h3 + t2 * h4;
	}
}

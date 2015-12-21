package loon.action;

import loon.utils.MathUtils;

public class ActionLinear implements ActionPath {
	
	@Override
	public float compute(float t, float[] points, int pointsCnt) {
		int segment =  MathUtils.floor((pointsCnt-1) * t);
		segment = MathUtils.max(segment, 0);
		segment = MathUtils.min(segment, pointsCnt-2);

		t = t * (pointsCnt-1) - segment;

		return points[segment] + t * (points[segment+1] - points[segment]);
	}
	
}

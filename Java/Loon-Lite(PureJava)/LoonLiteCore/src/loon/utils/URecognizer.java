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
package loon.utils;

import loon.geom.PointF;
import loon.geom.RectF;
import loon.utils.ObjectMap.Entries;
import loon.utils.ObjectMap.Entry;

/**
 * UnistrokeRecognizer手势识别
 */
public class URecognizer {

	public final static int GESTURES_DEFAULT = 0;

	public final static int GESTURES_CIRCLES = 1;

	public final static int GESTURES_NONE = 2;
	
	public final static int MXA_POINTS = 64;

	public final static float MAX_SQUARE_COUNT = 200f;

	protected float angleRange = 45f;

	protected float anglePre = 2f;

	protected float HalfDiagonal = 176.776f;

	protected PointF centroid = new PointF();

	protected RectF boundingBox = new RectF();

	protected int bounds[] = { 0, 0, 0, 0 };

	protected TArray<URecognizerObject> objects = new TArray<URecognizerObject>(MXA_POINTS);

	private GestureData gestureData;

	public URecognizer() {
		this(GESTURES_DEFAULT);
	}

	public URecognizer(int type) {
		this(new GestureData(), type);
	}

	public URecognizer(GestureData data, int type) {
		this.gestureData = data;
		gestureData.init();
		switch (type) {
		case GESTURES_DEFAULT:
			loadObjectsDefault();
			break;
		case GESTURES_CIRCLES:
			loadObjectsCircles();
			break;
		}
		if (data.userPoints != null) {
			for (Entries<String, TArray<PointF>> en = data.userPoints.iterator(); en.hasNext();) {
				Entry<String, TArray<PointF>> entry = en.next();
				if (entry.key != null && entry.value != null) {
					objects.add(loadObject(entry.key, entry.value));
				}
			}
		}
	}

	/**
	 * 默认手势，只能识别三角，圆，对勾，五芒星之类简单图像,有需要的请通过GestureData,setUserPoints注入自己的采样数据
	 */
	void loadObjectsDefault() {
		objects.add(loadObject("triangle", gestureData.trianglePoints));
		objects.add(loadObject("x", gestureData.xPoints));
		objects.add(loadObject("rectangle CCW", gestureData.rectangleCCWPoints));
		objects.add(loadObject("circle CCW", gestureData.circleCCWPoints));
		objects.add(loadObject("check", gestureData.checkPoints));
		objects.add(loadObject("caret CW", gestureData.caretCWPoints));
		objects.add(loadObject("question", gestureData.questionPoints));
		objects.add(loadObject("arrow", gestureData.arrowPoints));
		objects.add(loadObject("leftSquareBracket", gestureData.leftSquareBracketPoints));
		objects.add(loadObject("rightSquareBracket", gestureData.rightSquareBracketPoints));
		objects.add(loadObject("v", gestureData.vPoints));
		objects.add(loadObject("delete", gestureData.deletePoints));
		objects.add(loadObject("leftCurlyBrace", gestureData.leftCurlyBracePoints));
		objects.add(loadObject("rightCurlyBrace", gestureData.rightCurlyBracePoints));
		objects.add(loadObject("star", gestureData.starPoints));
		objects.add(loadObject("pigTail", gestureData.pigTailPoints));
	}

	void loadObjectsCircles() {
		objects.add(loadObject("circle CCW", gestureData.circleCCWPoints));
		objects.add(loadObject("circle CW", gestureData.circleCWPoints));
		objects.add(loadObject("caret CCW", gestureData.caretCCWPoints));
		objects.add(loadObject("caret CW", gestureData.caretCWPoints));
		objects.add(loadObject("question", gestureData.questionPoints));
	}

	public URecognizerObject loadObject(String name, int[] array) {
		return loadObject(name, loadArray(array));
	}

	public URecognizerObject loadObject(String name, TArray<PointF> points) {
		return new URecognizerObject(name, points);
	}

	public static TArray<PointF> loadArray(int[] array) {
		TArray<PointF> v = new TArray<PointF>(array.length / 2);
		for (int i = 0; i < array.length; i += 2) {
			PointF p = new PointF(array[i], array[i + 1]);
			v.add(p);
		}
		return v;
	}

	public URecognizerResult getRecognize(TArray<PointF> points) {
		points = loadResample(points, MXA_POINTS);
		points = loadRotateToZero(points, centroid, boundingBox);
		points = loadScaleToSquare(points, MAX_SQUARE_COUNT);
		points = loadTranslateToOrigin(points);

		bounds[0] = (int) boundingBox.x;
		bounds[1] = (int) boundingBox.y;
		bounds[2] = (int) (boundingBox.x + boundingBox.width);
		bounds[3] = (int) (boundingBox.y + boundingBox.height);

		int t = 0;

		float b = Float.MAX_VALUE;
		for (int i = 0; i < objects.size(); i++) {
			float d = getDistanceAtBestAngle(points, (URecognizerObject) objects.get(i), -angleRange, angleRange,
					anglePre);
			if (d < b) {
				b = d;
				t = i;
			}
		}
		float score = 1f - (b / HalfDiagonal);
		return new URecognizerResult((objects.get(t)).getName(), score, t, _lastTheta);
	}

	private static float _lastTheta;

	public static float getLastTheta() {
		return _lastTheta;
	}

	public static TArray<PointF> loadResample(TArray<PointF> points, int n) {
		float I = getPathLength(points) / (n - 1);
		float fv = 0f;

		TArray<PointF> srcPts = new TArray<PointF>(points.size());
		for (int i = 0; i < points.size(); i++) {
			srcPts.add(points.get(i));
		}

		TArray<PointF> dstPts = new TArray<PointF>(n);
		dstPts.add(srcPts.get(0));

		for (int i = 1; i < srcPts.size(); i++) {
			PointF pt1 = srcPts.get(i - 1);
			PointF pt2 = srcPts.get(i);

			float d = getDistance(pt1, pt2);
			if ((fv + d) >= I) {
				float qx = pt1.x + ((I - fv) / d) * (pt2.x - pt1.x);
				float qy = pt1.y + ((I - fv) / d) * (pt2.y - pt1.y);
				PointF q = new PointF(qx, qy);
				dstPts.add(q);
				srcPts.insert(i, q);
				fv = 0f;
			} else {
				fv += d;
			}
		}
		if (dstPts.size() == n - 1) {
			dstPts.add(srcPts.get(srcPts.size() - 1));
		}

		return dstPts;
	}

	public static TArray<PointF> loadRotateToZero(TArray<PointF> points) {
		return loadRotateToZero(points, null, null);
	}

	public static TArray<PointF> loadRotateToZero(TArray<PointF> points, PointF centroid, RectF boundingBox) {
		PointF c = getCentroid(points);
		PointF first = points.get(0);
		float theta = MathUtils.atan2(c.y - first.y, c.x - first.x);

		if (centroid != null) {
			centroid.set(c);
		}
		if (boundingBox != null) {
			getBoundingBox(points, boundingBox);
		}
		_lastTheta = theta;
		return getRotateBy(points, -theta);
	}

	public static TArray<PointF> getRotateBy(TArray<PointF> points, float theta) {
		return getRotateByRadians(points, theta);
	}

	public static TArray<PointF> getRotateByRadians(TArray<PointF> points, float radians) {
		TArray<PointF> newPoints = new TArray<PointF>(points.size());
		PointF c = getCentroid(points);

		float cos = MathUtils.cos(radians);
		float sin = MathUtils.sin(radians);

		float cx = c.x;
		float cy = c.y;

		for (int i = 0; i < points.size(); i++) {
			PointF p = points.get(i);

			float dx = p.x - cx;
			float dy = p.y - cy;

			newPoints.add(new PointF(dx * cos - dy * sin + cx, dx * sin + dy * cos + cy));
		}
		return newPoints;
	}

	public static TArray<PointF> loadScaleToSquare(TArray<PointF> points, float size) {
		return getScaleToSquare(points, size, null);
	}

	public static TArray<PointF> getScaleToSquare(TArray<PointF> points, float size, RectF boundingBox) {
		RectF B = getBoundingBox(points);
		TArray<PointF> newpoints = new TArray<PointF>(points.size());
		for (int i = 0; i < points.size(); i++) {
			PointF p = points.get(i);
			float qx = p.x * (size / B.width);
			float qy = p.y * (size / B.height);
			newpoints.add(new PointF(qx, qy));
		}

		if (boundingBox != null) {
			boundingBox.set(B);
		}
		return newpoints;
	}

	public static TArray<PointF> loadTranslateToOrigin(TArray<PointF> points) {
		PointF c = getCentroid(points);
		TArray<PointF> newpoints = new TArray<PointF>(points.size());
		for (int i = 0; i < points.size(); i++) {
			PointF p = points.get(i);
			float qx = p.x - c.x;
			float qy = p.y - c.y;
			newpoints.add(new PointF(qx, qy));
		}
		return newpoints;
	}

	public static float getDistanceAtBestAngle(TArray<PointF> points, URecognizerObject o, float a, float b,
			float threshold) {
		float Phi = MathUtils.PHI;

		float x1 = Phi * a + (1f - Phi) * b;
		float f1 = getDistanceAtAngle(points, o, x1);
		float x2 = (1f - Phi) * a + Phi * b;
		float f2 = getDistanceAtAngle(points, o, x2);

		for (; MathUtils.abs(b - a) > threshold;) {
			if (f1 < f2) {
				b = x2;
				x2 = x1;
				f2 = f1;
				x1 = Phi * a + (1f - Phi) * b;
				f1 = getDistanceAtAngle(points, o, x1);
			} else {
				a = x1;
				x1 = x2;
				f1 = f2;
				x2 = (1f - Phi) * a + Phi * b;
				f2 = getDistanceAtAngle(points, o, x2);
			}
		}
		return MathUtils.min(f1, f2);
	}

	public static float getDistanceAtAngle(TArray<PointF> points, URecognizerObject o, float theta) {
		TArray<PointF> newpoints = getRotateBy(points, theta);
		return getPathDistance(newpoints, o.getPoints());
	}

	public static RectF getBoundingBox(TArray<PointF> points) {
		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;

		for (PointF p : points) {

			if (p.x < minX) {
				minX = p.x;
			}
			if (p.x > maxX) {
				maxX = p.x;
			}

			if (p.y < minY) {
				minY = p.y;
			}
			if (p.y > maxY) {
				maxY = p.y;
			}
		}

		return new RectF(minX, minY, maxX - minX, maxY - minY);
	}

	public static void getBoundingBox(TArray<PointF> points, RectF dst) {

		float minX = Float.MAX_VALUE;
		float maxX = Float.MIN_VALUE;
		float minY = Float.MAX_VALUE;
		float maxY = Float.MIN_VALUE;

		for (PointF p : points) {

			if (p.x < minX)
				minX = p.x;
			if (p.x > maxX)
				maxX = p.x;

			if (p.y < minY)
				minY = p.y;
			if (p.y > maxY)
				maxY = p.y;
		}

		dst.x = minX;
		dst.y = minY;
		dst.width = maxX - minX;
		dst.height = maxY - minY;
	}

	public static float getDistance(PointF p1, PointF p2) {
		float dx = p2.x - p1.x;
		float dy = p2.y - p1.y;
		return MathUtils.sqrt(dx * dx + dy * dy);
	}

	public static PointF getCentroid(TArray<PointF> points) {
		float xsum = 0f;
		float ysum = 0f;

		for (PointF p : points) {
			xsum += p.x;
			ysum += p.y;
		}
		return new PointF(xsum / points.size(), ysum / points.size());
	}

	public static float getPathLength(TArray<PointF> points) {
		float length = 0;
		for (int i = 1; i < points.size(); i++) {
			length += getDistance(points.get(i - 1), points.get(i));
		}
		return length;
	}

	public static float getPathDistance(TArray<PointF> patha, TArray<PointF> pathb) {
		float distance = 0;
		for (int i = 0; i < patha.size(); i++) {
			distance += getDistance(patha.get(i), pathb.get(i));
		}
		return distance / patha.size();
	}
}

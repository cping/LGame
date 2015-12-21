/**
 * 
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.geom;

import loon.utils.MathUtils;
import loon.utils.NumberUtils;
import loon.utils.TArray;

public class Ellipse extends Shape {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected static final int DEFAULT_SEGMENT_MAX_COUNT = 50;

	private int segmentCount;

	private float radius1;

	private float radius2;

	public Ellipse(float centerPointX, float centerPointY, float radius1,
			float radius2) {
		this.set(centerPointX, centerPointY, radius1, radius2);
	}

	public Ellipse(float centerPointX, float centerPointY, float radius1,
			float radius2, int segmentCount) {
		this.set(centerPointX, centerPointY, radius1, radius2, segmentCount);
	}

	public void set(float centerPointX, float centerPointY, float radius1,
			float radius2) {
		set(centerPointX, centerPointY, radius1, radius2,
				DEFAULT_SEGMENT_MAX_COUNT);
	}

	public void set(float centerPointX, float centerPointY, float radius1,
			float radius2, int segmentCount) {
		this.x = centerPointX - radius1;
		this.y = centerPointY - radius2;
		this.radius1 = radius1;
		this.radius2 = radius2;
		this.segmentCount = segmentCount;
		checkPoints();
	}

	/**
	 * 设定当前椭圆形半径
	 * 
	 * @param radius1
	 * @param radius2
	 */
	public void setRadii(float radius1, float radius2) {
		setRadius1(radius1);
		setRadius2(radius2);
	}

	public float getRadius1() {
		return radius1;
	}

	public void setRadius1(float radius1) {
		if (radius1 != this.radius1) {
			this.radius1 = radius1;
			pointsDirty = true;
		}
	}

	public float getRadius2() {
		return radius2;
	}

	public void setRadius2(float radius2) {
		if (radius2 != this.radius2) {
			this.radius2 = radius2;
			pointsDirty = true;
		}
	}

	protected void createPoints() {
		TArray<Float> tempPoints = new TArray<Float>();

		maxX = -Float.MIN_VALUE;
		maxY = -Float.MIN_VALUE;
		minX = Float.MAX_VALUE;
		minY = Float.MAX_VALUE;

		float start = 0;
		float end = 359;

		float cx = x + radius1;
		float cy = y + radius2;

		int step = 360 / segmentCount;

		for (float a = start; a <= end + step; a += step) {
			float ang = a;
			if (ang > end) {
				ang = end;
			}
			float newX = (cx + (MathUtils.cos(MathUtils.toRadians(ang)) * radius1));
			float newY = (cy + (MathUtils.sin(MathUtils.toRadians(ang)) * radius2));

			if (newX > maxX) {
				maxX = newX;
			}
			if (newY > maxY) {
				maxY = newY;
			}
			if (newX < minX) {
				minX = newX;
			}
			if (newY < minY) {
				minY = newY;
			}

			tempPoints.add(newX);
			tempPoints.add(newY);
		}
		points = new float[tempPoints.size];
		for (int i = 0; i < points.length; i++) {
			points[i] = tempPoints.get(i);
		}
	}

	protected void findCenter() {
		center = new float[2];
		center[0] = x + radius1;
		center[1] = y + radius2;
	}

	protected void calculateRadius() {
		boundingCircleRadius = (radius1 > radius2) ? radius1 : radius2;
	}

	public int hashCode() {
		int bits = NumberUtils.floatToIntBits(getX());
		bits += NumberUtils.floatToIntBits(getY()) * 37;
		bits += NumberUtils.floatToIntBits(getWidth()) * 43;
		bits += NumberUtils.floatToIntBits(getHeight()) * 47;
		return bits ^ ((bits >> 32));
	}

	public Shape transform(Matrix3 transform) {
		checkPoints();

		Polygon resultPolygon = new Polygon();

		float result[] = new float[points.length];
		transform.transform(points, 0, result, 0, points.length / 2);
		resultPolygon.points = result;
		resultPolygon.checkPoints();

		return resultPolygon;
	}

}

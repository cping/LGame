/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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

public class URecognizerAnalyze {

	protected int _tx, _ty;

	protected int state;

	protected int _key = -1;

	protected boolean gesture = true;

	protected TArray<PointF> points = new TArray<PointF>(256);

	protected URecognizerResult result = new URecognizerResult("unkown", 0, -1);

	protected boolean active = false;

	protected int gestureSet;

	protected URecognizer recognizer;

	public URecognizerAnalyze() {
		this(URecognizer.GESTURES_DEFAULT);
	}

	public URecognizerAnalyze(int gesture) {
		this.gestureSet = gesture;
		this.recognizer = new URecognizer(gesture);
	}

	public TArray<PointF> getPoints() {
		return points;
	}

	public void addPoint(float x, float y) {
		if (!active) {
			return;
		}
		points.add(new PointF(x, y));
	}

	public void addPoints(TArray<PointF> points) {
		if (!active) {
			return;
		}
		points.addAll(points);
	}

	public URecognizerResult recognize() {
		if (!active) {
			return null;
		}
		if (points.size() == 0) {
			return null;
		}
		return (result = recognizer.getRecognize(points));
	}

	public RectF getBoundingBox() {
		return recognizer.boundingBox;
	}

	public int[] getBounds() {
		return recognizer.bounds;
	}

	public PointF getPosition() {
		return recognizer.centroid;
	}

	public String getName() {
		return result.getName();
	}

	public float getScore() {
		return result.getScore();
	}

	public int getIndex() {
		return result.getIndex();
	}

	public void setActive(boolean state) {
		active = state;
	}

	public boolean getActive() {
		return active;
	}

	public void pressed() {
		clear();
	}

	public void released() {
		recognize();
	}

	public void dragged(int x, int y) {
		addPoint(x, y);
	}

	public void clear() {
		points.clear();
		result._name = "";
		result._score = 0;
		result._index = -1;
	}

}

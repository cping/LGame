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

public class GestureLoader {

	protected String name;

	protected TArray<PointF> points = new TArray<PointF>();

	protected boolean resampleFirst;

	public GestureLoader(String name, TArray<PointF> points, boolean resampleFirst) {
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;
	}

	public GestureLoader(String name, TArray<PointF> points, float squareSize, int num, boolean resampleFirst) {
		this.name = name;
		this.points = points;
		this.resampleFirst = resampleFirst;

		if (resampleFirst) {
			this.points = URecognizer.loadResample(this.points, num);
		}

		this.points = URecognizer.loadRotateToZero(this.points);
		this.points = URecognizer.loadScaleToSquare(this.points, squareSize);
		this.points = URecognizer.loadTranslateToOrigin(this.points);

		if (!resampleFirst) {
			this.points = URecognizer.loadResample(this.points, num);
		}
	}

	public String getName() {
		return name;
	}
}

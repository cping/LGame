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
package loon.geom;

import loon.geom.Bezier;
import loon.utils.StringKeyValue;

public class Bezier {

	public Vector2f endPosition = new Vector2f();

	public Vector2f controlPoint1 = new Vector2f();

	public Vector2f controlPoint2 = new Vector2f();

	public Bezier() {
		this(0f, 0f, 0f, 0f, 0f, 0f);
	}

	public Bezier(float cp1x, float cp1y, float cp2x, float cp2y, float endx, float endy) {
		this(Vector2f.at(cp1x, cp1y), Vector2f.at(cp2x, cp2y), Vector2f.at(endx, endy));
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

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("Bezier");
		builder.kv("controlPoint1", controlPoint1)
		.comma()
		.kv("controlPoint2", controlPoint2)
		.comma()
		.kv("endPosition", endPosition);
		return builder.toString();
	}
}

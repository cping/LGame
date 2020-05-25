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
package loon.physics;

import loon.utils.MathUtils;

public class Utils {

	public static PPolygon getPPolygon(float[] points, float scale) {
		return new PPolygon(points, scale);
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] data, int newSize) {
		PConvexPolygonShape tempArr[] = new PConvexPolygonShape[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PConvexPolygonShape[] copyOf(PConvexPolygonShape[] data) {
		return copyOf(data, data.length);
	}

	public static PBody[] copyOf(PBody[] data, int newSize) {
		PBody tempArr[] = new PBody[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PBody[] copyOf(PBody[] data) {
		return copyOf(data, data.length);
	}

	public static PJoint[] copyOf(PJoint[] data) {
		return copyOf(data, data.length);
	}

	public static PJoint[] copyOf(PJoint[] data, int newSize) {
		PJoint tempArr[] = new PJoint[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PSolver[] copyOf(PSolver[] data) {
		return copyOf(data, data.length);
	}

	public static PSolver[] copyOf(PSolver[] data, int newSize) {
		PSolver tempArr[] = new PSolver[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PShape[] copyOf(PShape[] data) {
		return copyOf(data, data.length);
	}

	public static PShape[] copyOf(PShape[] data, int newSize) {
		PShape tempArr[] = new PShape[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

	public static PSortableObject[] copyOf(PSortableObject[] data) {
		return copyOf(data, data.length);
	}

	public static PSortableObject[] copyOf(PSortableObject[] data, int newSize) {
		PSortableObject tempArr[] = new PSortableObject[newSize];
		System.arraycopy(data, 0, tempArr, 0, MathUtils.min(data.length, newSize));
		return tempArr;
	}

}

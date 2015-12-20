/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.physics;

public class PPolygonDrawer {

	private final int maxVertices = 1024;
	public int numVertices;
	public float[] xs;
	public float[] ys;

	public PPolygonDrawer() {
		this(1024);
	}

	public PPolygonDrawer(int size) {
		xs = new float[size];
		ys = new float[size];
	}

	public void reset() {
		numVertices = 0;
	}

	public void addVertex(float wx, float wy) {
		if (numVertices == maxVertices) {
			return;
		} else {
			xs[numVertices] = wx;
			ys[numVertices] = wy;
			numVertices++;
			return;
		}
	}

}

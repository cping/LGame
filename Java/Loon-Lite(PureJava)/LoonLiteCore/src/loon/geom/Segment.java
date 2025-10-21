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
package loon.geom;

public class Segment {
	
	public final Vector3f _start = new Vector3f();

	public final Vector3f _end = new Vector3f();

	public Segment(Vector3f a, Vector3f b) {
		this._start.set(a);
		this._end.set(b);
	}

	public Segment(float x1, float y1, float z1, float x2, float y2, float z2) {
		this._start.set(x1, y1, z1);
		this._end.set(x2, y2, z2);
	}

	public float len() {
		return this._start.dst(this._end);
	}

	public float len2() {
		return this._start.dst2(this._end);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (o == null || o.getClass() != getClass()) {
			return false;
		}
		Segment s = (Segment) o;
		return (this._start.equals(s._start) && this._end.equals(s._end));
	}

	@Override
	public int hashCode() {
		int result = 86;
		result = 86 * result + this._start.hashCode();
		result = 86 * result + this._end.hashCode();
		return result;
	}
}

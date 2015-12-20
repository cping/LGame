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

import loon.core.geom.AABB;

public class PSortableAABB {

	AABB aabb;
	PSortableObject beginX;
	PSortableObject beginY;
	PSortableObject endX;
	PSortableObject endY;
	PShape parent;
	PSweepAndPrune sap;
	boolean set;

	public PSortableAABB() {
	}

	void remove() {
		if (!set) {
			return;
		} else {
			sap.removeObject(beginX, beginY);
			sap.removeObject(endX, endY);
			return;
		}
	}

	public void set(PSweepAndPrune sap, PShape s, AABB aabb) {
		set = true;
		this.sap = sap;
		parent = s;
		this.aabb = aabb;
		beginX = new PSortableObject(s, this, aabb.minX, true);
		beginY = new PSortableObject(s, this, aabb.minY, true);
		endX = new PSortableObject(s, this, aabb.maxX, false);
		endY = new PSortableObject(s, this, aabb.maxY, false);
		sap.addObject(beginX, beginY);
		sap.addObject(endX, endY);
	}

	void update() {
		if (!set) {
			return;
		} else {
			beginX.value = aabb.minX;
			beginY.value = aabb.minY;
			endX.value = aabb.maxX;
			endY.value = aabb.maxY;
			return;
		}
	}

}

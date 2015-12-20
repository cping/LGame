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

public class PSortableObject {

	PSortableAABB aabb;
	boolean begin;
	PShape parent;
	float value;

	public PSortableObject(PShape s, PSortableAABB aabb, float value,
			boolean begin) {
		this.parent = s;
		this.aabb = aabb;
		this.value = value;
		this.begin = begin;
	}

}

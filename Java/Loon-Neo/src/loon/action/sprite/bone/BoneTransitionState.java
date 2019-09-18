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
package loon.action.sprite.bone;

import loon.geom.Vector2f;

public class BoneTransitionState {

	protected Vector2f position;

	protected float rotation;

	public BoneTransitionState() {
		this(0, 0);
	}

	public BoneTransitionState(float x, float y) {
		this.setPosition(x, y);
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		this.position = new Vector2f(x, y);
	}

	public float getRotation() {
		return rotation;
	}

	public void setRotation(float r) {
		this.rotation = r;
	}

}

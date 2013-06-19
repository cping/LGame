/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action;

import loon.action.map.Field2D;
import loon.core.LSystem;
import loon.utils.MathUtils;

//0.3.3新增动作，让指定对象做弓箭射出状（抛物线）
public class ArrowTo extends ActionEvent {

	private float gravity = 200;

	private float startX;
	private float startY;

	private float endX;
	private float endY;

	private float vx;
	private float vy;

	private float speed;

	private int dir;

	public ArrowTo(float tx, float ty, float speed, float g) {
		this.endX = tx;
		this.endY = ty;
		this.speed = speed;
		this.gravity = g;
		this.speed = speed;
	}

	public ArrowTo(float tx, float ty) {
		this(tx, ty, 3f, 200f);
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	@Override
	public void onLoad() {
		this.startX = original.getX();
		this.startY = original.getY();
		float dx = endX - startX;
		float dy = endY - startY;
		this.vx = dx / speed;
		this.vy = 1 / speed * (dy - 1.0f / 2.0f * gravity * speed * speed);
		this.dir = Field2D.getDirection(MathUtils.atan2(endX - startX, endY
				- startY));
	}

	@Override
	public void update(long elapsedTime) {
		float dt = MathUtils.min(elapsedTime / 1000f, 0.1f);
		vy += gravity * dt;
		startX += vx * dt;
		startY += vy * dt;
		if (original.isContainer() && original.isBounded()) {
			if (startX < -original.getWidth() || startY < -original.getHeight()
					|| startX > original.getContainerWidth()
					|| startY > original.getContainerHeight()) {
				isComplete = true;
			}
		} else if (startX < -original.getWidth() * 2
				|| startY < -original.getHeight() * 2
				|| startX > LSystem.screenRect.width + original.getWidth() * 2
				|| startY > LSystem.screenRect.height + original.getHeight()
						* 2) {
			isComplete = true;
		}
		synchronized (original) {
			float slope = vy / vx;
			float theta = MathUtils.atan(slope);
			original.setRotation(theta * MathUtils.RAD_TO_DEG);
			original.setLocation(startX, startY);
		}
	}

	public int getDirection() {
		return dir;
	}

}

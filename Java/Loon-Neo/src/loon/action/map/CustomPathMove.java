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
package loon.action.map;

import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.TArray;

/**
 * CustomPath移动辅助用类,用以处理next移动计算,此类的移动不会删除原始CustomPath数据, 仅会进行移动索引累加.
 * 
 */
public class CustomPathMove {

	private float speed = 1f;

	private float afterIndexPoint = 0f;

	private int currentIndex = 0;

	private TArray<Vector2f> movePoints;
	private Vector2f currentPos;

	public CustomPathMove(CustomPath path) {
		this.setPath(path, 1f);
	}

	public CustomPathMove setPath(CustomPath path, float speed) {
		if (path != null) {
			this.movePoints = path._steps;
		}
		if (this.movePoints != null && this.movePoints.size > 0) {
			this.currentPos = movePoints.get(0);
		} else {
			this.currentPos = Vector2f.ZERO();
		}
		this.speed = speed;
		this.afterIndexPoint = 0f;
		this.currentIndex = 0;
		return this;
	}

	public CustomPathMove setSpeed(float s) {
		speed = s;
		return this;
	}

	public float getSpeed() {
		return this.speed;
	}

	public CustomPathMove setCurrentStepIndex(int idx) {
		this.currentIndex = idx;
		if (this.currentIndex < 0) {
			this.currentIndex = 0;
		}
		return this;
	}

	public int getCurrentStepIndex() {
		return this.currentIndex;
	}

	public Vector2f next() {
		if (movePoints == null) {
			return currentPos;
		}
		int maxStep = movePoints.size() - 1;
		if (currentIndex == maxStep) {
			return movePoints.get(maxStep);
		}
		this.calcAmount(maxStep);
		if (currentIndex >= maxStep) {
			return movePoints.get(this.currentIndex = maxStep);
		}
		this.currentPos = currentPosition();
		return currentPos;
	}

	private Vector2f currentPosition() {

		float distanceBetween = movePoints.get(currentIndex).dst(movePoints.get(currentIndex + 1));
		float completePath = afterIndexPoint / distanceBetween;

		Vector2f current = movePoints.get(currentIndex);
		Vector2f next = movePoints.get(currentIndex + 1);

		float x = current.x;
		float y = current.y;

		x += (next.x - current.x) * completePath;
		y += (next.y - current.y) * completePath;

		return new Vector2f(x, y);
	}

	private float calcAmount(int maxStep) {
		float amountDistance = -afterIndexPoint;
		for (;;) {
			float deltaCurrentDistance = movePoints.get(currentIndex).dst(movePoints.get(currentIndex + 1));
			if (deltaCurrentDistance >= (speed - amountDistance)) {
				this.afterIndexPoint = (speed - amountDistance);
				return amountDistance;
			} else {
				amountDistance += deltaCurrentDistance;
				this.currentIndex++;
				if (currentIndex >= maxStep) {
					return 0;
				}
			}
		}
	}

	@Override
	public String toString() {
		final StringKeyValue builder = new StringKeyValue("CustomPathMove");
		builder.kv("currentIndex", currentIndex).comma().kv("currentPosition", currentPos).comma().kv("steps",
				movePoints.toString());
		return builder.toString();
	}

}

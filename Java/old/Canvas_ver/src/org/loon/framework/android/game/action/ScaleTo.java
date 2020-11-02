package org.loon.framework.android.game.action;

/**
 * Copyright 2008 - 2011
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class ScaleTo extends ActionEvent {

	private float dt;

	private float deltaX, deltaY;

	private float startX, startY;

	private float endX, endY;

	public ScaleTo(float s) {
		this(s, s);
	}

	public ScaleTo(float sx, float sy) {
		this.endX = sx;
		this.endY = sy;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {
		if (original != null) {
			startX = original.getScaleX();
			startY = original.getScaleY();
			deltaX = endX - startX;
			deltaY = endY - startY;
		}
	}

	public void update(long elapsedTime) {
		if (original != null) {
			synchronized (original) {
				if (original != null) {
					dt += Math.max((elapsedTime / 1000), 0.01f);
					original.setScale(startX + (deltaX * dt), startY
							+ (deltaY * dt));
					isComplete = (deltaX > 0 ? (original.getScaleX() >= endX)
							: (original.getScaleX() <= endX))
							&& (deltaY > 0 ? (original.getScaleY() >= endY)
									: (original.getScaleY() <= endY));
				}
			}
		} else {
			isComplete = true;
		}
	}
}

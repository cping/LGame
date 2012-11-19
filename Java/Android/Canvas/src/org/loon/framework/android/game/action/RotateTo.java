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
public class RotateTo extends ActionEvent {

	private float dstAngle;

	private float diffAngle;

	private float startAngle;

	private float speed;

	public RotateTo(float dstAngle, float speed) {
		this.dstAngle = dstAngle;
		if (this.dstAngle > 360) {
			this.dstAngle = 360;
		} else if (this.dstAngle < 0) {
			this.dstAngle = 0;
		}
		this.speed = speed;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public void onLoad() {
		startAngle = original.getRotation();
		diffAngle = 1;
	}

	public void update(long elapsedTime) {
		startAngle += diffAngle * speed;
		original.setRotation((int) startAngle);
		if (startAngle >= dstAngle) {
			isComplete = true;
		}
	}

}

package loon.action;

import loon.action.sprite.ISprite;

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
public class FadeTo extends ActionEvent {

	public float time;

	public float currentFrame;

	public int type;

	private float opacity;

	public FadeTo(int type, int speed) {
		this.type = type;
		this.setSpeed(speed);
	}

	public int getIType() {
		return type;
	}

	public void setIType(int type) {
		this.type = type;
	}

	void setOpacity(float opacity) {
		this.opacity = opacity;
	}

	public float getOpacity() {
		return opacity;
	}

	@Override
	public boolean isComplete() {
		return isComplete;
	}

	public float getSpeed() {
		return time;
	}

	public void setSpeed(int delay) {
		this.time = delay;
		if (type == ISprite.TYPE_FADE_IN) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0;
		}
	}

	@Override
	public void onLoad() {

	}

	@Override
	public void update(long elapsedTime) {
		if (type == ISprite.TYPE_FADE_IN) {
			currentFrame--;
			if (currentFrame == 0) {
				setOpacity(0);
				isComplete = true;
			}
		} else {
			currentFrame++;
			if (currentFrame == time) {
				setOpacity(0);
				isComplete = true;
			}
		}
		setOpacity((currentFrame / time) * 255);
		if (opacity > 0) {
			original.setAlpha((opacity / 255));
		}
	}

}

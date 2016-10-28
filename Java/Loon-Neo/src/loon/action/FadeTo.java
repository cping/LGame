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
package loon.action;

import loon.action.sprite.ISprite;

public class FadeTo extends ActionEvent {

	public float time;

	public float currentFrame;

	public int type;

	public FadeTo(int type, float speed) {
		this.type = type;
		this.setSpeed(speed);
	}

	public int getIType() {
		return type;
	}

	public void setIType(int type) {
		this.type = type;
	}

	public boolean isComplete() {
		return isComplete;
	}

	public float getSpeed() {
		return time;
	}

	public void setSpeed(float delay) {
		this.time = delay;
		if (type == ISprite.TYPE_FADE_OUT) {
			this.currentFrame = this.time;
		} else {
			this.currentFrame = 0f;
		}
	}

	public void onLoad() {

	}

	public void update(long elapsedTime) {
		if (type == ISprite.TYPE_FADE_OUT) {
			currentFrame--;
			if (currentFrame == 0) {
				original.setAlpha(0f);
				isComplete = true;
				return;
			}
		} else {
			currentFrame++;
			if (currentFrame == time) {
				original.setAlpha(1f);
				isComplete = true;
				return;
			}
		}
		original.setAlpha(currentFrame / time);
	}

	@Override
	public ActionEvent cpy() {
		return new FadeTo(type, time);
	}

	@Override
	public ActionEvent reverse() {
		FadeTo fade = null;
		if (type == ISprite.TYPE_FADE_IN) {
			fade = new FadeTo(ISprite.TYPE_FADE_OUT, time);
		} else {
			fade = new FadeTo(ISprite.TYPE_FADE_IN, time);
		}
		return fade;
	}

	@Override
	public String getName() {
		return "fade";
	}
}

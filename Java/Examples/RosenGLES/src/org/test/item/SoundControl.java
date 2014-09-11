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
package org.test.item;

import loon.core.input.LInputFactory.Touch;
import loon.core.timer.GameTime;

import org.test.base.BaseSprite;

public class SoundControl extends BaseSprite {
	private boolean able = true;
	public static boolean on;
	private boolean pressed;

	public SoundControl() {
		super.Load("assets/soundControl", 2, 1, 10f, true);
	}

	protected void change() {
		if (!on) {

		} else {
			on = false;
			super.setAnimation(new int[] { 1 });
		}
	}


	public void setup() {
		if (on) {
			super.setAnimation(new int[1]);
		} else {
			super.setAnimation(new int[] { 1 });
		}
	}

	protected void specificUpdate(GameTime gameTime) {
		this.pressed = false;

		if (Touch.isDown()) {
			this.pressed = true;
		}

		if (this.pressed && this.able) {
			this.able = false;
			this.change();
		}
		if (!this.pressed) {
			this.able = true;
		}
	}
}
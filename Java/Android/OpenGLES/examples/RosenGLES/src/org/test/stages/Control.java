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
package org.test.stages;

import loon.core.input.LInputFactory.Key;
import loon.core.input.LInputFactory.Touch;
import loon.core.timer.GameTime;

import org.test.base.BaseContainer;
import org.test.base.BaseSprite;

public class Control extends BaseContainer {
	private BaseSprite buttonJ;
	private BaseSprite buttonK;
	public boolean KA;
	public boolean KD;
	public boolean KJ;
	public boolean KK;
	private BaseSprite leftArrow;
	private BaseSprite rightArrow;

	public void init() {
		this.leftArrow = new BaseSprite();
		this.rightArrow = new BaseSprite();
		this.buttonJ = new BaseSprite();
		this.buttonK = new BaseSprite();
		this.leftArrow.Load("assets/leftArrow", 2, 1f, true);
		this.rightArrow.Load("assets/rightArrow", 2, 1f, true);
		this.buttonJ.Load("assets/button", 2, 1f, true);
		this.buttonK.Load("assets/button", 2, 1f, true);
		this.leftArrow.Pos.x = 23f;
		this.leftArrow.Pos.y = 300f;
		this.rightArrow.Pos.x = 23f + this.leftArrow.getWidth();
		this.rightArrow.Pos.y = 300f;
		this.buttonJ.Pos.x = 465f;
		this.buttonJ.Pos.y = 310f;
		this.buttonK.Pos.x = 630f;
		this.buttonK.Pos.y = 310f;
		super.addChild(this.leftArrow);
		super.addChild(this.rightArrow);
		super.addChild(this.buttonJ);
		super.addChild(this.buttonK);
	}

	protected void specificUpdate(GameTime gameTime) {
		this.KA = false;
		this.KD = false;
		this.KJ = false;
		this.KK = false;
		if (Touch.isDown() || Touch.isDrag()) {
			if (this.rightArrow.getGlobalBounds()
					.contains(Touch.x(), Touch.y())) {
				this.KD = true;
				this.rightArrow._Frame = 1;
				this.leftArrow._Frame = 0;
			}
			if (this.leftArrow.getGlobalBounds().contains(Touch.x(), Touch.y())) {
				this.KA = true;
				this.leftArrow._Frame = 1;
				this.rightArrow._Frame = 0;

			}
			if (this.buttonJ.getGlobalBounds().contains(Touch.x(), Touch.y())) {
				this.KJ = true;
				this.buttonJ._Frame = 1;

			}
			if (this.buttonK.getGlobalBounds().contains(Touch.x(), Touch.y())) {

				this.KK = true;
				this.buttonK._Frame = 1;
			}
		} else if (Key.isDown()) {
			if (Key.isKeyPressed(Key.RIGHT)) {
				this.KD = true;
				this.rightArrow._Frame = 1;
				this.leftArrow._Frame = 0;
			} else if (Key.isKeyPressed(Key.LEFT)) {
				this.KA = true;
				this.leftArrow._Frame = 1;
				this.rightArrow._Frame = 0;

			}
			if (Key.isKeyPressed(Key.ENTER)) {
				this.KJ = true;
				this.buttonJ._Frame = 1;
			}
			if (Key.isKeyPressed(Key.UP)) {
				this.KK = true;
				this.buttonK._Frame = 1;
			}
		}

	}
}

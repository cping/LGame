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
package org.test.act.stages;
import loon.LSystem;
import loon.canvas.LColor;
import loon.event.SysTouch;
import loon.utils.timer.GameTime;

import org.test.act.base.BaseSprite;
import org.test.act.item.SoundControl;

public class Title extends Stage {
	public SoundControl soundControl;

	@Override
	public void destroy() {
		super.removeChild(super.backGround);
		super.removeChild(this.soundControl);
	}

	public void init(Control control) {
		super.backGround = new BaseSprite();
		super.backGround.Load("assets/title", 1, 1f, true);
		super.addChild(super.backGround);
		this.soundControl = new SoundControl();
		this.soundControl.Pos.x = LSystem.viewSize.width
				- this.soundControl.getWidth();
		this.soundControl.Pos.y = 0f;
		this.soundControl.setup();
		super.addChild(this.soundControl);
	}

	protected void isPlayerSuccessful() {
		if (SysTouch.isDown()) {
			super.successful = true;
		}
	}

	protected void specificUpdate(GameTime gameTime) {
		this.isPlayerSuccessful();
	}
}
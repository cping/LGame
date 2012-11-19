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

import loon.core.LSystem;

import org.test.actor.Player;
import org.test.base.BaseSprite;
import org.test.item.MovableBG;

public class Stage1 extends Stage {
	
	public void init(Control control) {
		super.setup();
		super.backGround = new BaseSprite();
		super.backGround.Load("assets/bg1", 1, 1f, true);
		super.backGround.moveRate = 0f;
		super.mbgs[0] = new MovableBG("assets/mbg1");
		super.player = new Player(control);
		super.setupStage(0);
	}

	protected void isPlayerSuccessful() {
		if ((super.player.Pos.y > (LSystem.screenRect.height + super.player.actHeight))
				&& ((super.player.Pos.x - super.blocks.get(1).Pos.x) > 3800f)) {
			super.successful = true;
		}
		if (super.player.life <= 0) {
			super.failed = true;
		}
	}

}

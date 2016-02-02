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
package org.test.act.item;

import loon.LSystem;

import org.test.act.base.BaseSprite;

public class MovableBG {
	public BaseSprite[] sps = new BaseSprite[2];

	public MovableBG(String asset) {
		for (int i = 0; i < 2; i++) {
			this.sps[i] = new BaseSprite();
			this.sps[i].Load(asset, 1, 1f, true);
		}
		this.sps[0].Pos.x = -LSystem.viewSize.width / 2;
		this.sps[1].Pos.x = LSystem.viewSize.height / 2;
	}

	private int getHeadIndex() {
		if ((this.sps[0].Pos.x >= -LSystem.viewSize.width / 2)
				&& (this.sps[0].Pos.x <= LSystem.viewSize.width / 2)) {
			return 0;
		}
		return 1;
	}

	private int getTailIndex() {
		if ((this.sps[0].Pos.x >= -LSystem.viewSize.width / 2)
				&& (this.sps[0].Pos.x <= LSystem.viewSize.width / 2)) {
			return 1;
		}
		return 0;
	}

	public void update() {
		if (this.sps[this.getHeadIndex()].Pos.x < 0f) {
			this.sps[this.getTailIndex()].Pos.x = this.sps[this.getHeadIndex()].Pos.x
					+ LSystem.viewSize.width;
		} else if (this.sps[this.getHeadIndex()].Pos.x > 0f) {
			this.sps[this.getTailIndex()].Pos.x = this.sps[this.getHeadIndex()].Pos.x
					- LSystem.viewSize.width;
		}
	}
}

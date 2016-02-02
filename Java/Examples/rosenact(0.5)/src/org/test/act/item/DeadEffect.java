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

import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

import org.test.act.base.BaseSprite;

public class DeadEffect {
	private float _countDis;
	private float _dis;
	private boolean isShooted;
	public BaseSprite[] sps = new BaseSprite[8];
	private float v = 12f;
	private float V_DELTA = 0.75f;
	private float V_MIN = 3f;
	private Vector2f[] vels = new Vector2f[8];
	public boolean visible = true;

	public void init(String asset, float dis) {
		this._dis = dis;
		for (int i = 0; i < 8; i++) {
			this.sps[i] = new BaseSprite();
			this.sps[i].Load(asset, 3, 3, 0f, false);
			int[] anindex = new int[3];
			anindex[1] = 1;
			anindex[2] = 2;
			this.sps[i].setAnimation(anindex);
			this.sps[i].Pos.x = -this.sps[i].getWidth() / 2f;
			this.sps[i].Pos.y = -this.sps[i].getHeight() / 2f;
			this.vels[i] = new Vector2f(
					(MathUtils.cos(0.78539816339744828f * i)) * this.v,
					(MathUtils.sin(0.78539816339744828f * i)) * this.v);
			this.sps[i].visible = false;
		}
	}

	public void shoot() {
		this.isShooted = true;
	}

	public void update(GameTime gameTime) {
		if (!this.visible) {
			for (int i = 0; i < 8; i++) {
				this.sps[i].visible = false;
			}
		} else if (this.isShooted) {
			this.v -= this.V_DELTA;
			if (this.v < this.V_MIN) {
				this.v = this.V_MIN;
			}
			for (int j = 0; j < 8; j++) {
				this.vels[j].set((MathUtils.cos(0.78539816339744828f * j))
						* this.v, (MathUtils.sin(0.78539816339744828f * j))
						* this.v);
				this.sps[j].visible = true;
				this.sps[j].Pos.x += this.vels[j].x;
				this.sps[j].Pos.y += this.vels[j].y;
			}
			this._countDis += gameTime.getElapsedGameTime();
			if ((this._countDis > this._dis) && (this._dis > 0f)) {
				this.visible = false;
			}
		}
	}
}

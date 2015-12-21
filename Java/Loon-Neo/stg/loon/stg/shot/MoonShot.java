package loon.stg.shot;

import loon.stg.STGScreen;
import loon.utils.MathUtils;


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
public abstract class MoonShot extends EnemyShot {

	float a;

	float p;

	float q;

	float ix;

	float iy;

	float direction;

	public MoonShot(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		this.ix = x;
		this.iy = y;
		this.q = getY(tpno) + MathUtils.random() * 50.0f;
		this.p = getX(tpno) + MathUtils.random() * 50.0f;
		if (y == this.q) {
			++this.q;
		}
		if (x == this.p) {
			++this.p;
		}
		this.a = (y - this.q) / MathUtils.pow(x - this.p, 2.0f);
		this.direction = MathUtils.abs(this.p - x) * 100.0f / 2990.0f + 0.05016722408026756f;
		if (x - this.p > 0.0f) {
			this.direction = -this.direction;
		}
		this.ix = x;
		this.iy = y;
	}

	@Override
	public void update() {
		this.ix += this.direction;
		this.iy += 2.0f * this.a * (this.ix - this.p) * this.direction;
		setLocation(this.ix, this.iy);
		if (getY() > getScreenHeight() || getY() < -32 || getX() < -32
				|| getX() > getScreenWidth()) {
			delete();
		}
	}
}

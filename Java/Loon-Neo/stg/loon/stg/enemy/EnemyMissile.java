package loon.stg.enemy;

import loon.stg.STGScreen;
import loon.stg.shot.EnemyShot;

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
public abstract class EnemyMissile extends EnemyShot {

	int vx = 0;

	int vy = 0;

	public EnemyMissile(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
	}

	@Override
	public void update() {
		if (getY(super.targetPlnNo) > getY()) {
			++this.vy;
		} else {
			--this.vy;
		}
		if (getX(super.targetPlnNo) > getX()) {
			++this.vx;
		} else {
			--this.vx;
		}
		move(this.vx, this.vy);
		if (getY() > getScreenHeight() || getX() < 0 || getX() > getScreenWidth() - 6) {
			delete();
		}
	}

}

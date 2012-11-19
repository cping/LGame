package loon.stg.enemy;

import loon.stg.STGObject;
import loon.stg.STGScreen;

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
public abstract class EnemyOne extends STGObject {

	String explosion;

	public EnemyOne(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.ENEMY;
		super.countUpdate = 20;
		super.scorePoint = 10;
		super.speed = 8;
	}

	public void update() {
		if (super.attribute != STGScreen.ENEMY) {
			this.beDestroyed();
		} else {
			move(0, speed);
			if (getY() > getScreenHeight()) {
				delete();
			}
		}
	}

	private void beDestroyed() {
		this.scrollMove();
		if (this.count == 0) {
			if (explosion != null) {
				addClass(explosion, getX(), getY(), super.plnNo);
			} else {
				onExplosion();
			}
		} else if (this.count > countUpdate || getY() > getScreenHeight()) {
			delete();
		}
		++this.count;
		if (this.count % 2 == 0) {
			setPlaneView(false);
		} else {
			setPlaneView(true);
		}
	}

	public String getExplosion() {
		return explosion;
	}

	public void setExplosion(String explosion) {
		this.explosion = explosion;
	}

	public abstract void onExplosion();

}

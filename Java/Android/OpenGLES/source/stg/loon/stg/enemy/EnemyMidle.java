package loon.stg.enemy;

import loon.stg.STGObject;
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
public abstract class EnemyMidle extends STGObject {

	int direction;

	String effectName1;

	String effectName2;

	public EnemyMidle(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.ENEMY;
		super.hitPoint = 1;
		super.scorePoint = 100;
		super.speed = 4;
	}

	public void advent(int x, int y) {
		setPlaneView(!super.hitFlag);
		if (super.hitFlag) {
			super.hitFlag = false;
		} else if (getY() < y) {
			move(0, speed);
		} else if (this.direction == 0) {
			this.direction = -speed;
		} else if (getX() <= 0) {
			this.direction = speed;
		} else if (getX() >= getScreenWidth() - getHeight() - x) {
			this.direction = -speed;
		}
		move(this.direction, 0);
	}

	public void update() {
		if (super.attribute != STGScreen.ENEMY) {
			this.beDestroyed(40, 40);
		} else {
			this.advent(40, 40);
			if ((int) (MathUtils.random() * 2.0f) == 0) {
				if (effectName2 != null) {
					addClass(effectName2, getX() + 20, getY() + 32,
							super.targetPlnNo);
				} else {
					onEffectTwo();
				}
			}
		}
	}

	public void beDestroyed(int x, int y) {
		this.scrollMove();
		if (this.count == 0) {
			if (effectName1 != null) {
				addClass(effectName1, getX() + x / 2, getY() + y / 2,
						super.plnNo + 1);
			} else {
				onEffectOne();
			}
		} else {
			delete();
		}
		++this.count;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public String getEffectName1() {
		return effectName1;
	}

	public void setEffectName1(String effectName1) {
		this.effectName1 = effectName1;
	}

	public String getEffectName2() {
		return effectName2;
	}

	public void setEffectName2(String effectName2) {
		this.effectName2 = effectName2;
	}

	public abstract void onEffectOne();

	public abstract void onEffectTwo();
}

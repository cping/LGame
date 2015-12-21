package loon.stg.item;

import loon.stg.STGHero;
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
public abstract class HpUp extends Item {

	String effectName;

	public HpUp(STGScreen stg, int no, float x, float y, int tpno) {
		super(stg, no, x, y, tpno);
		super.attribute = STGScreen.ITEM;
		super.hitX = 0;
		super.hitY = 0;
		super.scorePoint = 0;
		super.speed = 8;
	}

	@Override
	public void update() {
		if (super.attribute != STGScreen.ITEM) {
			if (effectName != null) {
				addBombHero(effectName);
			} else {
				onEffect();
			}
			delete();
		} else {
			move(0, speed);
			if (getY() > getScreenHeight()) {
				delete();
			}
		}
	}

	@Override
	public void giveHeroEvent(STGHero hero) {
		hero.upLastHp(1);
	}

	public String getEffectName() {
		return effectName;
	}

	public void setEffectName(String effectName) {
		this.effectName = effectName;
	}

	public abstract void onEffect();
}

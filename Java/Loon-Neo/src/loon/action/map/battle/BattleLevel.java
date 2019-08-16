/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.action.map.battle;

/**
 * 战斗用工具类,一个简单的等级与经验值和等级转换器
 *
 */
public class BattleLevel {

	private int level;
	private int levelMax;
	private int levelExp;
	private int exp;
	private int expMax;
	private int expForNextLevel;
	private int expForPrevLevel;
	private int expGained;
	private boolean leveledUp;

	public BattleLevel(int level, int levelMax, int levelExp, int exp, int expMax, int expForNextLevel,
			int expForPrevLevel, int expGained, boolean leveledUp) {
		this.init(level, levelMax, levelExp, exp, expMax, expForNextLevel, expForPrevLevel, expGained, leveledUp);
	}

	public void init(int level, int levelMax, int levelExp, int exp, int expMax, int expForNextLevel,
			int expForPrevLevel, int expGained, boolean leveledUp) {
		this.level = level;
		this.levelMax = levelMax;
		this.levelExp = levelExp;
		this.exp = exp;
		this.expMax = expMax;
		this.expForNextLevel = expForNextLevel;
		this.expForPrevLevel = expForPrevLevel;
		this.expGained = expGained;
		this.leveledUp = leveledUp;
	}

	public void update() {
		if (level < levelMax && exp >= levelExp) {
			exp = 0;
			level++;
			leveledUp = true;
		} else if (exp < expMax) {
			exp += expGained;
		}
		if (exp >= expMax) {
			exp = expMax;
		}
		if (level < levelMax) {
			expForPrevLevel = level + 1;
		} else {
			expForPrevLevel = level;
		}
		if (level > 0) {
			expForPrevLevel = level - 1;
		} else {
			expForPrevLevel = 0;
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isLeveledUp() {
		return leveledUp;
	}

	public void setLeveledUp(boolean leveledUp) {
		this.leveledUp = leveledUp;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public int getExpForNextLevel() {
		return expForNextLevel;
	}

	public void setExpForNextLevel(int expForNextLevel) {
		this.expForNextLevel = expForNextLevel;
	}

	public int getExpForPrevLevel() {
		return expForPrevLevel;
	}

	public void setExpForPrevLevel(int expForPrevLevel) {
		this.expForPrevLevel = expForPrevLevel;
	}

	public int getExpGained() {
		return expGained;
	}

	public void setExpGained(int expGained) {
		this.expGained = expGained;
	}

	public int getLevelMax() {
		return levelMax;
	}

	public void setLevelMax(int levelMax) {
		this.levelMax = levelMax;
	}

	public int getExpMax() {
		return expMax;
	}

	public void setExpMax(int expMax) {
		this.expMax = expMax;
	}

	public int getLevelExp() {
		return levelExp;
	}

	public void setLevelExp(int levelExp) {
		this.levelExp = levelExp;
	}

}

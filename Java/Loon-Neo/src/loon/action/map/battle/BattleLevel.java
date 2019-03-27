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

public class BattleLevel {

	private int level;
	private boolean leveledUp;
	private int exp;
	private int expForNextLevel;
	private int expForPrevLevel;
	private int expGained;

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

}

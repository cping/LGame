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

public class BattleSkillValue {

	public BattleSkillValue STR;
	public BattleSkillValue DUR;
	public BattleSkillValue CRT;
	public BattleSkillValue DEX;
	public BattleSkillValue MAG;
	public BattleSkillValue LUK;

	private String skillName;
	
	private int minimumPoints, maximumPoints, increment, currentPoint;

	public BattleSkillValue(String name, int minps, int maxps, int increment) {
		this.skillName = name;
		this.minimumPoints = minps;
		this.maximumPoints = maxps;
		currentPoint = increment;
	}

	public BattleSkillValue initTemplate() {
		STR = new BattleSkillValue("STR", 0, 5, 1);
		DUR = new BattleSkillValue("DUR", 0, 5, 1);
		CRT = new BattleSkillValue("CRT", 0, 5, 1);
		DEX = new BattleSkillValue("DEX", 0, 5, 1);
		MAG = new BattleSkillValue("MAG", 0, 5, 1);
		LUK = new BattleSkillValue("LUK", 0, 5, 1);
		return this;
	}

	public void update() {
		update(increment);
	}

	public void update(int p) {
		if (currentPoint + p <= maximumPoints) {
			currentPoint += p;
		} else
			currentPoint = maximumPoints;
	}

	public String getName() {
		return skillName;
	}

	public void setName(String name) {
		this.skillName = name;
	}

	public int getMinimumPoints() {
		return minimumPoints;
	}

	public void setMinimumPoints(int minps) {
		this.minimumPoints = minps;
	}

	public int getMaximumPoints() {
		return maximumPoints;
	}

	public void setMaximumPoints(int maxps) {
		this.maximumPoints = maxps;
	}

	public int getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(int cp) {
		this.currentPoint = cp;
	}

	public int getIncrement() {
		return increment;
	}

	public void setIncrement(int inc) {
		this.increment = inc;
	}

	public BattleSkillValue getSTR() {
		return STR;
	}

	public void setSTR(BattleSkillValue sTR) {
		STR = sTR;
	}

	public BattleSkillValue getDUR() {
		return DUR;
	}

	public void setDUR(BattleSkillValue dUR) {
		DUR = dUR;
	}

	public BattleSkillValue getCRT() {
		return CRT;
	}

	public void setCRT(BattleSkillValue cRT) {
		CRT = cRT;
	}

	public BattleSkillValue getDEX() {
		return DEX;
	}

	public void setDEX(BattleSkillValue dEX) {
		DEX = dEX;
	}

	public BattleSkillValue getMAG() {
		return MAG;
	}

	public void setMAG(BattleSkillValue mAG) {
		MAG = mAG;
	}

	public BattleSkillValue getLUK() {
		return LUK;
	}

	public void setLUK(BattleSkillValue lUK) {
		LUK = lUK;
	}

}

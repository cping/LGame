/**
 * Copyright 2008 - 2011
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable hpw or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific hpnguage governing permissions and limitations under
 * the License.
 * 
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
package loon.srpg.actor;

import loon.LTexture;
import loon.srpg.SRPGType;
import loon.utils.CollectionUtils;
import loon.utils.MathUtils;


public class SRPGStatus {

	// 正常移动
	public static final int MOVETYPE_NORMAL = 0;

	// 水中
	public static final int MOVETYPE_WATER = 1;

	// 病人
	public static final int MOVETYPE_INVALID = 2;

	// 大病
	public static final int MOVETYPE_BINVALID = 3;

	// 缓慢移动
	public static final int MOVETYPE_SLOWMOVE = 32;

	// -------- 魔法属性系列开始 ---------//

	// 物理
	public static final int ELEMENT_PHYSICS = 0;

	// 火系
	public static final int ELEMENT_FIRE = 1;

	// 水系
	public static final int ELEMENT_WATER = 2;

	// 雷系
	public static final int ELEMENT_THUNDER = 3;

	// 土系
	public static final int ELEMENT_EARTH = 4;

	// 风系
	public static final int ELEMENT_WIND = 5;

	// 神圣系
	public static final int ELEMENT_SAINT = 6;

	// 黑暗系
	public static final int ELEMENT_DARK = 7;

	// 治疗系
	public static final int ELEMENT_RECOVERY = 8;

	// 空间系
	public static final int ELEMENT_VOID = 9;

	// 魔法系列最大值
	public static final int ELEMENT_MAX = 10;

	// -------- 魔法属性系列结束 ---------//

	public static final int LEADER_NO = 0;

	public static final int LEADER_NORMAL = 1;

	public static final int LEADER_MAIN = 2;

	public static final int STATUS_GOOD = 0;

	public static final int STATUS_POWER = 0;

	public static final int STATUS_AGILITY = 1;

	public static final int STATUS_PROTECT = 2;

	public static final int STATUS_REFLECT = 3;

	public static final int STATUS_DIMENSION = 4;

	public static final int STATUS_REVIVE = 5;

	public static final int STATUS_DUPLICATE = 6;

	public static final int STATUS_DUAL = 7;

	public static final int STATUS_GOODEND = 8;

	public static final int STATUS_STUN = 9;

	public static final int STATUS_LOVER = 10;

	public static final int STATUS_POISON = 11;

	public static final int STATUS_INTERRUPT = 12;

	public static final int STATUS_WEAK = 13;

	public static final int STATUS_SILENCE = 14;

	public static final int STATUS_MAX = 15;

	// -------- 附带效果系列开始 ---------//

	// 两次行动
	public static final int SKILL_DOUBLEACTION = 0;

	// 一次伤害
	public static final int SKILL_STATUSINVALID = 1;

	// 两次伤害
	public static final int SKILL_DOUBLEATTACK = 2;

	// 立即死亡
	public static final int SKILL_UNDEAD = 3;

	// 传送
	public static final int SKILL_CARRY = 4;

	public static final int SKILL_MAX = 5;

	// -------- 附带效果系列结束 ---------//

	public static final int MAX_ABILITY = 10;

	public static final String[] STATUS_NAME = { "POWER", "AGILITY", "PROTECT",
			"REFLECT", "DIMENSION", "REVIVE", "DUPLICATE", "DUAL", "HALFMR",
			"STUN", "LOVER", "POISON", "INTERRUPT", "WEAK", "SILENCE" };

	public static final int statusDefault[] = { 4, 4, 8, 8, 2, -1, 4, 3, 4, -1,
			-1, 8, 3, 8, 3 };

	public static final int subStatusDefault[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0 };

	public String name;

	public String jobname;

	public int number;

	public int level;

	public int exp;

	public int hp;

	public int mp;

	public int max_hp;

	public int max_mp;

	public int strength;

	public int vitality;

	public int agility;

	public int magic;

	public int resume;

	public int mind;

	public int sp;

	public int dexterity;

	// 预定恢复或减少的生命值
	public int regeneration;

	public int move;

	public int movetype;

	// 免疫的攻击种类
	public int[] immunity;

	public int[] status;

	public int[] substatus;

	public int[] skill;

	public int[] ability;

	public int[] guardelement;

	public int[] computer;

	public int team;

	public int group;

	public int action;

	public int leader;

	public boolean isComputer;

	public LTexture face;

	public SRPGStatus(SRPGStatus status1) {
		this.status = new int[status1.status.length];
		this.substatus = new int[status1.substatus.length];
		this.ability = new int[status1.ability.length];
		this.guardelement = new int[status1.guardelement.length];
		this.skill = new int[status1.skill.length];
		this.computer = new int[status1.computer.length];
		this.copy(status1);
	}

	public void copy(SRPGStatus status1) {
		this.name = status1.name;
		this.jobname = status1.jobname;
		this.number = status1.number;
		this.level = status1.level;
		this.exp = status1.exp;
		this.hp = status1.hp;
		this.mp = status1.mp;
		this.max_hp = status1.max_hp;
		this.max_mp = status1.max_mp;
		this.strength = status1.strength;
		this.vitality = status1.vitality;
		this.agility = status1.agility;
		this.magic = status1.magic;
		this.resume = status1.resume;
		this.mind = status1.mind;
		this.sp = status1.sp;
		this.dexterity = status1.dexterity;
		this.regeneration = status1.regeneration;
		this.move = status1.move;
		this.movetype = status1.movetype;
		this.status = CollectionUtils.copyOf(status1.status);
		this.substatus = CollectionUtils.copyOf(status1.substatus);
		this.ability = CollectionUtils.copyOf(status1.ability);
		this.guardelement = CollectionUtils.copyOf(status1.guardelement);
		this.skill = CollectionUtils.copyOf(status1.skill);
		this.computer = CollectionUtils.copyOf(status1.computer);
		this.team = status1.team;
		this.group = status1.group;
		this.action = status1.action;
		this.leader = status1.leader;
		this.isComputer = status1.isComputer;
	}

	public SRPGStatus() {
		this.guardelement = new int[MAX_ABILITY];
		this.name = "";
		this.jobname = "";
		for (int i = 0; i < guardelement.length; i++) {
			guardelement[i] = 100;
		}
		this.ability = new int[MAX_ABILITY];
		for (int i = 0; i < ability.length; i++) {
			ability[i] = -1;
		}
		this.status = new int[15];
		this.substatus = new int[15];
		this.skill = new int[10];
		for (int i = 0; i < skill.length; i++) {
			skill[i] = -1;
		}
		this.computer = SRPGType.NORMAL;
	}

	public void allRecovery() {
		this.hp = max_hp;
		this.mp = max_mp;
	}

	public void defaultStatus() {
		this.hp = max_hp;
		this.mp = 0;
		this.clearStatus();
		this.action = 0;
	}

	public int getValidAbility() {
		int i = 0;
		for (int j = 0; j < ability.length; j++) {
			if (ability[j] != -1) {
				i++;
			}
		}
		return i;
	}

	public int[] getCutAbility() {
		int[] res = new int[getValidAbility()];
		int i = 0;
		for (int j = 0; j < ability.length; j++) {
			if (ability[j] != -1) {
				res[i] = ability[j];
				i++;
			}
		}
		return res;
	}

	public void clearAbility() {
		for (int i = 0; i < ability.length; i++) {
			ability[i] = -1;
		}
	}

	public void addAbility(int i) {
		int j = 0;
		do {
			if (j >= ability.length) {
				break;
			}
			if (ability[j] == -1) {
				ability[j] = i;
				break;
			}
			j++;
		} while (true);
	}

	public boolean checkAbility(int i) {
		for (int j = 0; j < ability.length; j++) {
			if (ability[j] == i) {
				return true;
			}
		}
		return false;
	}

	public boolean deleteAbility(int i) {
		for (int j = 0; j < ability.length; j++) {
			if (ability[j] == i) {
				ability[j] = -1;
				return true;
			}
		}
		return false;
	}

	public void cleanAbility() {
		int[] res = new int[MAX_ABILITY];
		int i = 0;
		for (int j = 0; j < res.length; j++) {
			res[j] = -1;
		}
		for (int j = 0; j < ability.length; j++) {
			if (ability[j] != -1) {
				res[i] = ability[j];
				i++;
			}
		}
		this.ability = res;
	}

	public void setAbility(int[] res) {
		clearAbility();
		for (int i = 0; i < res.length; i++) {
			ability[i] = res[i];
		}
	}

	public void setStatus(int i, int statusValue) {
		this.status[i] = statusValue;
	}

	public void clearStatus() {
		for (int i = 0; i < status.length; i++) {
			setStatus(i, 0);
		}
	}

	public void startTurn() {
		if (hp > 0 || checkSkill(SKILL_UNDEAD)) {
			if (hp > 0) {
				action = 1;
				if (checkSkill(SKILL_DOUBLEACTION) || status[STATUS_DUAL] != 0) {
					action = 2;
				}
				if (checkSkill(SKILL_STATUSINVALID)) {
					for (int i = 9; i < STATUS_MAX; i++) {
						setStatus(i, 0);
					}
				}
			}
			if (status[STATUS_POISON] != 0) {
				hp += regeneration;
			}
			if (hp > max_hp) {
				hp = max_hp;
			}
			if (status[STATUS_INTERRUPT] == 0) {
				mp += resume;
			}
			if (mp > max_mp) {
				mp = max_mp;
			}
			if (hp <= 0) {
				mp = 0;
			}
		}
		if (!moveCheck()) {
			action = 0;
		}
		if (status[STATUS_POISON] != 0) {
			hp -= vitality / 8;
		}
		if (status[STATUS_INTERRUPT] != 0) {
			mp -= resume;
		}
		if (mp < 0) {
			mp = 0;
		}
		for (int i = 0; i < STATUS_MAX; i++) {
			if (status[i] > 0) {
				status[i]--;
			}
			if (status[i] == 0) {
				continue;
			}
			if (i == STATUS_STUN && MathUtils.random.nextInt(100) < 50) {
				status[i] = 0;
			}
			if (i == STATUS_LOVER && MathUtils.random.nextInt(100) < 40) {
				status[i] = 0;
				action = 0;
			}
		}
	}

	public void statusChange() {
		if (status[STATUS_POWER] != 0) {
			strength += (strength * status[STATUS_POWER]) / 12;
			dexterity += (dexterity * status[STATUS_POWER]) / 12;
		}
		if (status[STATUS_AGILITY] != 0) {
			agility += (agility * status[STATUS_AGILITY]) / 10;
			dexterity += (dexterity * status[STATUS_AGILITY]) / 20;
			mind += (mind * status[STATUS_AGILITY]) / 20;
		}
		if (status[STATUS_PROTECT] != 0) {
			int i = (guardelement[0] * 3) / 4;
			guardelement[0] -= (i * status[STATUS_PROTECT])
					/ getDefaultStatus(STATUS_PROTECT);
		}
		if (status[STATUS_REFLECT] != 0) {
			for (int i = 0; i < 7; i++) {
				if (i != 0) {
					int l = (guardelement[i] * 3) / 4;
					guardelement[i] -= (l * status[STATUS_REFLECT])
							/ getDefaultStatus(STATUS_REFLECT);
				}
			}
		}
		if (status[STATUS_POISON] != 0) {
			strength -= strength / 5;
			vitality -= vitality / 5;
			dexterity -= dexterity / 5;
			agility -= agility / 5;
		}
		if (status[STATUS_WEAK] != 0) {
			int i = status[STATUS_WEAK];
			strength -= (strength * i) / 32;
			dexterity -= (dexterity * i) / 32;
			vitality -= (vitality * i) / 32;
			agility -= (agility * i) / 32;
			magic -= (magic * i) / 32;
			sp -= (sp * i) / 32;
			mind -= (mind * i) / 32;
		}
	}

	public boolean actionCheck() {
		return status[STATUS_LOVER] == 0 && moveCheck();
	}

	public boolean moveCheck() {
		return hp > 0 && moveCheckStatus();
	}

	public boolean moveCheckStatus() {
		return status[STATUS_STUN] == 0;
	}

	public boolean getGoodStatus() {
		for (int i = 0; i <= 8; i++) {
			if (status[i] != 0) {
				return true;
			}
		}
		return false;
	}

	public boolean getBadStatus() {
		for (int i = 9; i < STATUS_MAX; i++) {
			if (status[i] != 0) {
				return true;
			}
		}
		return false;
	}

	public static int getDefaultStatus(int i) {
		return statusDefault[i];
	}

	public static int getDefaultSubStatus(int i) {
		return subStatusDefault[i];
	}

	public boolean checkSkill(int i) {
		for (int j = 0; j < skill.length; j++) {
			if (skill[j] == i) {
				return true;
			}
		}
		return false;
	}

	public void addSkill(int i) {
		int j = 0;
		for (;;) {
			if (j >= skill.length) {
				break;
			}
			if (skill[j] == -1) {
				skill[j] = i;
				break;
			}
			j++;
		}
	}

	public void clearSkill() {
		for (int i = 0; i < skill.length; i++) {
			skill[i] = -1;
		}
	}

}

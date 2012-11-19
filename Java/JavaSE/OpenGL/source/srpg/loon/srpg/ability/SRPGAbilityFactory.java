package loon.srpg.ability;

import java.util.ArrayList;

import loon.core.LSystem;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.effect.SRPGEffect;
import loon.srpg.field.SRPGField;
import loon.srpg.field.SRPGFieldElement;
import loon.srpg.field.SRPGFieldMove;
import loon.utils.CollectionUtils;


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
public class SRPGAbilityFactory {

	boolean flag = false;

	boolean isDamage = true;

	boolean isDamageUpdate = true;

	boolean isHitUpdate = true;

	boolean isHit = true;

	boolean isStatus = false;

	boolean isSetStatus = true;

	int atk, def, damageValue, hitRate;

	SRPGStatus status, status1, status2;

	int damageChange = 100;

	int[] statasFlag;

	private static SRPGAbilityFactory instance;

	final static ArrayList<SRPGAbility> lazyAbilityClass = new ArrayList<SRPGAbility>(
			24);

	public static SRPGAbilityFactory getInstance(int number) {
		if (instance == null) {
			instance = new SRPGAbilityFactory(number);
		} else {
			instance.set(number);
		}
		return instance;
	}

	public static void putAbility(SRPGAbility ability) {
		lazyAbilityClass.add(ability);
	}

	public static void setAbility(int index, SRPGAbility ability) {
		lazyAbilityClass.set(index, ability);
	}

	public static void makeDefAbilitys() {
		if (lazyAbilityClass.size() == 0) {
			SRPGAbilityTemp.make();
		}
	}

	private int number;

	SRPGAbilityFactory() {

	}

	private SRPGAbilityFactory(int i) {
		set(i);
	}

	public void set(int i) {
		number = i;
	}

	public int getNumber() {
		return number;
	}

	public String getAbilityName() {
		return getAbility().abilityName;
	}

	public String getAbilityHelp() {
		return getAbility().abilityAbout;
	}

	public int getMinLength() {
		return getAbility().minLength;
	}

	public int getMaxLength() {
		return getAbility().maxLength;
	}

	public int getMP() {
		return getAbility().mp;
	}

	public SRPGAbility getAbility(int index) {
		return (SRPGAbility) lazyAbilityClass.get(index);
	}

	public SRPGAbility getAbility() {
		return getAbility(number);
	}

	public int getMP(SRPGStatus status) {
		int i = getAbility().mp;
		int j = 0;
		if (status.checkSkill(SRPGStatus.SKILL_CARRY)
				|| status.status[SRPGStatus.STATUS_GOODEND] != 0) {
			j = i / 2;
		}
		return i - j;
	}

	public int getRange() {
		return getAbility(number).range;
	}

	public int getTarget() {
		return getAbility(number).target;
	}

	public int getDirect() {
		return getAbility(number).direct;
	}

	public int getSelectNeed() {
		return getAbility(number).selectNeed;
	}

	public int getCounter() {
		return getAbility(number).counter;
	}

	public int getGenre() {
		return getAbility(number).genre;
	}

	public static int getOptimizeAbility(int[] range, SRPGField field,
			SRPGActors actors, int atk, int def) {
		if (range == null) {
			return -1;
		}
		boolean flag = false;
		int rangeSize = -1;
		int limit = -1;
		int original = 0;
		int mp = 0;
		for (int j = 0; j < range.length; j++) {
			boolean result = false;
			SRPGAbilityFactory ability = SRPGAbilityFactory
					.getInstance(range[j]);
			int[] res = ability.getOptimizeOriginal(field, actors, atk, def);
			int value = (res[0] * res[1]) / 100;
			if (res[0] >= actors.find(def).getActorStatus().hp) {
				if (flag && res[1] <= original) {
					continue;
				}
				result = true;
			} else if (flag || value < limit || value == limit
					&& mp < ability.getMP(actors.find(atk).getActorStatus())) {
				continue;
			}
			rangeSize = range[j];
			limit = value;
			original = res[1];
			mp = ability.getMP(actors.find(atk).getActorStatus());
			flag = result;
		}

		return rangeSize;
	}

	public static int getOptimizePoint(int i, SRPGField field,
			SRPGActors actors, int atk, int def) {
		if (i == -1) {
			return -1;
		} else {
			return (new SRPGAbilityFactory(i)).getOptimizePoint(field, actors,
					atk, def);
		}
	}

	public int getOptimizePoint(SRPGField field, SRPGActors actors, int atk,
			int def) {
		return getOptimizeAll(field, actors, atk, def)[0];
	}

	public int[] getOptimizeOriginal(SRPGField field, SRPGActors actors,
			int atk, int def) {
		SRPGDamageData damagedata = getDamageExpect(field, actors, atk, def);
		int damage = damagedata.getBeforeRandomDamage();
		int hit = damagedata.getBeforeRandomHitrate();
		if (damagedata.getGenre() == SRPGType.GENRE_HELPER
				&& damagedata.getGenre() == SRPGType.GENRE_CURE) {
			damage = 0;
		}
		SRPGStatus status = actors.find(def).getActorStatus();
		if (damagedata.getGenre() == SRPGType.GENRE_ATTACK) {
			if (damage > status.hp && status.hp > 0)
				damage = status.hp;
		} else if (damagedata.getGenre() == SRPGType.GENRE_RECOVERY) {
			if (status.hp + damage > status.max_hp) {
				damage = status.max_hp - status.hp;
			}
		}
		int[] res = { damage, hit };
		return res;
	}

	public int[] getOptimizeAll(SRPGField field, SRPGActors actors, int atk,
			int def) {
		int[] res = getOptimizeOriginal(field, actors, atk, def);
		res[0] = (res[0] * res[1]) / 100;
		return res;
	}

	/**
	 * 计算角色造成的伤害
	 * 
	 * @param field
	 * @param actors
	 * @param i
	 * @param j
	 * @return
	 */
	public SRPGDamageData getDamageExpect(SRPGField field, SRPGActors actors,
			int attackerIndex, int defenderIndex) {

		SRPGDamageData damageData = new SRPGDamageData();

		SRPGActor attacker = actors.find(attackerIndex);
		SRPGActor defender = actors.find(defenderIndex);

		this.status = new SRPGStatus(attacker.getActorStatus());
		this.status1 = new SRPGStatus(defender.getActorStatus());
		this.status2 = new SRPGStatus(status1);
		this.status.statusChange();
		this.status1.statusChange();

		SRPGFieldElement atkElement = field.getPosMapElement(
				attacker.getPosX(), attacker.getPosY());
		if (atkElement != null) {
			this.atk = atkElement.atk;
		} else {
			this.atk = 1;
		}

		SRPGFieldElement defElement = field.getPosMapElement(
				defender.getPosX(), defender.getPosY());
		if (atkElement != null) {
			this.def = defElement.def;
		} else {
			this.def = 1;
		}

		this.damageValue = ((status.dexterity / 2 + status.strength / 2) * atk)
				/ 100 + (status1.vitality * def) / 1000;
		this.hitRate = ((status.agility + status.mind / 2) * 50 * atk) / 100
				/ (((status1.agility + status1.dexterity / 4) * def) / 100);
		this.damageChange = 100;
		this.flag = false;
		this.isDamage = true;
		this.isDamageUpdate = true;
		this.isHitUpdate = true;
		this.isHit = true;
		this.isStatus = false;
		this.isSetStatus = true;
		damageData.setGenre(getGenre());
		if (getGenre() == SRPGType.GENRE_HELPER) {
			damageData.setDamageExpect(-1);
		}
		this.statasFlag = new int[15];
		getAbility().runDamageExpect(attacker, defender, this, field,
				damageData, actors);

		int[] immunity = defender.getActorStatus().immunity;
		if (immunity != null) {
			for (int i = 0; i < immunity.length; i++) {
				if (damageData.getElement(immunity[i])) {
					damageValue = 0;
					hitRate = 0;
				}
			}
		}

		if (!status1.moveCheck() && isHit) {
			hitRate = 100;
			for (int j = 0; j < statasFlag.length; j++) {
				statasFlag[j] = 100;
			}
		}
		if (damageData.getHelper() == null) {
			int i = 0;
			do {
				if (i >= SRPGStatus.STATUS_MAX)
					break;
				if (damageData.getStatus(i) != 0) {
					damageData.setHelperString(SRPGStatus.STATUS_NAME[i]);
					break;
				}
				i++;
			} while (true);
		}
		for (int i = 0; i < SRPGStatus.STATUS_MAX; i++) {
			damageData.setStatusExpect(i, damageData.getStatus(i));
		}
		boolean result = false;
		int value = 0;
		int[] guardelement = status1.guardelement;
		if (guardelement != null) {
			for (int i = 0; i < guardelement.length; i++) {
				if (!damageData.getElement(i)) {
					continue;
				}
				if (value < guardelement[i]) {
					value = guardelement[i];
				}
				result = true;
			}
		}

		if (!result) {
			value = 100;
		}
		float f = damageValue * value;
		f /= 100F;
		damageValue = (int) (f + 0.5F);
		if (damageValue < 0) {
			damageValue = 0;
		}
		if (isDamage) {
			int d = attacker.getDirectionStatus(defender);
			int v = 100;
			int v1 = 100;
			if (d == SRPGType.MOVE_DOWN) {
				v = 100;
				v1 = 100;
			} else if (d == SRPGType.MOVE_RIGHT) {
				v = 120;
				v1 = 150;
			} else {
				v = 110;
				v1 = 125;
			}
			if (isDamageUpdate) {
				damageValue = (damageValue * v) / 100;
			}
			if (isHitUpdate) {
				hitRate = (hitRate * v1) / 100;
			}
			if (isStatus && isSetStatus) {
				for (int j = 0; j < SRPGStatus.STATUS_MAX; j++) {
					statasFlag[j] = (statasFlag[j] * v1) / 100;
				}
			}
		}
		if (isStatus) {
			for (int j = 0; j < SRPGStatus.STATUS_MAX; j++) {
				if (damageData.getStatus(j) != 0
						&& statasFlag[j] <= LSystem.random.nextInt(100)) {
					damageData.setStatus(j, 0);
				}
			}
		}
		if (status1.checkSkill(SRPGStatus.SKILL_STATUSINVALID)) {
			for (int j = 9; j < SRPGStatus.STATUS_MAX; j++) {
				damageData.setStatus(j);
			}
		}
		if (status1.status[SRPGStatus.STATUS_DIMENSION] != 0
				&& (status1.checkSkill(SRPGStatus.SKILL_UNDEAD) || status1.hp > 0)) {
			hitRate = 0;
		}
		if (!flag) {
			if (damageData.getDamageExpect() != -1) {
				damageData.setDamageExpect(damageValue);
			}
			if (damageData.getHitrateExpect() != -1) {
				damageData.setHitrateExpect(hitRate);
			}
		}
		if (damageData.getGenre() != SRPGType.GENRE_HELPER) {
			damageData.setBeforeRandomDamage(damageValue);
		}
		damageData.setBeforeRandomHitrate(hitRate);
		if (hitRate <= LSystem.random.nextInt(100)) {
			damageData.setHit(false);
		} else {
			damageData.setHit(true);
		}
		if (damageChange > 0 && damageValue > 0) {
			int r = LSystem.random.nextInt(damageValue);
			r = (r * damageChange) / 10000 - (damageValue * r) / 2 / 10000;
			damageValue += r;
		}
		damageData.setDamage(damageValue);
		damageData.setHitrate(hitRate);
		{
			if (damageData.isHit()) {
				damageData.setActorStatus(damageInput(damageData, status2));
			} else {
				damageData.setActorStatus(new SRPGStatus(defender
						.getActorStatus()));
			}
		}
		return damageData;
	}

	/**
	 * 平均伤害判定
	 * 
	 * @param damageaverage
	 * @param status
	 * @return
	 */
	public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
			SRPGStatus status) {
		SRPGDamageData damageData = new SRPGDamageData();
		damageData.setHit(true);
		damageData.setGenre(0);
		SRPGStatus status1 = new SRPGStatus(status);
		Object o = this.getAbility().dataInput(damageaverage, damageData,
				status);
		if (o == null) {
			return null;
		}
		damageData.setActorStatus(damageInput(damageData, status1));
		return damageData;
	}

	public int[] getAbilitySkill() {
		return getAbility().getAbilitySkill();
	}

	public boolean checkAbilitySkill(int i) {
		int[] res = getAbilitySkill();
		if (res == null) {
			return false;
		}
		for (int j = 0; j < res.length; j++) {
			if (res[j] == i) {
				return true;
			}
		}
		return false;
	}

	public SRPGEffect getAbilityEffect(SRPGActor actor, int i, int j) {
		return getAbilityEffect(number, actor, i, j);
	}

	/**
	 * 使用技能
	 * 
	 * @param i
	 * @param actor
	 * @param j
	 * @param k
	 * @return
	 */
	public static SRPGEffect getAbilityEffect(int index, SRPGActor actor,
			int j, int k) {
		SRPGAbilityFactory ability = SRPGAbilityFactory.getInstance(index);
		SRPGEffect o = ability.getAbility()
				.runAbilityEffect(index, actor, j, k);
		return o == null ? new SRPGEffect() : o;
	}

	public static SRPGStatus damageInput(SRPGDamageData damage,
			SRPGStatus status) {
		if (damage.isHit()) {

			switch (damage.getGenre()) {

			// 无动作
			case SRPGType.GENRE_HELPER:
			case SRPGType.GENRE_CURE:
			default:
				break;

			// 伤害
			case SRPGType.GENRE_ATTACK:
				status.hp -= damage.getDamage();
				break;

			// 恢复
			case SRPGType.GENRE_RECOVERY:
				status.hp += damage.getDamage();
				if (status.hp > status.max_hp) {
					status.hp = status.max_hp;
				}
				break;

			// 减魔
			case SRPGType.GENRE_MPDAMAGE:
				status.mp -= damage.getDamage();
				if (status.mp < 0) {
					status.mp = 0;
				}
				break;

			// 补魔
			case SRPGType.GENRE_MPRECOVERY:
				status.mp += damage.getDamage();
				if (status.mp > status.max_mp) {
					status.mp = status.max_mp;
				}
				break;

			// 减魔减血
			case SRPGType.GENRE_ALLDAMAGE:
				status.hp -= damage.getDamage();
				status.mp -= damage.getMP();
				if (status.mp < 0) {
					status.mp = 0;
				}
				break;

			// 回魔回血
			case SRPGType.GENRE_ALLRECOVERY:
				status.hp += damage.getDamage();
				if (status.hp > status.max_hp) {
					status.hp = status.max_hp;
				}
				status.mp += damage.getMP();
				if (status.mp > status.max_mp) {
					status.mp = status.max_mp;
				}
				break;
			}
			for (int i = 0; i < 15; i++) {
				if (status.status[i] < damage.status[i]
						&& status.status[i] != -1 || damage.status[i] == -1) {
					status.status[i] = damage.status[i];
				}
				status.substatus[i] = damage.substatus[i];
			}

		}
		return status;
	}

	public static int[] filtedAbility(int[] abilitys, SRPGStatus status,
			boolean flag) {
		int[] res = status.status;
		boolean flag1 = false;
		if (res != null && res[SRPGStatus.STATUS_SILENCE] != 0) {
			flag1 = true;
		}
		int[] result = new int[0];
		int index = 0;
		for (int j = 0; j < abilitys.length; j++) {
			if (abilitys[j] == -1) {
				continue;
			}
			SRPGAbilityFactory ability = SRPGAbilityFactory
					.getInstance(abilitys[j]);
			if ((ability.getMP(status) <= status.mp || status.mp == -1)
					&& (ability.getMP() <= 0 || !flag1)
					&& (flag ? ability.getCounter() != 2
							: ability.getCounter() != 1
									&& ability.getTarget() != 1)) {
				result = (int[]) CollectionUtils.expand(result, 1);
				result[index] = abilitys[j];
				index++;
			}
		}
		if (index == 0) {
			return null;
		}
		return result;
	}

	public boolean filtedActor(SRPGActor actor, SRPGActor actor1) {
		int target = getTarget();
		if (target == 2) {
			return true;
		}
		if (actor.getActorStatus().group == actor1.getActorStatus().group) {
			if (target == 1) {
				return true;
			}
		} else if (target == 0) {
			return true;
		}
		return false;
	}

	public static int[] filtedRange(int[] range, SRPGField field, int x1,
			int y1, int x2, int y2) {
		if (range == null) {
			return null;
		}
		int[] result = new int[0];
		int index = 0;
		for (int j = 0; j < range.length; j++) {
			if (range[j] == -1) {
				continue;
			}
			SRPGAbilityFactory ability = SRPGAbilityFactory
					.getInstance(range[j]);
			if (ability.checkTargetTrue(field, x1, y1, x2, y2)) {
				result = (int[]) CollectionUtils.expand(result, 1);
				result[index] = range[j];
				index++;
			}
		}
		if (index == 0) {
			return null;
		}
		if (index == range.length) {
			return result;
		}
		return result;
	}

	public static int[] filtedRange(int[] range, int x1, int y1, int x2, int y2) {
		int x = x1 - x2;
		int y = y1 - y2;
		if (x < 0) {
			x *= -1;
		}
		if (y < 0) {
			y *= -1;
		}
		return filtedRange(range, x + y);
	}

	public static int[] filtedRange(int[] range, int i) {
		if (range == null) {
			return null;
		}
		int[] result = new int[0];
		int index = 0;
		for (int j = 0; j < range.length; j++) {
			if (range[j] == -1) {
				continue;
			}
			SRPGAbilityFactory ability = SRPGAbilityFactory
					.getInstance(range[j]);
			if (ability.getMinLength() <= i && ability.getMaxLength() >= i) {
				result = (int[]) CollectionUtils.expand(result, 1);
				result[index] = range[j];
				index++;
			}
		}

		if (index == 0) {
			return null;
		}
		if (index == range.length) {
			return range;
		}
		return result;
	}

	public boolean checkTargetTrue(SRPGField field, int x1, int y1, int x2,
			int y2) {
		int r = setAttackRange(field, x1, y1)[y2][x2];
		return r != -1 && r >= getMinLength() && r <= getMaxLength();
	}

	public static int[][] setAttackRange(int[] range, SRPGField field, int x,
			int y) {
		SRPGFieldMove fieldMove = SRPGFieldMove.getInstance(field
				.getMoveSpace(19));
		int[][] result = new int[field.getHeight()][field.getWidth()];
		int index = 1;
		for (int j = 0; j < range.length; j++) {
			if (range[j] == -1) {
				continue;
			}
			int n = (SRPGAbilityFactory.getInstance(range[j])).getMaxLength();
			if (index < n) {
				index = n;
			}
		}
		int[][] moves = fieldMove.movePower(x, y, index);
		again: for (int j = 0; j < range.length; j++) {
			if (range[j] == -1) {
				continue;
			}
			SRPGAbilityFactory ability = SRPGAbilityFactory
					.getInstance(range[j]);
			int t = ability.getTarget() + 1;
			int d = ability.getMaxLength();
			int d1 = ability.getMinLength();
			int count = 0;
			do {
				if (count >= result.length) {
					continue again;
				}
				for (int c = 0; c < result[0].length; c++) {
					if (moves[count][c] >= d1 && moves[count][c] <= d) {
						result[count][c] |= t;
					}
				}
				count++;
			} while (true);
		}

		return result;
	}

	public int[][] setAttackRange(SRPGField field, int x, int y) {
		SRPGFieldMove fieldmove = SRPGFieldMove.getInstance(field
				.getMoveSpace(19));
		return fieldmove.movePower(x, y, getMaxLength());
	}

	public boolean[][] setTrueRange(SRPGField field, int x, int y) {
		int[][] range = setAttackRange(field, x, y);
		boolean[][] res = new boolean[field.getHeight()][field.getWidth()];
		int max = getMaxLength();
		int min = getMinLength();
		for (int j = 0; j < field.getHeight(); j++) {
			for (int i = 0; i < field.getWidth(); i++) {
				if (range[j][i] >= min && range[j][i] <= max
						&& range[j][i] != -1) {
					res[j][i] = true;
				} else {
					res[j][i] = false;
				}
			}
		}
		return res;
	}

	public static int[][] setTargetRange(SRPGAbilityFactory ability,
			SRPGField field, int x, int y) {
		return ability.setTargetRange(field, x, y);
	}

	public int[][] setTargetRange(SRPGField field, int x, int y) {
		SRPGFieldMove fieldmove = SRPGFieldMove.getInstance(field
				.getMoveSpace(19));
		return fieldmove.movePower(x, y, getRange());
	}

	public static int[] getTargetTrue(int[] target, int x, int y) {
		if (target == null) {
			return null;
		}
		int[] result = new int[0];
		int index = 0;
		for (int j = 0; j < target.length; j++) {
			if (target[j] != -1
					&& (SRPGAbilityFactory.getInstance(target[j]))
							.getTargetTrue(x, y)) {
				result = (int[]) CollectionUtils.expand(result, 1);
				result[index] = target[j];
				index++;
			}
		}
		if (index == 0) {
			return null;
		}
		return result;
	}

	public boolean getTargetTrue(int x, int y) {
		int target = getTarget();
		return (target != 0 || x != y) && (target != 1 || x == y);
	}

}

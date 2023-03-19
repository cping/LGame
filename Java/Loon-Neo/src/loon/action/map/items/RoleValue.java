/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.map.items;

import loon.LSystem;
import loon.utils.MathUtils;

/**
 * 一个基本的游戏角色数值模板,可以套用其扩展自己的游戏属性以及属性变更算法
 *
 */
public abstract class RoleValue {

	private final int _id;
	private String _roleName;

	protected int maxHealth;
	protected int maxMana;
	protected int health;
	protected int mana;
	protected int attack;
	protected int defence;
	protected int strength;
	protected int intelligence;
	protected int agility;
	protected int fitness;
	protected int dexterity;
	protected int level;
	protected int team = Team.Unknown;
	protected int movePoints;
	protected int turnPoints;
	protected int actionPoints;

	protected boolean isAttack;
	protected boolean isDefense;
	protected boolean isSkill;
	protected boolean isMoved;
	protected boolean isDead;

	protected RoleInfo info;

	public RoleValue(int id, RoleInfo info, int maxHealth, int maxMana, int attack, int defence, int strength,
			int intelligence, int fitness, int dexterity, int agility) {
		this(id, LSystem.UNKNOWN, info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity,
				agility);
	}

	public RoleValue(int id, String name, RoleInfo info, int maxHealth, int maxMana, int attack, int defence,
			int strength, int intelligence, int fitness, int dexterity, int agility) {
		this._id = id;
		this._roleName = name;
		this.info = info;
		this.maxHealth = maxHealth;
		this.maxMana = maxMana;
		this.health = maxHealth;
		this.mana = maxMana;
		this.agility = agility;
		this.attack = attack;
		this.defence = defence;
		this.strength = strength;
		this.intelligence = intelligence;
		this.fitness = fitness;
		this.dexterity = dexterity;
	}

	public float updateTurnPoints() {
		int randomBuffer = MathUtils.nextInt(100);
		this.turnPoints += this.fitness + randomBuffer / 100;
		if (this.turnPoints > 100) {
			this.turnPoints = 100;
		}
		return this.turnPoints;
	}

	public int calculateDamage(int enemyDefence) {
		int damageBufferMax = 20;
		float damage = this.attack + 0.5f * this.strength - 0.5f * enemyDefence;
		if ((damage = MathUtils.ceil(this.variance(damage, damageBufferMax, true))) < 1f) {
			damage = 1f;
		}
		return (int) damage;
	}

	public int hit(int enemyDex, int enemyAgi, int enemyFitness) {
		int maxChance = 95;
		int minChance = 15;
		float hitChance = 55f;
		hitChance += (this.dexterity - enemyDex) + 0.5 * (this.fitness - enemyFitness) - enemyAgi;
		if ((hitChance = this.variance(hitChance, 10, true)) > maxChance) {
			hitChance = maxChance;
		} else if (hitChance < minChance) {
			hitChance = minChance;
		}
		return MathUtils.ceil(hitChance);
	}

	public RoleValue damage(float damageTaken) {
		this.health = (int) ((float) this.health - damageTaken);
		return this;
	}

	public boolean flee(int enemyLevel, int enemyFitness) {
		int maxChance = 95;
		int minChance = 5;
		int baseChance = 55;
		int fleeChance = baseChance - 3 * (enemyFitness - this.fitness);
		if (fleeChance > maxChance) {
			fleeChance = maxChance;
		} else if (fleeChance < minChance) {
			fleeChance = minChance;
		}
		int fleeRoll = MathUtils.nextInt(100);
		if (fleeRoll <= fleeChance) {
			return true;
		}
		return false;
	}

	public int getID() {
		return _id;
	}

	public RoleValue heal() {
		int healCost = 5;
		int healAmount = 20;
		if (this.getMana() >= healCost) {
			healAmount = (int) this.variance(healAmount, 20, true);
			this.health += healAmount;
			if (this.health > this.maxHealth) {
				this.health = this.maxHealth;
			}
			this.mana -= healCost;
		}
		return this;
	}

	public int regenerateMana() {
		int regen = intelligence / 4;
		int minRegen = 2;
		int maxRegen = 50;
		if (regen < minRegen) {
			regen = minRegen;
		}
		if (regen > maxRegen) {
			regen = maxRegen;
		}

		return regen;
	}

	private float variance(float base, int variance, boolean negativeAllowed) {
		if (variance < 1) {
			variance = 1;
		} else if (variance > 100) {
			variance = 100;
		}
		int buffer = MathUtils.nextInt(++variance);
		if (MathUtils.nextBoolean() && negativeAllowed) {
			buffer = -buffer;
		}
		float percent = (float) (100 - buffer) / 100.0f;
		float variedValue = base * percent;
		return variedValue;
	}

	public RoleValue updateAttack(float attackModifier) {
		this.info.updateAttack(attackModifier);
		return this;
	}

	public RoleValue updateDefence(float defenceModifier) {
		this.info.updateDefence(defenceModifier);
		return this;
	}

	public RoleValue updateStrength(float strengthModifier) {
		this.info.updateStrength(strengthModifier);
		return this;
	}

	public RoleValue updateIntelligence(float intelligenceModifier) {
		this.info.updateIntelligence(intelligenceModifier);
		return this;
	}

	public RoleValue updateFitness(float fitnessModifier) {
		this.info.updateFitness(fitnessModifier);
		return this;
	}

	public RoleValue updateDexterity(float dexterityModifier) {
		this.info.updateDexterity(dexterityModifier);
		return this;
	}

	public RoleValue updateMaxHealth(float maxHealthModifier) {
		this.info.updateMaxHealth(maxHealthModifier);
		return this;
	}

	public RoleValue updateSkillPoints(float skillModifier) {
		this.info.updateSkillPoints(skillModifier);
		return this;
	}

	public RoleValue updateManaPoints(float manaModifier) {
		this.info.updateManaPoints(manaModifier);
		return this;
	}

	public RoleValue updateAgility(float agilityModifier) {
		this.info.updateAgility(agilityModifier);
		return this;
	}

	public boolean fellow(RoleValue c) {
		if (c == null) {
			return false;
		}
		return this.team == c.team;
	}

	public boolean fellow(Team team) {
		if (team == null) {
			return false;
		}
		return this.team == team.getTeam();
	}

	public int getAttack() {
		return this.attack;
	}

	public RoleValue setAttack(int attack) {
		this.attack = attack;
		return this;
	}

	public int getMaxMana() {
		return this.maxMana;
	}

	public RoleValue setMaxMana(int maxMana) {
		this.maxMana = maxMana;
		return this;
	}

	public int getDefence() {
		return this.defence;
	}

	public RoleValue setDefence(int defence) {
		this.defence = defence;
		return this;
	}

	public int getStrength() {
		return this.strength;
	}

	public RoleValue setStrength(int strength) {
		this.strength = strength;
		return this;
	}

	public int getIntelligence() {
		return this.intelligence;
	}

	public RoleValue setIntelligence(int intelligence) {
		this.intelligence = intelligence;
		return this;
	}

	public int getFitness() {
		return this.fitness;
	}

	public RoleValue setFitness(int fitness) {
		this.fitness = fitness;
		return this;
	}

	public int getDexterity() {
		return this.dexterity;
	}

	public RoleValue setDexterity(int dexterity) {
		this.dexterity = dexterity;
		return this;
	}

	public RoleValue setHealth(int health) {
		this.health = health;
		return this;
	}

	public RoleValue setMana(int mana) {
		this.mana = mana;
		return this;
	}

	public float getTurnPoints() {
		return this.turnPoints;
	}

	public RoleValue setTurnPoints(int turnPoints) {
		this.turnPoints = turnPoints;
		return this;
	}

	public int getLevel() {
		return this.level;
	}

	public int getHealth() {
		return this.health;
	}

	public int getMana() {
		return this.mana;
	}

	public int getBaseMaxHealth() {
		return this.info.getBaseMaxHealth();
	}

	public RoleValue setBaseMaxHealth(int baseMaxHealth) {
		this.info.setBaseMaxHealth(baseMaxHealth);
		return this;
	}

	public int getEquipMaxHealth() {
		return this.info.getEquipMaxHealth();
	}

	public RoleValue setEquipMaxHealth(int equipMaxHealth) {
		this.info.setEquipMaxHealth(equipMaxHealth);
		return this;
	}

	public int getMaxHealth() {
		return this.maxHealth;
	}

	public RoleValue setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public int getAgility() {
		return agility;
	}

	public RoleValue setAgility(int agility) {
		this.agility = agility;
		return this;
	}

	public int getTeam() {
		return team;
	}

	public RoleValue setTeam(int team) {
		this.team = team;
		return this;
	}

	public int getMovePoints() {
		return movePoints;
	}

	public RoleValue setMovePoints(int movePoints) {
		this.movePoints = movePoints;
		return this;
	}

	public boolean isAllDoneAction() {
		return isAttack && isDefense && isMoved && isSkill;
	}

	public boolean isAllUnDoneAction() {
		return !isAttack && !isDefense && !isMoved && !isSkill;
	}

	public RoleValue undoneAction() {
		setAttack(false);
		setDefense(false);
		setSkill(false);
		setMoved(false);
		return this;
	}

	public RoleValue doneAction() {
		setAttack(true);
		setDefense(true);
		setSkill(true);
		setMoved(true);
		return this;
	}

	public boolean isAttack() {
		return isAttack;
	}

	public RoleValue setAttack(boolean isAttack) {
		this.isAttack = isAttack;
		return this;
	}

	public boolean isDefense() {
		return isDefense;
	}

	public RoleValue setDefense(boolean defense) {
		this.isDefense = defense;
		return this;
	}

	public boolean isSkill() {
		return isSkill;
	}

	public RoleValue setSkill(boolean skill) {
		this.isSkill = skill;
		return this;
	}

	public boolean isMoved() {
		return isMoved;
	}

	public RoleValue setMoved(boolean moved) {
		this.isMoved = moved;
		return this;
	}

	public boolean isDead() {
		return this.isDead;
	}

	public RoleValue setDead(boolean dead) {
		this.isDead = dead;
		return this;
	}

	public RoleInfo getInfo() {
		return info;
	}

	public RoleValue setInfo(RoleInfo info) {
		this.info = info;
		return this;
	}

	public RoleValue setLevel(int level) {
		this.level = level;
		return this;
	}

	public int getActionPoints() {
		return actionPoints;
	}

	public RoleValue setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
		return this;
	}

	public String getRoleName() {
		return _roleName;
	}

	public RoleValue setRoleName(String n) {
		this._roleName = n;
		return this;
	}
}

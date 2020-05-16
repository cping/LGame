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
package loon.action.map;

import loon.LSystem;
import loon.utils.MathUtils;

/**
 * 一个基本的游戏角色数值模板,可以套用其扩展自己的游戏属性以及属性变更算法
 *
 */
public abstract class CharacterValue {

	private String roleName;
	
	private int maxHealth;
	private int maxMana;
	private int health;
	private int mana;
	private int attack;
	private int defence;
	private int strength;
	private int intelligence;
	private int agility;
	private int fitness;
	private int dexterity;
	private int level;
	private int team;
	private int movePoints;
	private int turnPoints;
	private int actionPoints;

	private boolean isAttack;
	private boolean isDefense;
	private boolean isSkill;
	private boolean isMoved;

	private CharacterInfo info;

	public CharacterValue(CharacterInfo info, int maxHealth, int maxMana, int attack, int defence, int strength,
			int intelligence, int fitness, int dexterity, int agility) {
		this(LSystem.UNKNOWN, info, maxHealth, maxMana, attack, defence, strength, intelligence, fitness, dexterity,
				agility);
	}

	public CharacterValue(String name, CharacterInfo info, int maxHealth, int maxMana, int attack, int defence,
			int strength, int intelligence, int fitness, int dexterity, int agility) {
		this.roleName = name;
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

	public void damage(float damageTaken) {
		this.health = (int) ((float) this.health - damageTaken);
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

	public void heal() {
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

	public void updateAttack(float attackModifier) {
		this.info.updateAttack(attackModifier);
	}

	public void updateDefence(float defenceModifier) {
		this.info.updateDefence(defenceModifier);
	}

	public void updateStrength(float strengthModifier) {
		this.info.updateStrength(strengthModifier);
	}

	public void updateIntelligence(float intelligenceModifier) {
		this.info.updateIntelligence(intelligenceModifier);
	}

	public void updateFitness(float fitnessModifier) {
		this.info.updateFitness(fitnessModifier);
	}

	public void updateDexterity(float dexterityModifier) {
		this.info.updateDexterity(dexterityModifier);
	}

	public void updateMaxHealth(float maxHealthModifier) {
		this.info.updateMaxHealth(maxHealthModifier);
	}

	public void updateSkillPoints(float skillModifier) {
		this.info.updateSkillPoints(skillModifier);
	}

	public void updateManaPoints(float manaModifier) {
		this.info.updateManaPoints(manaModifier);
	}

	public void updateAgility(float agilityModifier) {
		this.info.updateAgility(agilityModifier);
	}

	public int getAttack() {
		return this.attack;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public int getMaxMana() {
		return this.maxMana;
	}

	public void setMaxMana(int maxMana) {
		this.maxMana = maxMana;
	}

	public int getDefence() {
		return this.defence;
	}

	public void setDefence(int defence) {
		this.defence = defence;
	}

	public int getStrength() {
		return this.strength;
	}

	public void setStrength(int strength) {
		this.strength = strength;
	}

	public int getIntelligence() {
		return this.intelligence;
	}

	public void setIntelligence(int intelligence) {
		this.intelligence = intelligence;
	}

	public int getFitness() {
		return this.fitness;
	}

	public void setFitness(int fitness) {
		this.fitness = fitness;
	}

	public int getDexterity() {
		return this.dexterity;
	}

	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public void setMana(int mana) {
		this.mana = mana;
	}

	public float getTurnPoints() {
		return this.turnPoints;
	}

	public void setTurnPoints(int turnPoints) {
		this.turnPoints = turnPoints;
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

	public void setBaseMaxHealth(int baseMaxHealth) {
		this.info.setBaseMaxHealth(baseMaxHealth);
	}

	public int getEquipMaxHealth() {
		return this.info.getEquipMaxHealth();
	}

	public void setEquipMaxHealth(int equipMaxHealth) {
		this.info.setEquipMaxHealth(equipMaxHealth);
	}

	public int getMaxHealth() {
		return this.maxHealth;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getAgility() {
		return agility;
	}

	public void setAgility(int agility) {
		this.agility = agility;
	}

	public int getTeam() {
		return team;
	}

	public void setTeam(int team) {
		this.team = team;
	}

	public int getMovePoints() {
		return movePoints;
	}

	public void setMovePoints(int movePoints) {
		this.movePoints = movePoints;
	}

	public boolean isAttack() {
		return isAttack;
	}

	public void setAttack(boolean isAttack) {
		this.isAttack = isAttack;
	}

	public boolean isDefense() {
		return isDefense;
	}

	public void setDefense(boolean isDefense) {
		this.isDefense = isDefense;
	}

	public boolean isSkill() {
		return isSkill;
	}

	public void setSkill(boolean isSkill) {
		this.isSkill = isSkill;
	}

	public boolean isMoved() {
		return isMoved;
	}

	public void setMoved(boolean isMoved) {
		this.isMoved = isMoved;
	}

	public CharacterInfo getInfo() {
		return info;
	}

	public void setInfo(CharacterInfo info) {
		this.info = info;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getActionPoints() {
		return actionPoints;
	}

	public void setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}
}

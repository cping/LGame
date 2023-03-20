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

/**
 * 一个简单的角色基本附加参数类,用于简单的rpg类游戏角色属性配置(和RoleValue的关系在于这个更接近装备参数)
 */
public class RoleEquip {
	// 基本生命值
	private int _baseMaxHealth;
	// 基本攻击力
	private int _baseAttack;
	// 基本防御力
	private int _baseDefence;
	// 基本力量
	private int _baseStrength;
	// 基本智力(挂钩特技与魔力)
	private int _baseIntelligence;
	// 基本特技(为0时无法用特技)
	private int _baseSkillPoints;
	// 基本魔力(为0时无法用魔力)
	private int _baseManaPoint;
	// 健康值(挂钩掉血与能力下降,简单设定上逃跑之类行为会降低)
	private int _baseFitness;
	// 基本灵敏(挂钩命中)
	private int _baseDexterity;
	// 基本敏捷(挂钩闪避)
	private int _baseAgility;

	// 在基础参数上的装备附加
	private int _equipMaxHealth;
	private int _equipAttack;
	private int _equipDefence;
	private int _equipStrength;
	private int _equipIntelligence;
	private int _equipFitness;
	private int _equipDexterity;
	private int _equipSkillPoints;
	private int _equipManaPoint;
	private int _equipAgility;

	// 角色性别
	private int _sexType;
	// 角色年龄
	private int _ageValue;

	public RoleEquip(int maxHealth, int maxMana, int attack, int defence, int strength, int intelligence, int fitness,
			int dexterity, int agility, int skill) {
		this._baseMaxHealth = maxHealth;
		this._baseAttack = attack;
		this._baseDefence = defence;
		this._baseStrength = strength;
		this._baseIntelligence = intelligence;
		this._baseManaPoint = maxMana;
		this._baseSkillPoints = skill;
		this._baseFitness = fitness;
		this._baseDexterity = dexterity;
		this._baseAgility = agility;
	}

	public RoleEquip setEquip(int maxHealth, int maxMana, int attack, int defence, int strength, int intelligence,
			int fitness, int dexterity, int agility, int skill) {
		this._equipMaxHealth = maxHealth;
		this._equipAttack = attack;
		this._equipDefence = defence;
		this._equipStrength = strength;
		this._equipIntelligence = intelligence;
		this._equipManaPoint = maxMana;
		this._equipSkillPoints = skill;
		this._equipFitness = fitness;
		this._equipDexterity = dexterity;
		this._equipAgility = agility;
		return this;
	}

	public int updateAttack(float attackModifier) {
		return (int) (attackModifier * (this._baseAttack + this._equipAttack));
	}

	public int updateDefence(float defenceModifier) {
		return (int) (defenceModifier * (this._baseDefence + this._equipDefence));
	}

	public int updateStrength(float strengthModifier) {
		return (int) (strengthModifier * (this._baseStrength + this._equipStrength));
	}

	public int updateIntelligence(float intelligenceModifier) {
		return (int) (intelligenceModifier * (this._baseIntelligence + this._equipIntelligence));
	}

	public int updateFitness(float fitnessModifier) {
		return (int) (fitnessModifier * (this._baseFitness + this._equipFitness));
	}

	public int updateSkillPoints(float skillModifier) {
		return (int) (skillModifier * (this._baseSkillPoints + this._equipSkillPoints));
	}

	public int updateManaPoints(float manaModifier) {
		return (int) (manaModifier * (this._baseManaPoint + this._equipManaPoint));
	}

	public int updateDexterity(float dexterityModifier) {
		return (int) (dexterityModifier * (this._baseDexterity + this._equipDexterity));
	}

	public int updateMaxHealth(float maxHealthModifier) {
		return (int) (maxHealthModifier * (this._baseMaxHealth + this._equipMaxHealth));
	}

	public int updateAgility(float agilityModifier) {
		return (int) (agilityModifier * (this._baseAgility + this._equipAgility));
	}

	public int getBaseMaxHealth() {
		return this._baseMaxHealth;
	}

	public RoleEquip setBaseMaxHealth(int h) {
		this._baseMaxHealth = h;
		return this;
	}

	public int getBaseAttack() {
		return this._baseAttack;
	}

	public RoleEquip setBaseAttack(int a) {
		this._baseAttack = a;
		return this;
	}

	public int getBaseDefence() {
		return this._baseDefence;
	}

	public RoleEquip setBaseDefence(int d) {
		this._baseDefence = d;
		return this;
	}

	public int getBaseStrength() {
		return this._baseStrength;
	}

	public RoleEquip setBaseStrength(int s) {
		this._baseStrength = s;
		return this;
	}

	public int getBaseIntelligence() {
		return this._baseIntelligence;
	}

	public RoleEquip setBaseIntelligence(int i) {
		this._baseIntelligence = i;
		return this;
	}

	public int getBaseFitness() {
		return this._baseFitness;
	}

	public RoleEquip setBaseFitness(int f) {
		this._baseFitness = f;
		return this;
	}

	public int getBaseDexterity() {
		return this._baseDexterity;
	}

	public RoleEquip setBaseDexterity(int d) {
		this._baseDexterity = d;
		return this;
	}

	public int getEquipMaxHealth() {
		return this._equipMaxHealth;
	}

	public RoleEquip setEquipMaxHealth(int m) {
		this._equipMaxHealth = m;
		return this;
	}

	public int getEquipAttack() {
		return this._equipAttack;
	}

	public RoleEquip setEquipAttack(int a) {
		this._equipAttack = a;
		return this;
	}

	public int getEquipDefence() {
		return this._equipDefence;
	}

	public RoleEquip setEquipDefence(int d) {
		this._equipDefence = d;
		return this;
	}

	public int getEquipStrength() {
		return this._equipStrength;
	}

	public RoleEquip setEquipStrength(int s) {
		this._equipStrength = s;
		return this;
	}

	public int getEquipIntelligence() {
		return this._equipIntelligence;
	}

	public RoleEquip setEquipIntelligence(int e) {
		this._equipIntelligence = e;
		return this;
	}

	public int getEquipFitness() {
		return this._equipFitness;
	}

	public RoleEquip setEquipFitness(int f) {
		this._equipFitness = f;
		return this;
	}

	public int getEquipDexterity() {
		return this._equipDexterity;
	}

	public RoleEquip setEquipDexterity(int d) {
		this._equipDexterity = d;
		return this;
	}

	public int getBaseSkillPoints() {
		return _baseSkillPoints;
	}

	public RoleEquip setBaseSkillPoints(int s) {
		this._baseSkillPoints = s;
		return this;
	}

	public int getBaseManaPoint() {
		return _baseManaPoint;
	}

	public RoleEquip setBaseManaPoint(int m) {
		this._baseManaPoint = m;
		return this;
	}

	public int getBaseAgility() {
		return _baseAgility;
	}

	public RoleEquip setBaseAgility(int a) {
		this._baseAgility = a;
		return this;
	}

	public int getEquipSkillPoints() {
		return _equipSkillPoints;
	}

	public RoleEquip setEquipSkillPoints(int s) {
		this._equipSkillPoints = s;
		return this;
	}

	public int getEquipManaPoint() {
		return _equipManaPoint;
	}

	public RoleEquip setEquipManaPoint(int m) {
		this._equipManaPoint = m;
		return this;
	}

	public int getEquipAgility() {
		return _equipAgility;
	}

	public RoleEquip setEquipAgility(int a) {
		this._equipAgility = a;
		return this;
	}

	public RoleEquip setSexType(int s) {
		this._sexType = s;
		return this;
	}

	public int getSexType() {
		return this._sexType;
	}

	public RoleEquip setAgeValue(int a) {
		this._ageValue = a;
		return this;
	}

	public int getAgeValue() {
		return _ageValue;
	}

}

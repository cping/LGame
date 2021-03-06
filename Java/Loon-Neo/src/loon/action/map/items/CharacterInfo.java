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
 * 一个简单的角色基本参数类,用于简单的rpg类游戏角色属性配置
 */
public class CharacterInfo {
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

	public int updateAttack(float attackModifier) {
		return (int) (attackModifier * (float) (this._baseAttack + this._equipAttack));
	}

	public int updateDefence(float defenceModifier) {
		return (int) (defenceModifier * (float) (this._baseDefence + this._equipDefence));
	}

	public int updateStrength(float strengthModifier) {
		return (int) (strengthModifier * (float) (this._baseStrength + this._equipStrength));
	}

	public int updateIntelligence(float intelligenceModifier) {
		return (int) (intelligenceModifier * (float) (this._baseIntelligence + this._equipIntelligence));
	}

	public int updateFitness(float fitnessModifier) {
		return (int) (fitnessModifier * (float) (this._baseFitness + this._equipFitness));
	}

	public int updateSkillPoints(float skillModifier) {
		return (int) (skillModifier * (float) (this._baseSkillPoints + this._equipSkillPoints));
	}

	public int updateManaPoints(float manaModifier) {
		return (int) (manaModifier * (float) (this._baseManaPoint + this._equipManaPoint));
	}

	public int updateDexterity(float dexterityModifier) {
		return (int) (dexterityModifier * (float) (this._baseDexterity + this._equipDexterity));
	}

	public int updateMaxHealth(float maxHealthModifier) {
		return (int) (maxHealthModifier * (float) (this._baseMaxHealth + this._equipMaxHealth));
	}

	public int updateAgility(float agilityModifier) {
		return (int) (agilityModifier * (float) (this._baseAgility + this._equipAgility));
	}
	
	public int getBaseMaxHealth() {
		return this._baseMaxHealth;
	}

	public CharacterInfo setBaseMaxHealth(int h) {
		this._baseMaxHealth = h;
		return this;
	}

	public int getBaseAttack() {
		return this._baseAttack;
	}

	public CharacterInfo setBaseAttack(int a) {
		this._baseAttack = a;
		return this;
	}

	public int getBaseDefence() {
		return this._baseDefence;
	}

	public CharacterInfo setBaseDefence(int d) {
		this._baseDefence = d;
		return this;
	}

	public int getBaseStrength() {
		return this._baseStrength;
	}

	public CharacterInfo setBaseStrength(int s) {
		this._baseStrength = s;
		return this;
	}

	public int getBaseIntelligence() {
		return this._baseIntelligence;
	}

	public CharacterInfo setBaseIntelligence(int i) {
		this._baseIntelligence = i;
		return this;
	}

	public int getBaseFitness() {
		return this._baseFitness;
	}

	public CharacterInfo setBaseFitness(int f) {
		this._baseFitness = f;
		return this;
	}

	public int getBaseDexterity() {
		return this._baseDexterity;
	}

	public CharacterInfo setBaseDexterity(int d) {
		this._baseDexterity = d;
		return this;
	}

	public int getEquipMaxHealth() {
		return this._equipMaxHealth;
	}

	public CharacterInfo setEquipMaxHealth(int m) {
		this._equipMaxHealth = m;
		return this;
	}

	public int getEquipAttack() {
		return this._equipAttack;
	}

	public CharacterInfo setEquipAttack(int a) {
		this._equipAttack = a;
		return this;
	}

	public int getEquipDefence() {
		return this._equipDefence;
	}

	public CharacterInfo setEquipDefence(int d) {
		this._equipDefence = d;
		return this;
	}

	public int getEquipStrength() {
		return this._equipStrength;
	}

	public CharacterInfo setEquipStrength(int s) {
		this._equipStrength = s;
		return this;
	}

	public int getEquipIntelligence() {
		return this._equipIntelligence;
	}

	public CharacterInfo setEquipIntelligence(int e) {
		this._equipIntelligence = e;
		return this;
	}

	public int getEquipFitness() {
		return this._equipFitness;
	}

	public CharacterInfo setEquipFitness(int f) {
		this._equipFitness = f;
		return this;
	}

	public int getEquipDexterity() {
		return this._equipDexterity;
	}

	public CharacterInfo setEquipDexterity(int d) {
		this._equipDexterity = d;
		return this;
	}

	public int getBaseSkillPoints() {
		return _baseSkillPoints;
	}

	public CharacterInfo setBaseSkillPoints(int s) {
		this._baseSkillPoints = s;
		return this;
	}

	public int getBaseManaPoint() {
		return _baseManaPoint;
	}

	public CharacterInfo setBaseManaPoint(int m) {
		this._baseManaPoint = m;
		return this;
	}

	public int getBaseAgility() {
		return _baseAgility;
	}

	public CharacterInfo setBaseAgility(int a) {
		this._baseAgility = a;
		return this;
	}

	public int getEquipSkillPoints() {
		return _equipSkillPoints;
	}

	public CharacterInfo setEquipSkillPoints(int s) {
		this._equipSkillPoints = s;
		return this;
	}

	public int getEquipManaPoint() {
		return _equipManaPoint;
	}

	public CharacterInfo setEquipManaPoint(int m) {
		this._equipManaPoint = m;
		return this;
	}

	public int getEquipAgility() {
		return _equipAgility;
	}

	public CharacterInfo setEquipAgility(int a) {
		this._equipAgility = a;
		return this;
	}

	public CharacterInfo setSexType(int s) {
		this._sexType = s;
		return this;
	}

	public int getSexType() {
		return this._sexType;
	}

	public CharacterInfo setAgeValue(int a) {
		this._ageValue = a;
		return this;
	}
	
	public int getAgeValue() {
		return _ageValue;
	}

}

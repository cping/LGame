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
	private int baseMaxHealth;
	// 基本攻击力
	private int baseAttack;
	// 基本防御力
	private int baseDefence;
	// 基本力量
	private int baseStrength;
	// 基本智力(挂钩特技与魔力)
	private int baseIntelligence;
	// 基本特技(为0时无法用特技)
	private int baseSkillPoints;
	// 基本魔力(为0时无法用魔力)
	private int baseManaPoint;
	// 健康值(挂钩掉血与能力下降,简单设定上逃跑之类行为会降低)
	private int baseFitness;
	// 基本灵敏(挂钩命中)
	private int baseDexterity;
	// 基本敏捷(挂钩闪避)
	private int baseAgility;

	// 在基础参数上的装备附加
	private int equipMaxHealth;
	private int equipAttack;
	private int equipDefence;
	private int equipStrength;
	private int equipIntelligence;
	private int equipFitness;
	private int equipDexterity;
	private int equipSkillPoints;
	private int equipManaPoint;
	private int equipAgility;

	// 角色性别
	private int sexType;
	// 角色年龄
	private int ageValue;

	public int updateAttack(float attackModifier) {
		return (int) (attackModifier * (float) (this.baseAttack + this.equipAttack));
	}

	public int updateDefence(float defenceModifier) {
		return (int) (defenceModifier * (float) (this.baseDefence + this.equipDefence));
	}

	public int updateStrength(float strengthModifier) {
		return (int) (strengthModifier * (float) (this.baseStrength + this.equipStrength));
	}

	public int updateIntelligence(float intelligenceModifier) {
		return (int) (intelligenceModifier * (float) (this.baseIntelligence + this.equipIntelligence));
	}

	public int updateFitness(float fitnessModifier) {
		return (int) (fitnessModifier * (float) (this.baseFitness + this.equipFitness));
	}

	public int updateSkillPoints(float skillModifier) {
		return (int) (skillModifier * (float) (this.baseSkillPoints + this.equipSkillPoints));
	}

	public int updateManaPoints(float manaModifier) {
		return (int) (manaModifier * (float) (this.baseManaPoint + this.equipManaPoint));
	}

	public int updateDexterity(float dexterityModifier) {
		return (int) (dexterityModifier * (float) (this.baseDexterity + this.equipDexterity));
	}

	public int updateMaxHealth(float maxHealthModifier) {
		return (int) (maxHealthModifier * (float) (this.baseMaxHealth + this.equipMaxHealth));
	}

	public int updateAgility(float agilityModifier) {
		return (int) (agilityModifier * (float) (this.baseAgility + this.equipAgility));
	}
	
	public int getBaseMaxHealth() {
		return this.baseMaxHealth;
	}

	public CharacterInfo setBaseMaxHealth(int baseMaxHealth) {
		this.baseMaxHealth = baseMaxHealth;
		return this;
	}

	public int getBaseAttack() {
		return this.baseAttack;
	}

	public CharacterInfo setBaseAttack(int baseAttack) {
		this.baseAttack = baseAttack;
		return this;
	}

	public int getBaseDefence() {
		return this.baseDefence;
	}

	public CharacterInfo setBaseDefence(int baseDefence) {
		this.baseDefence = baseDefence;
		return this;
	}

	public int getBaseStrength() {
		return this.baseStrength;
	}

	public CharacterInfo setBaseStrength(int baseStrength) {
		this.baseStrength = baseStrength;
		return this;
	}

	public int getBaseIntelligence() {
		return this.baseIntelligence;
	}

	public CharacterInfo setBaseIntelligence(int baseIntelligence) {
		this.baseIntelligence = baseIntelligence;
		return this;
	}

	public int getBaseFitness() {
		return this.baseFitness;
	}

	public CharacterInfo setBaseFitness(int baseFitness) {
		this.baseFitness = baseFitness;
		return this;
	}

	public int getBaseDexterity() {
		return this.baseDexterity;
	}

	public CharacterInfo setBaseDexterity(int baseDexterity) {
		this.baseDexterity = baseDexterity;
		return this;
	}

	public int getEquipMaxHealth() {
		return this.equipMaxHealth;
	}

	public CharacterInfo setEquipMaxHealth(int equipMaxHealth) {
		this.equipMaxHealth = equipMaxHealth;
		return this;
	}

	public int getEquipAttack() {
		return this.equipAttack;
	}

	public CharacterInfo setEquipAttack(int equipAttack) {
		this.equipAttack = equipAttack;
		return this;
	}

	public int getEquipDefence() {
		return this.equipDefence;
	}

	public CharacterInfo setEquipDefence(int equipDefence) {
		this.equipDefence = equipDefence;
		return this;
	}

	public int getEquipStrength() {
		return this.equipStrength;
	}

	public CharacterInfo setEquipStrength(int equipStrength) {
		this.equipStrength = equipStrength;
		return this;
	}

	public int getEquipIntelligence() {
		return this.equipIntelligence;
	}

	public CharacterInfo setEquipIntelligence(int equipIntelligence) {
		this.equipIntelligence = equipIntelligence;
		return this;
	}

	public int getEquipFitness() {
		return this.equipFitness;
	}

	public CharacterInfo setEquipFitness(int equipFitness) {
		this.equipFitness = equipFitness;
		return this;
	}

	public int getEquipDexterity() {
		return this.equipDexterity;
	}

	public CharacterInfo setEquipDexterity(int equipDexterity) {
		this.equipDexterity = equipDexterity;
		return this;
	}

	public int getBaseSkillPoints() {
		return baseSkillPoints;
	}

	public CharacterInfo setBaseSkillPoints(int baseSkillPoints) {
		this.baseSkillPoints = baseSkillPoints;
		return this;
	}

	public int getBaseManaPoint() {
		return baseManaPoint;
	}

	public CharacterInfo setBaseManaPoint(int baseManaPoint) {
		this.baseManaPoint = baseManaPoint;
		return this;
	}

	public int getBaseAgility() {
		return baseAgility;
	}

	public CharacterInfo setBaseAgility(int baseAgility) {
		this.baseAgility = baseAgility;
		return this;
	}

	public int getEquipSkillPoints() {
		return equipSkillPoints;
	}

	public CharacterInfo setEquipSkillPoints(int equipSkillPoints) {
		this.equipSkillPoints = equipSkillPoints;
		return this;
	}

	public int getEquipManaPoint() {
		return equipManaPoint;
	}

	public CharacterInfo setEquipManaPoint(int equipManaPoint) {
		this.equipManaPoint = equipManaPoint;
		return this;
	}

	public int getEquipAgility() {
		return equipAgility;
	}

	public CharacterInfo setEquipAgility(int equipAgility) {
		this.equipAgility = equipAgility;
		return this;
	}

	public CharacterInfo setSexType(int sextype) {
		this.sexType = sextype;
		return this;
	}

	public int getSexType() {
		return this.sexType;
	}

	public CharacterInfo setAgeValue(int agevalue) {
		this.ageValue = agevalue;
		return this;
	}
	
	public int getAgeValue() {
		return ageValue;
	}

}

package loon.action.map;

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

	public void setBaseMaxHealth(int baseMaxHealth) {
		this.baseMaxHealth = baseMaxHealth;
	}

	public int getBaseAttack() {
		return this.baseAttack;
	}

	public void setBaseAttack(int baseAttack) {
		this.baseAttack = baseAttack;
	}

	public int getBaseDefence() {
		return this.baseDefence;
	}

	public void setBaseDefence(int baseDefence) {
		this.baseDefence = baseDefence;
	}

	public int getBaseStrength() {
		return this.baseStrength;
	}

	public void setBaseStrength(int baseStrength) {
		this.baseStrength = baseStrength;
	}

	public int getBaseIntelligence() {
		return this.baseIntelligence;
	}

	public void setBaseIntelligence(int baseIntelligence) {
		this.baseIntelligence = baseIntelligence;
	}

	public int getBaseFitness() {
		return this.baseFitness;
	}

	public void setBaseFitness(int baseFitness) {
		this.baseFitness = baseFitness;
	}

	public int getBaseDexterity() {
		return this.baseDexterity;
	}

	public void setBaseDexterity(int baseDexterity) {
		this.baseDexterity = baseDexterity;
	}

	public int getEquipMaxHealth() {
		return this.equipMaxHealth;
	}

	public void setEquipMaxHealth(int equipMaxHealth) {
		this.equipMaxHealth = equipMaxHealth;
	}

	public int getEquipAttack() {
		return this.equipAttack;
	}

	public void setEquipAttack(int equipAttack) {
		this.equipAttack = equipAttack;
	}

	public int getEquipDefence() {
		return this.equipDefence;
	}

	public void setEquipDefence(int equipDefence) {
		this.equipDefence = equipDefence;
	}

	public int getEquipStrength() {
		return this.equipStrength;
	}

	public void setEquipStrength(int equipStrength) {
		this.equipStrength = equipStrength;
	}

	public int getEquipIntelligence() {
		return this.equipIntelligence;
	}

	public void setEquipIntelligence(int equipIntelligence) {
		this.equipIntelligence = equipIntelligence;
	}

	public int getEquipFitness() {
		return this.equipFitness;
	}

	public void setEquipFitness(int equipFitness) {
		this.equipFitness = equipFitness;
	}

	public int getEquipDexterity() {
		return this.equipDexterity;
	}

	public void setEquipDexterity(int equipDexterity) {
		this.equipDexterity = equipDexterity;
	}

	public int getBaseSkillPoints() {
		return baseSkillPoints;
	}

	public void setBaseSkillPoints(int baseSkillPoints) {
		this.baseSkillPoints = baseSkillPoints;
	}

	public int getBaseManaPoint() {
		return baseManaPoint;
	}

	public void setBaseManaPoint(int baseManaPoint) {
		this.baseManaPoint = baseManaPoint;
	}

	public int getBaseAgility() {
		return baseAgility;
	}

	public void setBaseAgility(int baseAgility) {
		this.baseAgility = baseAgility;
	}

	public int getEquipSkillPoints() {
		return equipSkillPoints;
	}

	public void setEquipSkillPoints(int equipSkillPoints) {
		this.equipSkillPoints = equipSkillPoints;
	}

	public int getEquipManaPoint() {
		return equipManaPoint;
	}

	public void setEquipManaPoint(int equipManaPoint) {
		this.equipManaPoint = equipManaPoint;
	}

	public int getEquipAgility() {
		return equipAgility;
	}

	public void setEquipAgility(int equipAgility) {
		this.equipAgility = equipAgility;
	}

}

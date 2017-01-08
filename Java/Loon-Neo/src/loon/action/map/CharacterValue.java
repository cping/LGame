package loon.action.map;

import loon.utils.MathUtils;

public abstract class CharacterValue {

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
	private int turnPoints;
	private CharacterInfo info;

	public CharacterValue(CharacterInfo info,int maxHealth, int maxMana, int attack, int defence, int strength, int intelligence,
			int fitness, int dexterity, int agility) {
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
}

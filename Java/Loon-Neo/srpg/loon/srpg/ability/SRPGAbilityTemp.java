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
package loon.srpg.ability;

import java.util.ArrayList;
import java.util.Arrays;

import loon.LSystem;
import loon.canvas.LColor;
import loon.srpg.SRPGType;
import loon.srpg.actor.SRPGActor;
import loon.srpg.actor.SRPGActorFactory;
import loon.srpg.actor.SRPGActors;
import loon.srpg.actor.SRPGStatus;
import loon.srpg.effect.SRPGEffect;
import loon.srpg.effect.SRPGEffectFactory;
import loon.srpg.effect.SRPGExtinctEffect;
import loon.srpg.field.SRPGField;
import loon.srpg.field.SRPGMoveStack;
import loon.utils.MathUtils;


final class SRPGAbilityTemp {

	final static void make() {

		ArrayList<SRPGAbility> lazyAbilityClass = SRPGAbilityFactory.lazyAbilityClass;

		int index = 0;

		SRPGAbility[] abilitys = new SRPGAbility[24];

		// 0-斩击
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "斩击";
				this.abilityAbout = "仅凭手中剑给予对手物理攻击，力量不足时杀伤力非常有限";
				this.minLength = 1;
				this.maxLength = 1;
				this.mp = 0;
				this.baseDamage = 1;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				factory.damageValue = factory.status.strength / 10
						+ factory.status.dexterity / 100
						+ factory.status.vitality / 100;
				factory.damageValue = factory.damageValue > 0 ? factory.damageValue
						: baseDamage;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CHOP, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {

				return null;
			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 1-射箭
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "射箭";
				this.abilityAbout = "用弓弩射箭，看似费力,其实对使用者臂力的要求并不高";
				this.minLength = 2;
				this.maxLength = 3;
				this.mp = 0;
				this.baseDamage = 1;
				this.range = 0;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				factory.damageValue = factory.status.strength / 10
						+ factory.status.dexterity / 100
						+ factory.status.vitality / 100;
				factory.damageValue = factory.damageValue > 0 ? factory.damageValue
						: baseDamage;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_ARROW, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;
		
		// 2-连射
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "连射";
				this.abilityAbout = "向某一方向拼命射箭，看似简单,其实对使用者臂力的要求很高";
				this.minLength = 2;
				this.maxLength = 3;
				this.mp = 0;
				this.baseDamage = 1;
				this.range = 1;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				factory.damageValue = (((factory.status.dexterity * 3) / 4 + factory.status.strength / 4) * factory.atk)
						/ 100 - (factory.status1.vitality * factory.def) / 100;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_ARROW, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;


		// 3-重斧
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "重斧";
				this.abilityAbout = "非常沉重的斧击,所以对使用者的体力有一定要求";
				this.minLength = 1;
				this.maxLength = 1;
				this.mp = 0;
				this.baseDamage = 10;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				factory.damageValue = ((factory.status.strength * factory.atk + factory.status.vitality / 10) / 100 - ((factory.status1.vitality / 8) * factory.def) / 100)
						+ baseDamage;
				factory.hitRate = (factory.hitRate * 7) / 10;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CHOP, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 4-魅惑
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "魅惑";
				this.abilityAbout = "迷惑对方角色令其不受控制";
				this.minLength = 1;
				this.maxLength = 1;
				this.mp = 10;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_MPDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_RECOVERY);
				factory.hitRate = (factory.hitRate * 7) / 10;
				d.setStatus(SRPGStatus.STATUS_LOVER);
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_S, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 5-神速
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "神速";
				this.abilityAbout = "提高角色的敏捷";
				this.minLength = 0;
				this.maxLength = 1;
				this.mp = 10;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 1;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_HELPER;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				factory.hitRate = 100;
				d.setStatus(SRPGStatus.STATUS_AGILITY);
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CURE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 6-休息
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "休息";
				this.abilityAbout = "通过自我调节来恢复体力，效果常常十分有限";
				this.minLength = 0;
				this.maxLength = 0;
				this.mp = 0;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 1;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_RECOVERY;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_RECOVERY);
				factory.damageValue = (factory.status.sp + factory.status.mind) / 16;
				factory.hitRate = 100;
				factory.isDamage = false;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CURE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 7-治疗
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "治疗";
				this.abilityAbout = "使用医学手段，挽救常见的轻重伤害";
				this.minLength = 0;
				this.maxLength = 1;
				this.mp = 10;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 1;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_RECOVERY;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_RECOVERY);
				factory.damageValue = (factory.status.sp + factory.status.mind) / 6;
				factory.hitRate = 100;
				factory.isDamage = false;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CURE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {

				return null;
			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 8-回天术
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "回天术";
				this.abilityAbout = "只有神医级别的人物才能施展,治疗一切顽疾的神技";
				this.minLength = 0;
				this.maxLength = 1;
				this.mp = 30;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 1;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_RECOVERY;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_RECOVERY);
				factory.damageValue = factory.status1.max_hp / 2;
				factory.hitRate = 100;
				for (int i = 9; i < 15; i++) {
					factory.status2.status[i] = 0;
				}
				factory.isDamage = false;
				factory.damageChange = 0;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_CURE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {

				return null;
			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 9-舍己救人
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "舍己救人";
				this.abilityAbout = "这是一种牺牲自己生命，挽救他人生命的崇高技能，但在超过自己能力极限时可能会死掉……";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 100;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 2;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_HELPER;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_SAINT);
				factory.damageValue = factory.status1.max_hp - 1;
				if (factory.damageValue <= factory.status.hp) {
					factory.damageValue = factory.status.hp - 1;
				}
				for (int i = 9; i < 15; i++) {
					factory.status2.status[i] = 0;
				}
				factory.hitRate = 100;
				factory.isDamage = false;
				factory.damageChange = 0;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_LOOT_1, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				damageData.setDamage(damageaverage.damage);
				return damageData;
			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 10-万毒心经
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "万毒心经";
				this.abilityAbout = "源于清末民初的邪派武功,能把所有负面状态一股脑施加于对方之上，威力之强，堪称毒霸天下";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 100;
				this.baseDamage = 0;
				this.range = 3;
				this.target = 0;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ALLRECOVERY;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_DARK);
				factory.damageValue = (factory.status.sp / 100
						+ factory.status.strength / 100
						+ factory.status.vitality / 100
						+ factory.status.dexterity / 100 + factory.status.mp / 1000)
						+ factory.status.mind / 10;
				factory.hitRate = factory.status.dexterity / 50
						+ factory.hitRate / 2 - factory.def / 1000;
				for (int i = 9; i < SRPGStatus.STATUS_MAX; i++) {
					factory.statasFlag[i] = MathUtils.random
							.nextInt(factory.hitRate * 2);
					d.setStatus(i);
				}
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_BLOOD_1, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {

				return null;

			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 11-剑戮十方
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "剑戮十方";
				this.abilityAbout = "本质上就是向四周不分敌我的舍命乱砍,有一定机率造成对方或自己弱化";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 10;
				this.baseDamage = 0;
				this.range = 1;
				this.target = 2;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				factory.isStatus = true;
				factory.damageValue = ((factory.status.strength + factory.status.agility / 5) * factory.atk)
						/ 100
						- (((factory.status2.vitality * 7) / 16) * factory.def)
						/ 100;
				factory.statasFlag[SRPGStatus.STATUS_WEAK] = factory.hitRate / 8;
				d.setStatus(SRPGStatus.STATUS_WEAK);
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_ARROWS, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				if (damageaverage.number > 0) {
					damageData.setDamage(status.strength / 5);
					return damageData;
				} else {
					return null;
				}
			}

			@Override
			public int[] getAbilitySkill() {

				return null;
			}

		};
		index++;

		// 12-传送
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "传送";
				this.abilityAbout = "传送指定对象";
				this.minLength = 0;
				this.maxLength = 1;
				this.mp = 0;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 1;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_HELPER;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_VOID);
				d.setDirection(defender.getDirection());
				factory.hitRate = 100;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_FADE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {

				return null;

			}

			@Override
			public int[] getAbilitySkill() {

				return new int[] { SRPGStatus.SKILL_CARRY };
			}

		};
		index++;

		// 13-天打雷劈屠真龙
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "天打雷劈屠真龙";
				this.abilityAbout = "西楚霸王临死前由紫雷七击所悟出的第八击,专破真龙帝气,天雷击灭下寸草不生";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 50;
				this.baseDamage = 100;
				this.range = 3;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ALLDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_THUNDER);
				factory.damageValue = (((((factory.status.strength + factory.status.vitality / 2) * 10) / 13) * factory.atk) / 100 - (factory.status1.sp * factory.def) / 100)
						+ baseDamage;
				factory.hitRate = factory.hitRate * 2;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_T, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 14-军道杀拳
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "军道杀拳";
				this.abilityAbout = "某神奇国度的护国神功,有雷霆万钧之威,击天天开,击地地裂,其武不破,其政不亡";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 100;
				this.baseDamage = 666;
				this.range = 1;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_DARK);
				factory.damageValue = ((factory.status.strength * factory.atk) / 100 - (factory.status1.vitality * factory.def) / 100) / 8;
				factory.hitRate *= 2;
				factory.damageValue = factory.damageValue + baseDamage;
				int fd = defender.findDirection(attacker.getPosX(), attacker
						.getPosY());
				int x = defender.getPosX();
				int y = defender.getPosY();
				int md = SRPGActor.matchDirection(fd);
				SRPGMoveStack stack = new SRPGMoveStack(x, y);
				stack.setDefault(10, false, false);
				stack.addStack(md);
				if (field.checkArea(stack.getPosX(), stack.getPosY())
						&& actors.checkActor(stack.getPosX(), stack.getPosY()) == -1
						&& field.getMoveCost(
								defender.getActorStatus().movetype | 0x20,
								stack.getPosX(), stack.getPosY()) != -1) {
					d.setMoveStack(stack);
				}
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_FADE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 15-冰弹
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "冰弹";
				this.abilityAbout = "冰系魔法攻击,威力颇为霸道";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 20;
				this.baseDamage = 5;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_WATER);
				factory.damageValue = ((factory.status.magic
						+ factory.status.mind + factory.status.vitality) * 3) / 16;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_ICE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 16-曙光女神之宽恕
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "曙光女神之宽恕";
				this.abilityAbout = "传说中达到绝对零度的神技,似乎只有神圣的斗士才能学会";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 100;
				this.baseDamage = 50;
				this.range = 2;
				this.target = 0;
				this.counter = 0;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ALLDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_WATER);
				factory.damageValue = ((factory.status.magic
						+ factory.status.mind + factory.status.vitality) * 10) / 16;
				d.setStatus(SRPGStatus.STATUS_STUN);
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_SNOW, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 17-火焰弹
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "火焰弹";
				this.abilityAbout = "火焰系魔法攻击,威力颇为霸道";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 20;
				this.baseDamage = 5;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_FIRE);
				factory.damageValue = ((factory.status.magic
						+ factory.status.mind + factory.status.sp) * 3) / 16;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_FIRE, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 18-魔法弹
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "魔法弹";
				this.abilityAbout = "十分常见的魔法攻击";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 10;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 1;
				this.selectNeed = 1;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_DARK);
				factory.damageValue = ((factory.status.magic + factory.status.mind) * 2) / 16;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_S, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 19-霸王色霸气
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "霸王色霸气";
				this.abilityAbout = "非常罕见的技能,利用气势威压对方令其无法行动,减弱对方能力,同时取消对方的物理攻击免疫特性";
				this.minLength = 1;
				this.maxLength = 2;
				this.mp = 300;
				this.baseDamage = 0;
				this.range = 3;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_PHYSICS);
				d.setHelpers("STOP");
				SRPGStatus status = factory.status2;
				d.setStatus(SRPGStatus.STATUS_WEAK);
				d.setStatus(SRPGStatus.STATUS_SILENCE);
				d.setStatus(SRPGStatus.STATUS_STUN);
				boolean result = false;
				if (status.immunity != null) {
					for (int i = 0; i < status.immunity.length; i++) {
						if (status.immunity[i] == SRPGStatus.ELEMENT_PHYSICS) {
							result = true;
							break;
						}
					}
				}
				if (result) {
					int[] immunity = new int[status.immunity.length - 1];
					if (status.immunity != null) {
						for (int i = 0; i < status.immunity.length; i++) {
							if (status.immunity[i] != SRPGStatus.ELEMENT_PHYSICS) {
								immunity[i] = status.immunity[i];
							}
						}
					}
					status.immunity = immunity;
				}
				factory.hitRate = 100;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_BLAST, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 20-吸星大法
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "吸星大法";
				this.abilityAbout = "失传已久的武林绝学,吸收对方内力为己所用";
				this.minLength = 1;
				this.maxLength = 1;
				this.mp = 1;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 0;
				this.counter = 1;
				this.direct = 1;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ALLDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_DARK);
				factory.flag = false;
				factory.damageValue = factory.status.mp;
				if (factory.damageValue > factory.status.max_mp
						- factory.status.mp) {
					factory.damageValue = factory.status.max_mp
							- factory.status.mp;
				}
				factory.hitRate = 100;
				factory.damageChange = 0;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_LOOT_1, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				damageData.setDamage((damageaverage.damage * 8) / 10);
				damageData.setGenre(SRPGType.GENRE_MPRECOVERY);
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 21-罗渊沌灭
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "罗渊沌灭";
				this.abilityAbout = "诞生自天地幽境的终极黑暗法咒,有毁天灭地之能,除非免疫黑暗系攻击,否则所有中招者必死无疑";
				this.minLength = 0;
				this.maxLength = 0;
				this.mp = 999;
				this.baseDamage = 999;
				this.range = 7;
				this.target = 2;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ALLDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_DARK);
				factory.damageValue = this.baseDamage;
				factory.hitRate = 100;
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return new SRPGExtinctEffect(x * actor.getTileWidth()
						+ actor.getTileWidth() / 2, y * actor.getTileHeight()
						+ actor.getTileHeight() / 2, LColor.black,
						this.abilityName);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return new int[] { SRPGStatus.SKILL_UNDEAD };
			}

		};
		index++;

		// 22-嘴炮
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "嘴炮";
				this.abilityAbout = "仅凭嘴力喷死对手的神技,习得此技是成为神上的必要条件之一";
				// 最小攻击范围两格(很显然,嘴炮无法近身格斗用)
				this.minLength = 2;
				// 最大攻击范围三格
				this.maxLength = 3;
				this.mp = 30;
				this.baseDamage = 10;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 0;
				// 是否能够反击，0不能,1可以
				this.selectNeed = 0;
				// 技能类型
				this.genre = SRPGType.GENRE_ATTACK;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_SAINT);
				factory.damageValue = ((factory.status.strength * factory.atk * baseDamage) / 1000 + (factory.status.mind + baseDamage)) / 10;
				int fd = defender.findDirection(attacker.getPosX(), attacker
						.getPosY());
				SRPGMoveStack movestack = new SRPGMoveStack(defender.getPosX(),
						defender.getPosY());
				movestack.setDefault(2, false, false);
				int direction = SRPGActor.matchDirection(fd);
				int count = 0;
				for (;;) {
					if (count >= 3) {
						break;
					}
					movestack.addStack(direction);
					int mx = movestack.getPosX();
					int my = movestack.getPosY();
					if (!field.checkArea(mx, my)
							|| actors.checkActor(mx, my) != -1
							|| field.getMoveCost(
									defender.getActorStatus().movetype
											| SRPGStatus.MOVETYPE_SLOWMOVE, mx,
									my) == -1) {
						movestack.removeStack();
						break;
					}
					count++;
				}
				d.setMoveStack(movestack);
				factory.damageValue = factory.damageValue > 0 ? factory.damageValue
						: baseDamage;

			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_T, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		// 23-道之本
		abilitys[index] = new SRPGAbility() {

			@Override
			public void initConfig() {
				this.abilityName = "道之本";
				this.abilityAbout = "一念所及,万里无形,再造六爻,返璞还真,定云止水,悟道之本";
				this.minLength = 0;
				this.maxLength = 3;
				this.mp = 666;
				this.baseDamage = 0;
				this.range = 0;
				this.target = 0;
				this.counter = 0;
				this.direct = 0;
				this.selectNeed = 0;
				this.genre = SRPGType.GENRE_ALLDAMAGE;
			}

			@Override
			public void runDamageExpect(SRPGActor attacker, SRPGActor defender,
					SRPGAbilityFactory factory, SRPGField field,
					SRPGDamageData d, SRPGActors actors) {
				d.setElement(SRPGStatus.ELEMENT_VOID);
				SRPGActorFactory.runLevelUp(factory.status2, 1);
				factory.status2.exp = -100;
				factory.status2.hp = factory.status2.max_hp;
				factory.status2.mp = factory.status2.max_mp;
				factory.status2.computer = SRPGType.NOMOVE;
				factory.status2.immunity = null;
				factory.damageValue = 1;
				d.setHelpers("FIRST");
				d.setStatus(SRPGStatus.STATUS_STUN);
			}

			@Override
			public SRPGEffect runAbilityEffect(int index, SRPGActor actor,
					int x, int y) {
				return SRPGEffectFactory.getAbilityEffect(
						SRPGEffectFactory.EFFECT_TAICHI, actor, x, y);
			}

			@Override
			public SRPGDamageData dataInput(SRPGDamageAverage damageaverage,
					SRPGDamageData damageData, SRPGStatus status) {
				return null;
			}

			@Override
			public int[] getAbilitySkill() {
				return null;
			}

		};
		index++;

		lazyAbilityClass.addAll(Arrays.asList(abilitys));

	}
}

package loon.srpg.effect;

import loon.core.graphics.device.LColor;
import loon.srpg.actor.SRPGActor;


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
public final class SRPGEffectFactory {

	// 斩击
	public final static int EFFECT_CHOP = 0;

	// 弓箭
	public final static int EFFECT_ARROW = 1;

	// 万箭
	public final static int EFFECT_ARROWS = 2;

	// 爆裂
	public final static int EFFECT_BURST = 3;

	// 退出效果
	public final static int EFFECT_OUT = 4;

	// 黑暗魔法
	public final static int EFFECT_DARK = 5;

	// 时间魔法
	public final static int EFFECT_CLOCK = 6;

	// 治疗
	public final static int EFFECT_CURE = 7;

	// 吸血1
	public final static int EFFECT_BLOOD_1 = 8;

	// 吸血2
	public final static int EFFECT_BLOOD_2 = 9;

	// 退去
	public final static int EFFECT_FADE = 10;

	// 抢夺1
	public final static int EFFECT_LOOT_1 = 11;

	// 抢夺2
	public final static int EFFECT_LOOT_2 = 12;

	// 内聚
	public final static int EFFECT_C = 13;

	// 反转内聚
	public final static int EFFECT_RC = 14;

	// 雪弹
	public final static int EFFECT_SNOW = 15;

	// 冰(水)弹
	public final static int EFFECT_ICE = 16;

	// 爆炸
	public final static int EFFECT_BLAST = 17;

	// 落雷
	public final static int EFFECT_T = 18;

	// 魔法弹
	public final static int EFFECT_S = 19;

	// 风
	public final static int EFFECT_WIND = 20;

	// 火
	public final static int EFFECT_FIRE = 21;

	// 太极
	public final static int EFFECT_TAICHI = 22;

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param x
	 * @param y
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, int x, int y) {
		return getAbilityEffect(i, null, x, y, null);
	}

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param actor
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, SRPGActor actor) {
		return getAbilityEffect(i, actor, actor.getPosX(), actor.getPosY(),
				null);
	}

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param x
	 * @param y
	 * @param c
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, int x, int y, LColor c) {
		return getAbilityEffect(i, null, x, y, c);
	}

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param actor
	 * @param c
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, SRPGActor actor,
			LColor c) {
		return getAbilityEffect(i, actor, actor.getPosX(), actor.getPosY(), c);
	}

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, SRPGActor actor,
			int x, int y) {
		return getAbilityEffect(i, actor, x, y, null);
	}

	/**
	 * 使用指定索引的默认魔法特效
	 * 
	 * @param i
	 * @param actor
	 * @param x
	 * @param y
	 * @return
	 */
	public final static SRPGEffect getAbilityEffect(int i, SRPGActor actor,
			int x, int y, LColor c) {
		int tileWidth = 32;
		int tileHeight = 32;
		if (actor != null) {
			tileWidth = actor.getTileWidth();
			tileHeight = actor.getTileHeight();
		}
		int halfTileWidth = tileWidth / 2;
		int halfTileHeight = tileHeight / 2;
		SRPGEffect o = null;
		switch (i) {
		case EFFECT_CHOP:
			o = new SRPGChopEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_ARROW:
			if (c == null) {
				o = new SRPGArrowEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGArrowEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_ARROWS:
			if (c == null) {
				o = new SRPGArrowsEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileWidth
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGArrowsEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileWidth
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_BURST:
			if (c == null) {
				o = new SRPGBurstEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGBurstEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_OUT:
			if (c == null) {
				o = new SRPGOUTEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGOUTEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_DARK:
			o = new SRPGDarkEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_CLOCK:
			o = new SRPGClockEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_CURE:
			if (c == null) {
				o = new SRPGSparkEffect(x * tileWidth - halfTileWidth, y
						* tileHeight - halfTileHeight, tileWidth * 2,
						tileHeight * 2, 10, 3);
			} else {
				o = new SRPGSparkEffect(x * tileWidth - halfTileWidth, y
						* tileHeight - halfTileHeight, tileWidth * 2,
						tileHeight * 2, 10, 3, c);
			}
			break;
		case EFFECT_BLOOD_1:
			if (c == null) {
				o = new SRPGBloodEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGBloodEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_BLOOD_2:
			if (c == null) {
				o = new SRPGBloodEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, LColor.red);
			} else {
				o = new SRPGBloodEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_FADE:
			o = new SRPGFadeEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_LOOT_1:
			if (c == null) {
				o = new SRPGForceEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, actor.getPosX()
						* tileWidth + halfTileWidth, actor.getPosY()
						* tileHeight + halfTileHeight, 24, LColor.red);
			} else {
				o = new SRPGForceEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, actor.getPosX()
						* tileWidth + halfTileWidth, actor.getPosY()
						* tileHeight + halfTileHeight, 24, c);
			}
			break;
		case EFFECT_LOOT_2:
			if (c == null) {
				o = new SRPGForceEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, actor.getPosX()
						* tileWidth + halfTileWidth, actor.getPosY()
						* tileHeight + halfTileHeight, 24, LColor.black);
			} else {
				o = new SRPGForceEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, actor.getPosX()
						* tileWidth + halfTileWidth, actor.getPosY()
						* tileHeight + halfTileHeight, 24, c);
			}
			break;
		case EFFECT_C:
			if (c == null) {
				o = new SRPGCohesionEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGCohesionEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_RC:
			if (c == null) {
				o = new SRPGRCohesionEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGRCohesionEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_SNOW:
			o = new SRPGSnowEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_ICE:
			if (c == null) {
				o = new SRPGIceEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGIceEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_BLAST:
			if (c == null) {
				o = new SRPGBlastEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGBlastEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_T:
			o = new SRPGThunderEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight);
			break;
		case EFFECT_S:
			if (c == null) {
				o = new SRPGStrikeEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight);
			} else {
				o = new SRPGStrikeEffect(actor.getPosX() * tileWidth
						+ halfTileWidth, actor.getPosY() * tileHeight
						+ halfTileHeight, x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_WIND:
			o = new SRPGWindEffect();
			break;
		case EFFECT_FIRE:
			if (c == null) {
				o = new SRPGOUTEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, LColor.red);
			} else {
				o = new SRPGOUTEffect(x * tileWidth + halfTileWidth, y
						* tileHeight + halfTileHeight, c);
			}
			break;
		case EFFECT_TAICHI:
			o = new SRPGTaichiEffect(x * tileWidth + halfTileWidth, y
					* tileHeight + halfTileHeight, 90);
			break;
		}
		return o;
	}
}

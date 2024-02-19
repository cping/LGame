/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon;

import loon.action.sprite.ISprite;
import loon.action.sprite.effect.BaseAbstractEffect;
import loon.action.sprite.effect.FadeArcEffect;
import loon.action.sprite.effect.FadeBoardEffect;
import loon.action.sprite.effect.FadeDoorEffect;
import loon.action.sprite.effect.FadeDoorIrregularEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeSwipeEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.action.sprite.effect.FadeOvalHollowEffect;
import loon.canvas.LColor;
import loon.events.Updateable;
import loon.geom.BooleanValue;
import loon.utils.MathUtils;

/**
 * Screen场景切换用类,用于同时设定当前场景的离场效果,以及转化新场景的入场效果 (全部为成对的对称效果,不对称的请自动构建)
 */
public class ScreenExitEffect {

	// 普通全屏渐变淡出淡入
	public final static int STANDARD_FADE = 0;

	// 椭圆镂空围绕中心淡出,中心淡入
	public final static int OVAL_HOLLOW_FADE = 1;

	// 椭圆实心中心点淡出,中心淡入
	public final static int OVAL_SOLID_FADE = 2;

	// 水滴点状淡出,点状淡入
	public final static int DOT_FADE = 3;

	// 瓦片堆砌样左侧淡出，右侧淡入
	public final static int BOARD_LEFT_FADE = 4;

	// 瓦片堆砌样右侧淡出，左侧淡入
	public final static int BOARD_RIGHT_FADE = 5;

	// 瓦片螺旋样淡出,淡入
	public final static int SPIRAL_FADE = 6;

	// 左右方向的开门,关门效果
	public final static int DOOR_LR_FADE = 7;

	// 上下方向的开门,关门效果
	public final static int DOOR_TB_FADE = 8;

	// 左中右方向的开门,关门效果
	public final static int DOOR_LCR_FADE = 9;

	// 上中下方向的开门,关门效果
	public final static int DOOR_TCB_FADE = 10;

	// 不规则的开门,关门效果
	public final static int DOOR_IRREGULAR_FADE = 11;

	// 斜角离开,斜角进入
	public final static int SWIPE_FADE = 12;

	// 扇形淡出,淡入
	public final static int ARC_FADE = 13;

	// 瓦片淡出,淡入
	public final static int TILES_FADE = 14;

	static class ReleasedScreen implements LRelease {

		private final Screen srcScreen;

		private final Screen dstScreen;

		private final int index;

		private final LColor color;

		private final BooleanValue locked;

		public ReleasedScreen(final int idx, final LColor c, final Screen src, final Screen dst, final BooleanValue p) {
			this.index = idx;
			this.color = c;
			this.srcScreen = src;
			this.dstScreen = dst;
			this.locked = p;
		}

		@Override
		public void close() {
			if (srcScreen != null && dstScreen != null) {
				// 提交一个事务变化(跳出当前循环,避免在奇怪的场合卡死,有备无患)
				srcScreen.addUnLoad(new UpdateableScreen(index, color, srcScreen, dstScreen, locked));
			}
		}

	}

	static class UpdateableScreen implements Updateable {

		private final Screen srcScreen;

		private final Screen dstScreen;

		private final int index;

		private final LColor color;

		private final BooleanValue locked;

		public UpdateableScreen(final int idx, final LColor c, final Screen src, final Screen dst,
				final BooleanValue p) {
			this.index = idx;
			this.color = c;
			this.srcScreen = src;
			this.dstScreen = dst;
			this.locked = p;
		}

		@Override
		public void action(Object a) {
			if (srcScreen == null || dstScreen == null) {
				if (locked != null) {
					locked.set(false);
				}
				return;
			}
			try {
				final int effectType = ISprite.TYPE_FADE_IN;
				switch (index) {
				default:
				case STANDARD_FADE:
					dstScreen.setTransition(LTransition.newFade(effectType, color));
					break;
				case OVAL_HOLLOW_FADE:
					dstScreen.setTransition(LTransition.newOvalHollow(effectType, color));
					break;
				case OVAL_SOLID_FADE:
					dstScreen.setTransition(LTransition.newFadeOval(effectType, color));
					break;
				case DOT_FADE:
					dstScreen.setTransition(LTransition.newFadeDot(effectType, color));
					break;
				case BOARD_LEFT_FADE:
					dstScreen.setTransition(LTransition.newFadeBoard(effectType, FadeBoardEffect.START_RIGHT, color));
					break;
				case BOARD_RIGHT_FADE:
					dstScreen.setTransition(LTransition.newFadeBoard(effectType, FadeBoardEffect.START_LEFT, color));
					break;
				case SPIRAL_FADE:
					dstScreen.setTransition(LTransition.newFadeSpiral(effectType, color));
					break;
				case DOOR_LR_FADE:
					dstScreen.setTransition(LTransition.newFadeDoor(effectType, FadeDoorEffect.LEFT_RIGHT, color));
					break;
				case DOOR_TB_FADE:
					dstScreen.setTransition(LTransition.newFadeDoor(effectType, FadeDoorEffect.TOP_BOTTOM, color));
					break;
				case DOOR_LCR_FADE:
					dstScreen.setTransition(
							LTransition.newFadeDoor(effectType, FadeDoorEffect.LEFT_CENTER_RIGHT, color));
					break;
				case DOOR_TCB_FADE:
					dstScreen.setTransition(
							LTransition.newFadeDoor(effectType, FadeDoorEffect.TOP_CENTER_BOTTOM, color));
					break;
				case DOOR_IRREGULAR_FADE:
					dstScreen.setTransition(LTransition.newFadeDoorIrregular(effectType, color));
					break;
				case SWIPE_FADE:
					dstScreen.setTransition(LTransition.newFadeSwipe(effectType, color));
					break;
				case ARC_FADE:
					dstScreen.setTransition(LTransition.newFadeArc(effectType, color));
					break;
				case TILES_FADE:
					dstScreen.setTransition(LTransition.newFadeTile(effectType, color));
					break;
				}
				srcScreen.setScreen(dstScreen);
			} catch (Exception ex) {
				LSystem.error("Screen Exit Effect Exception:", ex);
			} finally {
				if (locked != null) {
					locked.set(false);
				}
			}
		}

	}

	/**
	 * 以指定特效离开当前Screen,并进入指定Screen
	 * 
	 * @param index
	 * @param color
	 * @param src
	 * @param dst
	 * @param locked
	 */
	public static void gotoEffectExit(final int index, final LColor color, final Screen src, final Screen dst,
			final BooleanValue locked) {
		gotoEffectExit(index, color, src, dst, locked, true);
	}

	/**
	 * 以指定特效离开当前Screen,并进入指定Screen
	 * 
	 * @param index
	 * @param color
	 * @param src
	 * @param dst
	 * @param locked
	 * @param hideUI
	 */
	public static void gotoEffectExit(final int index, final LColor color, final Screen src, final Screen dst,
			final BooleanValue locked, final boolean hideUI) {
		if (src == null) {
			return;
		}
		if (dst == null) {
			return;
		}
		if (src == dst) {
			return;
		}
		if (locked != null) {
			if (locked.get()) {
				return;
			}
			locked.set(true);
		}
		final int effectType = ISprite.TYPE_FADE_OUT;
		BaseAbstractEffect baseEffect = null;
		switch (index) {
		default:
		case STANDARD_FADE:
			baseEffect = FadeEffect.create(effectType, color);
			break;
		case OVAL_HOLLOW_FADE:
			baseEffect = new FadeOvalHollowEffect(effectType, color);
			break;
		case OVAL_SOLID_FADE:
			baseEffect = new FadeOvalEffect(effectType, color);
			break;
		case DOT_FADE:
			baseEffect = new FadeDotEffect(effectType, color);
			break;
		case BOARD_LEFT_FADE:
			baseEffect = new FadeBoardEffect(effectType, FadeBoardEffect.START_LEFT, color);
			break;
		case BOARD_RIGHT_FADE:
			baseEffect = new FadeBoardEffect(effectType, FadeBoardEffect.START_RIGHT, color);
			break;
		case SPIRAL_FADE:
			baseEffect = new FadeSpiralEffect(effectType, color);
			break;
		case DOOR_LR_FADE:
			baseEffect = new FadeDoorEffect(effectType, FadeDoorEffect.LEFT_RIGHT, color);
			break;
		case DOOR_TB_FADE:
			baseEffect = new FadeDoorEffect(effectType, FadeDoorEffect.TOP_BOTTOM, color);
			break;
		case DOOR_LCR_FADE:
			baseEffect = new FadeDoorEffect(effectType, FadeDoorEffect.LEFT_CENTER_RIGHT, color);
			break;
		case DOOR_TCB_FADE:
			baseEffect = new FadeDoorEffect(effectType, FadeDoorEffect.TOP_CENTER_BOTTOM, color);
			break;
		case DOOR_IRREGULAR_FADE:
			baseEffect = new FadeDoorIrregularEffect(effectType, color);
			break;
		case SWIPE_FADE:
			baseEffect = new FadeSwipeEffect(effectType, color);
			break;
		case ARC_FADE:
			baseEffect = new FadeArcEffect(effectType, color);
			break;
		case TILES_FADE:
			baseEffect = new FadeTileEffect(effectType, color);
			break;
		}
		baseEffect.setCompletedAfterBlack(true);
		// 把渐变效果渲染层级调到最高,避免被其它效果遮挡
		baseEffect.setZ(Integer.MAX_VALUE);
		baseEffect.completedDispose(new ReleasedScreen(index, color, src, dst, locked));
		if (hideUI) {
			src.hideUI();
		}
		// 最后渲染精灵类,避免被其它组件遮挡
		src.lastSpriteDraw();
		src.setLock(true);
		src.add(baseEffect);
	}

	private final BooleanValue _effectLocked = new BooleanValue();

	private LColor _color;

	private int _index;

	private boolean _hideUI;

	public ScreenExitEffect() {
		this(-1, LColor.black, true);
	}

	public ScreenExitEffect(int idx, LColor c, boolean hideUI) {
		this._index = idx;
		this._color = c;
		this._hideUI = hideUI;
	}

	public void gotoEffectExit(final Screen src, final Screen dst) {
		gotoEffectExit(_index, _color, src, dst);
	}

	public void gotoEffectExit(final int index, final Screen src, final Screen dst) {
		gotoEffectExit(index, _color, src, dst, _effectLocked, _hideUI);
	}

	public void gotoEffectExit(final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(_index, color, src, dst);
	}

	public void gotoEffectExit(final int index, final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(index, color, src, dst, _effectLocked, _hideUI);
	}

	public void gotoEffectExitRand(final Screen src, final Screen dst) {
		gotoEffectExitRand(_color, src, dst);
	}

	public void gotoEffectExitRand(final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(MathUtils.random(0, TILES_FADE), color, src, dst, _effectLocked, _hideUI);
	}

	public LColor getEffectExitColor() {
		return _color;
	}

	public ScreenExitEffect setEffectExitColor(LColor c) {
		this._color = c;
		return this;
	}

	public int getEffectExitIndex() {
		return this._index;
	}

	public ScreenExitEffect setEffectExitIndex(int idx) {
		this._index = idx;
		return this;
	}

	public boolean isHideUI() {
		return _hideUI;
	}

	public ScreenExitEffect setHideUI(boolean h) {
		this._hideUI = h;
		return this;
	}
}

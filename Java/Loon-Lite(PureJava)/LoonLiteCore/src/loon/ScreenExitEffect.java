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
import loon.action.sprite.effect.FadeBoardEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.action.sprite.effect.OvalEffect;
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

	// 瓦片淡出,淡入
	public final static int TILES_FADE = 7;

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
				switch (index) {
				default:
				case STANDARD_FADE:
					dstScreen.setTransition(LTransition.newFade(ISprite.TYPE_FADE_IN, color));
					break;
				case OVAL_HOLLOW_FADE:
					dstScreen.setTransition(LTransition.newOval(ISprite.TYPE_FADE_IN, color));
					break;
				case OVAL_SOLID_FADE:
					dstScreen.setTransition(LTransition.newOvalFade(ISprite.TYPE_FADE_IN, color));
					break;
				case DOT_FADE:
					dstScreen.setTransition(LTransition.newFadeDot(ISprite.TYPE_FADE_IN, color));
					break;
				case BOARD_LEFT_FADE:
					dstScreen.setTransition(
							LTransition.newFadeBoard(ISprite.TYPE_FADE_IN, FadeBoardEffect.START_RIGHT, color));
					break;
				case BOARD_RIGHT_FADE:
					dstScreen.setTransition(
							LTransition.newFadeBoard(ISprite.TYPE_FADE_IN, FadeBoardEffect.START_LEFT, color));
					break;
				case SPIRAL_FADE:
					dstScreen.setTransition(LTransition.newFadeSpiral(ISprite.TYPE_FADE_IN, color));
					break;
				case TILES_FADE:
					dstScreen.setTransition(LTransition.newFadeTile(ISprite.TYPE_FADE_IN, color));
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
	 * @param src
	 * @param dst
	 * @param color
	 */
	public static void gotoEffectExit(final int index, final LColor color, final Screen src, final Screen dst,
			final BooleanValue locked) {
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
		BaseAbstractEffect baseEffect = null;
		switch (index) {
		default:
		case STANDARD_FADE:
			baseEffect = FadeEffect.create(ISprite.TYPE_FADE_OUT, color);
			break;
		case OVAL_HOLLOW_FADE:
			baseEffect = new OvalEffect(ISprite.TYPE_FADE_OUT, color);
			break;
		case OVAL_SOLID_FADE:
			baseEffect = new FadeOvalEffect(ISprite.TYPE_FADE_OUT, color);
			break;
		case DOT_FADE:
			baseEffect = new FadeDotEffect(ISprite.TYPE_FADE_OUT, color);
			break;
		case BOARD_LEFT_FADE:
			baseEffect = new FadeBoardEffect(ISprite.TYPE_FADE_OUT, FadeBoardEffect.START_LEFT, color);
			break;
		case BOARD_RIGHT_FADE:
			baseEffect = new FadeBoardEffect(ISprite.TYPE_FADE_OUT, FadeBoardEffect.START_RIGHT, color);
			break;
		case SPIRAL_FADE:
			baseEffect = new FadeSpiralEffect(ISprite.TYPE_FADE_OUT, color);
			break;
		case TILES_FADE:
			baseEffect = new FadeTileEffect(ISprite.TYPE_FADE_OUT, color);
			break;
		}
		baseEffect.setCompletedAfterBlack(true);
		// 把渐变效果渲染层级调到最高,避免被其它效果遮挡
		baseEffect.setZ(Integer.MAX_VALUE);
		baseEffect.completedDispose(new ReleasedScreen(index, color, src, dst, locked));
		src.hideUI();
		// 最后渲染精灵类,避免被其它组件遮挡
		src.lastSpriteDraw();
		src.setLock(true);
		src.add(baseEffect);
	}

	private final BooleanValue _effectLocked = new BooleanValue();

	private LColor _color;

	private int _index;

	public ScreenExitEffect() {
		this(-1, LColor.black);
	}

	public ScreenExitEffect(int idx, LColor c) {
		this._index = idx;
		this._color = c;
	}

	public void gotoEffectExit(final Screen src, final Screen dst) {
		gotoEffectExit(_index, _color, src, dst);
	}

	public void gotoEffectExit(final int index, final Screen src, final Screen dst) {
		gotoEffectExit(index, _color, src, dst, _effectLocked);
	}

	public void gotoEffectExit(final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(_index, color, src, dst);
	}

	public void gotoEffectExit(final int index, final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(index, color, src, dst, _effectLocked);
	}

	public void gotoEffectExitRand(final Screen src, final Screen dst) {
		gotoEffectExitRand(_color, src, dst);
	}

	public void gotoEffectExitRand(final LColor color, final Screen src, final Screen dst) {
		gotoEffectExit(MathUtils.random(0, TILES_FADE), color, src, dst, _effectLocked);
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
}

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
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.1
 */
package loon;

import loon.action.map.Config;
import loon.action.sprite.ISprite;
import loon.action.sprite.effect.ArcEffect;
import loon.action.sprite.effect.BaseAbstractEffect;
import loon.action.sprite.effect.FadeBoardEffect;
import loon.action.sprite.effect.FadeDoorIrregularEffect;
import loon.action.sprite.effect.CrossEffect;
import loon.action.sprite.effect.FadeDotEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.FadeOvalEffect;
import loon.action.sprite.effect.FadeSpiralEffect;
import loon.action.sprite.effect.FadeTileEffect;
import loon.action.sprite.effect.FadeOvalHollowEffect;
import loon.action.sprite.effect.PixelDarkInEffect;
import loon.action.sprite.effect.PixelDarkOutEffect;
import loon.action.sprite.effect.PixelThunderEffect;
import loon.action.sprite.effect.PixelWindEffect;
import loon.action.sprite.effect.SplitEffect;
import loon.action.sprite.effect.FadeSwipeEffect;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 自0.3.2版起新增的Screen切换过渡效果类，内置有多种Screen过渡特效。
 * 
 * example:
 * 
 * public class Sample extends Screen{
 * 
 * ......
 * 
 * public LTransition onTransition(){ return xxx(method) } }
 * 
 * 
 */
public class LTransition {

	public static interface TransitionListener extends LRelease {

		public ISprite getSprite();

		public void update(long elapsedTime);

		public void draw(GLEx g);

		public boolean completed();

		@Override
		public void close();
	}

	static class CombinedTransition implements TransitionListener {

		final TArray<LTransition> transitions;

		public CombinedTransition(TArray<LTransition> ts) {
			this.transitions = ts;
		}

		@Override
		public void draw(GLEx g) {
			for (int i = 0; i < transitions.size; i++) {
				((LTransition) transitions.get(i)).draw(g);
			}
		}

		@Override
		public void update(long elapsedTime) {
			for (int i = 0; i < transitions.size; i++) {
				LTransition t = (LTransition) transitions.get(i);
				if (!t.completed()) {
					t.update(elapsedTime);
				}
			}
		}

		@Override
		public boolean completed() {
			for (int i = 0; i < transitions.size; i++) {
				if (!((LTransition) transitions.get(i)).completed()) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void close() {
			for (int i = 0; i < transitions.size; i++) {
				((LTransition) transitions.get(i)).close();
			}
		}

		@Override
		public ISprite getSprite() {
			return null;
		}

	}

	static class EffectToTransition implements TransitionListener {

		private BaseAbstractEffect _effect;

		public EffectToTransition(BaseAbstractEffect effect) {
			this(effect, -1);
		}

		public EffectToTransition(BaseAbstractEffect effect, long delay) {
			this._effect = effect;
			if (_effect != null && delay > -1) {
				_effect.setDelay(delay);
			}
		}

		@Override
		public void update(long elapsedTime) {
			if (_effect != null) {
				_effect.update(elapsedTime);
			}
		}

		@Override
		public void draw(GLEx g) {
			if (_effect != null) {
				_effect.createUI(g);
			}
		}

		@Override
		public boolean completed() {
			return _effect == null ? true : _effect.isCompleted();
		}

		@Override
		public ISprite getSprite() {
			return _effect;
		}

		@Override
		public void close() {
			if (_effect != null) {
				_effect.close();
			}
		}

	}

	/**
	 * 常用特效枚举列表
	 */
	public static enum TransType {
		FadeIn, FadeOut, FadeBoardIn, FadeBoardOut, FadeOvalIn, FadeOvalOut, FadeDotIn, FadeDotOut, FadeTileIn,
		FadeTileOut, FadeSpiralIn, FadeSpiralOut, FadeSwipeIn, FadeSwipeOut, PixelDarkIn, PixelDarkOut, CrossRandom,
		SplitRandom, PixelWind, PixelThunder, FadeOvalHollowIn, FadeOvalHollowOut, FadeDoorIrregularIn,
		FadeDoorIrregularOut;
	}

	/**
	 * 转换字符串为转换特效的枚举类型
	 * 
	 * @param name
	 * @return
	 */
	public static TransType transStringToType(String name) {
		if (name != null) {
			String key = name.trim().toLowerCase();
			if ("fadein".equals(key)) {
				return TransType.FadeIn;
			} else if ("fadeout".equals(key)) {
				return TransType.FadeOut;
			} else if ("fadeboardin".equals(key)) {
				return TransType.FadeBoardIn;
			} else if ("fadeboardout".equals(key)) {
				return TransType.FadeBoardOut;
			} else if ("fadeovalin".equals(key)) {
				return TransType.FadeOvalIn;
			} else if ("fadeovalout".equals(key)) {
				return TransType.FadeOvalOut;
			} else if ("fadeovalfollowin".equals(key)) {
				return TransType.FadeOvalHollowIn;
			} else if ("fadeovalhollowout".equals(key)) {
				return TransType.FadeOvalHollowOut;
			} else if ("fadedotin".equals(key)) {
				return TransType.FadeDotIn;
			} else if ("fadedotout".equals(key)) {
				return TransType.FadeDotOut;
			} else if ("fadetilein".equals(key)) {
				return TransType.FadeTileIn;
			} else if ("fadetileout".equals(key)) {
				return TransType.FadeTileOut;
			} else if ("fadeswipein".equals(key)) {
				return TransType.FadeSwipeIn;
			} else if ("fadeswipeout".equals(key)) {
				return TransType.FadeSwipeOut;
			} else if ("fadespiralin".equals(key)) {
				return TransType.FadeSpiralIn;
			} else if ("fadespiralout".equals(key)) {
				return TransType.FadeSpiralOut;
			} else if ("pixeldarkin".equals(key)) {
				return TransType.PixelDarkIn;
			} else if ("pixeldarkout".equals(key)) {
				return TransType.PixelDarkOut;
			} else if ("crossrandom".equals(key)) {
				return TransType.CrossRandom;
			} else if ("splitrandom".equals(key)) {
				return TransType.SplitRandom;
			} else if ("pixelwind".equals(key)) {
				return TransType.PixelWind;
			} else if ("pixelthunder".equals(key)) {
				return TransType.PixelThunder;
			} else if ("pixelwind".equals(key)) {
				return TransType.PixelWind;
			} else if ("doorirregularin".equals(key)) {
				return TransType.FadeDoorIrregularIn;
			} else if ("doorirregularout".equals(key)) {
				return TransType.FadeDoorIrregularOut;
			} else {
				return TransType.FadeIn;
			}
		}
		return TransType.FadeIn;
	}

	/**
	 * 返回一定指定色彩过滤后的特效
	 * 
	 * @param key
	 * @param c
	 * @return
	 */
	public static LTransition newTransition(String key, LColor c) {
		return newTransition(transStringToType(key), c);
	}

	/**
	 * 返回一定指定色彩过滤后的特效
	 * 
	 * @param key 过渡类型字符串
	 * @param cs  描述颜色的字符串
	 * 
	 * @return
	 */
	public static LTransition newTransition(String key, String cs) {
		return newTransition(transStringToType(key), new LColor(cs));
	}

	/**
	 * 返回一定指定色彩过滤后的特效
	 * 
	 * @param t
	 * @param c
	 * @return
	 */
	public static LTransition newTransition(TransType t, LColor c) {
		LTransition transition = null;
		switch (t) {
		default:
		case FadeIn:
			transition = newFadeIn(c);
			break;
		case FadeOut:
			transition = newFadeOut(c);
			break;
		case FadeBoardIn:
			transition = newFadeBoardIn(c);
			break;
		case FadeBoardOut:
			transition = newFadeBoardOut(c);
			break;
		case FadeOvalIn:
			transition = newFadeOvalIn(c);
			break;
		case FadeOvalOut:
			transition = newFadeOvalOut(c);
			break;
		case FadeOvalHollowIn:
			transition = newOvalHollowIn(c);
			break;
		case FadeOvalHollowOut:
			transition = newOvalHollowOut(c);
			break;
		case FadeDotIn:
			transition = newFadeDotIn(c);
			break;
		case FadeDotOut:
			transition = newFadeDotOut(c);
			break;
		case FadeTileIn:
			transition = newFadeTileIn(c);
			break;
		case FadeTileOut:
			transition = newFadeTileOut(c);
			break;
		case FadeSpiralIn:
			transition = newFadeSpiralIn(c);
			break;
		case FadeSpiralOut:
			transition = newFadeSpiralOut(c);
			break;
		case FadeSwipeIn:
			transition = newFadeSwipeIn(c);
			break;
		case FadeSwipeOut:
			transition = newFadeSwipeOut(c);
			break;
		case FadeDoorIrregularIn:
			transition = newFadeDoorIrregularIn(c);
			break;
		case FadeDoorIrregularOut:
			transition = newFadeDoorIrregularOut(c);
			break;
		case PixelDarkIn:
			transition = newPixelDarkIn(c);
			break;
		case PixelDarkOut:
			transition = newPixelDarkOut(c);
			break;
		case CrossRandom:
			transition = newCrossRandom(c);
			break;
		case SplitRandom:
			transition = newSplitRandom(c);
			break;
		case PixelWind:
			transition = newPixelWind(c);
			break;
		case PixelThunder:
			transition = newPixelThunder(c);
			break;
		}
		return transition;
	}

	/**
	 * 特效混合播放，把指定好的特效一起播放出去
	 * 
	 * @param transitions
	 * @return
	 */
	public static final LTransition newCombinedTransition(final TArray<LTransition> ts) {
		if (LSystem.base() != null) {
			return new LTransition(new CombinedTransition(ts));
		}
		return null;
	}

	/**
	 * 随机的百叶窗特效
	 * 
	 * @return
	 */
	public static final LTransition newCrossRandom() {
		return newCrossRandom(LColor.black);
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newCrossRandom(LColor c) {
		return newCross(MathUtils.random(0, 1), c, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public static final LTransition newCross(final int t, final LColor color, final float w, final float h) {
		return newCross(t, color, w, h, -1);
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param t
	 * @param color
	 * @param w
	 * @param h
	 * @param delay
	 * @return
	 */
	public static final LTransition newCross(final int t, final LColor color, final float w, final float h,
			final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new CrossEffect(t, color, w, h), delay);
		}
		return null;
	}

	/**
	 * 默认使用黑色的圆弧渐变特效
	 * 
	 * @return
	 */
	public static final LTransition newArc() {
		return newArc(LColor.black);
	}

	public static final LTransition newArc(final LColor c) {
		return newArc(c, -1);
	}

	/**
	 * 单一色彩的圆弧渐变特效
	 * 
	 * @param c
	 * @param delay
	 * @return
	 */
	public static final LTransition newArc(final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new ArcEffect(c), delay);
		}
		return null;
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param texture
	 * @return
	 */
	public static final LTransition newSplitRandom() {
		return newSplitRandom(LColor.black);
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newSplitRandom(LColor c) {
		return newSplit(MathUtils.random(0, Config.TDOWN), c, LSystem.viewSize.getWidth(),
				LSystem.viewSize.getHeight());
	}

	public static final LTransition newSplit(final int d, final LColor color, final float w, final float h) {
		return newSplit(d, color, w, h, -1);
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效(方向的静态值位于Config类中)
	 * 
	 * @param d
	 * @param color
	 * @param w
	 * @param h
	 * @param delay
	 * @return
	 */
	public static final LTransition newSplit(final int d, final LColor color, final float w, final float h,
			final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new SplitEffect(color, w, h, d), delay);
		}
		return null;
	}

	public static final LTransition newSplitRandom(LTexture texture) {
		return newSplit(MathUtils.random(0, Config.TDOWN), texture);
	}

	public static final LTransition newSplit(final int d, final LTexture texture) {
		return newSplit(d, texture, -1);
	}

	public static final LTransition newSplit(final int d, final LTexture texture, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new SplitEffect(texture, d), delay);
		}
		return null;
	}

	/**
	 * 产生一个黑色的淡入效果
	 * 
	 * @return
	 */
	public static final LTransition newFadeIn() {
		return newFade(FadeEffect.TYPE_FADE_IN);
	}

	/**
	 * 产生一个淡入效果
	 * 
	 * @param c
	 * @return
	 * 
	 */
	public static final LTransition newFadeIn(LColor c) {
		return newFade(FadeEffect.TYPE_FADE_IN, c);
	}

	/**
	 * 产生一个黑色的淡出效果
	 * 
	 * @return
	 */
	public static final LTransition newFadeOut() {
		return newFade(FadeEffect.TYPE_FADE_OUT);
	}

	/**
	 * 产生一个淡入效果
	 * 
	 * @param c
	 * @return
	 * 
	 */
	public static final LTransition newFadeOut(LColor c) {
		return newFade(FadeEffect.TYPE_FADE_OUT, c);
	}

	/**
	 * 产生一个黑色的淡入/淡出效果
	 * 
	 * @param t
	 * @return
	 */
	public static final LTransition newFade(int type) {
		return newFade(type, LColor.black);
	}

	public static final LTransition newFade(final int t, final LColor c) {
		return newFade(t, c, -1);
	}

	/**
	 * 产生一个指定色彩的淡入效果
	 * 
	 * @param t
	 * @param c
	 * @param delay
	 * @return
	 */
	public static final LTransition newFade(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeEffect(t, c), delay);
		}
		return null;
	}

	public static final LTransition newFadeDotOut(final LColor c) {
		return newFadeDot(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeDotIn(final LColor c) {
		return newFadeDot(FadeEffect.TYPE_FADE_IN, c);
	}

	public static final LTransition newFadeDot(final int t, final LColor c) {
		return newFadeDot(t, c, -1);
	}

	public static final LTransition newFadeDot(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeDotEffect(t, c), delay);
		}
		return null;
	}

	public static final LTransition newFadeTileOut(final LColor c) {
		return newFadeTile(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeTileIn(final LColor c) {
		return newFadeTile(FadeEffect.TYPE_FADE_IN, c);
	}

	public static final LTransition newFadeTile(final int t, final LColor c) {
		return newFadeTile(t, c, -1);
	}

	public static final LTransition newFadeTile(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeTileEffect(t, c), delay);
		}
		return null;
	}

	public static final LTransition newFadeSpiralOut(final LColor c) {
		return newFadeSpiral(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeSpiralIn(final LColor c) {
		return newFadeSpiral(FadeEffect.TYPE_FADE_IN, c);
	}

	public static final LTransition newFadeSpiral(final int t, final LColor c) {
		return newFadeSpiral(t, c, -1);
	}

	public static final LTransition newFadeSpiral(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeSpiralEffect(t, c), delay);
		}
		return null;
	}

	public static final LTransition newFadeDoorIrregularIn(final LColor c) {
		return newFadeDoorIrregular(ISprite.TYPE_FADE_IN, c);
	}

	public static final LTransition newFadeDoorIrregularOut(final LColor c) {
		return newFadeDoorIrregular(ISprite.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeDoorIrregular(final int t, final LColor c) {
		return newFadeDoorIrregular(t, c, -1);
	}

	public static final LTransition newFadeDoorIrregular(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeDoorIrregularEffect(t, c), delay);
		}
		return null;
	}

	/**
	 * 斜滑过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newFadeSwipeOut(final LColor c) {
		return newFadeSwipe(FadeEffect.TYPE_FADE_OUT, c);
	}

	/**
	 * 斜滑过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newFadeSwipeIn(final LColor c) {
		return newFadeSwipe(FadeEffect.TYPE_FADE_IN, c);
	}

	public static final LTransition newFadeSwipe(final int t, final LColor c) {
		return newFadeSwipe(t, c, -1);
	}

	/**
	 * 斜滑过渡特效
	 * 
	 * @param t
	 * @param c
	 * @param delay
	 * @return
	 */
	public static final LTransition newFadeSwipe(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeSwipeEffect(t, c), delay);
		}
		return null;
	}

	/**
	 * 瓦片淡入(从左到右)
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newFadeBoardIn(LColor c) {
		return newFadeBoard(FadeEffect.TYPE_FADE_IN, c);
	}

	/**
	 * 瓦片淡出(从左到右)
	 * 
	 * @param c
	 * @return
	 */
	public static final LTransition newFadeBoardOut(LColor c) {
		return newFadeBoard(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeBoard(final int type, final LColor c) {
		return newFadeBoard(type, FadeBoardEffect.START_LEFT, c);
	}

	public static final LTransition newFadeBoard(final int t, final int dir, final LColor c) {
		return newFadeBoard(t, dir, c, -1);
	}

	/**
	 * 瓦片淡出或淡入(从左到右,或从右到左)
	 * 
	 * @param t
	 * @param dir
	 * @param c
	 * @param delay
	 * @return
	 */
	public static final LTransition newFadeBoard(final int t, final int dir, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeBoardEffect(t, dir, c), delay);
		}
		return null;
	}

	/**
	 * 产生一个椭圆进入效果
	 * 
	 * @return
	 */
	public static final LTransition newOvalHollowIn(LColor c) {
		return newOvalHollow(FadeEffect.TYPE_FADE_IN, c);
	}

	/**
	 * 产生一个椭圆出现效果
	 * 
	 * @return
	 */
	public static final LTransition newOvalHollowOut(LColor c) {
		return newOvalHollow(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newOvalHollow(final int t, final LColor c) {
		return newOvalHollow(t, c, -1);
	}

	public static final LTransition newOvalHollow(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeOvalHollowEffect(t, c), delay);
		}
		return null;
	}

	/**
	 * 产生一个黑色的淡入效果
	 * 
	 * @return
	 */
	public static final LTransition newFadeOvalIn(LColor c) {
		return newFadeOval(FadeEffect.TYPE_FADE_IN, c);
	}

	/**
	 * 产生一个黑色的淡出效果
	 * 
	 * @return
	 */
	public static final LTransition newFadeOvalOut(LColor c) {
		return newFadeOval(FadeEffect.TYPE_FADE_OUT, c);
	}

	public static final LTransition newFadeOval(final int t, final LColor c) {
		return newFadeOval(t, c, -1);
	}

	public static final LTransition newFadeOval(final int t, final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new FadeOvalEffect(t, c), delay);
		}
		return null;
	}

	public static final LTransition newPixelWind(final LColor c) {
		return newPixelWind(c, -1);
	}

	public static final LTransition newPixelWind(final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new PixelWindEffect(c), delay);
		}
		return null;
	}

	public static final LTransition newPixelDarkIn(final LColor c) {
		return newPixelDarkIn(c, -1);
	}

	public static final LTransition newPixelDarkIn(final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new PixelDarkInEffect(c), delay);
		}
		return null;
	}

	public static final LTransition newPixelDarkOut(final LColor c) {
		return newPixelDarkOut(c, -1);
	}

	public static final LTransition newPixelDarkOut(final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new PixelDarkOutEffect(c), delay);
		}
		return null;
	}

	public static final LTransition newPixelThunder(final LColor c) {
		return newPixelThunder(c, -1);
	}

	public static final LTransition newPixelThunder(final LColor c, final long delay) {
		if (LSystem.base() != null) {
			return createEffectTransition(new PixelThunderEffect(c), delay);
		}
		return null;
	}

	public static final LTransition newEmpty() {
		return new LTransition(new EffectToTransition(null));
	}

	public static final LTransition createEffectTransition(BaseAbstractEffect effect) {
		return createEffectTransition(effect, -1);
	}

	public static final LTransition createEffectTransition(BaseAbstractEffect effect, long delay) {
		return new LTransition(new EffectToTransition(effect, delay));
	}

	// 是否在在启动过渡效果同时显示游戏画面（即是否顶层绘制过渡画面，底层同时绘制标准游戏画面）
	boolean isDisplayGameUI;

	int code;

	TransitionListener listener;

	public LTransition(TransitionListener l) {
		this(true, 1, l);
	}

	public LTransition(boolean display, int c, TransitionListener l) {
		this.isDisplayGameUI = display;
		this.code = c;
		this.listener = l;
	}

	public void setDisplayGameUI(boolean s) {
		this.isDisplayGameUI = s;
	}

	public boolean isDisplayGameUI() {
		return this.isDisplayGameUI;
	}

	public void setTransitionListener(TransitionListener l) {
		this.listener = l;
	}

	public TransitionListener getTransitionListener() {
		return this.listener;
	}

	final void update(long elapsedTime) {
		if (listener != null) {
			listener.update(elapsedTime);
		}
	}

	final void draw(GLEx g) {
		if (listener != null) {
			listener.draw(g);
		}
	}

	final boolean completed() {
		if (listener != null) {
			return listener.completed();
		}
		return false;
	}

	final void close() {
		if (listener != null) {
			listener.close();
		}
	}
}

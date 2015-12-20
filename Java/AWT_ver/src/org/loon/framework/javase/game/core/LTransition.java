package org.loon.framework.javase.game.core;

import org.loon.framework.javase.game.action.map.Config;
import org.loon.framework.javase.game.action.sprite.WaitSprite;
import org.loon.framework.javase.game.action.sprite.effect.ArcEffect;
import org.loon.framework.javase.game.action.sprite.effect.CrossEffect;
import org.loon.framework.javase.game.action.sprite.effect.FadeEffect;
import org.loon.framework.javase.game.action.sprite.effect.OutEffect;
import org.loon.framework.javase.game.action.sprite.effect.ScaleEffect;
import org.loon.framework.javase.game.action.sprite.effect.SplitEffect;
import org.loon.framework.javase.game.core.graphics.LColor;
import org.loon.framework.javase.game.core.graphics.LImage;
import org.loon.framework.javase.game.core.graphics.device.LGraphics;
import org.loon.framework.javase.game.utils.GraphicsUtils;
import org.loon.framework.javase.game.utils.ScreenUtils;

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
public class LTransition {

	/**
	 * 随机的百叶窗特效
	 * 
	 * @return
	 */
	public final static LTransition newCrossRandom() {
		return newCrossRandom(LColor.black);
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCrossRandom(LColor c) {
		return newCross(LSystem.getRandomBetWeen(0, 1), GraphicsUtils
				.createLImage(LSystem.screenRect.width,
						LSystem.screenRect.height, c));
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCross(final int c) {
		return newCross(c, ScreenUtils.toScreenCaptureImage());
	}

	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCross(final int c, final LImage texture) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final CrossEffect cross = new CrossEffect(c, texture);

			public void draw(LGraphics g) {
				cross.createUI(g);
			}

			public void update(long elapsedTime) {
				cross.update(elapsedTime);
			}

			public boolean completed() {
				return cross.isComplete();
			}

			public void dispose() {
				cross.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 将当前Screen画面缩小后消失
	 * 
	 * @return
	 */
	public final static LTransition newScale() {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final ScaleEffect scale = new ScaleEffect(ScreenUtils
					.toScreenCaptureImage(), true);

			public void draw(LGraphics g) {
				scale.createUI(g);
			}

			public void update(long elapsedTime) {
				scale.update(elapsedTime);
			}

			public boolean completed() {
				return scale.isComplete();
			}

			public void dispose() {
				scale.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 默认使用黑色的圆弧渐变特效
	 * 
	 * @return
	 */
	public final static LTransition newArc() {
		return newArc(LColor.black);
	}

	/**
	 * 单一色彩的圆弧渐变特效
	 * 
	 * @return
	 */
	public final static LTransition newArc(final LColor c) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final ArcEffect arc = new ArcEffect(c);

			public void draw(LGraphics g) {
				arc.createUI(g);
			}

			public void update(long elapsedTime) {
				arc.update(elapsedTime);
			}

			public boolean completed() {
				return arc.isComplete();
			}

			public void dispose() {
				arc.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 产生一个随机的Screen画面分裂过渡特效
	 * 
	 * @return
	 */
	public final static LTransition newSplitRandom() {
		return newSplit(LSystem.getRandomBetWeen(0, Config.TDOWN));
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplitRandom(LImage texture) {
		return newSplit(LSystem.getRandomBetWeen(0, Config.TDOWN), texture);
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newSplitRandom(LColor c) {
		return newSplitRandom(GraphicsUtils.createLImage(
				LSystem.screenRect.width, LSystem.screenRect.height, c));
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效(方向的静态值位于Config类中)
	 * 
	 * @param d
	 * @return
	 */
	public final static LTransition newSplit(final int d) {
		return newSplit(d, ScreenUtils.toScreenCaptureImage());
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效(方向的静态值位于Config类中)
	 * 
	 * @param d
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplit(final int d, final LImage texture) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final SplitEffect split = new SplitEffect(texture, d);

			public void draw(LGraphics g) {
				split.createUI(g);
			}

			public void update(long elapsedTime) {
				split.update(elapsedTime);
			}

			public boolean completed() {
				return split.isComplete();
			}

			public void dispose() {
				split.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 产生上一个Screen画面向指定方向离开的过渡特效
	 * 
	 * PS:方向的静态值位于Config类中(以T开头者为实际方向，其余皆为45度移动时方向)
	 * 
	 * @param type
	 * @return
	 */
	public final static LTransition newOut(final int type) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final OutEffect out = new OutEffect(ScreenUtils
					.toScreenCaptureImage(), type);

			public void draw(LGraphics g) {
				out.createUI(g);
			}

			public void update(long elapsedTime) {
				out.update(elapsedTime);
			}

			public boolean completed() {
				return out.isComplete();
			}

			public void dispose() {
				out.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	/**
	 * 获得一个随机的循环过渡特效
	 * 
	 * @return
	 */
	public final static LTransition newCycleRandom() {
		return newCycle(LSystem.getRandomBetWeen(0, 8));
	}

	/**
	 * 获得一个循环方式的（首尾呼应）的过渡特效，默认支持0-11共12种特效
	 * 
	 * @param type
	 * @return
	 */
	public final static LTransition newCycle(final int type) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final WaitSprite wait = new WaitSprite(type);

			public void draw(LGraphics g) {
				wait.createUI(g);
			}

			public void update(long elapsedTime) {
				wait.update(elapsedTime);
			}

			public boolean completed() {
				return true;
			}

			public void dispose() {
				wait.dispose();
			}

		});
		transition.setDisplayGameUI(false);
		transition.code = 0;
		return transition;

	}

	/**
	 * 产生一个黑色的淡入效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeIn() {
		return LTransition.newFade(FadeEffect.TYPE_FADE_IN);
	}

	/**
	 * 产生一个黑色的淡出效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeOut() {
		return LTransition.newFade(FadeEffect.TYPE_FADE_OUT);
	}

	/**
	 * 产生一个黑色的淡入/淡出效果
	 * 
	 * @param type
	 * @return
	 */
	public final static LTransition newFade(int type) {
		return LTransition.newFade(type, LColor.black);
	}

	/**
	 * 产生一个指定色彩的淡入效果
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newFade(final int type, final LColor c) {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			final FadeEffect fade = FadeEffect.getInstance(type, c);

			public void draw(LGraphics g) {
				fade.createUI(g);
			}

			public void update(long elapsedTime) {
				fade.update(elapsedTime);
			}

			public boolean completed() {
				return fade.isStop();
			}

			public void dispose() {
				fade.dispose();
			}

		});
		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	public final static LTransition newEmpty() {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			public void draw(LGraphics g) {
			}

			public void update(long elapsedTime) {
			}

			public boolean completed() {
				return true;
			}

			public void dispose() {
			}

		});

		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	public static interface TransitionListener {

		public void update(long elapsedTime);

		public void draw(LGraphics g);

		public boolean completed();

		public void dispose();
	}

	// 是否在在启动过渡效果同时显示游戏画面（即是否顶层绘制过渡画面，底层同时绘制标准游戏画面）
	boolean isDisplayGameUI;

	int code;

	TransitionListener listener;

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

	final void draw(LGraphics g) {
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

	final void dispose() {
		if (listener != null) {
			listener.dispose();
		}
	}
}

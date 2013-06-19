package loon.core.input;

import loon.action.map.Config;
import loon.action.sprite.ISprite;
import loon.action.sprite.effect.ArcEffect;
import loon.action.sprite.effect.CrossEffect;
import loon.action.sprite.effect.FadeEffect;
import loon.action.sprite.effect.PShadowEffect;
import loon.action.sprite.effect.SplitEffect;
import loon.core.LSystem;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.TextureUtils;
import loon.utils.MathUtils;


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
/**
 * 自0.3.2版起新增的Screen切换过渡效果类，内置有多种过渡特效。
 * 
 * example:
 * 
 * public class Sample extends Screen{
 * 
 * ......
 * 
 * public LTransition onTransition(){ return LTransition.xxx(method) } }
 * 
 * 
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
		return newCross(MathUtils.random(0, 1), TextureUtils
				.createTexture(LSystem.screenRect.width,
						LSystem.screenRect.height, c));
	}


	/**
	 * 百叶窗特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newCross(final int c, final LTexture texture) {

		if (GLEx.self != null) {

			final LTransition transition = new LTransition();

			transition.setTransitionListener(new TransitionListener() {

				final CrossEffect cross = new CrossEffect(c, texture);

				@Override
				public void draw(GLEx g) {
					cross.createUI(g);
				}

				@Override
				public void update(long elapsedTime) {
					cross.update(elapsedTime);
				}

				@Override
				public boolean completed() {
					return cross.isComplete();
				}

				@Override
				public void dispose() {
					cross.dispose();
				}

			});
			transition.setDisplayGameUI(true);
			transition.code = 1;
			return transition;
		}
		return null;
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

		if (GLEx.self != null) {

			final LTransition transition = new LTransition();

			transition.setTransitionListener(new TransitionListener() {

				final ArcEffect arc = new ArcEffect(c);

				@Override
				public void draw(GLEx g) {
					arc.createUI(g);
				}

				@Override
				public void update(long elapsedTime) {
					arc.update(elapsedTime);
				}

				@Override
				public boolean completed() {
					return arc.isComplete();
				}

				@Override
				public void dispose() {
					arc.dispose();
				}

			});
			transition.setDisplayGameUI(true);
			transition.code = 1;
			return transition;
		}
		return null;
	}


	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplitRandom(LTexture texture) {
		return newSplit(MathUtils.random(0, Config.TDOWN), texture);
	}

	/**
	 * 产生一个Screen画面向双向分裂的过渡特效
	 * 
	 * @param c
	 * @return
	 */
	public final static LTransition newSplitRandom(LColor c) {
		return newSplitRandom(TextureUtils.createTexture(
				LSystem.screenRect.width, LSystem.screenRect.height, c));
	}


	/**
	 * 产生一个Screen画面向双向分裂的过渡特效(方向的静态值位于Config类中)
	 * 
	 * @param d
	 * @param texture
	 * @return
	 */
	public final static LTransition newSplit(final int d, final LTexture texture) {

		if (GLEx.self != null) {

			final LTransition transition = new LTransition();

			transition.setTransitionListener(new TransitionListener() {

				final SplitEffect split = new SplitEffect(texture, d);

				@Override
				public void draw(GLEx g) {
					split.createUI(g);
				}

				@Override
				public void update(long elapsedTime) {
					split.update(elapsedTime);
				}

				@Override
				public boolean completed() {
					return split.isComplete();
				}

				@Override
				public void dispose() {
					split.dispose();
				}

			});
			transition.setDisplayGameUI(true);
			transition.code = 1;
			return transition;
		}
		return null;
	}

	/**
	 * 产生一个黑色的淡入效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeIn() {
		return LTransition.newFade(ISprite.TYPE_FADE_IN);
	}

	/**
	 * 产生一个黑色的淡出效果
	 * 
	 * @return
	 */
	public final static LTransition newFadeOut() {
		return LTransition.newFade(ISprite.TYPE_FADE_OUT);
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
		if (GLEx.self != null) {
			final LTransition transition = new LTransition();

			transition.setTransitionListener(new TransitionListener() {

				final FadeEffect fade = FadeEffect.getInstance(type, c);

				@Override
				public void draw(GLEx g) {
					fade.createUI(g);
				}

				@Override
				public void update(long elapsedTime) {
					fade.update(elapsedTime);
				}

				@Override
				public boolean completed() {
					return fade.isStop();
				}

				@Override
				public void dispose() {
					fade.dispose();
				}

			});
			transition.setDisplayGameUI(true);
			transition.code = 1;
			return transition;
		}
		return null;
	}

	/**
	 * 以指定地址图片作渐变式过滤(实际上这种过渡方式所能产生的效果是无穷的，具体可参考吉里吉里(krkr) 用过渡图)
	 * 
	 * @param fileName
	 * @param alhpa
	 */
	public final static LTransition newPShadow(String fileName, float alhpa) {
		PShadowEffect shadow = new PShadowEffect(fileName);
		shadow.setAlpha(alhpa);
		return newPShadow(shadow);
	}

	/**
	 * 以指定地址图片作渐变式过滤
	 * 
	 * @param fileName
	 */
	public final static LTransition newPShadow(String fileName) {
		return newPShadow(fileName, 0.5f);
	}

	/**
	 * 转化PShadowEffect为LTransition
	 * 
	 * @param effect
	 * @return
	 */
	public final static LTransition newPShadow(final PShadowEffect effect) {
		if (GLEx.self != null) {

			final LTransition transition = new LTransition();

			transition.setTransitionListener(new TransitionListener() {

				@Override
				public void draw(GLEx g) {
					effect.createUI(g);
				}

				@Override
				public void update(long elapsedTime) {
					effect.update(elapsedTime);
				}

				@Override
				public boolean completed() {
					return effect.isComplete();
				}

				@Override
				public void dispose() {
					effect.dispose();
				}

			});
			transition.setDisplayGameUI(true);
			transition.code = 1;
			return transition;
		}
		return null;
	}

	public final static LTransition newEmpty() {

		final LTransition transition = new LTransition();

		transition.setTransitionListener(new TransitionListener() {

			@Override
			public void draw(GLEx g) {
			}

			@Override
			public void update(long elapsedTime) {
			}

			@Override
			public boolean completed() {
				return true;
			}

			@Override
			public void dispose() {
			}

		});

		transition.setDisplayGameUI(true);
		transition.code = 1;
		return transition;

	}

	public static interface TransitionListener {

		public void update(long elapsedTime);

		public void draw(GLEx g);

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

	final void dispose() {
		if (listener != null) {
			listener.dispose();
		}
	}
}

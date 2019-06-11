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

import loon.event.GameTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimerContext;

/**
 * 一个Screen的衍生抽象类,除了create默认都不必实现,纯组件构建游戏时可以使用此类派生画面
 */
public abstract class Stage extends Screen {

	private float percent;

	private float maxPercent;

	public final Stage setPercentMaximum(float max) {
		this.maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage setPercent(float cur, float max) {
		this.percent = MathUtils.clamp(cur, 0f, 100f);
		this.maxPercent = MathUtils.clamp(max, 0f, 100f);
		return this;
	}

	public final Stage updatePercent(float num) {
		this.percent = MathUtils.clamp(num, 0f, 100f) / maxPercent;
		return this;
	}

	public final Stage addPercent() {
		return updatePercent(percent++);
	}

	public final Stage removePercent() {
		return updatePercent(percent--);
	}

	public final Stage resetPercent() {
		this.percent = 0f;
		return this;
	}

	public final int getMaximumPercent() {
		return (int) maxPercent;
	}

	public final int getPercent() {
		return (int) percent;
	}

	@Override
	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	public abstract void create();

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void onLoad() {
		try {
			create();
		} catch (Throwable cause) {
			LSystem.error("Screen create failure", cause);
		}
	}

	@Override
	public void alter(LTimerContext timer) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void touchDown(GameTouch e) {

	}

	@Override
	public void touchUp(GameTouch e) {

	}

	@Override
	public void touchMove(GameTouch e) {

	}

	@Override
	public void touchDrag(GameTouch e) {

	}

	@Override
	public void resume() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void close() {

	}

}

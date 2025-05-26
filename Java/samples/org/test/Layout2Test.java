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
package org.test;

import loon.Screen;
import loon.component.LLabel;
import loon.component.LTextField;
import loon.component.layout.HorizontalLayout;
import loon.component.layout.VerticalLayout;
import loon.events.GameTouch;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class Layout2Test extends Screen {

	@Override
	public void onLoad() {

		// 设定留空大小
		getRootConstraints().setPadding(25);
		// 单独设置下方间隔
		getRootConstraints().setPaddingBottom(50);

		// 设定横排排序样式
		HorizontalLayout hlayout = HorizontalLayout.at();
		// 不允许自动拉伸组件为全屏
		hlayout.setChangeSize(false);
		// 设定参与排序的组件
		hlayout.add(LLabel.make("NAME:"));
		hlayout.add(new LTextField());
		hlayout.add(LLabel.make("AGE:"));
		hlayout.add(new LTextField());
		// 添加组件到Screen
		hlayout.packTo(this);

		// 设定竖排排序样式
		VerticalLayout vlayout = VerticalLayout.at();
		vlayout.setChangeSize(false);
		vlayout.add(LLabel.make("testing1"));
		vlayout.add(LLabel.make("testing2"));
		vlayout.add(LLabel.make("testing3"));
		// 添加组件到Screen
		vlayout.packTo(this);
	}

	@Override
	public void draw(GLEx g) {

	}

	@Override
	public void alter(LTimerContext context) {

	}

	@Override
	public void resize(int w, int h) {

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

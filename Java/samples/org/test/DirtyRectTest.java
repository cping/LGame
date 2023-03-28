/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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

import loon.LTransition;
import loon.Screen;
import loon.canvas.LColor;
import loon.events.GameTouch;
import loon.geom.DirtyRectList;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimerContext;

public class DirtyRectTest extends Screen {

	DirtyRectList list = new DirtyRectList(true);

	public LTransition onTransition() {
		return LTransition.newEmpty();
	}

	@Override
	public void draw(GLEx g) {
		// 绘制初始位置
		for (RectBox rect : list.initList()) {
			if (rect != null) {
				g.drawRect(rect.x, rect.y, rect.width, rect.height, LColor.white);
			}
		}
		// 绘制脏绘位置(坐标偏移1位)
		for (RectBox rect : list.list()) {
			if (rect != null) {
				g.drawRect(rect.x + 1, rect.y + 1, rect.width + 1, rect.height + 1, LColor.red);
			}
		}

		// 绘制最大范围合并后矩形
		g.drawRect(list.merge().x() + 1, list.merge().y() + 1, list.merge().width() + 2, list.merge().height() + 2, LColor.green);

	}

	@Override
	public void onLoad() {
		// 添加不同位置矩形
		list.add(45, 45, 40, 60);
		list.add(145, 45, 190, 60);
		list.add(45, 45, 95, 65);
		list.add(195, 245, 95, 65);
		list.add(35, 35, 95, 65);

	}

	@Override
	public void alter(LTimerContext context) {

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

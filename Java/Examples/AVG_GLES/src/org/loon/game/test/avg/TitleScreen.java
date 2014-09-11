package org.loon.game.test.avg;

import loon.core.LSystem;
import loon.core.event.ActionKey;
import loon.core.graphics.Screen;
import loon.core.graphics.component.LButton;
import loon.core.graphics.component.LPaper;
import loon.core.graphics.opengl.GLEx;
import loon.core.input.LTouch;
import loon.core.input.LTransition;
import loon.core.timer.LTimerContext;

/**
 * Copyright 2008 - 2010
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
public class TitleScreen extends Screen {

	LButton start, end;

	LPaper title;

	public TitleScreen() {

	}

	public LTransition onTransition() {
		return LTransition.newCrossRandom();
	}

	public void onLoad() {
		// 变更背景
		setBackground("assets/back1.png");

		// 创建一个开始按钮，按照宽191，高57分解按钮图，并设定其Click事件
		start = new LButton("assets/title_start.png", 191, 57) {

			ActionKey action = new ActionKey(
					ActionKey.DETECT_INITIAL_PRESS_ONLY);

			public void doClick() {
				if (!action.isPressed()) {
					action.press();
					replaceScreen(new MyAVGScreen(), MoveMethod.FROM_LEFT);
				}
			}
		};
		// 设定按钮位置为x=2,y=5
		start.setLocation(2, 5);
		// 设定此按钮不可用
		start.setEnabled(false);
		// 添加按钮
		add(start);

		// 创建一个记录读取按钮，按照宽160，高56分解按钮图
		LButton btn2 = new LButton("assets/title_load.png", 160, 56);
		// 设定按钮位置为x=2,y=start位置类推
		btn2.setLocation(2, start.getY() + start.getHeight() + 20);
		// 设定此按钮不可用
		btn2.setEnabled(false);
		// 添加按钮
		add(btn2);

		// 创建一个环境设置按钮，按照宽215，高57分解按钮图
		LButton btn3 = new LButton("assets/title_option.png", 215, 57);
		// 设定按钮位置为x=2,y=btn2位置类推
		btn3.setLocation(2, btn2.getY() + btn2.getHeight() + 20);
		// 设定此按钮不可用
		btn3.setEnabled(false);
		// 添加按钮
		add(btn3);

		// 创建一个退出按钮，按照宽142，高57分解按钮图，并设定其Click事件
		end = new LButton("assets/title_end.png", 142, 57) {
			public void doClick() {
				LSystem.exit();
			}
		};
		// 设定按钮位置为x=2,y=btn3位置类推
		end.setLocation(2, btn3.getY() + btn3.getHeight() + 20);
		// 设定此按钮不可用
		end.setEnabled(false);
		// 添加按钮
		add(end);

		// 增加一个标题
		title = new LPaper("assets/title.png", -200, 0);
		// 添加标题
		add(title);
	}

	public void alter(LTimerContext c) {
		if (isOnLoadComplete()) {
			// 标题未达到窗体边缘
			if (title.getScreenX() + title.getWidth() + 25 <= getWidth()) {
				// 以三倍速移动（红色无角……）
				title.move_right(3);
			} else {
				// 设定开始按钮可用
				start.setEnabled(true);
				// 设定结束按钮可用
				end.setEnabled(true);
			}
		}
	}

	public void draw(GLEx g) {

	}

	public void touchDown(LTouch e) {

	}

	public void touchMove(LTouch e) {

	}

	public void touchUp(LTouch e) {

	}

}

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
 * @email锛歝eponline@yahoo.com.cn
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
		// 鍙樻洿鑳屾櫙
		setBackground("assets/back1.png");

		// 鍒涘缓涓�釜寮�鎸夐挳锛屾寜鐓у191锛岄珮57鍒嗚В鎸夐挳鍥撅紝骞惰瀹氬叾Click浜嬩欢
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
		// 璁惧畾鎸夐挳浣嶇疆涓簒=2,y=5
		start.setLocation(2, 5);
		// 璁惧畾姝ゆ寜閽笉鍙敤
		start.setEnabled(false);
		// 娣诲姞鎸夐挳
		add(start);

		// 鍒涘缓涓�釜璁板綍璇诲彇鎸夐挳锛屾寜鐓у160锛岄珮56鍒嗚В鎸夐挳鍥�
		LButton btn2 = new LButton("assets/title_load.png", 160, 56);
		// 璁惧畾鎸夐挳浣嶇疆涓簒=2,y=start浣嶇疆绫绘帹
		btn2.setLocation(2, start.getY() + start.getHeight() + 20);
		// 璁惧畾姝ゆ寜閽笉鍙敤
		btn2.setEnabled(false);
		// 娣诲姞鎸夐挳
		add(btn2);

		// 鍒涘缓涓�釜鐜璁剧疆鎸夐挳锛屾寜鐓у215锛岄珮57鍒嗚В鎸夐挳鍥�
		LButton btn3 = new LButton("assets/title_option.png", 215, 57);
		// 璁惧畾鎸夐挳浣嶇疆涓簒=2,y=btn2浣嶇疆绫绘帹
		btn3.setLocation(2, btn2.getY() + btn2.getHeight() + 20);
		// 璁惧畾姝ゆ寜閽笉鍙敤
		btn3.setEnabled(false);
		// 娣诲姞鎸夐挳
		add(btn3);

		// 鍒涘缓涓�釜閫�嚭鎸夐挳锛屾寜鐓у142锛岄珮57鍒嗚В鎸夐挳鍥撅紝骞惰瀹氬叾Click浜嬩欢
		end = new LButton("assets/title_end.png", 142, 57) {
			public void doClick() {
				LSystem.exit();
			}
		};
		// 璁惧畾鎸夐挳浣嶇疆涓簒=2,y=btn3浣嶇疆绫绘帹
		end.setLocation(2, btn3.getY() + btn3.getHeight() + 20);
		// 璁惧畾姝ゆ寜閽笉鍙敤
		end.setEnabled(false);
		// 娣诲姞鎸夐挳
		add(end);

		// 澧炲姞涓�釜鏍囬
		title = new LPaper("assets/title.png", -200, 0);
		// 娣诲姞鏍囬
		add(title);
	}

	public void alter(LTimerContext c) {
		if (isOnLoadComplete()) {
			// 鏍囬鏈揪鍒扮獥浣撹竟缂�
			if (title.getScreenX() + title.getWidth() + 25 <= getWidth()) {
				// 浠ヤ笁鍊嶉�绉诲姩锛堢孩鑹叉棤瑙掆�鈥︼級
				title.move_right(3);
			} else {
				// 璁惧畾寮�鎸夐挳鍙敤
				start.setEnabled(true);
				// 璁惧畾缁撴潫鎸夐挳鍙敤
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

	@Override
	public void touchDrag(LTouch e) {
		// TODO Auto-generated method stub
		
	}

}

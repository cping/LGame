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
package application;

import loon.Stage;
import loon.action.sprite.NumberSprite;
import loon.canvas.LColor;
import loon.component.LClickButton;
import loon.events.Touched;
import loon.utils.timer.CountdownTimer;

public class StageTest extends Stage {

	@Override
	public void create() {

		// 设置默认倒计时器,倒数30秒
		final CountdownTimer timer = new CountdownTimer(30);
		// 不显示毫秒,只显示秒
		// final CountdownTimer timer = new CountdownTimer(30,false);
		// 以CountdownTimer设置NumberSprite内容,显示色彩白色,构成数字的每块小格像素大小5（渲染为3x6的像素块）
		NumberSprite sprite = new NumberSprite(timer, LColor.white, 5);
		centerOn(sprite);
		add(sprite);
		add(new LClickButton("30 Play", 50, 50, 100, 50).up((x, y) -> {
			timer.play(30);
		}));
		add(new LClickButton("60 Play", 50, 180, 100, 50).up((x, y) -> {
			timer.play(60);
		}));

	}

}

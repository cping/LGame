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

import loon.ScreenExitEffect;
import loon.Stage;
import loon.component.LClickButton;

public class ScreenExitEffectTest extends Stage {

	public static class StageConvertTest extends Stage {

		@Override
		public void create() {
			setBackground("assets/rpg/battle/back.png");
			LClickButton click = node("click", "GoScreen1", 0, 0, 150, 30);
			centerOn(click);
			add(click.up((x, y) -> {
				// 以固定的空心椭圆渐变效果转换回Screen1
				gotoScreenEffectExit(ScreenExitEffect.OVAL_HOLLOW_FADE, new ScreenExitEffectTest());
			}));

		}

	}

	@Override
	public void create() {
		background("assets/back1.png");
		LClickButton click = node("click", "GoScreen2", 0, 0, 150, 30);
		centerOn(click);
		add(click.up((x, y) -> {
			//以随机效果跳转去Screen2
			gotoScreenEffectExitRand(new StageConvertTest());
		}));
	}

}

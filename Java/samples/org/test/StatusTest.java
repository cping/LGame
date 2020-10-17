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

import loon.Stage;
import loon.action.sprite.StatusBar;
import loon.events.Touched;
import loon.font.BMFont;

public class StatusTest extends Stage{

	@Override
	public void create() {
		final StatusBar bar = new StatusBar(55, 55, 200, 25);
		// 字体使用图片文字
		bar.setFont(new BMFont("assets/info.fnt", "assets/info.png"));
		// 显示状态条数值变化
		bar.setShowNumber(true);
		// 格式化状态条显示内容
		bar.setFormatNumber("HP : {0}");
		// 状态变更为数值30
		bar.setUpdate(30);
		//bar.setPercentage(0.3f);
		centerOn(bar);
		add(bar);
		up(new Touched() {
			
			@Override
			public void on(float x, float y) {
				//触屏后数值归零
				bar.setUpdate(0);
				//bar.setPercentage(0f);
			}
		});
		add(MultiScreenTest.getBackButton(this, 2));
	}

}

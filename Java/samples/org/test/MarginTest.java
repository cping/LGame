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

public class MarginTest extends Stage {

	@Override
	public void create() {
		// 注入30个按钮
		for (int i = 0; i < 30; i++) {
			addButton(String.valueOf(i), 0, 0, 50, 50);
		}
		// 竖行(垂直)排列模式,越界自动换行,左上右下分别间隔5,5,5,5像素，整体坐标偏移15,5
		// (如果需要改变对象大小以适应区域大小,应使用LayoutManager处理,Margin只负责改变动作对象位置)
		getDesktop().margin(true, 5, 5, 5, 5).setOffset(15, 5).layout();

		/*
		 * for(int i=0;i<30;i++){ add(new SpriteLabel(String.valueOf(i))); }
		 * getSprites().margin(true, 5, 5, 5, 5).setOffset(15, 5).layout();
		 */

		add(MultiScreenTest.getBackButton(this, 2));
	}

}

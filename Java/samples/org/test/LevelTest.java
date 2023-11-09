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

public class LevelTest extends Stage{

	@Override
	public void create() {
		//构建基于二维数组的层级地图,瓦片大小32x32,布局如下
		createLevel().addLevel(32, 32, 
				"AAAAAAAAAA", 
				"A      A", 
				"A c    A", 
				"A      AAA", 
				"A      A", 
				"A B    A", 
				"AAAAAAAA");
		//默认使用层级0
		setLevelIndex(0);
        //转换层级坐标到游戏坐标并添加对应精灵
		getLevelMap().switchMap((f, x, y) -> {

			switch (f) {
			case 'A':
				addTextureObject(x, y, "assets/a4.png");
				break;
			case 'B':
				addTextureObject(x, y, "assets/ball.png");
				break;
			case 'c':
				addTextureObject(x, y, 64, 64, "assets/ccc.png");
				break;
			}

		});

	}

}

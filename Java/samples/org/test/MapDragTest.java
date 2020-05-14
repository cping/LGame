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
import loon.action.map.TileMap;
import loon.event.Touched;

public class MapDragTest extends Stage {

	@Override
	public void create() {
		final TileMap map = new TileMap("assets/rpg/map.txt", 32, 32);
		// 按照瓦片规格自动获取地图切片(切出来大小都是一样的,只对规则图片有效)
		map.setImagePackAuto("assets/rpg/map.png", 32, 32);
		// 执行切图
		map.pack();

		// 注入地图到窗体
		add(map);

		drag(new Touched() {

			@Override
			public void on(float x, float y) {
				map.scroll(x, y);
			}
		});
	}

}

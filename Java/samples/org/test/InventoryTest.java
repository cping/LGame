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
import loon.action.map.items.ItemInfo;
import loon.component.LInventory;
import loon.component.LLabel;

public class InventoryTest extends Stage {

	@Override
	public void create() {
		setBackground("back1.png");
		// 构建背包到位置70,40大小310x240,不限制背包物品拖拽出背包
		LInventory inv = new LInventory(70, 40, 310, 240, false);
		// inv.setDisplayDrawGrid(false);
		// 绘制圆形网格
		// inv.setCircleGrid(true);
		// 构建背包格分布,左右间隔像素2,30,拆分为每行,列6个格
		inv.leftTop(2, 30, 6, 6);
		// inv.rightBottom(22, 30, 6, 6);
		// inv.topBottom(30,25,6,6);
		// 注入物品图像与信息
		inv.putItem("a4.png", new ItemInfo("桶", "一个桶"));
		inv.putItem("ball.png");
		inv.putItem("coin.png");
		inv.putItem("ball.png");
		// 添加一个label到背包的坐标5,5
		inv.add(LLabel.make("我是背包", 5, 5));
		add(inv);
	}

}

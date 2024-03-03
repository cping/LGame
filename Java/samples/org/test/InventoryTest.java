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
import loon.component.LClickButton;
import loon.component.LInventory;
import loon.component.LLabel;
import loon.utils.MathUtils;

public class InventoryTest extends Stage {

	@Override
	public void create() {

		// setLocation(66, 100);
		setBackground("back1.png");
		// 构建背包到位置40,40大小310x240,不限制背包物品拖拽出背包
		final LInventory inv = new LInventory(40, 40, 310, 240, false);

		// 允许背包拖拽出窗口范围
		// inv.setLimitMove(false);
		// 禁止actor对象拖拽(物品既actor)
		// inv.setActorDrag(false);
		// inv.setDisplayDrawGrid(false);
		// 绘制圆形网格
		// inv.setCircleGrid(true);
		// 构建背包格分布,左右间隔像素2,30,拆分为每行,列6个格
		inv.leftTop(2, 30, 6, 6);
		// inv.rightBottom(22, 30, 6, 6);
		// inv.topBottom(30,25,6,6);
		// 注入物品图像与信息
		inv.putItem("a4.png", new ItemInfo("桶", "一个桶堆放姜山,\n一颗球颠倒人间"));
		inv.putItem("ball.png");
		inv.putItem("coin.png");
		inv.putItem("ball.png", new ItemInfo("球", "一颗球颠倒人间,\n一个桶堆放姜山"));
		// 添加一个label到背包的坐标5,5
		inv.add(LLabel.make("我是背包", 5, 5));
		// 添加按钮1
		LClickButton click1 = LClickButton.make("Add", getWidth() - 120, 25, 90, 50);
		click1.up((x, y) -> {
			// 增加物品
			inv.putItem(MathUtils.nextChars("a4.png", "ball.png").toString());
		});
		// 添加按钮2
		LClickButton click2 = LClickButton.make("Del", getWidth() - 120, 85, 90, 50);
		click2.up((x, y) -> {
			// 删除最后一个物品
			inv.popItem();
		});
		add(inv, click1, click2);
		// 不允许拖拽
		// inv.setLocked(true);
		// 背包添加为滚动条容器(高度或者宽度大于背包大小,滚动容器默认变为其2/3大小)
		// add(inv.toVerticalScroll(25, 25, 9999), click1, click2);
		add(MultiScreenTest.getBackButton(this, 2));
	}

}

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

import loon.LTexture;
import loon.Stage;
import loon.action.collision.Gravity;
import loon.action.collision.GravityHandler;
import loon.action.sprite.Sprite;
import loon.action.sprite.Sprites;
import loon.utils.Easing.EasingMode;

public class GravityTest extends Stage {

	@Override
	public void create() {

		// 导入足球纹理
		LTexture ball = loadTexture("ball.png");

		// 在同一行构建7个足球精灵,间隔4像素
		for (int i = 0; i < 7; i++) {
			addRow(new Sprite(ball), 4);
		}

		// 开启Screen重力控制
		final GravityHandler handler = setGravity(EasingMode.Linear, true);

		// 获得精灵控制器
		Sprites sprites = getSprites();

		for (int i = 0; i < sprites.size(); i++) {
			// 为精灵加入重力控制
			Gravity g = handler.add(sprites.getSprite(i));
			// 分两组重力设置
			if (i % 2 == 0) {
				g.g = 0.5f;
			} else {
				g.g = 1f;
			}
			// y轴移动速度5
			g.velocityY = 5f;
			// 弹力值
			// g.bounce = 10f;
		}

		Gravity solid = handler.add(addSprite(texture("a4.png")).coord(0, 200));
		solid.isSolid = true;
		// 监听重力对象
		handler.setListener((g, x, y) -> {
			// 如果开启了边界检查(Screen中默认开启),并且触底或触顶
			if (g.isLimited()) {
				// 让y轴反重力(颠倒g值,正变负,负变正)
				g.setAntiGravityY();
			}
		});

		down((x, y) -> {
			// 如果点击处存在重力对象
			Gravity g = handler.contains(x, y);
			if (g != null) {
				// 删除重力控制
				handler.remove(g);
				// 删除精灵
				remove(g.bind);
			}
		});

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

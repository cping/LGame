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

public class GravityTest extends Stage {

	@Override
	public void create() {

		// 导入足球纹理
		LTexture ball = loadTexture("ball.png");

		// 在同一行构建7个足球精灵,间隔5像素
		for (int i = 0; i < 7; i++) {
			addRow(new Sprite(ball), 5);
		}

		// 开启Screen重力控制
		GravityHandler handler = setGravity(true);

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
		}

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

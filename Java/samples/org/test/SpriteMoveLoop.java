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
import loon.action.sprite.ISprite;
import loon.action.sprite.SpriteControls;
import loon.events.EventActionN;
import loon.geom.BooleanValue;
import loon.utils.MathUtils;

//精灵于屏幕循环移动示例
public class SpriteMoveLoop extends Stage {

	@Override
	public void create() {
		// 添加一个移动标记
		BooleanValue overMoved = refBool();
		// 构建一个循环触发用事件,随机产生2-9个球
		EventActionN eve = () -> {
			for (int i = 0; i < MathUtils.randomLong(2, 9); i++) {
				node("e", "assets/ball.png");
			}
		};
		// 执行一次
		eve.update();
		// 获得当前Screen中精灵集合对应的控制器
		SpriteControls controls = getSprites().controls();
		// 将添加到控制器中的精灵置于屏幕顶部之外，位置随机
		controls.outsideTopRandOn(this);
		// 添加一组循环事件到Screen
		loop(() -> {
			// 若没有移动完毕标记,并且精灵全部在屏幕中
			if (!overMoved.get() && controls.allIn(this)) {
				// 添加允许移动完毕标记true
				overMoved.set(true);
			}
			// 存在移动完毕标记,且精灵已经全部不在屏幕中
			if (overMoved.get() && controls.allNotIn(this)) {
				// 清空Screen以及控制器中包含的控制器内精灵
				controls.clear(this);
				// 重新执行足球生成事件
				eve.update();
				// 将新添加的Screen中精灵注入控制器
				controls.set(this);
				// 依旧在屏幕外部上方生成随机精灵位置
				controls.outsideTopRandOn(this);
				// 设置移动完毕标记为false
				overMoved.set(false);
			}
			// 向下移动五像素
			controls.move_down(5f);
		});

		// 点击屏幕
		down((x, y) -> {
			// 如果触屏点存在唯一的精灵,将其返回
			ISprite result = controls.intersectsOnly(x, y);
			if (result != null) {
				// 从Screen与控制器中删除此精灵
				controls.remove(this, result);
				// 如果控制器中精灵数量为0
				if (controls.isEmpty()) {
					// 移动结束标记为true
					overMoved.set(true);
				}
			}
		});

		add(MultiScreenTest.getBackButton(this, 0));

	}

}

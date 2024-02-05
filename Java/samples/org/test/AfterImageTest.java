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
import loon.action.map.Config;
import loon.action.sprite.effect.AfterImageEffect;
import loon.component.LClickButton;

public class AfterImageTest extends Stage {

	@Override
	public void create() {

		// 构建残像图片特效.默认向右移动,图片为ball.png,初始位置60,160,大小32x32,残像数量9
		AfterImageEffect effect = new AfterImageEffect(Config.TRIGHT, "ball.png", 60, 160, 32, 32, 9);
		// 残影间隔2像素
		effect.setInterval(2f);
		// 曲线方式移动残影
		//effect.setMoveOrbit(AfterImageEffect.CURVE);
		// 开始播放残像动画
		effect.start();
		// 注入效果
		add(effect);

		// 构建Click节点,名称AfterStart,位置60,60,大小100x50
		LClickButton click = node("click", "start", 60, 60, 100, 50);

		// 注入节点,并设定点击节点时事件
		add(click.down((x, y) -> {
			// 停止循环播放移动
			effect.setMoveLoop(false);
			// 重新开始
			effect.restart();
		}));

		LClickButton loopPlay = node("click", "loop", 220, 60, 100, 50);

		add(loopPlay.down((x, y) -> {
			// 循环播放移动
			effect.setMoveLoop(true);
			// 重新开始
			effect.restart();
		}));

		// 监听Screen点击,返回是否点击了残影对象
		down((x, y) -> {
			println(effect.containsAfterObject(x, y));
		});
	}

}

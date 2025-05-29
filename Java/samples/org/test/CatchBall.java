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

import loon.Counter;
import loon.Stage;
import loon.action.ActionBind;
import loon.action.collision.Gravity;
import loon.action.collision.GravityHandler;
import loon.action.sprite.Entity;
import loon.component.LLabel;
import loon.events.GestureType;
import loon.utils.Easing.EasingMode;

public class CatchBall extends Stage {

	//一个简单的木桶接球示例
	@Override
	public void create() {
		// 构建一个重力体控制器,缓动模式InQuad(非物理引擎,仅仅有移动加速和简单的碰撞检测功能)
		final GravityHandler handler = setGravity(EasingMode.InQuad);
		// 木桶移动速度为6(像素)
		final float speed = 6;
		// 构建一个木桶
		final Entity e = node("e", "assets/a4.png");
		// 构建一个label用于显示接触皮球次数
		final LLabel l = node("l", "Contact the ball : 0");
		// 木桶放置于屏幕最下方并居中
		centerBottomOn(e);
		// 居中显示label
		centerOn(l);
		// 构建计数器
		final Counter counter = newCounter();
		// 构建计划任务,用于反复生成足球,0.5秒提交一次,关闭Screen时注销此任务(因为没有设定执行次数，需要注入putRelease关闭Screen时自动注销，不注销会一直执行)
		putRelease(postTask(() -> {
			// 构建一个皮球
			final Entity b = node("e", "assets/ball.png");
			// 随机生成一个X轴位置
			b.randomScreenX(0, getWidth());
			// 添加皮球到重力体控制器
			Gravity g = handler.add(b);
			g.g = 0.5f;
			g.velocityY = 0.2f;
		}, 0.5f));
		// 监听重力体控制器
		handler.setListener((g, x, y) -> {
			// 获得当前重力体绑定对象
			ActionBind act = g.getBind();
			// 碰触到设定的重力边界(也就是可移动的矩形范围,默认为与Screen等大,可设置)
			if (g.isLimited()) {
				// 删除重力体绑定对象
				remove(act);
				// 如果在碰触边界前，此对象和木桶碰触
			} else if (e.isCollision(act)) {
				// 删除重力体对象
				remove(g);
				// 显示碰撞次数
				l.setText("Contact the ball : " + counter.increment());
			}
		});
		// 按下键盘向左
		down("left", () -> {
			e.move_left(speed);
		});
		// 按下键盘向右
		down("right", () -> {
			e.move_right(speed);
		});
		// 构建一个循环监听
		loop(() -> {
			// 如果鼠标拖拽移动
			if (isMoving()) {
				// 获得屏幕手势方向
				GestureType g = getGestureDirection();
				switch (g) {
				case Right:
					// 向右移动
					e.move_right(speed);
					break;
				case Left:
					// 向左移动
					e.move_left(speed);
					break;
				default:
					break;
				}
			}
			// 限制皮球X坐标移动，最小0，最大为Screen屏幕宽度
			e.clampScreenX(0, getWidth());
		});
		add(MultiScreenTest.getBackButton(this, 0));
	}

}

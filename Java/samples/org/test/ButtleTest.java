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

import loon.LSystem;
import loon.Stage;
import loon.action.collision.CollisionObject;
import loon.action.map.Config;
import loon.action.sprite.BulletEntity;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.event.Touched;
import loon.event.Updateable;
import loon.utils.Easing.EasingMode;

public class ButtleTest extends Stage {

	@Override
	public void create() {
		// 可以通过设置syncTween与让缓动计算与画布刷新完全同步
		// syncTween(true);
		// 也可以设置全局的缓动动画延迟
		// delayTween(10);
		// 创建一个普通的Entity
		final Entity fish = createEntity("assets/fish.png");
		// 靠右待着
		rightOn(fish);
		// 注入Screen
		add(fish);

		// 构建子弹Entity
		final BulletEntity bullets = new BulletEntity();
		bullets.initializeCollision(4);
		// 注入子弹碰撞对象role
		bullets.putCollision(fish);
		// 注入Screen
		add(bullets);

		// 监听Screen的up操作
		up(new Touched() {
			@Override
			public void on(float x, float y) {
				// 移动模式InExp,向东(右)发射[一个]子弹(支持8方向移动),初始速度300,持续时间1
				bullets.addBullet(EasingMode.InExp, "assets/ball.png", x, y, Config.E, 300, 1f);
				// 此项为false时子弹越出BulletEntity范围时(默认与窗口等大)不删除子弹,默认为true
				// bullets.setLimitMoved(false);
			}
		});

		// 监听碰撞,一秒一次
		setTimeout(new Updateable() {

			@Override
			public void action(Object a) {
				// 查看是否有和fish碰撞的子弹
				CollisionObject obj = bullets.getOnlyIntersectingButtle(fish);
				// 存在
				if (obj != null) {
					//调用缓动动画,fish变红,震动,还原颜色
					fish.selfAction().colorTo(LColor.red).shakeTo(5f).colorTo(LColor.white).start();
			
				}

			}
		}, LSystem.SECOND);

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

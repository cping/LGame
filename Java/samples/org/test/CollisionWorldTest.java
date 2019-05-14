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
import loon.action.ActionBind;

import loon.action.MoveTo;
import loon.action.collision.CollisionWorld;
import loon.action.sprite.Entity;
import loon.event.Touched;

public class CollisionWorldTest extends Stage {

	@Override
	public void create() {

		// 定义一个碰撞世界(此类会处理位置变化)
		CollisionWorld world = new CollisionWorld(this);
		ActionBind item1 = world.add(new Entity("ball.png", 66, 66));
		ActionBind item2 = world.add(new Entity("ball.png", 266, 166));
		ActionBind item3 = world.add(new Entity("ball.png", 155, 100));
		up(new Touched() {

			@Override
			public void on(final float x, final float y) {
				// item1移动到指定位置,速度8
				MoveTo move = new MoveTo(x, y, false, 8);
				// 不寻径,单纯移动
				move.setMoveByMode(true);
				// 注入碰撞世界
				move.setCollisionWorld(world);
				// 调用移动事件
				item1.selfAction().event(move).start();
			}
		});

	}

}

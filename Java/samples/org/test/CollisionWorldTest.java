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
import loon.action.ActionListener;
import loon.action.MoveTo;
import loon.action.collision.CollisionWorld;
import loon.action.map.Field2D;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.AnimatedEntity.PlayIndex;
import loon.action.sprite.Entity;
import loon.event.Touched;

public class CollisionWorldTest extends Stage {

	@Override
	public void create() {

		// 定义一个碰撞世界(此类会处理位置变化)
		final CollisionWorld world = new CollisionWorld(this);
		// 构建精灵以32x32的大小拆分图片，放置在坐标位置200x60,显示大小宽32,高32
		final AnimatedEntity hero = new AnimatedEntity("assets/rpg/hero.gif", 32, 32, 200, 60, 32, 32);
		// 播放动画,速度每帧220
		final long[] frames = { 220, 220, 220 };
		// 左右下上四方向的帧播放顺序(也可以理解为具体播放的帧)
		final int[] leftIds = { 3, 4, 5 };
		final int[] rightIds = { 6, 7, 8 };
		final int[] downIds = { 0, 1, 2 };
		final int[] upIds = { 9, 10, 11 };
		// 绑定字符串和帧索引关系
		hero.setPlayIndex("left", PlayIndex.at(frames, leftIds));
		hero.setPlayIndex("right", PlayIndex.at(frames, rightIds));
		hero.setPlayIndex("down", PlayIndex.at(frames, downIds));
		hero.setPlayIndex("up", PlayIndex.at(frames, upIds));
		// 播放绑定到down的动画帧
		hero.animate("down");
		//添加Hero到碰撞世界
		world.add(hero);
		//添加一个球到碰撞世界
		world.add(new Entity("ball.png", 66, 66));
		world.add(new Entity("ball.png", 266, 166));
		world.add(new Entity("ball.png", 155, 100));

		up(new Touched() {

			@Override
			public void on(final float x, final float y) {
				// item1移动到指定位置,8方向移动,速度8
				final MoveTo move = new MoveTo(x, y, true, 8);
				// 不寻径,单纯移动
				move.setMoveByMode(true);
				// 注入碰撞世界,让缓动动画自动计算碰撞结果
				move.setCollisionWorld(world);
				// 监听移动事件
				move.setActionListener(new ActionListener() {

					@Override
					public void stop(ActionBind o) {

					}

					@Override
					public void start(ActionBind o) {

					}

					@Override
					public void process(ActionBind o) {
						if (move.isDirectionUpdate()) {
							switch (move.getDirection()) {
							case Field2D.TUP:
							case Field2D.UP:
								hero.animate("up");
								break;
							default:
							case Field2D.TDOWN:
							case Field2D.DOWN:
								hero.animate("down");
								break;
							case Field2D.TLEFT:
							case Field2D.LEFT:
								hero.animate("left");
								break;
							case Field2D.TRIGHT:
							case Field2D.RIGHT:
								hero.animate("right");
								break;
							}
						}
					}
				});
				// 调动角色缓动动画,开始移动
				hero.selfAction().event(move).start();
			}
		});
		
		//关闭Screen时注销碰撞世界
		putRelease(world);

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

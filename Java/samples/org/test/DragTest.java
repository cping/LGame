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
import loon.action.sprite.ISprite;
import loon.action.sprite.AnimatedEntity.PlayIndex;
import loon.component.LDragging;
import loon.event.SelectAreaListener;
import loon.event.Touched;
import loon.geom.BooleanValue;
import loon.utils.TArray;

public class DragTest extends Stage {

	public AnimatedEntity createRole(float x, float y) {
		// 构建精灵以70x124的大小拆分图片，放置在坐标位置300x60,显示大小宽70,高124
		final AnimatedEntity role = new AnimatedEntity("assets/rpg/sword.png", 70, 124, x, y, 70, 124);
		role.setFixedWidthOffset(24);
		role.setFixedHeightOffset(24);
		// 播放动画,速度每帧220
		// final long[] frames = { 220, 220, 220, 220 };
		// 绑定字符串和帧索引关系,左右下上以及斜角(等距视角)上下左右共8方向的帧播放顺序(也可以理解为具体播放的帧)
		// PlayIndex的作用是序列化帧,注入每帧播放时间以及播放帧的顺序,比如4,7就是播放索引号4,5,6,7这4帧
		role.setPlayIndex("tleft", PlayIndex.at(220, 4, 7));
		role.setPlayIndex("tright", PlayIndex.at(220, 8, 11));
		role.setPlayIndex("tdown", PlayIndex.at(220, 0, 3));
		role.setPlayIndex("tup", PlayIndex.at(220, 12, 15));
		role.setPlayIndex("left", PlayIndex.at(220, 24, 27));
		role.setPlayIndex("right", PlayIndex.at(220, 20, 23));
		role.setPlayIndex("down", PlayIndex.at(220, 16, 19));
		role.setPlayIndex("up", PlayIndex.at(220, 28, 31));
		// 播放绑定到down的动画帧
		role.animate("tdown");
		role.setTag("hero");
		return role;
	}

	@Override
	public void create() {
		// 构建一个以Screen为基础的移动区域
		final Field2D tempMap = new Field2D(this, 16, 16);
		final BooleanValue selectFlag = refBool();
		// 用于存放选中的角色
		final TArray<AnimatedEntity> selected = new TArray<AnimatedEntity>();
		// 创建4个动画角色
		AnimatedEntity hero1 = createRole(60, 50);
		AnimatedEntity hero2 = createRole(160, 50);
		AnimatedEntity hero3 = createRole(260, 50);
		AnimatedEntity hero4 = createRole(360, 50);
		// 注入角色到屏幕
		add(hero1, hero2, hero3, hero4);
		// 构建一个矩形碰撞世界
		final CollisionWorld world = new CollisionWorld(this);
		// 注入角色到碰撞计算
		world.add(hero1, hero2, hero3, hero4);
		// 让组件事件穿过Screen传递，也就是不限制组件的屏幕点击传递(否则down事件会被拦截,监听不到)
		// setDesktopPenetrate(true);
		// 构建拖拽器
		final LDragging drag = new LDragging();
		drag.setSelectAreaListener(new SelectAreaListener() {

			@Override
			public void onArea(float x, float y, float w, float h) {
				if (!selectFlag.get()) {
					selected.clear();
					// 遍历所有在指定区域内标记为hero的精灵
					TArray<ISprite> sprites = findSpriteTags("hero").intersects(x, y, w, h);
					for (ISprite s : sprites) {
						// 如果精灵动作已经完成
						if (s.isActionCompleted()) {
							// 让精灵闪烁
							s.selfAction().flashTo().start();
							// 注入角色到selectd
							selected.add((AnimatedEntity) s);
						}
					}
					selectFlag.set(selected.size > 0);
				}
			}
		});
		add(drag);

		// 删除针对drag组件的屏幕事件拦截
		// removeTouchLimit(drag);
		// 当触屏按下时
		down(new Touched() {

			@Override
			public void on(float x, float y) {
				// 若选择了目标
				if (selectFlag.get()) {
					// 让选中的精灵移动向指定目标
					for (AnimatedEntity s : selected) {
						final AnimatedEntity hero = s;
						// 8方向移动
						final MoveTo move = new MoveTo(tempMap, x, y, true);
						// 使用碰撞世界限制移动
						move.setCollisionWorld(world);
						// 监听移动
						move.setActionListener(new ActionListener() {

							@Override
							public void stop(ActionBind o) {
								selectFlag.set(false);
							}

							@Override
							public void start(ActionBind o) {

							}

							@Override
							public void process(ActionBind o) {
								// 判断是否移动方向是存储的上一个移动方向，避免反复刷新动画事件
								if (move.isDirectionUpdate()) {
									// 根据移动方向切换角色动画
									switch (move.getDirection()) {
									case Field2D.TUP:
										hero.animate("tup");
										break;
									case Field2D.TDOWN:
										hero.animate("tdown");
										break;
									case Field2D.TLEFT:
										hero.animate("tleft");
										break;
									case Field2D.TRIGHT:
										hero.animate("tright");
										break;
									case Field2D.RIGHT:
										hero.animate("right");
										break;
									case Field2D.LEFT:
										hero.animate("left");
										break;
									case Field2D.UP:
										hero.animate("up");
										break;
									case Field2D.DOWN:
										hero.animate("down");
										break;
									}
								}

							}
						});
						// 让精灵执行事件
						s.selfAction().event(move).start();
					}
				}
				selected.clear();
			}
		});
		add(MultiScreenTest.getBackButton(this, 2));
	}

}

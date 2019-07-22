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
import loon.action.ActionTween;
import loon.action.DefineMoveTo;
import loon.action.map.Config;
import loon.action.map.CustomPath;
import loon.action.sprite.AnimatedEntity;
import loon.action.sprite.AnimatedEntity.PlayIndex;
import loon.event.Touched;
import loon.geom.Vector2f;

public class DefineMoveTest extends Stage {

	@Override
	public void create() {
		// 构建精灵以70x124的大小拆分图片，放置在坐标位置300x60,显示大小宽70,高124
		final AnimatedEntity hero = new AnimatedEntity("assets/rpg/sword.png", 70, 124, 300, 60, 70, 124);
		// 播放动画,速度每帧220
		// final long[] frames = { 220, 220, 220, 220 };
		// 绑定字符串和帧索引关系,左右下上以及斜角(等距视角)上下左右共8方向的帧播放顺序(也可以理解为具体播放的帧)
		// PlayIndex的作用是序列化帧,注入每帧播放时间以及播放帧的顺序,比如4,7就是播放索引号4,5,6,7这4帧
		hero.setPlayIndex("tleft", PlayIndex.at(220, 4, 7));
		hero.setPlayIndex("tright", PlayIndex.at(220, 8, 11));
		hero.setPlayIndex("tdown", PlayIndex.at(220, 0, 3));
		hero.setPlayIndex("tup", PlayIndex.at(220, 12, 15));
		hero.setPlayIndex("left", PlayIndex.at(220, 24, 27));
		hero.setPlayIndex("right", PlayIndex.at(220, 20, 23));
		hero.setPlayIndex("down", PlayIndex.at(220, 16, 19));
		hero.setPlayIndex("up", PlayIndex.at(220, 28, 31));
		// 播放绑定到down的动画帧
		hero.animate("tdown");

		add(hero);

		// 构建一组自定义路径
		final CustomPath path = new CustomPath();

		// 构建一个默认的行走路径,并把所有坐标缩放32倍(实际上就是二维坐标转显示坐标)
		path.add(Vector2f.at(0, 0), Vector2f.at(0, 4), Vector2f.at(0, 6), Vector2f.at(1, 0), Vector2f.at(7, 5),
				Vector2f.at(5, 5),Vector2f.at(9, 5)).setScale(32);

		// 把上述路径循环操作3次
		path.loop(3);

		// 获得针对ball的缓动动画管理实体
		final ActionTween tween = hero.selfAction();

		// 点击Screen时触发
		down(new Touched() {

			@Override
			public void on(float x, float y) {
				// 如果ball的缓动动画没有运行
				if (!tween.isRunning()) {
					// 构建缓动事件自定义移动
					final DefineMoveTo move = new DefineMoveTo(path);
					// 调用自定义移动事件，并开始执行缓动动画
					tween.event(move).start().setActionListener(new ActionListener() {

						@Override
						public void stop(ActionBind o) {
							// 播放结束后让角色动画向下
							hero.animate("tdown");
						}

						@Override
						public void start(ActionBind o) {

						}

						@Override
						public void process(ActionBind o) {
							// 如果缓动事件发生了方向改变
							if (move.isDirectionUpdate()) {
								// 转换移动方向到角色动画
								switch (move.getDirection()) {
								case Config.TUP:
									hero.animate("tup");
									break;
								case Config.UP:
									hero.animate("up");
									break;
								case Config.TDOWN:
									hero.animate("tdown");
									break;
								case Config.DOWN:
									hero.animate("down");
									break;
								case Config.TLEFT:
									hero.animate("tleft");
									break;
								case Config.LEFT:
									hero.animate("left");
									break;
								case Config.TRIGHT:
									hero.animate("tright");
									break;
								case Config.RIGHT:
									hero.animate("right");
									break;
								}
							}
						}
					});
				}
			}
		});

		add(MultiScreenTest.getBackButton(this, 2));
	}

}

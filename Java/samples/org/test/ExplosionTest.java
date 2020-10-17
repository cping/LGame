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
import loon.action.sprite.effect.explosion.ExplosionEffect;
import loon.action.sprite.effect.explosion.ExplosionEffect.Mode;
import loon.canvas.Image;
import loon.events.Touched;
import loon.events.Updateable;
import loon.utils.Easing.EasingMode;

public class ExplosionTest extends Stage {

	@Override
	public void create() {
		// 加载一个图片
		Image img = loadImage("fish.png");
		// 设定特效Explode,初始碎块纹理大小8x8,缓动模式Linear,动画时长1f
		final ExplosionEffect effect = new ExplosionEffect(Mode.Explode, img, 8, 8, EasingMode.Linear, 1f);
		// 直接加载指定位置图片
		// final ExplosionEffect effect = new
		// ExplosionEffect(Mode.Explode,"fish.png");
		// 设定效果位置228x28
		effect.setLocation(228, 28);
		//effect.setDebugDraw(true);
		// 旋转90度
		// effect.setRotation(90f);
		// 注入Screen
		add(effect);
		// Explode效果
		addButton("Explode", 25, 25, 100, 40).up(new Touched() {

			@Override
			public void on(float x, float y) {
				// 开始特效
				effect.start(Mode.Explode);
			}
		});
		// Tattered效果
		addButton("Tattered", 25, 75, 100, 40).up(new Touched() {

			@Override
			public void on(float x, float y) {
				effect.start(Mode.Tattered);
			}
		});
		addButton("FlyLeft", 25, 125, 100, 40).up(new Touched() {

			@Override
			public void on(float x, float y) {
				effect.start(Mode.FlyLeft);
			}
		});
		addButton("FlyRight", 25, 175, 100, 40).up(new Touched() {

			@Override
			public void on(float x, float y) {
				effect.start(Mode.FlyRight);
			}
		});
		// 在Screen中注册一个事件监听,每隔1秒刷新一次
		setTimeout(new Updateable() {

			@Override
			public void action(Object a) {
				// 如果爆炸特效完成
				if (effect.isCompleted()) {
					// 停止特效
					effect.stop();
				}
			}
		}, LSystem.SECOND);

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

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
import loon.action.map.CustomPath;
import loon.action.sprite.Entity;
import loon.event.Touched;
import loon.geom.Vector2f;

public class DefineMoveTest extends Stage {

	@Override
	public void create() {

		final Entity ball = new Entity("ball.png", 160, 160);

		add(ball);

		// 构建一组自定义路径
		final CustomPath path = new CustomPath();

		// 构建一个默认的行走路径,并把所有坐标缩放32倍(实际上就是二维坐标转显示坐标)
		path.add(Vector2f.at(1, 0), Vector2f.at(1, 5), Vector2f.at(1, 8), Vector2f.at(1, 0), Vector2f.at(10, 5),
				Vector2f.at(5, 5)).setScale(32);

		// 把上述路径循环操作3次
		path.loop(3);

		// 点击Screen时触发
		down(new Touched() {

			@Override
			public void on(float x, float y) {
					// 调用缓动事件自定义移动,开始执行缓动动画
					ball.selfAction().defineMoveTo(path).start();
			}
		});

		add(MultiScreenTest.getBackButton(this, 2));
	}

}

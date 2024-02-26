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
import loon.action.sprite.Entity;

public class KeyTest extends Stage {

	@Override
	public void create() {

		background("assets/back1.png");

		Entity e = node("e", "assets/ball.png", 125, 125);
		e.buildToScreen();

		Entity e1 = node("e", "assets/ball.png", 225, 125);
		e1.buildToScreen();

		final float speed = 2f;
		keyPress("up", () -> {
			e1.move_up(speed);
		});
		keyPress("down", () -> {
			e1.move_down(speed);
		});
		keyPress("left", () -> {
			e1.move_left(speed);
		});
		keyPress("right", () -> {
			e1.move_right(speed);
		});

		// 只查询设定视图范围内的碰撞
		// getSprites().setViewWindow(...);
		// getSprites().viewCollision(...)

		getSprites().triggerCollision((src, dst, dir) -> {
			dst.removeFromScreen();
		});

	}

}

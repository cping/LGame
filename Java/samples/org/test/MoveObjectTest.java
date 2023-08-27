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
import loon.action.sprite.MoveObject;
import loon.component.LToast;
import loon.events.Touched;
import loon.utils.Easing.EasingMode;

public class MoveObjectTest extends Stage {

	@Override
	public void create() {

		final MoveObject o = new MoveObject(266, 99, "ball.png");

		 add(o);
		// 不检查碰撞
		// o.setAllowCheckCollision(false);
		// 全方位移动
		o.setAllDirection(true);
		o.setEasingMode(EasingMode.Linear);
		o.setDirectionListener(new MoveObject.DirectionListener() {

			@Override
			public void onDirection(int dir) {

				add(LToast.makeText("move to " + o.getDirectionString()));

			}
		});
		down(new Touched() {

			@Override
			public void on(float x, float y) {
				// o.pressedIsoLeft();
				o.onTouch(x, y);
			}
		});
	}

}

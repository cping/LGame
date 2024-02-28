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
import loon.component.LClickButton;
import loon.events.DrawLoop;

public class DrawableInterfaceTest extends Stage {

	@Override
	public void create() {
		DrawLoop.Drawable draw1 = (g, x, y) -> {
			g.drawText("循环嵌套1", 150, 150);
		};
		DrawLoop.Drawable draw2 = (g, x, y) -> {
			g.drawText("循环嵌套2", 150, 150);
		};
		drawable(draw1).onUpdate((s, l) -> {
			System.out.println("数据在循环");
		});
		LClickButton click = node("c", "Update", 0, 0, 130, 30);
		centerBottomOn(click, 0, -10);
		add(click.up((x, y) -> {
			drawable(draw2);
		}));
	}

}

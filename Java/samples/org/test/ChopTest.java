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
import loon.action.sprite.effect.PixelChopEffect;
import loon.action.sprite.effect.StringEffect;
import loon.canvas.LColor;
import loon.event.Touched;
import loon.geom.Vector2f;

public class ChopTest extends Stage {

	@Override
	public void create() {
		up(new Touched() {

			@Override
			public void on(float x, float y) {
				// 斩击效果
				add(new PixelChopEffect(LColor.red, x, y, 2));
				// 文字上浮
				add(StringEffect.up("9999", Vector2f.at(x, y), LColor.red));
			}
		});
	}

}

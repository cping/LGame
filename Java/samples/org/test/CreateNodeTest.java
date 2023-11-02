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

public class CreateNodeTest extends Stage {

	@Override
	public void create() {
		// 构建sprite,注入图片
		node("sprite", "a4.png");
		// 构建entity,注入图片,位置150,150
		Entity e = node("entity", "ccc.png", 150, 150);
		// 构建label,注入文字,位置120,120
		node("label", "testing", 120, 120);
		// 缓动动画,旋转180度
		e.selfAction().rotateTo(180).start();
	}

}

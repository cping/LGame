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
import loon.canvas.NineBuilder;

public class NineImageTest extends Stage {

	@Override
	public void create() {
		// 拆分九宫图并重新构建为指定大小
		NineBuilder builder = new NineBuilder("assets/9grid.jpg")
			    .topLeft(0, 0, 53, 53).top(53, 0, 420, 53).topRight(473, 0, 52, 53)
				.left(0, 52, 53, 420).center(53, 52, 420, 420).right(473, 52, 52, 420)
				.botLeft(0, 473, 53, 52).botRight(473, 473, 52, 52).bot(53, 473, 420, 52);
		Entity e = new Entity(builder.build(300, 300));
		centerOn(e);
		add(e);
	}

}

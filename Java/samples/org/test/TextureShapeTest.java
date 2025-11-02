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

import loon.LTextureShape;
import loon.LTextures;
import loon.Stage;

public class TextureShapeTest extends Stage {

	@Override
	public void create() {

		LTextureShape s = new LTextureShape();
		s.setTexture(LTextures.loadTexture("back1.png"));
		// 清空已经注入的纹理顶点构建数据
		// s.clearVertexStream();
		// s.addRoundedRect(45, 45, 300, 300, 32);
		//将纹理视为圆形展示，显示位置60,20,大小200x200,显示范围为圆形时的260度
		s.addCircleProgress(60, 20, 200, 200, 260);
		drawable((g, x, y) -> {
			s.begin();
			s.end();
		});

		add(MultiScreenTest.getBackButton(this, 0));
	}

}

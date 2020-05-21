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

import loon.Drawer;
import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.FrameBuffer;
import loon.opengl.GLEx;

public class DrawTest extends Drawer {

	private FrameBuffer buffer = null;
	private LTexture tex;

	@Override
	public void create() {
		tex = loadTexture("assets/ccc.png");
		// 构建一个纹理缓冲器
		buffer = new FrameBuffer(getWidth(), getHeight());
	}

	@Override
	public void paint(GLEx g) {
		// 如果缓冲区没有锁定
		if (!buffer.isLocked()) {
			// 开始缓冲屏幕纹理到缓冲器
			buffer.begin();
			g.draw(tex, 55, 55, LColor.red);
			// 结束缓冲
			buffer.end();
			// 锁定纹理不再缓冲
			buffer.lock();
		}
		// 显示缓冲的数据
		g.drawFlip(buffer.texture(), 0, 0);
	}

	@Override
	public void paintFrist(GLEx g) {

	}

	@Override
	public void paintLast(GLEx g) {

	}
}

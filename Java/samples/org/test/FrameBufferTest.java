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

import loon.Counter;
import loon.LRelease;
import loon.LTexture;
import loon.Stage;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class FrameBufferTest extends Stage {

	Counter count = new Counter(0);

	@Override
	protected void afterUI(GLEx g) {
		// 保存GLEx内容到缓冲数据
		g.saveFrameBuffer();
	}

	@Override
	protected void drawLast(GLEx g) {
		// 获得FrameBuffer缓冲(否则会把下面的部分也渲染到缓存上),并显示纹理到GLEx中
		LTexture texture = g.freeFrameBuffer(0);
		// 渲染缓冲的纹理
		g.drawFlip(texture, 0, 0, getWidth(), getHeight());
		g.drawFlip(texture, 230, 0, 240, 200);
		// 缩放一份缓冲数据纹理到屏幕边角
		g.drawRect(230 + 1, 0 + 1, 240, 200, LColor.red);
		// 清空缓存图像
		g.clearFrame();
		// 禁止FrameBuffer(否则不会刷新相关数据到窗体，GLEx中内容不会显示)
		g.disableFrameBuffer();
	}

	@Override
	public void create() {
		addPadding(Entity.make("bird.png"), 100, 150);
		addRow(Entity.make("ccc.png"), 100);
		add(MultiScreenTest.getBackButton(this, 2));
		putRelease(new LRelease() {
			
			@Override
			public void close() {
				//禁止GLEx渲染到FrameBuffer
				disableFrameBuffer();
			}
		});
	}

}

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

import loon.Json;
import loon.Stage;
import loon.action.sprite.ISprite;
import loon.component.LComponent;
import loon.component.layout.JsonLayout;
import loon.component.layout.JsonLayoutListener;

public class JSonViewTest extends Stage {

	@Override
	public void create() {

		// 加载json布局文件
		JsonLayout layout = new JsonLayout("test.txt");
		//监听json解析,可以在此进行自己的针对性操作
		layout.setListener(new JsonLayoutListener() {

			@Override
			public void on(Json.Object props, String name, ISprite comp) {

			}

			@Override
			public void on(Json.Object props, String name, LComponent comp) {

			}
		});
		// 为无图窗口创建背景图
		layout.setCreateGameWindowImage(true);
		// 解析
		layout.parse();
		// 打包显示在screen
		layout.pack(this);

		// 最后渲染精灵组件
		lastSpriteDraw();

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

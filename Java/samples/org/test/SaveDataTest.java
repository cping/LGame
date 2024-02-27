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

import loon.Session;
import loon.Stage;
import loon.component.LClickButton;
import loon.component.LLabel;
import loon.utils.ObjectBundle;

public class SaveDataTest extends Stage {

	public static class LoadDataTest extends Stage {

		@Override
		public void create() {
			// 获得ObjectBundle中数据
			ObjectBundle bundle = getBundle();
			final boolean open = bundle.getBool("open");
			final float speed = bundle.getFloat("speed");
			final String mes = bundle.getStr("mes");
			// 显示数据内容
			LLabel label = LLabel.make(open + "," + speed + "," + mes);
			centerOn(label);
			add(label);
		}

	}

	@Override
	public void setting(ObjectBundle vars) {
		// 获得本地session名称saved
		Session session = getSession("saved");
		// 如果没有session数据
		if (!session.isSaved()) {
			// 设定ObjectBundle中数据(此数据loon全局缓存,但不会保存在本地)
			vars.setBool("open", true);
			vars.setFloat("speed", 0.6f);
			vars.setStr("mes", "game begin");
			// 保存ObjectBundle数据到session(session数据会保存到本地)
			vars.savaTo(session);
		} else {
			// 直接加载session中数据到ObjectBundle
			vars.loadFrom(session);
		}
	}

	@Override
	public void create() {
		// 创建按钮
		LClickButton click = node("click", "Load Data", 0, 0, 150, 50);
		click.up((x, y) -> {
			// 点击切换Screen
			setScreen(new LoadDataTest());
		});
		// 构建按钮
		click.buildToScreen();
		// 居中
		centerOn(click);

	}

}

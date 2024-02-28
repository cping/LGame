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
import loon.font.BMFont;
import loon.font.ITranslator;

public class FontTest extends Stage {

	@Override
	public void create() {
		// 获得图像字体
		final BMFont infofont = new BMFont("assets/info.fnt");
		// 构建click, 设定click字体为BMFont
		LClickButton click = node("c", infofont, "test", 50, 150, 130, 30);
		// 注入翻译器(loon默认的翻译器为I18NTranslator),此示例仅为演示loon翻译原理
		// loon的翻译器绑定于所有IFont实现,实时翻译,实时刷新,替换不同翻译器即可全局改变文字
		infofont.setTranslator(new ITranslator() {

			@Override
			public String toTanslation(String original, String def) {
				// 如果font要求显示test,转为abc输出
				if ("test".equals(original)) {
					return "abc";
				}
				return original;
			}

			// 允许翻译数据
			@Override
			public boolean isAllow() {
				return true;
			}
		});
		click.up((x, y) -> {
			// 注入一个新翻译器
			infofont.setTranslator(new ITranslator() {

				@Override
				public String toTanslation(String original, String def) {
					// 如果font要求显示test,转为update输出
					if ("test".equals(original)) {
						return "update";
					}
					return original;
				}

				@Override
				public boolean isAllow() {
					return true;
				}
			});

		});
		add(click);
	}

}

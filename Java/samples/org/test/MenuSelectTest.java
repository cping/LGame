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

import loon.LSystem;
import loon.Stage;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.component.LMenuSelect;
import loon.component.LToast;
import loon.component.LToast.Style;

public class MenuSelectTest extends Stage {

	@Override
	public void create() {
		
		setBackground("back1.png");
		LMenuSelect ms = new LMenuSelect("第一选项,第二个,第三个,第四个,我是第五个", 66, 66);
		// 选中行的选择外框渲染颜色,不设置不显示
		// ms.setSelectRectColor(LColor.red);
		// 选中行所用的图像标记(箭头图之类),不设置使用默认样式
		ms.setImageFlag(LSystem.FRAMEWORK_IMG_NAME + "creese.png");
		//选择框菜单所用的背景图,不设置使用默认样式
		ms.setBackground(getGameWinFrame(ms.width(), ms.height(), LColor.black, LColor.blue, false));
		ms.setMenuListener(new LMenuSelect.ClickEvent() {

			// 监听当前点击的索引与内容
			@Override
			public void onSelected(int index, String context) {
				// 添加气泡提示
				add(LToast.makeText(context, Style.SUCCESS));

			}
		});

		add(ms);


		add(MultiScreenTest.getBackButton(this,1));
		
	}

}

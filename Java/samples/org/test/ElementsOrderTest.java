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
import loon.action.ActionBind;
import loon.component.LClickButton;
import loon.utils.TArray;

public class ElementsOrderTest extends Stage {

	@Override
	public void create() {

		TArray<ActionBind> clicks = new TArray<ActionBind>();
		LClickButton click1 = LClickButton.make("click1");
		LClickButton click2 = LClickButton.make("click2");
		LClickButton click3 = LClickButton.make("click3");
		LClickButton click4 = LClickButton.make("click4");
		LClickButton click5 = LClickButton.make("click5");
		LClickButton click6 = LClickButton.make("click6");
		LClickButton click7 = LClickButton.make("click7");
		LClickButton click8 = LClickButton.make("click8");

		clicks.addAll(click1, click2, click3, click4, click5, click6, click7, click8);

		// elementsTriangle(clicks, 44,44,266,266);
		
		// 将集合中的按钮布局为直线,坐标55,55,x向55延伸,y向125延伸
		// elementsLine(clicks,55,55,55,125);

		//将集合中的按钮布局为圆形排列,圆坐标166,166,半径90
		elementsCircle(clicks, 166, 166, 90);
		
		//将集合中的按钮按照方形排列,坐标128,128,大小300x200
		// elements(clicks, RectBox.at(128,128,300,200));

		add(MultiScreenTest.getBackButton(this, 1));

	}

}

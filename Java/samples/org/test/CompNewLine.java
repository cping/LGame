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
import loon.component.LClickButton;

public class CompNewLine extends Stage {

	@Override
	public void create() {
		
		LClickButton c = LClickButton.make("按钮1");
		addRow(c);

		LClickButton c1 = LClickButton.make("按钮2");
		addRow(c1);

		LClickButton c2 = LClickButton.make("按钮3");
		addRow(c2);

		LClickButton c4 = LClickButton.make("按钮4");
		addRow(c4);

		LClickButton c5 = LClickButton.make("按钮5");
		addRow(c5);

		LClickButton c6 = LClickButton.make("按钮6");
		addCol(c6);

		LClickButton c7 = LClickButton.make("按钮7");
		addRow(c7);

		LClickButton c8 = LClickButton.make("按钮8");
		addRow(c8);

		LClickButton c9 = LClickButton.make("按钮9");
		addCol(c9);

		LClickButton c10 = LClickButton.make("按钮10");
		addCol(c10);

		LClickButton c11 = LClickButton.make("按钮11");
		addRow(c11);

		Entity e = new Entity("ccc.png");
		addRow(e, 200);

		Entity e1 = new Entity("ccc.png");
		addRow(e1);
	}

}

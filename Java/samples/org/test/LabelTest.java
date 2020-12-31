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
import loon.canvas.LColor;
import loon.component.LLabel;
import loon.component.layout.HorizontalAlign;
import loon.font.TextOptions;

public class LabelTest extends Stage{

	@Override
	public void create() {
		//Label文字居左显示
		add(LLabel.make(HorizontalAlign.LEFT, "Label文字居左", 65, 90, LColor.red));
		//Label文字竖排居左显示
		add(LLabel.make(TextOptions.VERTICAL_LEFT(), "Label文字居左", 300, 40,LColor.red));

		add(MultiScreenTest.getBackButton(this, 1));
	}

}

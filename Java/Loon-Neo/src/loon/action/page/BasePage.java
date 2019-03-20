/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.action.page;

import loon.Screen;

public abstract class BasePage {

	public abstract void onTransform(Screen screen, float position);

	public void resetTransform(Screen screen) {
		screen.setRotation(0);
		screen.setScaleX(1);
		screen.setScaleY(1);
		screen.setPivotX(-1f);
		screen.setPivotY(-1f);
		screen.setY(0);
		screen.setX(0);
		screen.setVisible(true);
	}

}

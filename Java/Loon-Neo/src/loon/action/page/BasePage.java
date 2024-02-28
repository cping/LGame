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

/**
 * 多个Screen之间转换效果的基础封装用类
 */
public abstract class BasePage {

	protected boolean _locked;

	public boolean isLocked() {
		return _locked;
	}

	public BasePage unlockPage() {
		_locked = false;
		return this;
	}

	public BasePage lockPage() {
		_locked = true;
		return this;
	}

	public abstract void onTransform(Screen screen, float position);

	public void resetTransform(Screen screen) {
		screen.setRotation(0f);
		screen.setScaleX(1f);
		screen.setScaleY(1f);
		screen.setPivotX(-1f);
		screen.setPivotY(-1f);
		screen.setX(0f);
		screen.setY(0f);
		screen.setAlpha(1f);
		screen.setVisible(true);
	}

}

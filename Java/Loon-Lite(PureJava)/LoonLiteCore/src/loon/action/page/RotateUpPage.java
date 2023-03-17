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

public class RotateUpPage extends BasePage{

	private static final float ROT_MOD = -15f;

	@Override
	public void onTransform(Screen screen, float position) {
		final float width = screen.getWidth();
		final float rotation = ROT_MOD * position * 12.25f;
		screen.setPivotX(width * 0.5f);
		screen.setPivotY(0);
		screen.setX(0f);
		screen.setRotation(rotation);
	}

	public boolean isPagingEnabled() {
		return true;
	}

}

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
import loon.utils.MathUtils;

public class RotatePage extends BasePage {

	private float lastX = 0;

	@Override
	public void onTransform(Screen screen, float position) {
		float percentage = 1f - MathUtils.abs(position);
		setVisible(screen, position);
		setTranslation(screen);
		setSize(screen, position, percentage);
		setRotation(screen, position, percentage);
	}

	private void setVisible(Screen screen, float position) {
		if (position < 0.5 && position > -0.5) {
			screen.setVisible(true);
		} else {
			screen.setVisible(false);
		}
	}

	private void setTranslation(Screen screen) {
		float scroll = lastX - screen.getX();
		screen.setX(scroll);
		lastX = screen.getX();
	}

	private void setSize(Screen screen, float position, float percentage) {
		screen.setScaleX((position != 0 && position != 1) ? percentage : 1);
		screen.setScaleY((position != 0 && position != 1) ? percentage : 1);
	}

	private void setRotation(Screen screen, float position, float percentage) {
		if (position > 0) {
			screen.setRotation(-180 * (percentage + 1));
		} else {
			screen.setRotation(180 * (percentage + 1));
		}
	}
}

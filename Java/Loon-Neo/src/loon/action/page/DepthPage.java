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

public class DepthPage extends BasePage{

	private static final float MIN_SCALE = 0.75f;

	public void onTransform(Screen screen, float position) {
		if (position <= 0f) {
			screen.setX(0f);
			screen.setScaleX(1f);
			screen.setScaleY(1f);
		} else if (position <= 1f) {
			final float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
					* (1 - MathUtils.abs(position));
			screen.setAlpha(1 - position);
			screen.setX(screen.getWidth() * -position);
			screen.setScaleX(scaleFactor);
			screen.setScaleY(scaleFactor);
		}
	}

	public boolean isPagingEnabled() {
		return true;
	}
}

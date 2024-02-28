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

public class FadePage extends BasePage {

	@Override
	public void onTransform(Screen screen, float position) {
		if (position < -1f || position > 1f) {
			screen.setAlpha(0.6f);
		} else if (position <= 0f || position <= 1f) {
			float alpha = (position <= 0) ? position + 1f : 1f - position;
			screen.setAlpha(alpha);
		} else if (position == 0f) {
			screen.setAlpha(1f);
		}
	}
}

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
package loon.component;

import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 空白类,不显示,单纯占位
 */
public class LLineBreak extends LComponent {

	public LLineBreak(float x, float y, float width, float height) {
		this(MathUtils.ifloor(x), MathUtils.ifloor(y), MathUtils.iceil(width), MathUtils.iceil(height));
	}

	public LLineBreak(int x, int y, int width, int height) {
		super(x, y, width, height);
		setLocked(true);
		setElastic(false);
		setEnabled(false);
		setSelected(false);
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "break";
	}

	@Override
	public void destory() {
	}

}

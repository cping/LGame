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
package loon.component.skin;

import loon.LSystem;
import loon.canvas.LColor;
import loon.font.FontSet;
import loon.font.IFont;

public abstract class SkinAbstract<T> implements ISkin, FontSet<SkinAbstract<T>> {

	private IFont font;

	private LColor fontColor;

	public SkinAbstract() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy());
	}

	public SkinAbstract(IFont font, LColor fontColor) {
		this.font = font;
		this.fontColor = fontColor;
	}

	@Override
	public final IFont getFont() {
		return font;
	}

	@Override
	public final SkinAbstract<T> setFont(IFont font) {
		this.font = font;
		return this;
	}

	@Override
	public final LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public final SkinAbstract<T> setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

}

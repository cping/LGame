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
package loon.component.skin;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.FontSet;
import loon.font.IFont;

public class CheckBoxSkin implements FontSet<CheckBoxSkin>{

	private IFont font;
	private LColor fontColor;

	private LTexture uncheckedTexture;
	private LTexture checkedTexture;

	public final static CheckBoxSkin def() {
		return new CheckBoxSkin();
	}

	public CheckBoxSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(5),
				DefUI.self().getDefaultTextures(6));
	}

	public CheckBoxSkin(IFont font, LColor fontColor, LTexture unchecked,
			LTexture checked) {
		this.font = font;
		this.fontColor = fontColor;
		this.uncheckedTexture = unchecked;
		this.checkedTexture = checked;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public CheckBoxSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public CheckBoxSkin setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public LTexture getUncheckedTexture() {
		return uncheckedTexture;
	}

	public void setUncheckedTexture(LTexture uncheckedTexture) {
		this.uncheckedTexture = uncheckedTexture;
	}

	public LTexture getCheckedTexture() {
		return checkedTexture;
	}

	public void setCheckedTexture(LTexture checkedTexture) {
		this.checkedTexture = checkedTexture;
	}
}

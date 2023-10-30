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

public class TextListSkin implements FontSet<TextListSkin>{

	private LTexture backgoundTexture;
	private LTexture choiceTexture;
	private LTexture scrollTexture;
	private LTexture scrollFlagATexture;
	private LTexture scrollFlagBTexture;
	private IFont font;
	private LColor fontColor;

	public final static TextListSkin def() {
		return new TextListSkin();
	}

	public TextListSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(1),
				DefUI.self().getDefaultTextures(4), DefUI.self().getDefaultTextures(3),
				null, null);
	}

	public TextListSkin(IFont font, LColor fontColor, LTexture bg,
			LTexture choice, LTexture scroll, LTexture scrollFlagA,
			LTexture scrollFlagB) {
		this.font = font;
		this.fontColor = fontColor;
		this.backgoundTexture = bg;
		this.choiceTexture = choice;
		this.scrollTexture = scroll;
		this.scrollFlagATexture = scrollFlagA;
		this.scrollFlagBTexture = scrollFlagB;
	}

	public LTexture getBackgoundTexture() {
		return backgoundTexture;
	}

	public void setBackgoundTexture(LTexture bgTexture) {
		this.backgoundTexture = bgTexture;
	}

	public LTexture getChoiceTexture() {
		return choiceTexture;
	}

	public void setChoiceTexture(LTexture choiceTexture) {
		this.choiceTexture = choiceTexture;
	}

	public LTexture getScrollTexture() {
		return scrollTexture;
	}

	public void setScrollTexture(LTexture scrollTexture) {
		this.scrollTexture = scrollTexture;
	}

	public LTexture getScrollFlagATexture() {
		return scrollFlagATexture;
	}

	public void setScrollFlagATexture(LTexture scrollFlagATexture) {
		this.scrollFlagATexture = scrollFlagATexture;
	}

	public LTexture getScrollFlagBTexture() {
		return scrollFlagBTexture;
	}

	public void setScrollFlagBTexture(LTexture scrollFlagBTexture) {
		this.scrollFlagBTexture = scrollFlagBTexture;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public TextListSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor;
	}

	@Override
	public TextListSkin setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

}

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
import loon.font.IFont;

public class TextListSkin extends SkinAbstract<TextListSkin> {

	public static TextListSkin def() {
		return new TextListSkin();
	}

	private LTexture backgoundTexture;
	private LTexture choiceTexture;
	private LTexture scrollTexture;
	private LTexture scrollFlagATexture;
	private LTexture scrollFlagBTexture;

	public TextListSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(1),
				DefUI.self().getDefaultTextures(4), DefUI.self().getDefaultTextures(3), null, null);
	}

	public TextListSkin(IFont font, LColor fontColor, LTexture bg, LTexture choice, LTexture scroll,
			LTexture scrollFlagA, LTexture scrollFlagB) {
		super(font, fontColor);
		this.backgoundTexture = bg;
		this.choiceTexture = choice;
		this.scrollTexture = scroll;
		this.scrollFlagATexture = scrollFlagA;
		this.scrollFlagBTexture = scrollFlagB;
	}

	public LTexture getBackgoundTexture() {
		return backgoundTexture;
	}

	public TextListSkin setBackgoundTexture(LTexture bgTexture) {
		this.backgoundTexture = bgTexture;
		return this;
	}

	public LTexture getChoiceTexture() {
		return choiceTexture;
	}

	public TextListSkin setChoiceTexture(LTexture choiceTexture) {
		this.choiceTexture = choiceTexture;
		return this;
	}

	public LTexture getScrollTexture() {
		return scrollTexture;
	}

	public TextListSkin setScrollTexture(LTexture scrollTexture) {
		this.scrollTexture = scrollTexture;
		return this;
	}

	public LTexture getScrollFlagATexture() {
		return scrollFlagATexture;
	}

	public TextListSkin setScrollFlagATexture(LTexture scrollFlagATexture) {
		this.scrollFlagATexture = scrollFlagATexture;
		return this;
	}

	public LTexture getScrollFlagBTexture() {
		return scrollFlagBTexture;
	}

	public TextListSkin setScrollFlagBTexture(LTexture scrollFlagBTexture) {
		this.scrollFlagBTexture = scrollFlagBTexture;
		return this;
	}

	@Override
	public String getSkinName() {
		return "textlist";
	}
}

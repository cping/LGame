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

public class TextBarSkin implements FontSet<TextBarSkin> {

	private LTexture leftTexture;
	private LTexture rightTexture;
	private LTexture bodyTexture;
	private IFont font;
	private LColor fontColor;

	public final static TextBarSkin def() {
		return new TextBarSkin();
	}

	public final static TextBarSkin defEmpty() {
		return new TextBarSkin(LSystem.getSystemGameFont(), LColor.white, null, null, null);
	}

	public TextBarSkin() {
		this(LSystem.getSystemGameFont(), LColor.white, DefUI.self().getDefaultTextures(7),
				DefUI.self().getDefaultTextures(7), DefUI.self().getDefaultTextures(7));
	}

	public TextBarSkin(IFont font, LColor fontColor, LTexture left, LTexture right, LTexture body) {
		this.font = font;
		this.fontColor = fontColor;
		this.leftTexture = left;
		this.rightTexture = right;
		this.bodyTexture = body;
	}

	public LTexture getLeftTexture() {
		return leftTexture;
	}

	public void setLeftTexture(LTexture leftTexture) {
		this.leftTexture = leftTexture;
	}

	public LTexture getRightTexture() {
		return rightTexture;
	}

	public void setRightTexture(LTexture rightTexture) {
		this.rightTexture = rightTexture;
	}

	public LTexture getBodyTexture() {
		return bodyTexture;
	}

	public void setBodyTexture(LTexture bodyTexture) {
		this.bodyTexture = bodyTexture;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public TextBarSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public TextBarSkin setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}
}

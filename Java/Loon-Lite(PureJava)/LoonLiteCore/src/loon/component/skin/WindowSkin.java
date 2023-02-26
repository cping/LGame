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

public class WindowSkin implements FontSet<WindowSkin>{

	private IFont font;
	private LTexture barTexture;
	private LTexture backgroundTexture;
	private LColor fontColor;

	public final static WindowSkin def() {
		return new WindowSkin();
	}

	public WindowSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(0),
				DefUI.self().getDefaultTextures(7));
	}

	public WindowSkin(IFont font, LColor fontColor, LTexture bar,
			LTexture background) {
		this.font = font;
		this.fontColor = fontColor;
		this.barTexture = bar;
		this.backgroundTexture = background;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public WindowSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	public LTexture getBarTexture() {
		return barTexture;
	}

	public void setBarTexture(LTexture barTexture) {
		this.barTexture = barTexture;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture backgroundTexture) {
		this.backgroundTexture = backgroundTexture;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public WindowSkin setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}
}

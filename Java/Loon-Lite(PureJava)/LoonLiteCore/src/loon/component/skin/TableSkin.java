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

public class TableSkin implements FontSet<TableSkin>{

	private IFont font;

	private LTexture backgroundTexture;

	private LTexture headerTexture;

	private LColor fontColor;

	public final static TableSkin def() {
		return new TableSkin();
	}

	public TableSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(7),
				DefUI.self().getDefaultTextures(4));
	}

	public TableSkin(IFont font, LColor fontColor, LTexture header,
			LTexture background) {
		this.font = font;
		this.fontColor = fontColor;
		this.headerTexture = header;
		this.backgroundTexture = background;
	}

	@Override
	public IFont getFont() {
		return font;
	}

	@Override
	public TableSkin setFont(IFont font) {
		this.font = font;
		return this;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public void setBackgroundTexture(LTexture background) {
		this.backgroundTexture = background;
	}

	@Override
	public LColor getFontColor() {
		return fontColor.cpy();
	}

	@Override
	public TableSkin setFontColor(LColor fontColor) {
		this.fontColor = fontColor;
		return this;
	}

	public LTexture getHeaderTexture() {
		return headerTexture;
	}

	public void setHeaderTexture(LTexture headerTexture) {
		this.headerTexture = headerTexture;
	}

}

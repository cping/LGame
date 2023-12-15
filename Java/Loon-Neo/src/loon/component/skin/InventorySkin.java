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
import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;
import loon.font.IFont;

public class InventorySkin extends SkinAbstract<InventorySkin> {

	public static InventorySkin def() {
		return new InventorySkin();
	}

	private LTexture backgroundTexture;

	private LTexture barTexture;

	private LColor gridColor;

	public InventorySkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), LColor.gray.cpy(), DefUI.self().getDefaultTextures(1),
				DefUI.self().getDefaultTextures(7));
	}

	public InventorySkin(IFont font, LColor color, LColor fontColor, LTexture back, LTexture bar) {
		super(font, fontColor);
		this.gridColor = color;
		this.backgroundTexture = back;
		this.barTexture = bar;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public InventorySkin setBackground(LTexture background) {
		this.backgroundTexture = background;
		return this;
	}

	public LTexture getBarTexture() {
		return barTexture;
	}

	public InventorySkin setBarTexture(LTexture bar) {
		this.barTexture = bar;
		return this;
	}

	public LColor getGridColor() {
		return gridColor;
	}

	public InventorySkin setGridColor(LColor gridColor) {
		this.gridColor = gridColor;
		return this;
	}

	@Override
	public String getSkinName() {
		return "inventory";
	}
}

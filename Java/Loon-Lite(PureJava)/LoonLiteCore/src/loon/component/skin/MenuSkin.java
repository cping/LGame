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

public class MenuSkin extends SkinAbstract<MenuSkin> {

	public static MenuSkin def() {
		return new MenuSkin();
	}

	private LTexture mainTexture;
	private LTexture tabTexture;

	public MenuSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(1),
				DefUI.self().getDefaultTextures(4));
	}

	public MenuSkin(IFont font, LColor fontColor, LTexture mainTexture, LTexture tabTexture) {
		super(font, fontColor);
		this.mainTexture = mainTexture;
		this.tabTexture = tabTexture;
	}

	public LTexture getMainTexture() {
		return mainTexture;
	}

	public MenuSkin setMainTexture(LTexture mainTexture) {
		this.mainTexture = mainTexture;
		return this;
	}

	public LTexture getTabTexture() {
		return tabTexture;
	}

	public MenuSkin setTabTexture(LTexture tabTexture) {
		this.tabTexture = tabTexture;
		return this;
	}

	@Override
	public String getSkinName() {
		return "menu";
	}
}

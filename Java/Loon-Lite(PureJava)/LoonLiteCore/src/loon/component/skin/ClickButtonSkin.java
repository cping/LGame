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

public class ClickButtonSkin extends SkinAbstract<ClickButtonSkin> {

	public final static ClickButtonSkin def() {
		return new ClickButtonSkin();
	}

	private LTexture idleClickTexture;
	private LTexture hoverClickTexture;
	private LTexture disableTexture;

	public ClickButtonSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(7),
				DefUI.self().getDefaultTextures(8), DefUI.self().getDefaultTextures(9));
	}

	public ClickButtonSkin(IFont font, LColor fontColor, LTexture idle, LTexture hover, LTexture disable) {
		super(font, fontColor);
		this.idleClickTexture = idle;
		this.hoverClickTexture = hover;
		this.disableTexture = disable;
	}

	public LTexture getIdleClickTexture() {
		return idleClickTexture;
	}

	public ClickButtonSkin setIdleClickTexture(LTexture idleClickTexture) {
		this.idleClickTexture = idleClickTexture;
		return this;
	}

	public LTexture getHoverClickTexture() {
		return hoverClickTexture;
	}

	public ClickButtonSkin setHoverClickTexture(LTexture hoverClickTexture) {
		this.hoverClickTexture = hoverClickTexture;
		return this;
	}

	public LTexture getDisableTexture() {
		return disableTexture;
	}

	public ClickButtonSkin setDisableTexture(LTexture disableTexture) {
		this.disableTexture = disableTexture;
		return this;
	}

	@Override
	public String getSkinName() {
		return "clickbutton";
	}
}

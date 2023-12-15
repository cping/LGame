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

public class ToastSkin extends SkinAbstract<ToastSkin> {

	public static ToastSkin def() {
		return new ToastSkin();
	}

	private LTexture backgroundTexture;

	private LColor backColor;

	public ToastSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), LColor.gray, DefUI.self().getDefaultTextures(1));
	}

	public ToastSkin(IFont font, LColor fontColor, LColor backColor, LTexture back) {
		super(font, fontColor);
		this.backColor = backColor;
		this.backgroundTexture = back;
	}

	public LTexture getBackgroundTexture() {
		return backgroundTexture;
	}

	public ToastSkin setBackground(LTexture background) {
		this.backgroundTexture = background;
		return this;
	}

	public LColor getBackgroundColor() {
		return backColor.cpy();
	}

	public ToastSkin setBackgroundColor(LColor color) {
		this.backColor = color;
		return this;
	}

	@Override
	public String getSkinName() {
		return "toast";
	}
}

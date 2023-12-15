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

public class CheckBoxSkin extends SkinAbstract<CheckBoxSkin> {

	public final static CheckBoxSkin def() {
		return new CheckBoxSkin();
	}

	private LTexture uncheckedTexture;
	private LTexture checkedTexture;

	public CheckBoxSkin() {
		this(LSystem.getSystemGameFont(), LColor.white.cpy(), DefUI.self().getDefaultTextures(5),
				DefUI.self().getDefaultTextures(6));
	}

	public CheckBoxSkin(IFont font, LColor fontColor, LTexture unchecked, LTexture checked) {
		super(font, fontColor);
		this.uncheckedTexture = unchecked;
		this.checkedTexture = checked;
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

	@Override
	public String getSkinName() {
		return "checkbox";
	}
}

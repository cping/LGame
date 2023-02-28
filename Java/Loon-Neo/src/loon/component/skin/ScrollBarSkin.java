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

import loon.LTexture;
import loon.component.DefUI;

public class ScrollBarSkin {

	private LTexture scrollBarTexture;

	private LTexture sliderTexture;

	public static ScrollBarSkin def() {
		return new ScrollBarSkin();
	}

	public ScrollBarSkin() {
		this(DefUI.self().getDefaultTextures(2), DefUI.self().getDefaultTextures(8));
	}

	public ScrollBarSkin(LTexture scrollBar, LTexture slider) {
		this.scrollBarTexture = scrollBar;
		this.sliderTexture = slider;
	}

	public LTexture getScrollBarTexture() {
		return scrollBarTexture;
	}

	public void setScrollBarTexture(LTexture scrollBarTexture) {
		this.scrollBarTexture = scrollBarTexture;
	}

	public LTexture getSliderTexture() {
		return sliderTexture;
	}

	public void setSliderTexture(LTexture sliderTexture) {
		this.sliderTexture = sliderTexture;
	}

}

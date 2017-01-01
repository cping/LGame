package loon.component.skin;

import loon.LTexture;
import loon.component.DefUI;

public class ScrollBarSkin {

	private LTexture scrollBarTexture;

	private LTexture sliderTexture;
	
	public static ScrollBarSkin def(){
		return new ScrollBarSkin();
	}

	public ScrollBarSkin() {
		this(DefUI.get().getDefaultTextures(2), DefUI.get().getDefaultTextures(8));
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

package loon.component.skin;

import loon.LTexture;
import loon.canvas.LColor;
import loon.component.DefUI;

public class SliderSkin {

	private LTexture sliderText;
	private LTexture barText;

	public static SliderSkin def(LColor sliderColor, LColor barColor,
			boolean vertical) {
		return new SliderSkin(sliderColor, barColor, vertical);
	}

	public SliderSkin(LColor sliderColor, LColor barColor, boolean vertical) {
		this(DefUI.getGameWinDiamond(100, 100, sliderColor), vertical ? DefUI
				.getGameWinHollow(30, 100, 12, barColor) : DefUI
				.getGameWinHollow(100, 30, 12, barColor));
	}

	public SliderSkin(LTexture slider, LTexture bar) {
		this.sliderText = slider;
		this.barText = bar;
	}

	public LTexture getSliderText() {
		return sliderText;
	}

	public void setSliderText(LTexture sliderText) {
		this.sliderText = sliderText;
	}

	public LTexture getBarText() {
		return barText;
	}

	public void setBarText(LTexture barText) {
		this.barText = barText;
	}
}

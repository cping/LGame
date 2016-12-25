package org.test;

import loon.Stage;
import loon.action.sprite.Entity;
import loon.component.LLayer;
import loon.component.LSlider;
import loon.font.LFont;

public class SliderTest extends Stage {

	@Override
	public void create() {
		// 构建一个Layer
		LLayer layer = new LLayer(500, 500);
		// 不锁定拖拽
		layer.setLocked(false);
		LSlider slider = new LSlider(22, 22, 40, 150, true);
		layer.add(slider);
		LSlider slider2 = new LSlider(122, 122, 150, 40);
		layer.add(slider2);
		layer.addSpriteAt(new Entity("ccc.png"), 150, 0);
		layer.addSpriteAt(new Entity("ccc.png"), 200, 350);
		LFont.setDefaultFont(LFont.getFont(20));
		layer.add(MultiScreenTest.getBackButton(this, 1));
		add(layer);
	}

}

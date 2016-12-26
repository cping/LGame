package org.test;

import loon.Stage;
import loon.action.sprite.ScrollText;
import loon.action.sprite.ScrollText.Direction;
import loon.font.LFont;
import loon.font.TextOptions;

public class ScrollTextTest extends Stage {

	@Override
	public void create() {
		String[] texts = { "九阳神功惊俗世", "君临天下易筋经", "葵花宝典兴国邦", "欢喜禅功祸苍生",
				"紫雷刀出乾坤破", "如来掌起山河动", "浑天玄宇称宝鉴", "天晶不出谁争锋", "啦啦啦啦啦" };
		ScrollText s = new ScrollText(LFont.getFont("黑体", 25), texts,
				TextOptions.VERTICAL_LEFT());
		s.setDirection(Direction.LEFT);
		s.setLocation(115, 20);
		add(s);
		LFont.setDefaultFont(LFont.getFont(20));
		add(MultiScreenTest.getBackButton(this, 1));
	}

}

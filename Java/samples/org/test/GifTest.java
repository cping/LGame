package org.test;

import loon.Stage;
import loon.action.sprite.GifAnimation;

public class GifTest extends Stage {

	@Override
	public void create() {
		GifAnimation an = new GifAnimation("33.gif");
		add(an);

		add(MultiScreenTest.getBackButton(this,0));
	}

}

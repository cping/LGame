package org.test;

import loon.LTexture;
import loon.LTextures;

public class Global {

	public static LTexture Load(String name) {
		return LTextures.loadTexture("assets/" + name + ".png");
	}

}

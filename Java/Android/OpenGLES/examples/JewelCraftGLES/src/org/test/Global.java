package org.test;

import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class Global {

	public static LTexture Load(String name) {
		return LTextures.loadTexture("assets/" + name + ".png");
	}

}

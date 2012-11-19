package org.test;

import loon.core.graphics.opengl.LTextures;

public class GameBackground extends GameObject
{
	public GameBackground()
	{
		super.setTexture(LTextures.loadTexture("assets/Background.png"));
		super.setSource(0, 0, 480, 800);
	}
}
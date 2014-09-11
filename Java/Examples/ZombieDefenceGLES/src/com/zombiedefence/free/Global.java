package com.zombiedefence.free;

import loon.core.graphics.LColorPool;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;


public class Global
{
	
	static LColorPool Pool = new LColorPool();

	public static LTexture Load(String name) {
		return LTextures.loadTexture("assets/" + name + ".png");
	}

	public static void Read(GameSave data)
	{

	}

	public static void Save(GameSave data)
	{

	}
}
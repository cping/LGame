package org.test.zombiedefence;

import loon.LTexture;
import loon.LTextures;
import loon.canvas.LColorPool;

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
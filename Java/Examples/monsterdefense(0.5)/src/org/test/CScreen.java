package org.test;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;

public interface CScreen
{
	void draw(SpriteBatch batch,LColor defaultSceneColor);
	void LoadContent();
	void update(float time);
}
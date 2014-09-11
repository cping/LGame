package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.graphics.opengl.LTexture;

public class Tips {
	private LTexture t2DIcon;
	private String text;

	public Tips(String text) {
		this.text = text;
	}

	public Tips(LTexture t2DIcon, String text) {
		this.t2DIcon = t2DIcon;
		this.text = text;
	}

	public final void Draw(SpriteBatch batch, float alpha, int yDelta) {
		if (this.t2DIcon != null) {
			batch.draw(this.t2DIcon, 100f, 350 + yDelta, Global.Pool.getColor(1f, 1f,
					1f, alpha));
		}
		batch.drawString(Screen.gothic24, this.text, 220f,
				 (350 + yDelta), Global.Pool.getColor(1f, 1f, 1f, alpha), 0f, 0f,
				0f,  0.68f);
	}
}
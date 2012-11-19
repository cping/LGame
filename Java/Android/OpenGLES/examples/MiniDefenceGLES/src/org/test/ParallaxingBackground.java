package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;

public class ParallaxingBackground {
	
	private Vector2f[] positions;
	
	private int speed;
	
	private LTexture texture;

	public final void Draw(SpriteBatch batch) {
		for (int i = 0; i < this.positions.length; i++) {
			batch.draw(this.texture, this.positions[i], LColor.white);
		}
	}

	public final void Initialize(String texturePath, int screenWidth, int speed) {
		this.texture = LTextures.loadTexture(texturePath);
		this.speed = speed;
		this.positions = new Vector2f[(screenWidth / this.texture.getWidth()) + 1];
		for (int i = 0; i < this.positions.length; i++) {
			this.positions[i] = new Vector2f((i * this.texture.getWidth()), 0f);
		}
	}

	public final void Update() {
		for (int i = 0; i < this.positions.length; i++) {
			this.positions[i].x += this.speed;
			if (this.speed <= 0) {
				if (this.positions[i].x <= -this.texture.getWidth()) {
					this.positions[i].x = this.texture.getWidth()
							* (this.positions.length - 1);
				}
			} else if (this.positions[i].x >= (this.texture.getWidth() * (this.positions.length - 1))) {
				this.positions[i].x = -this.texture.getWidth();
			}
		}
	}
}
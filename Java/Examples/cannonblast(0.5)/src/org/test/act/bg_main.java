package org.test.act;

import loon.LTexture;
import loon.LTextures;
import loon.geom.Vector2f;


public class bg_main {
	private Vector2f[] positions;
	private int speed;
	private LTexture texture;

	public final void Draw() {
		texture.glBegin();
		for (int i = 0; i < this.positions.length; i++) {
			texture.draw(this.positions[i].x, this.positions[i].y);
		}
		texture.glEnd();
	}

	public final void Initialize(String texturePath, int screenWidth, int speed) {
		this.texture = LTextures.loadTexture(texturePath);
		this.speed = speed;
		this.positions = new Vector2f[(screenWidth / this.texture.getWidth()) + 1];
		for (int i = 0; i < this.positions.length; i++) {
			this.positions[i] = new Vector2f(
					(float) (i * this.texture.getWidth()), 0f);
		}
	}

	public final void Update(float me_speed) {
		for (int i = 0; i < this.positions.length; i++) {
			this.positions[i].x += this.speed + -((int) (me_speed - 3f));
			if (this.speed <= 0) {
				if (this.positions[i].x <= -this.texture.getWidth()) {
					this.positions[i].x += this.texture.getWidth() * 2;
				}
			} else if (this.positions[i].x >= (this.texture.getWidth() * (this.positions.length - 1))) {
				this.positions[i].x = -this.texture.getWidth();
			}
		}
	}
}
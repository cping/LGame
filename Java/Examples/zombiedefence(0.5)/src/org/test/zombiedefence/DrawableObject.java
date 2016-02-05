package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class DrawableObject {

	public float alpha;
	public float alphaFinal;
	public float angle;
	public int iLife;
	public boolean isDead;
	public int life;
	public Vector2f origin;
	public Vector2f position = new Vector2f();
	public Vector2f scale;
	public LTexture texture;

	public DrawableObject(LTexture texture, Vector2f position) {
		this.position = position.cpy();
		this.texture = texture;
		this.origin = new Vector2f((texture.getWidth() / 2),
				(texture.getHeight() / 2));
		this.life = 300;
		this.iLife = 0;
		this.isDead = false;
		this.alphaFinal = 1f;
		this.alpha = this.alphaFinal;
		this.angle = 0f;
		this.scale = new Vector2f(1f, 1f);
	}

	public void Draw(SpriteBatch batch) {
		batch.draw(this.texture, this.position, null, Global.Pool.getColor(
				this.alpha, this.alpha, this.alpha, this.alpha), MathUtils
				.toDegrees(this.angle), this.origin, this.scale,
				SpriteEffects.None);
	}

	public void Update() {
		this.iLife++;
		if (this.iLife >= this.life) {
			this.isDead = true;
		}
	}
}
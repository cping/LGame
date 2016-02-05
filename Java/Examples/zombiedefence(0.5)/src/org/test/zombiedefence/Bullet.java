package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class Bullet extends DrawableObject {
	
	public int power;
	private Random rand;

	public Bullet(LTexture t2DBullet, Vector2f position, float angle, int power) {
		super(t2DBullet, position);
		super.angle = angle;
		this.power = power;
		this.rand = new Random();
		super.scale = new Vector2f(
				(((float) this.rand.NextDouble()) * 0.5f) + 1f, 1f);
		float num = ((float) this.rand.NextDouble()) * 200f;
		this.origin.x = num + t2DBullet.getWidth();
	}

	@Override
	public void Draw(SpriteBatch batch) {
		batch.draw(
				ScreenGameplay.t2DFiringSpark,
				super.position.add(0f, -5f),
				null,
				LColor.white,
				MathUtils.toDegrees(super.angle),
				((ScreenGameplay.t2DGunInField.getWidth() - 10) + ScreenGameplay.t2DFiringSpark
						.getWidth()), (ScreenGameplay.t2DFiringSpark
						.getHeight() / 2), 1f, SpriteEffects.None);
		super.Draw(batch);
	}

	@Override
	public void Update() {
		super.Update();
	}
}
package org.test.rtsgame;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;
import loon.utils.timer.GameTime;

public class Bullet {

	private float direction;

	private Vector2f position;

	private RoleRank rank = RoleRank.values()[0];

	private float rotation;

	private float speed = 3f;

	private LTexture texture;

	public float totalDistance;

	public Bullet(LTexture texture, RoleRank rank, Vector2f position, float direction) {
		this.position = position.cpy();
		this.direction = direction;
		this.texture = texture;
		this.rank = rank;
	}

	public final void Draw(SpriteBatch spriteBatch) {
		spriteBatch.draw(this.texture, this.position, null, LColor.white,
				MathUtils.toDegrees(this.direction + this.rotation), 15f, 15f, 1f,
				(Math.abs(this.direction) > 1.570796f) ? SpriteEffects.FlipVertically : SpriteEffects.None);
	}

	public final void Update(GameTime gameTime) {
		this.position
				.addSelf(((new Vector2f(MathUtils.cos(this.direction), MathUtils.sin(this.direction)).mul(this.speed))
						.mul(gameTime.getElapsedGameTime())).mul(50f));
		this.totalDistance += this.speed;
		if (this.rank.equals(RoleRank.ninja)) {
			this.rotation += 0.1745329f;
		}
	}

	private RectBox rect = new RectBox();

	public final RectBox getBoundingRectangle() {
		int num = 3;
		rect.setBounds((this.position.x) - num, (this.position.y) - num, 2 * num, 2 * num);
		return rect;
	}
}
package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.LSystem;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class Projectile {

	public boolean Active;

	public int Damage;

	public int enemyLevel;

	public float flightTime;

	private int playerLevel;

	public Vector2f Position = new Vector2f();

	private float projectilefallSpeed;

	private float projectileMoveSpeed;

	public Vector2f startPosition = new Vector2f();

	public LTexture Texture;

	public final void Draw(SpriteBatch batch) {
		batch.draw(this.Texture, this.Position, null, LColor.white, 0f,
				(this.getWidth() / 2), (this.getHeight() / 2), (float) 1f,
				SpriteEffects.None);
	}

	public final void Initialize(LTexture texture, Vector2f position,
			Vector2f delta, int enemylevel) {
		this.Texture = texture;
		this.Position.set(position);
		this.startPosition.set(this.Position);
		this.Active = true;
		this.enemyLevel = enemylevel;
		switch (this.enemyLevel) {
		case 1:
			this.Damage = 5;
			break;

		case 2:
			this.Damage = 10;
			break;

		case 3:
			this.Damage = 15;
			break;

		case 4:
			this.Damage = 150;
			break;

		case 5:
			this.Damage = 200;
			break;
		}
		this.projectileMoveSpeed = -delta.x - MathUtils.nextInt(250);
		this.projectilefallSpeed = -delta.y - MathUtils.nextInt(50);
	}

	public final void Initialize(LTexture texture, Vector2f position,
			int playerlevel, Vector2f delta) {
		this.Texture = texture;
		this.Position.set(position);
		this.startPosition.set(this.Position);
		this.Active = true;
		this.playerLevel = playerlevel;
		int num = 0;
		switch (this.playerLevel) {
		case 1:
			this.Damage = 15;
			break;

		case 2:
			this.Damage = 30;
			num = -50;
			break;

		case 3:
			this.Damage = 50;
			num = 60;
			break;

		case 4:
			this.Damage = 60;
			num = 100;
			break;
		}
		this.projectileMoveSpeed = delta.x;
		this.projectilefallSpeed = delta.y + num;
	}

	public final void Update(GameTime gameTime) {
		this.flightTime += gameTime.getElapsedGameTime();
		this.Position.x = this.startPosition.x
				+ (this.projectileMoveSpeed * this.flightTime);
		this.Position.y = (this.startPosition.y + (this.projectilefallSpeed * this.flightTime))
				+ (0.5f * (500f * (MathUtils.pow(this.flightTime, 2f))));
		if ((this.Position.x - (this.Texture.getWidth() / 2)) > LSystem.screenRect.width) {
			this.Active = false;
		}
	}

	public final int getHeight() {
		return this.Texture.getHeight();
	}

	public final int getWidth() {
		return this.Texture.getWidth();
	}
}
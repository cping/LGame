package org.test;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class Enemy {
	public boolean Active;
	public int Damage;
	public Animation EnemyAnimation;
	public int enemyLevel;
	private float enemyMoveSpeed;
	public float fireTime = 0f;
	public int Health;
	public Vector2f Position;
	public float previousFireTime = 0f;
	public int Value;

	public final void Draw(SpriteBatch batch) {
		this.EnemyAnimation.Draw(batch);
	}

	public final Vector2f getProjectileVector() {
		Vector2f vector = new Vector2f(400f, 20f);
		if (this.enemyLevel == 1) {
			vector.set(400f, 100f);
		}
		if (this.enemyLevel == 2) {
			vector.set(600f, 100f);
		}
		if (this.enemyLevel == 3) {
			vector.set(500f, 150f);
		}
		if (this.enemyLevel == 4) {
			vector.set(700f, 150f);
		}
		if (this.enemyLevel == 5) {
			vector.set(500f, 160f);
		}
		return vector;
	}

	public final void Initialize(Animation animation, Vector2f position,
			int enemyLevel) {
		this.EnemyAnimation = animation;
		this.Position = position;
		this.Position.x += MathUtils.nextInt(50);
		this.Active = true;
		this.enemyLevel = enemyLevel;
		this.enemyMoveSpeed = 1f;
		this.Health = 10;
		this.Damage = 10;
		if (enemyLevel == 1) {
			this.Health = 10;
			this.Damage = 10;
			this.Value = 100;
			this.fireTime = ((float) (0.8f + (MathUtils.nextInt(10) / 20)));
			this.enemyMoveSpeed = 1f;
		} else if (enemyLevel == 2) {
			this.Health = 0x19;
			this.Damage = 20;
			this.Value = 200;
			this.fireTime = ((float) (0.8f + (MathUtils.nextInt(10) / 20)));
			this.enemyMoveSpeed = 1.5f;
		} else if (enemyLevel == 3) {
			this.Health = 300;
			this.Damage = 200;
			this.Value = 0x3e8;
			this.fireTime = ((float) (2f + (MathUtils.nextInt(5) / 20)));
			this.enemyMoveSpeed = 0.4f;
		} else if (enemyLevel == 4) {
			this.Health = 300;
			this.Damage = 500;
			this.Value = 0x7d0;
			this.fireTime = ((float) (3f + (MathUtils.nextInt(20) / 20)));
			this.enemyMoveSpeed = 1f;
		} else if (enemyLevel == 5) {
			this.Health = 0xbb8;
			this.Damage = 0x1388;
			this.Value = 0x1388;
			this.fireTime = ((float) (1f + (MathUtils.nextInt(20) / 20)));
			this.enemyMoveSpeed = 0.2f;
		}
	}

	public final void Update(GameTime gameTime, float height) {
		this.Position.x -= this.enemyMoveSpeed;
		this.Position.y = height - (this.EnemyAnimation.FrameHeight / 2);
		this.EnemyAnimation.Position.set(this.Position);
		this.EnemyAnimation.Update(gameTime);
		if ((this.Position.x < -this.getWidth()) || (this.Health <= 0)) {
			this.Active = false;
		}
	}

	public final int getHeight() {
		return this.EnemyAnimation.FrameHeight;
	}

	public final int getWidth() {
		return this.EnemyAnimation.FrameWidth;
	}
}
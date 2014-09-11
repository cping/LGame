package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class BulletShell extends DrawableObject {
	private double accelY;
	private int groundLevel;
	public Random rand;
	private double speedAngular;
	private double speedX;
	private double speedY;

	public BulletShell(LTexture t2DBulletShell, Vector2f position,
			int groundLevel, int seed) {
		super(t2DBulletShell, position);
		this.groundLevel = groundLevel;
		super.life = 0x2d;
		this.rand = new Random(seed);
		this.speedX = this.rand.NextDouble() + 1.0;
		this.speedY = -this.rand.NextDouble() - 2.0;
		this.accelY = 0.1;
		this.speedAngular = 0.1;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	@Override
	public void Update() {
		this.position.x += this.speedX;
		this.position.y += this.speedY;
		this.speedY += this.accelY;
		super.angle += this.speedAngular;
		super.Update();
	}

	public int getGroundLevel() {
		return groundLevel;
	}

	public void setGroundLevel(int groundLevel) {
		this.groundLevel = groundLevel;
	}
}
package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;

public class Mud extends DrawableObject {
	private float accY;
	private float speedX;
	private float speedY;
	private Vector2f startPosition;

	public Mud(LTexture t2DMud, Vector2f position) {
		super(t2DMud, position);
		this.startPosition = position.cpy();
		this.startPosition.x = ((((MathUtils.random()) + (MathUtils.random())) * 10f) - 10f) + position.x;
		this.speedX = (this.startPosition.x - position.x) / 5f;
		position = this.startPosition.cpy();
		this.speedY = -8f + (MathUtils.random() * 5f);
		this.startPosition.y -= this.speedY * 5f;
		this.accY = 0.2333333f;
		super.life = 90;
		super.scale = new Vector2f(
				((MathUtils.random()) * 2f) + 0.3f,
				((MathUtils.random()) * 2f) + 0.3f);
		super.origin = new Vector2f((t2DMud.getWidth() / 2),
				(float) (t2DMud.getHeight() / 2));
		super.angle = 3.141593f * MathUtils.random();
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	@Override
	public void Update() {
		if (this.position.y > this.startPosition.y) {
			super.isDead = true;
		}
		super.position.addLocal(this.speedX, this.speedY);
		this.speedY += this.accY;
		super.Update();
	}
}
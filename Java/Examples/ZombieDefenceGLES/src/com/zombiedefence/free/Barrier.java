package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class Barrier extends DrawableObject {
	public float health;
	private LTexture t2DBarrierBroken;
	private LTexture t2dBarrierOriginal;

	public Barrier(LTexture t2dBarrierOriginal, LTexture t2DBarrierBroken,
			Vector2f position) {
		super(t2dBarrierOriginal, position);
		this.t2dBarrierOriginal = t2dBarrierOriginal;
		this.t2DBarrierBroken = t2DBarrierBroken;
		this.health = 100f;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	@Override
	public void Update() {
		super.Update();
		if (this.health <= 0f) {
			this.health = 0f;
			super.texture = this.t2DBarrierBroken;
		} else {
			super.texture = this.t2dBarrierOriginal;
		}
	}
}
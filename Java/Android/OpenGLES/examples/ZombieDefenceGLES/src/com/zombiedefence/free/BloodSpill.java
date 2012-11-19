package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class BloodSpill extends DrawableObject {
	
	public BloodSpill(LTexture t2DBloodSpill, Vector2f position, float angle) {
		super(t2DBloodSpill, position);
		super.angle = angle;
		super.life = 10;
		super.scale = new Vector2f(0.2f, 0.8f);
		super.origin = new Vector2f((float) t2DBloodSpill.getWidth(),
				(float) (t2DBloodSpill.getHeight() / 2));
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	@Override
	public void Update() {
		super.scale.addLocal(0.8f / ( super.life), 0f);
		super.alpha -= 1f / (super.life);
		super.Update();
	}
}
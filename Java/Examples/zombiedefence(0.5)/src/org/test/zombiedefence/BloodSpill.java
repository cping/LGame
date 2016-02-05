package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

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
		super.scale.addSelf(0.8f / ( super.life), 0f);
		super.alpha -= 1f / (super.life);
		super.Update();
	}
}
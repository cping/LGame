package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class Explosion extends DrawableObject {
	public int damage;
	public boolean isActive;
	public int radius;
	private java.util.ArrayList<Smoke> smokeList;

	public Explosion(LTexture texture, Vector2f position, int radius, int damage) {
		super(texture, position);
		this.radius = radius;
		super.life = 60;
		super.scale = new Vector2f(0.6f, 0.6f);
		this.isActive = true;
		this.damage = damage;
		this.smokeList = new java.util.ArrayList<Smoke>();
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
		this.smokeList.add(new Smoke(ScreenGameplay.t2DSmoke, position));
	}

	@Override
	public void Draw(SpriteBatch batch) {
		for (Smoke smoke : this.smokeList) {
			smoke.Draw(batch);
		}
		super.Draw(batch);
	}

	@Override
	public void Update() {
		super.Update();
		super.scale.addSelf(0.19f, 0.19f);
		super.alpha -= 0.2f;
		for (Smoke smoke : this.smokeList) {
			smoke.Update();
		}
	}
}
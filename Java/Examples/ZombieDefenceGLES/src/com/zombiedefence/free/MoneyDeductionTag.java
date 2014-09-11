package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;

public class MoneyDeductionTag {
	private float alpha;
	private int iLife;
	public boolean isDead;
	private int life;
	private String msg;
	public Vector2f position;

	public MoneyDeductionTag(String msg, Vector2f position) {
		this.msg = msg;
		this.position = position.cpy();
		this.life = 0x2d;
		this.isDead = false;
		this.alpha = 1f;
	}

	public final void Draw(SpriteBatch batch) {
		batch.drawString(Screen.ariel18, this.msg, this.position, Global.Pool.getColor(
				1f, 1f, 1f, 1f * this.alpha));
	}

	public final void Update() {
		this.iLife++;
		if (this.iLife > this.life) {
			this.isDead = true;
		}
		this.position.y++;
		this.alpha -= 0.02f;
	}
}
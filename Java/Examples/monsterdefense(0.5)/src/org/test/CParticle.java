package org.test;

import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class CParticle {
	public LColor alpha;
	public boolean isThere;
	public float lifetime;
	public float maxLifeTime;
	public Vector2f origin;
	public Vector2f pos;
	public RectBox rectangle;
	public float rotation;
	public float scale;

	public CParticle(int lifeTime, int width, int height) {
		this.maxLifeTime = lifeTime;
		this.scale = 0.5f;
		this.rotation = 0f;
		this.pos = new Vector2f();
		this.alpha = new LColor(0f, 0f, 0f, 0f);
		this.rectangle = new RectBox(0, 0, width, height);
		this.origin = new Vector2f((width / 2),(height / 2));
	}

	public final void init(Vector2f position) {
		this.pos.x = position.x - this.origin.x;
		this.pos.y = position.y - this.origin.y;
		this.rectangle.x = (this.pos.x + this.origin.x);
		this.rectangle.y =  (this.pos.y + this.origin.y);
		this.isThere = true;
		this.lifetime = this.maxLifeTime;
		this.scale = 0.5f;
		this.rotation = 0f;
		this.alpha.setColor(0xff, 0xff, 0xff, 0xff);
		this.rectangle.width = (int) ((this.origin.x * 2f) * this.scale);
		this.rectangle.height = (int) ((this.origin.y * 2f) * this.scale);
	}

	public final void init(Vector2f position, int width, int height) {
		this.origin.x = width / 2;
		this.origin.y = height / 2;
		this.pos.x = position.x - this.origin.x;
		this.pos.y = position.y - this.origin.y;
		this.rectangle.x = (int) (this.pos.x + this.origin.x);
		this.rectangle.y = (int) (this.pos.y + this.origin.y);
		this.isThere = true;
		this.lifetime = this.maxLifeTime;
		this.scale = 0.5f;
		this.rotation = 0f;
		this.alpha.setColor(0xff, 0xff, 0xff, 0xff);
		this.rectangle.width = (int) ((this.origin.x * 2f) * this.scale);
		this.rectangle.height = (int) ((this.origin.y * 2f) * this.scale);
	}

	public final void update(float time) {
		if (this.isThere) {
			int num = (int) ((255f * this.lifetime) / this.maxLifeTime);
			this.alpha.setColor(num, num, num, num);
			this.rotation += time;
			if (this.rotation >= 6.283185f) {
				this.rotation = 0f;
			}
			this.scale += time;
			this.rectangle.width = (int) ((this.origin.x * 2f) * this.scale);
			this.rectangle.height = (int) ((this.origin.y * 2f) * this.scale);
			this.lifetime -= time;
			if (this.lifetime <= 0f) {
				this.isThere = false;
			}
		}
	}
}
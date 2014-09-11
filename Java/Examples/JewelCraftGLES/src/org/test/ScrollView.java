package org.test;

import loon.core.geom.Vector2f;

public class ScrollView {
	public boolean isMoving;
	public Vector2f offset;
	public Vector2f offset2;
	public Vector2f position;
	public java.util.ArrayList<ImageView> subviews;
	public int touchX;
	public int touchX2;
	public Vector2f velocity;

	public ScrollView(Vector2f position) {
		this.position = position.cpy();
		this.subviews = new java.util.ArrayList<ImageView>();
		this.offset = new Vector2f(0f, 0f);
	}

	public final void transition(float dt) {
		if (this.isMoving) {
			this.velocity = new Vector2f(this.velocity.x, this.velocity.y);
			this.offset = new Vector2f(this.offset.x + this.velocity.x,
					this.offset.y + this.velocity.y);
			if (((this.velocity.x * (this.offset2.x - this.offset.x)) < 0f)
					|| ((this.velocity.y * (this.offset2.y - this.offset.y)) < 0f)) {
				this.offset = this.offset2;
				this.offset2 = Vector2f.ZERO();
				this.isMoving = false;
			}
		}
	}
}
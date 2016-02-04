package org.test;

import loon.LTexture;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class AnimationView {
	public float alpha;
	public int animationDelay;
	public java.util.ArrayList<LTexture> animationImages;
	public int animationIndex;
	public float animationTimer = 0f;
	public RectBox frame;
	public LTexture image;
	public boolean isAnimating;
	public boolean isFramed;
	public Vector2f position;
	public int repeatCount;

	public AnimationView(RectBox rectangle) {
		this.frame = new RectBox(rectangle);
		this.position = new Vector2f(rectangle.x, rectangle.y);
		this.alpha = 1f;
		this.isFramed = true;
	}

	public AnimationView(Vector2f position) {
		this.position = position.cpy();
		this.alpha = 1f;
	}
}
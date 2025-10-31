package org.test.rtsgame;

import loon.LTexture;
import loon.geom.Vector2f;

//动画用类
public class Animation {

	private int frameCount;
	private float frameTime;
	private boolean isLooping;
	private Vector2f originFactor;
	private LTexture texture;

	public Animation(LTexture texture, float frameTime, boolean isLooping) {
		this.texture = texture;
		this.frameTime = frameTime;
		this.isLooping = isLooping;
		this.frameCount = texture.getWidth() / texture.getHeight();
		this.originFactor = new Vector2f(0.5f);
	}

	public Animation(LTexture texture, int frameCount, float frameTime, boolean isLooping, Vector2f originFactor) {
		this(texture, frameTime, isLooping);
		this.frameCount = frameCount;
		this.originFactor = originFactor;
	}

	public final int getFrameCount() {
		return this.frameCount;
	}

	public final int getFrameHeight() {
		return this.getTexture().getHeight();
	}

	public final float getFrameTime() {
		return this.frameTime;
	}

	public final int getFrameWidth() {
		return (this.getTexture().getWidth() / this.getFrameCount());
	}

	public final boolean getIsLooping() {
		return this.isLooping;
	}

	public final Vector2f getOriginFactor() {
		return this.originFactor;
	}

	public final LTexture getTexture() {
		return this.texture;
	}
}
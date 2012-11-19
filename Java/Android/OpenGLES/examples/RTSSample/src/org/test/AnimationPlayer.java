package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

//动画播放用类（可理解为多帧混合播放）
public final class AnimationPlayer {
	
	private Animation animation;
	private int frameIndex;
	private float time;

	public Animation getAnimation() {
		return this.animation;
	}

	public int getFrameIndex() {
		return this.frameIndex;
	}

	public void PlayAnimation(Animation animation) {
		if (this.getAnimation() != animation) {
			this.animation = animation;
			this.frameIndex = 0;
			this.time = 0f;
		}
	}

	private RectBox rectangle = new RectBox();

	public void Draw(GameTime gameTime, SpriteBatch spriteBatch,
			Vector2f position, LColor colour, float rotation,
			SpriteEffects spriteEffects) {
		if (this.getAnimation() == null) {
			throw new UnsupportedOperationException(
					"No animation is currently playing.");
		}
		this.time += gameTime.getElapsedGameTime();
		while (this.time > this.getAnimation().getFrameTime()) {
			this.time -= this.getAnimation().getFrameTime();
			if (this.getAnimation().getIsLooping()) {
				this.frameIndex = (this.frameIndex + 1)
						% this.getAnimation().getFrameCount();
			} else {
				this.frameIndex = MathUtils.min((int) (this.frameIndex + 1),
						(int) (this.getAnimation().getFrameCount() - 1));
			}
		}
		rectangle.setBounds(this.getFrameIndex()
				* this.getAnimation().getFrameWidth(), 0, this.getAnimation()
				.getFrameWidth(), this.getAnimation().getFrameHeight());
		spriteBatch.draw(this.getAnimation().getTexture(), position, rectangle,
				colour, rotation, new Vector2f(this.getAnimation()
						.getFrameWidth(), this.getAnimation().getFrameHeight())
						.mul(this.getAnimation().getOriginFactor()), 1f,
				spriteEffects);
	}

	public void Draw(GameTime gameTime, SpriteBatch spriteBatch,
			Vector2f position) {
		this.Draw(gameTime, spriteBatch, position, LColor.white, 0f,
				SpriteEffects.None);
	}

	public AnimationPlayer clone() {
		AnimationPlayer varCopy = new AnimationPlayer();

		varCopy.animation = this.animation;
		varCopy.frameIndex = this.frameIndex;
		varCopy.time = this.time;

		return varCopy;
	}
}
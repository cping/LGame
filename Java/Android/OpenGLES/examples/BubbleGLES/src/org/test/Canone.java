package org.test;

import java.util.ArrayList;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public class Canone extends GameObject {
	private java.util.ArrayList<Integer> ballColors;
	private java.util.ArrayList<Ball> nextBalls;

	public Canone() {
		super.setTexture(LTextures.loadTexture("assets/Canone.png"));
		super.setPosition(240f, -680f);
		super.setSource(480, 0, 70, 0x55);
		super.setOrigin(35f, 55f);
		this.ballColors = new ArrayList<Integer>();
		this.nextBalls = new ArrayList<Ball>();
		this.nextBalls.add(Ball.CreateBall(super.getPosition(),
				MathUtils.random(0x65 - 1) % (Ball.maxColorIndex + 1)));
		this.nextBalls.add(Ball.CreateBall(
				super.getPosition().add(Ball.ballGraphWidth * 0.8f,
						Ball.ballGraphWidth * 0.5f), MathUtils.random(0x65 - 1)
						% (Ball.maxColorIndex + 1)));
		this.nextBalls.add(Ball.CreateBall(
				super.getPosition().add(Ball.ballGraphWidth * 1.6f,
						Ball.ballGraphWidth * 0.5f), MathUtils.random(0x68 - 1)
						% (Ball.maxColorIndex + 1)));
		this.nextBalls.get(0).SetScale(1f);
		this.nextBalls.get(1).SetScale(0.8f);
		this.nextBalls.get(2).SetScale(0.8f);
	}

	public final void AimToPosition(Vector2f position) {
		if (position.y > super.getPosition().y) {
			position.y = super.getPosition().y - 15f;
		}
		super.setRotation(Trigonometry.getAngle(position.sub(super
				.getPosition())) + 1.570796f);
		if (super.getRotation() < -1.4137166941154069) {
			super.setRotation(-1.413717f);
		}
		if (super.getRotation() > 1.4137166941154069) {
			super.setRotation(1.413717f);
		}
	}

	@Override
	public void Draw(GameTime gameTime, SpriteBatch spriteBatch) {
		for (int i = 0; i < this.nextBalls.size(); i++) {
			this.nextBalls.get(i).Draw(gameTime, spriteBatch);
		}
		spriteBatch.draw(super.getTexture(), 0f, super.getPosition().y, 0, 0,
				480, 50, getColor(), getRotation(), 0, 0, super.getScale(),
				super.getScale(), SpriteEffects.None);
		super.Draw(gameTime, spriteBatch);
	}

	public final void Fire(Vector2f position,
			java.util.ArrayList<Ball> objectList) {
		this.AimToPosition(position);
		Ball item = this.nextBalls.get(0);
		this.nextBalls.remove(0);
		this.nextBalls.get(0).SetPosition(super.getPosition());
		this.nextBalls.get(1).SetPosition(
				super.getPosition().add(Ball.ballGraphWidth * 0.8f,
						Ball.ballGraphWidth * 0.5f));
		this.nextBalls.add(Ball.CreateBall(
				super.getPosition().add(Ball.ballGraphWidth * 1.6f,
						Ball.ballGraphWidth * 0.5f), this.ballColors
						.get(MathUtils.random(this.ballColors.size() - 1))));
		item.FireBall(super.getPosition(),
				Trigonometry.getDirection(super.getRotation() - 1.570796f));
		this.nextBalls.get(0).SetScale(1f);
		this.nextBalls.get(1).SetScale(0.8f);
		this.nextBalls.get(2).SetScale(0.8f);
		objectList.add(item);
	}

	public final void Reset() {
		super.setRotation(0f);
		this.nextBalls.clear();
		this.ballColors.clear();
		this.nextBalls.add(Ball.CreateBall(super.getPosition(),
				MathUtils.random(0x65 - 1) % (Ball.maxColorIndex + 1)));
		this.nextBalls.add(Ball.CreateBall(
				super.getPosition().add(Ball.ballGraphWidth * 0.8f,
						Ball.ballGraphWidth * 0.5f), MathUtils.random(0x65 - 1)
						% (Ball.maxColorIndex + 1)));
		this.nextBalls.add(Ball.CreateBall(
				super.getPosition().add(Ball.ballGraphWidth * 1.6f,
						Ball.ballGraphWidth * 0.5f), MathUtils.random(0x68 - 1)
						% (Ball.maxColorIndex + 1)));
		this.nextBalls.get(0).SetScale(1f);
		this.nextBalls.get(1).SetScale(0.8f);
		this.nextBalls.get(2).SetScale(0.8f);
	}

	@Override
	public void SetPosition(Vector2f newPosition) {
		Vector2f vector = super.getPosition().sub(newPosition);
		if (!vector.equals(0, 0)) {
			for (int i = 0; i < this.nextBalls.size(); i++) {
				this.nextBalls.get(i).SetPosition(
						this.nextBalls.get(i).GetPosition().sub(vector));
			}
		}
		super.SetPosition(newPosition);
	}
	
	public final void SetSpawnableColors(java.util.ArrayList<Integer> colors) {
		this.ballColors = colors;
		for (int i = 0; i < this.nextBalls.size(); i++) {
			if (!this.ballColors.contains(this.nextBalls.get(i).GetColor())) {
				this.nextBalls.get(i).ChangeColor(
						this.ballColors.get(MathUtils.random(this.ballColors
								.size() - 1)));
			}
		}
	}
}
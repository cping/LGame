package org.test;

import java.util.ArrayList;

import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class Ball extends GameObject {
	public static int ballGraphWidth = 50;
	public static int ballPhysicsWidth = 0x2a;
	private static ArrayList<Ball> BallPool = new ArrayList<Ball>();
	public static int BlueColor = 2;
	private float bobbleAnimation = 0f;
	public static int BombColor = 8;

	public static int BrownColor = 7;
	private int colorIndex = 0;
	private Vector2f direction = new Vector2f();
	public static GameplayScreen game;
	public static int GreenColor = 3;
	public static int JokerColor = 9;
	public static int maxColorIndex = 5;
	private boolean moving = false;
	private float nextEffect = 0.2f;
	public static int PinkColor = 5;
	public static int RedColor = 1;
	private static float speed = 700f;

	public static int TealColor = 6;
	public static int WhiteColor = 4;
	public static int YellowColor = 0;

	private Ball(Vector2f position, int colorIndex) {
		super.setTexture(LTextures.loadTexture("assets/Balls.png"));
		super.setPosition(position);
		super.setRotation(0f);
		this.ChangeColor(colorIndex);
		super.setOrigin(super.getSource().width * 0.5f, super
				.getSource().height * 0.5f);
		this.direction = Vector2f.ZERO();
		this.moving = false;
	}

	public static void AddBallToPool(Ball ball) {
		if (ball != null) {
			BallPool.add(ball);
		}
	}

	public final void BubbleAnimateBall(float bobbleTimer) {
		this.bobbleAnimation = bobbleTimer;
	}

	public final void ChangeColor(int newColor) {
		this.colorIndex = newColor;
		super.setSource(50 * this.colorIndex, 0, 50, 50);
		super.setRotation(0f);
	}

	public static Ball CreateBall(Vector2f position, int colorIndex) {
		Ball ball;
		if (BallPool.size() > 0) {
			ball = BallPool.get(0);
			BallPool.remove(0);
		} else {
			ball = new Ball(position, colorIndex);
		}
		ball.moving = false;
		ball.setRotation(0f);
		ball.direction = Vector2f.ZERO();
		ball.ChangeColor(colorIndex);
		ball.setPosition(position);
		ball.bobbleAnimation = 0f;
		ball.setRemove(false);
		return ball;
	}

	public final void FireBall(Vector2f position, Vector2f direction) {
		super.setPosition(position);
		this.direction = direction.cpy();
		this.direction.nor();
		this.moving = true;
	}

	public final int GetColor() {
		return this.colorIndex;
	}

	public final boolean HasColor(int colorIndex) {
		return (colorIndex == this.colorIndex);
	}

	public final void HitByBall() {
		if ((this.colorIndex == BombColor) && BubbleDataManager.vibrateEnabled) {
			game.AddBombEffect(super.getPosition());
			if (BubbleDataManager.soundEnabled) {

			}
		}
		if (this.colorIndex == JokerColor) {
			GameplayScreen.particleEngine
					.AddJokerHitEffect(super.getPosition());
			if (BubbleDataManager.soundEnabled) {

			}
		}
	}

	public final void LastBallEffect() {
		game.AddBombEffect(super.getPosition());
	}

	public static void LoadSounds() {

	}

	public final void RemoveFromGrit() {
		GameplayScreen.particleEngine.BallFallEffect(super.getPosition(),
				this.colorIndex, super.getRotation());
	}

	public final void StuckInGrit() {
		GameplayScreen.particleEngine.BallAddToGritEffect(super.getPosition());
		this.moving = false;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.bobbleAnimation > 0f) {
			this.bobbleAnimation -= (float) gameTime.getElapsedGameTime();
			if ((this.bobbleAnimation > 0f) && (this.bobbleAnimation <= 0.5f)) {
				super.setScale(1f + (0.1f * ((float) Math
						.sin((-this.bobbleAnimation * 4f) * 3.1415926535897931f))));
			} else {
				super.setScale(1f);
			}
		}
		if (this.moving) {
			super.setPosition(super.getPosition().add(
					(this.direction.mul(speed)).mul((float) gameTime
							.getElapsedGameTime())));
			if ((((super.getPosition().x - (super.getSource().width / 2)) < 10f) && (this.direction.x < 0f))
					|| (((super.getPosition().x + (super.getSource().width / 2)) > 470f) && (this.direction.x > 0f))) {
				this.direction.x *= -1f;
			}
		}
		this.nextEffect -= (float) gameTime.getElapsedGameTime();
		if (this.nextEffect <= 0f) {
			this.nextEffect += 0.1f;
			if (this.colorIndex == JokerColor) {
				super.setRotation(super.getRotation()
						+ (float) gameTime.getElapsedGameTime());
				GameplayScreen.particleEngine.AddJokerEffect(super
						.getPosition());
			}
			if (this.colorIndex == BombColor) {
				super.setRotation(super.getRotation()
						- ((float) gameTime.getElapsedGameTime()) * 0.5f);
			}
		}
	}
}
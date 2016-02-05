package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.Vector2f;

public class Grenade extends DrawableObject {
	public float accelY;
	public float aimingAngle;
	public int damage;
	public int distance;
	public float initialSpeedY;
	public boolean isExploding;
	public float speedAngle;
	public float speedX;
	public float speedXTrans;
	public float speedY;
	public float speedYTrans;
	public Vector2f targetPosition;

	public Grenade(LTexture t2DGrenade, Vector2f position,
			Vector2f targetPosition) {
		super(t2DGrenade, position);
		this.targetPosition = targetPosition.cpy();
		this.speedAngle = -0.1256637f;
		this.isExploding = false;
		this.accelY = 0.3f;
		this.speedX = -10f;
		this.distance = (int) Vector2f.dst(targetPosition, position);
		this.aimingAngle = (float) Math.atan2((targetPosition.y - position.y),
				(targetPosition.x - position.x));
		this.initialSpeedY = ((((float) this.distance) / this.speedX) / 2f)
				* this.accelY;
		this.speedY = this.initialSpeedY;
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	@Override
	public void Update() {
		super.Update();
		super.angle += this.speedAngle;
		this.speedY += this.accelY;
		this.speedXTrans = ((float) Math
				.sqrt((double) ((this.speedY * this.speedY) + (this.speedX * this.speedX))))
				* ((float) Math.cos(this.aimingAngle
						+ Math.atan2((double) -this.speedY,
								(double) -this.speedX)));
		this.speedYTrans = ((float) Math
				.sqrt((double) ((this.speedY * this.speedY) + (this.speedX * this.speedX))))
				* ((float) Math.sin(this.aimingAngle
						+ Math.atan2((double) -this.speedY,
								(double) -this.speedX)));
		super.position.addSelf(this.speedXTrans, this.speedYTrans);
		if (this.speedY > -this.initialSpeedY) {
			this.isExploding = true;
		}
	}
}
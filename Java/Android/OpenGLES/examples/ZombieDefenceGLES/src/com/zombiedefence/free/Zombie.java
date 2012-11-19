package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;

public class Zombie extends WalkingBiped {
	private float attackAngleLowArmMax;
	private float attackAngleLowArmMin;
	private float attackAngleUppArmMax;
	private float attackAngleUppArmMin;
	public int attackCycleLength;
	public float damage;
	public DeathType deathType = DeathType.values()[0];
	public int fallLength;
	public java.util.ArrayList<BodyPart> flyingBodyPartList;
	public int iCycleAttack;
	public int iCycleFall;
	public float incomingBulletAngle;
	public boolean isAttacking;
	public boolean isExploding;
	public boolean isFalling;
	public boolean isFallingBackward;
	public int iShot;
	public boolean isShot;
	public boolean isTargeted;
	public boolean isWithTNT;
	public int shotH;
	public int shotLength;

	public Zombie(LTexture t2DHead, LTexture t2DTorso, LTexture t2DUpperArm,
			LTexture t2DLowerArm, LTexture t2DUpperLeg, LTexture t2DLowerLeg,
			Vector2f positionGround, boolean isWithTNT, int day) {
		super(t2DHead, t2DTorso, t2DUpperArm, t2DLowerArm, t2DUpperLeg,
				t2DLowerLeg, positionGround, day);
		this.attackAngleUppArmMax = 2.408554f;
		this.attackAngleUppArmMin = 0.3490659f;
		this.attackAngleLowArmMax = 3.769912f;
		this.attackAngleLowArmMin = -0.7853982f;
		this.isWithTNT = isWithTNT;
		this.flyingBodyPartList = new java.util.ArrayList<BodyPart>();
		super.health = Help.zombieHealthMax;
		this.damage = 0.1f;
		this.isShot = false;
		this.isAttacking = false;
		this.isFalling = false;
		this.isTargeted = false;
		this.isExploding = false;
		this.isFallingBackward = true;
		this.attackCycleLength = 30;
		this.shotLength = 1;
		this.fallLength = 30;
		this.iShot = 0;
		this.iCycleAttack = 0;
		this.iCycleFall = 0;
		this.attackCycleLength = 30;
		this.shotH = 50;
		if (isWithTNT) {
			super.cycleLength /= 2;
			super.speed = 1.5f * super.speed;
		}
		this.deathType = DeathType.Normal;
	}

	public final void Attack() {
		this.iCycleAttack++;
		if (this.iCycleAttack >= this.attackCycleLength) {
			this.iCycleAttack = 0;
		}
		super.upperArmL.angle = (((this.attackAngleUppArmMax - this.attackAngleUppArmMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycleAttack) / ((float) this.attackCycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.attackAngleUppArmMax + this.attackAngleUppArmMin) / 2f);
		super.upperArmR.angle = (((this.attackAngleUppArmMax - this.attackAngleUppArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycleAttack) / ((float) this.attackCycleLength)) * 2f) * 3.1415926535897931) + 0.3490658503988659)))
				+ ((this.attackAngleUppArmMax + this.attackAngleUppArmMin) / 2f);
		super.lowerArmL.angle = (((this.attackAngleLowArmMax - this.attackAngleLowArmMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycleAttack) / ((float) this.attackCycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.attackAngleLowArmMax + this.attackAngleLowArmMin) / 2f);
		super.lowerArmR.angle = (((this.attackAngleLowArmMax - this.attackAngleLowArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycleAttack) / ((float) this.attackCycleLength)) * 2f) * 3.1415926535897931) + 0.3490658503988659)))
				+ ((this.attackAngleLowArmMax + this.attackAngleLowArmMin) / 2f);
		if (super.upperArmL.angle < 1.0471975511965976) {
			super.upperArmL.angle = 1.047198f;
		}
		if (super.upperArmR.angle < 1.2566370614359172) {
			super.upperArmR.angle = 1.256637f;
		}
		if (super.lowerArmL.angle < 1.5707963267948966) {
			super.lowerArmL.angle = 1.570796f;
		}
		if (super.lowerArmR.angle < 1.6534698384271505) {
			super.lowerArmR.angle = 1.65347f;
		}
		super.torso.angle = (super.upperArmL.angle - 1.047198f) / 5f;
		super.upperArmL.angle = -super.upperArmL.angle;
		super.upperArmR.angle = -super.upperArmR.angle;
		super.lowerArmL.angle = -super.lowerArmL.angle;
		super.lowerArmR.angle = -super.lowerArmR.angle;
	}

	public final void BeingShot() {
		this.position.x--;
		this.iShot++;
		if (this.iShot >= this.shotLength) {
			this.iShot = 0;
			this.isShot = false;
		}
	}

	public final void Burst() {
		BodyPart item = new BodyPart(super.t2DHead, super.head.position);
		item.speedAngle = (-0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 3f;
		item.isFlying = true;
		super.head.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DTorso, super.torso.position);
		item.speedX = (((float) ScreenGameplay.rand.NextDouble()) * 4f) - 4f;
		item.isFlying = true;
		super.torso.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DUpperArm, super.upperArmL.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 10f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.upperArmL.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DUpperArm, super.upperArmR.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 16f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.upperArmR.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.lowerArmR.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.lowerArmR.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.lowerArmL.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.lowerArmL.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.lowerLegL.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.lowerLegL.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.lowerLegR.position);
		item.speedX = ((float) ScreenGameplay.rand.NextDouble()) * 4f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.lowerLegR.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.upperLegL.position);
		item.speedX = (((float) ScreenGameplay.rand.NextDouble()) * 10f) - 20f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.upperLegL.isVisable = false;
		this.flyingBodyPartList.add(item);
		item = new BodyPart(super.t2DLowerArm, super.upperLegR.position);
		item.speedX = (((float) ScreenGameplay.rand.NextDouble()) * 6f) - 12f;
		item.speedY = ((float) ScreenGameplay.rand.NextDouble()) * 6f;
		item.speedAngle = (0.1047198f * ((float) ScreenGameplay.rand
				.NextDouble())) * 4f;
		item.isFlying = true;
		super.upperLegR.isVisable = false;
		this.flyingBodyPartList.add(item);
	}

	@Override
	public void Draw(SpriteBatch batch) {
		for (BodyPart part : this.flyingBodyPartList) {
			part.Draw(batch);
		}
		batch.draw(ScreenGameplay.t2DShadow, this.position.x,
				this.positionGround.y - 20f, null, Global.Pool.getColor(0.5f, 0.5f, 0.5f,
				0.5f), 0f, 
				 (ScreenGameplay.t2DShadow.getWidth() / 2),
				 (ScreenGameplay.t2DShadow.getHeight() / 2), (float) 1f,
				SpriteEffects.None);
		super.Draw(batch);
		if (this.isWithTNT) {
			batch.draw(ScreenGameplay.t2DTNT, super.lowerArmR.position
					.add(-5f, -18f), null, LColor.white, 0f,
					0f, 0f, 1f, SpriteEffects.None);
			super.lowerArmR.Draw(batch);
		}
	}

	public final void Explode() {
		this.isFalling = true;
		this.isAttacking = false;
		super.health = 0;
		this.isWithTNT = false;
		this.isExploding = true;
		this.deathType = DeathType.Explosion;
		this.Burst();
	}

	public final void FallBackward() {
		this.iCycleFall++;
		if (this.iCycleFall >= this.fallLength) {
			this.iCycleFall = 0;
			this.isFalling = false;
			super.isDead = true;
		}
		if ((super.angleBody > -1.5707963267948966) && (super.angleBody <= 0f)) {
			super.upperArmL.angle = -1.047198f + super.angleBody;
			super.upperArmR.angle = -1.570796f + super.angleBody;
			super.lowerArmL.angle = -1.570796f + super.angleBody;
			super.lowerArmR.angle = -2.094395f + super.angleBody;
			super.upperLegL.angle = -0.5235988f + super.angleBody;
			super.upperLegR.angle = 0.3926991f + super.angleBody;
			super.lowerLegL.angle = -0.3926991f + super.angleBody;
			super.lowerLegL.angle = 0.7853982f + super.angleBody;
			super.torso.angle = super.angleBody;
			super.head.angle = super.angleBody;
			this.position.x -= (this.fallLength - this.iCycleFall) / 8;
			this.position.y += this.iCycleFall / 4;
			super.angleBody -= 3.141593f / ((float) this.fallLength);
		} else if (super.angleBody <= -1.570796f) {
			for (BodyPart part : super.bodyPartList) {
				part.angle += (super.angleBody - part.angle) / 10f;
			}
		}
	}

	public final void FallForward() {
		this.iCycleFall++;
		if (this.iCycleFall >= this.fallLength) {
			this.iCycleFall = 0;
			this.isFalling = false;
			super.isDead = true;
		}
		super.angleBody += 3.141593f / ((float) this.fallLength);
		super.position .addLocal(
				2f,
				(float) Math
						.sin((double) (((((float) this.iCycleFall) / ((float) this.fallLength)) * 3.141593f) / 2f)));
		for (BodyPart part : super.bodyPartList) {
			part.angle -= ((part.angle - super.angleBody) / ((float) this.fallLength)) * 2f;
		}
	}

	public final void TakeShot(int damage, boolean isFrontFacing, int shotH) {
		if ((shotH < (this.position.y - super.torso.texture.getHeight()))
				&& (shotH > ((this.position.y - super.torso.texture.getHeight()) - super.head.texture.getHeight()))) {
			super.health -= 2 * damage;
			if (super.health <= 0) {
				BodyPart item = new BodyPart(super.t2DHead, super.head.position);
				item.speedAngle = (-0.1047198f * ((float) ScreenGameplay.rand
						.NextDouble())) * 3f;
				item.isFlying = true;
				super.head.isVisable = false;
				this.flyingBodyPartList.add(item);
				this.deathType = DeathType.HeadShot;
			}
		} else {
			this.isFallingBackward = isFrontFacing;
			if (((super.health < (Help.zombieHealthMax / 5)) && (this.flyingBodyPartList
					.size() < 2)) && (ScreenGameplay.rand.NextDouble() < 0.3)) {
				super.lowerArmR.isVisable = false;
				BodyPart part2 = new BodyPart(super.t2DLowerArm,
						super.lowerArmR.position);
				part2.isFlying = true;
				this.flyingBodyPartList.add(part2);
			}
			if (((damage >= super.health) && (damage > 0x16))
					&& (ScreenGameplay.rand.NextDouble() < 0.3)) {
				this.Burst();
			}
			if (this.isWithTNT && (ScreenGameplay.rand.NextDouble() < 0.1)) {
				this.Explode();
			}
			super.health -= damage;
		}
	}

	@Override
	public void Update() {
		if (super.health <= 0) {
			this.isFalling = true;
			this.isAttacking = false;
		}
		if (this.isShot) {
			this.BeingShot();
		} else if (this.isAttacking) {
			this.Attack();
		} else if (this.isFalling) {
			if (this.isFallingBackward) {
				this.FallBackward();
			} else {
				this.FallForward();
			}
		} else {
			this.Walk();
		}
		for (BodyPart part : this.flyingBodyPartList) {
			part.Update();
		}
		super.Update();
	}

	public enum DeathType {
		Normal, HeadShot, Explosion;

		public int getValue() {
			return this.ordinal();
		}

		public static DeathType forValue(int value) {
			return values()[value];
		}
	}
}
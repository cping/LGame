package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;

public class WalkingBiped {
	public float angleBody;
	public float angleLowArmL;
	private float angleLowArmMax = 1.570796f;
	private float angleLowArmMin = 1.256637f;
	public float angleLowArmR;
	public float angleLowLegL;
	private float angleLowLegMax = 0.2855994f;
	private float angleLowLegMin = -0.7853982f;
	public float angleLowLegR;
	public float angleUppArmL;
	private float angleUppArmMax = 1.047198f;
	private float angleUppArmMin = 0.7853982f;
	public float angleUppArmR;
	public float angleUppLegL;
	private float angleUppLegMax = 0.5235988f;
	private float angleUppLegMin = -0.3490659f;
	public float angleUppLegR;
	public java.util.ArrayList<BodyPart> bodyPartList;
	public int cycleLength = 60;
	public BodyPart head;
	public int health;
	private int iCycle;
	public boolean isDead;
	public BodyPart lowerArmL;
	public BodyPart lowerArmR;
	public BodyPart lowerLegL;
	public BodyPart lowerLegR;
	private float phaseDelay = 0.1f;
	public Vector2f position;
	public Vector2f positionGround;
	public float speed = 1f;
	public LTexture t2DHead;
	public LTexture t2DLowerArm;
	public LTexture t2DLowerLeg;
	public LTexture t2DTorso;
	public LTexture t2DUpperArm;
	public LTexture t2DUpperLeg;
	public BodyPart torso;
	public BodyPart upperArmL;
	public BodyPart upperArmR;
	public BodyPart upperLegL;
	public BodyPart upperLegR;

	public WalkingBiped(LTexture t2DHead, LTexture t2DTorso,
			LTexture t2DUpperArm, LTexture t2DLowerArm, LTexture t2DUpperLeg,
			LTexture t2DLowerLeg, Vector2f positionGround, int day) {
		this.t2DHead = t2DHead;
		this.t2DTorso = t2DTorso;
		this.t2DLowerArm = t2DLowerArm;
		this.t2DLowerLeg = t2DLowerLeg;
		this.t2DUpperArm = t2DUpperArm;
		this.t2DUpperLeg = t2DUpperLeg;
		this.positionGround = positionGround.cpy();
		this.cycleLength = (int) (((MathUtils.random() * 40f) + 40f) / ((1f + (((float) day) / 22f))));
		this.speed = 60f / ((float) this.cycleLength);
		this.angleBody = 0f;
		this.isDead = false;
		this.iCycle = 0;
		this.angleUppLegL = (((this.angleUppLegMax - this.angleUppLegMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.angleUppLegMax + this.angleUppLegMin) / 2f);
		this.angleUppLegR = (((this.angleUppLegMax - this.angleUppLegMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)))
				+ ((this.angleUppLegMax + this.angleUppLegMin) / 2f);
		this.angleLowLegL = (((this.angleLowLegMax - this.angleLowLegMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowLegMax + this.angleLowLegMin) / 2f);
		this.angleLowLegR = (((this.angleLowLegMax - this.angleLowLegMin) / 2f) * ((float) Math
				.sin(((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowLegMax + this.angleLowLegMin) / 2f);
		this.position = positionGround.cpy();
		float num = (((float) (Math.cos((double) this.angleUppLegL) + Math
				.cos((double) this.angleLowLegL))) > ((float) (Math
				.cos((double) this.angleUppLegR) + Math
				.cos((double) this.angleLowLegR)))) ? ((float) (t2DUpperLeg.getHeight() * (Math
				.cos((double) this.angleUppLegL) + Math
				.cos((double) this.angleLowLegL))))
				: ((float) (t2DUpperLeg.getHeight() * (Math
						.cos((double) this.angleUppLegR) + Math
						.cos((double) this.angleLowLegR))));
		this.position.y = positionGround.y - num;
		this.angleUppArmL = (((this.angleUppArmMax - this.angleUppArmMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.angleUppArmMax + this.angleUppArmMin) / 2f);
		this.angleUppArmR = (((this.angleUppArmMax - this.angleUppArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)))
				+ ((this.angleUppArmMax + this.angleUppArmMin) / 2f);
		this.angleLowArmL = (((this.angleLowArmMax - this.angleLowArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowArmMax + this.angleLowArmMin) / 2f);
		this.angleLowArmR = (((this.angleLowArmMax - this.angleLowArmMin) / 2f) * ((float) Math
				.sin(((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowArmMax + this.angleLowArmMin) / 2f);
		this.head = new BodyPart(t2DHead, this.position
				.sub(0f,
						(float) (t2DTorso.getHeight() + (t2DHead.getHeight() / 2))));
		this.torso = new BodyPart(t2DTorso, this.position);
		this.upperArmL = new BodyPart(t2DUpperArm, this.position
				.sub(0f, ((float) t2DTorso.getHeight()) / 1.2f));
		this.upperArmR = new BodyPart(t2DUpperArm, this.position
				.sub(0f, ((float) t2DTorso.getHeight()) / 1.2f));
		this.lowerArmL = new BodyPart(
				t2DLowerArm,
				(this.position.sub(0f,
						((float) t2DTorso.getHeight()) / 1.2f))
						.add(
								t2DUpperArm.getHeight()
										* ((float) Math
												.sin((double) this.angleUppArmL)),
								t2DUpperArm.getHeight()
										* ((float) Math
												.cos((double) this.angleUppArmL))));
		this.lowerArmR = new BodyPart(
				t2DLowerArm,
				(this.position.sub(0f,
						((float) t2DTorso.getHeight()) / 1.2f))
						.add(
								t2DUpperArm.getHeight()
										* ((float) Math
												.sin((double) this.angleUppArmR)),
								t2DUpperArm.getHeight()
										* ((float) Math
												.cos((double) this.angleUppArmR))));
		this.upperLegL = new BodyPart(t2DUpperLeg, this.position);
		this.upperLegR = new BodyPart(t2DUpperLeg, this.position);
		this.lowerLegL = new BodyPart(
				t2DLowerLeg,
				this.position
						.add(
								t2DUpperLeg.getHeight()
										* ((float) Math
												.sin((double) this.angleUppLegL)),
								(t2DUpperLeg.getHeight() * 0.8f)
										* ((float) Math
												.cos((double) this.angleUppLegL))));
		this.lowerLegR = new BodyPart(
				t2DLowerLeg,
				this.position
						.add(
								t2DUpperLeg.getHeight()
										* ((float) Math
												.sin((double) this.angleUppLegR)),
								(t2DUpperLeg.getHeight() * 0.8f)
										* ((float) Math
												.cos((double) this.angleUppLegR))));
		this.torso.origin = new Vector2f((float) (t2DTorso.getWidth() / 2),
				(float) t2DTorso.getHeight());
		this.upperArmL.origin = new Vector2f((float) (t2DUpperArm.getWidth() / 2),
				0f);
		this.upperArmR.origin = new Vector2f((float) (t2DUpperArm.getWidth() / 2),
				0f);
		this.lowerArmL.origin = new Vector2f((float) (t2DLowerArm.getWidth() / 2),
				4f);
		this.lowerArmR.origin = new Vector2f((float) (t2DLowerArm.getWidth() / 2),
				4f);
		this.upperLegL.origin = new Vector2f((float) (t2DUpperLeg.getWidth() / 2),
				6f);
		this.upperLegR.origin = new Vector2f((float) (t2DUpperLeg.getWidth() / 2),
				6f);
		this.lowerLegL.origin = new Vector2f((float) t2DUpperLeg.getWidth(), 6f);
		this.lowerLegR.origin = new Vector2f((float) t2DUpperLeg.getWidth(), 6f);
		this.bodyPartList = new java.util.ArrayList<BodyPart>();
		this.bodyPartList.add(this.lowerLegL);
		this.bodyPartList.add(this.lowerLegR);
		this.bodyPartList.add(this.upperLegL);
		this.bodyPartList.add(this.upperLegR);
		this.bodyPartList.add(this.torso);
		this.bodyPartList.add(this.head);
		this.bodyPartList.add(this.lowerArmL);
		this.bodyPartList.add(this.lowerArmR);
		this.bodyPartList.add(this.upperArmL);
		this.bodyPartList.add(this.upperArmR);
	}

	public void Draw(SpriteBatch batch) {
		for (BodyPart part : this.bodyPartList) {
			part.Draw(batch);
		}
	}

	public void Update() {
		this.head.position = this.position
				.add(
						((float) Math.sin((double) this.torso.angle))
								* this.t2DTorso.getHeight(),
						((-((float) Math.cos((double) this.torso.angle)) * this.t2DTorso.getHeight()) - (this.t2DHead.getHeight() / 2)) + 4f);
		this.torso.position = this.position.cpy();
		this.upperArmL.position = this.position
				.add(
						(((float) Math.sin((double) this.torso.angle)) * this.t2DTorso.getHeight()) / 1.3f,
						(-((float) Math.cos((double) this.torso.angle)) * this.t2DTorso.getHeight()) / 1.3f);
		this.upperArmR.position = this.position
				.add(
						(((float) Math.sin((double) this.torso.angle)) * this.t2DTorso.getHeight()) / 1.3f,
						(-((float) Math.cos((double) this.torso.angle)) * this.t2DTorso.getHeight()) / 1.3f);
		this.lowerArmL.position = this.upperArmL.position
				.add((((float) this.t2DUpperArm.getHeight()) / 1.3f)
						* ((float) Math.sin((double) -this.upperArmL.angle)),
						(((float) this.t2DUpperArm.getHeight()) / 1.3f)
								* ((float) Math
										.cos((double) -this.upperArmL.angle)));
		this.lowerArmR.position = this.upperArmR.position
				.add((((float) this.t2DUpperArm.getHeight()) / 1.3f)
						* ((float) Math.sin((double) -this.upperArmR.angle)),
						(((float) this.t2DUpperArm.getHeight()) / 1.3f)
								* ((float) Math
										.cos((double) -this.upperArmR.angle)));
		this.upperLegL.position = this.position .add(0f, -2f);
		this.upperLegR.position = this.position .add(0f, -2f);
		this.lowerLegL.position = this.upperLegL.position
				.add(
						((((float) this.t2DUpperLeg.getHeight()) / 1.6f) * ((float) Math
								.sin((double) -this.upperLegL.angle))) + 2f,
						(((float) this.t2DUpperLeg.getHeight()) / 1.6f)
								* ((float) Math
										.cos((double) -this.upperLegL.angle)));
		this.lowerLegR.position = this.upperLegR.position
				.add(
						((((float) this.t2DUpperLeg.getHeight()) / 1.6f) * ((float) Math
								.sin((double) -this.upperLegR.angle))) + 2f,
						(((float) this.t2DUpperLeg.getHeight()) / 1.6f)
								* ((float) Math
										.cos((double) -this.upperLegR.angle)));
	}

	public void Walk() {
		this.iCycle++;
		this.angleUppLegL = (((this.angleUppLegMax - this.angleUppLegMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.angleUppLegMax + this.angleUppLegMin) / 2f);
		this.angleUppLegR = (((this.angleUppLegMax - this.angleUppLegMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)))
				+ ((this.angleUppLegMax + this.angleUppLegMin) / 2f);
		this.angleLowLegL = (((this.angleLowLegMax - this.angleLowLegMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)
						- (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowLegMax + this.angleLowLegMin) / 2f);
		this.angleLowLegR = (((this.angleLowLegMax - this.angleLowLegMin) / 2f) * ((float) Math
				.sin(((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)
						- (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowLegMax + this.angleLowLegMin) / 2f);
		this.position.x += this.speed;
		float num = (((float) (Math.cos((double) this.angleUppLegL) + Math
				.cos((double) this.angleLowLegL))) < ((float) (Math
				.cos((double) this.angleUppLegR) + Math
				.cos((double) this.angleLowLegR)))) ? ((float) (this.t2DUpperLeg.getHeight() * (Math
				.cos((double) this.angleUppLegL) + Math
				.cos((double) this.angleLowLegL))))
				: ((float) (this.t2DUpperLeg.getHeight() * (Math
						.cos((double) this.angleUppLegR) + Math
						.cos((double) this.angleLowLegR))));
		this.position.y = this.positionGround.y - num;
		this.angleUppArmL = (((this.angleUppArmMax - this.angleUppArmMin) / 2f) * ((float) Math
				.sin(((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)))
				+ ((this.angleUppArmMax + this.angleUppArmMin) / 2f);
		this.angleUppArmR = (((this.angleUppArmMax - this.angleUppArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)))
				+ ((this.angleUppArmMax + this.angleUppArmMin) / 2f);
		this.angleLowArmL = (((this.angleLowArmMax - this.angleLowArmMin) / 2f) * ((float) Math
				.sin((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowArmMax + this.angleLowArmMin) / 2f);
		this.angleLowArmR = (((this.angleLowArmMax - this.angleLowArmMin) / 2f) * ((float) Math
				.sin(((((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931) + 3.1415926535897931)
						+ (6.2831853071795862 * this.phaseDelay))))
				+ ((this.angleLowArmMax + this.angleLowArmMin) / 2f);
		this.upperArmL.angle = -this.angleUppArmL;
		this.upperArmR.angle = -this.angleUppArmR;
		this.lowerArmL.angle = -this.angleLowArmL;
		this.lowerArmR.angle = -this.angleLowArmR;
		this.upperLegL.angle = -this.angleUppLegL;
		this.upperLegR.angle = -this.angleUppLegR;
		this.lowerLegL.angle = -this.angleLowLegL;
		this.lowerLegR.angle = -this.angleLowLegR;
		this.torso.angle = (0.08246681f * ((float) Math
				.sin(((((float) this.iCycle) / ((float) this.cycleLength)) * 2f) * 3.1415926535897931))) + 0.1090831f;
		for (BodyPart part : this.bodyPartList) {
			part.Update();
		}
		if (this.iCycle >= this.cycleLength) {
			this.iCycle = 0;
		}
	}
}
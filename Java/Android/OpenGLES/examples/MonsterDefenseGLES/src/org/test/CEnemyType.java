package org.test;

import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

public class CEnemyType {
	public CAnimObject anim;
	public int bounty;
	public CBulletType[] bulletResistend;
	public int deciLives;
	public float healtFactor;
	public int health;
	private Vector2f origin;
	public LTexture speedReducer;
	public float walkSpeed;
	public CWaypoints wayPoints;

	public CEnemyType(int deciLives, int health, float healtFactor,
			float walkSpeed, CAnimObject anim, LTexture speedReducer,
			int bounty, CBulletType[] bulletResistend) {
		this.health = health;
		this.walkSpeed = walkSpeed;
		this.anim = anim;
		this.bounty = bounty;
		this.healtFactor = healtFactor;
		this.speedReducer = speedReducer;
		this.origin = new Vector2f((float) (anim.getTexture().getWidth() / 2),
				(float) (anim.getTexture().getHeight() / 2));
		this.bulletResistend = bulletResistend;
		this.deciLives = deciLives;
	}

	public final void setWaypoints(CWaypoints wayPoints) {
		this.wayPoints = wayPoints;
	}

	public Vector2f getOrigin() {
		return origin;
	}

	public void setOrigin(Vector2f origin) {
		this.origin = origin;
	}
}
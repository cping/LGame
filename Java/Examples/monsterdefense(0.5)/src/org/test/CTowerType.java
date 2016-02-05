package org.test;

import loon.LTexture;
import loon.geom.Vector2f;

public class CTowerType {
	public CAnimObject[] anim;
	public CBulletType[] bulletType;
	public int[] cost;
	public int damage;
	public float damageLevelFactor;
	public LTexture iconTexture;
	public LTexture iconTexture2;
	public int maxLevel;
	public Vector2f origin;
	public float radius;
	public float radiusLevelFactor;
	public CSound shotSnd;
	public float shotSpeed;
	public float shotSpeedLevelFactor;

	public CTowerType(int maxLevel, int damage, float shotSpeed,
			float damageLevelFactor, float shotSpeedLevelFactor,
			float radiusLevelFactor, CAnimObject[] anim, CSound shotsnd,
			float radius, int[] cost, CBulletType[] bulletType,
			LTexture iconTexture, LTexture iconTexture2) {
		this.maxLevel = maxLevel;
		this.damageLevelFactor = damageLevelFactor;
		this.shotSpeedLevelFactor = shotSpeedLevelFactor;
		this.radiusLevelFactor = radiusLevelFactor;
		this.shotSnd = shotsnd;
		this.damage = damage;
		this.shotSpeed = shotSpeed;
		this.anim = anim;
		this.radius = radius;
		this.origin = new Vector2f(
				(float) (anim[0].getTexture().getWidth() / 2), (float) (anim[0]
						.getTexture().getHeight() / 2));
		this.iconTexture = iconTexture;
		this.iconTexture2 = iconTexture2;
		this.cost = cost;
		this.bulletType = bulletType;
	}
}
package com.mygame;

import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public abstract class Missile extends AnimatedSprite {

	private float dist;
	private MainGame game;
	private float speed;
	private float speedPrFrame;
	private Monster targetMonster;

	public Missile(MainGame game, MissileType missileType, String textureFile,
			Vector2f towerPosition, Monster targetMonster, int damage,
			int columnCount, int spriteCount, int spriteWidth, int spriteHeight) {
		super(game, textureFile, towerPosition.sub(9f, 9f), columnCount,
				spriteCount, spriteWidth, spriteHeight, 1f);
		this.speed = 6f;
		this.targetMonster = targetMonster;
		this.setHasHitTarget(false);
		this.game = game;
		targetMonster.addReservedHitPoints(this.getDamage());
		this.setDamage(damage);
		this.speedPrFrame = 15f;
		this.setDirection(Utils.GetDirection(towerPosition.sub(9f, 9f),
				targetMonster.getPosition()));
		super.setOrigin(new Vector2f((spriteWidth / 2), (spriteHeight / 2)));
		super.setDrawOrder(0x1f);
		if (missileType == MissileType.SPEAR) {
			super.setRotation(Utils.GetAngle(this.getDirection()) + 1.570796f);
		}
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		float distance = Utils.GetDistance(this.targetMonster.getPosition(),
				super.getDrawPosition());
		super.addDrawPosition(this.getDirection().mul(this.speed));
		if ((this.speedPrFrame >= distance)
				|| ((this.dist > 0f) && (distance > this.dist))) {
			this.setHasHitTarget(true);
			this.targetMonster.removeReservedHitPoints(this.getDamage());
			this.targetMonster.Hit(this.getDamage());
			this.game.Components().remove(this);
		}
		this.dist = distance;
	}

	private int privateDamage;

	public int getDamage() {
		return privateDamage;
	}

	public void setDamage(int value) {
		privateDamage = value;
	}

	private Vector2f privateDirection;

	public final Vector2f getDirection() {
		return privateDirection;
	}

	public final void setDirection(Vector2f value) {
		privateDirection = value;
	}

	private boolean privateHasHitTarget;

	public final boolean getHasHitTarget() {
		return privateHasHitTarget;
	}

	public final void setHasHitTarget(boolean value) {
		privateHasHitTarget = value;
	}

	private MissileType privateMissileType = MissileType.values()[0];

	public MissileType getMissileType() {
		return privateMissileType;
	}

	public void setMissileType(MissileType value) {
		privateMissileType = value;
	}

	private boolean privatePlayed_Sound;

	public final boolean getPlayed_Sound() {
		return privatePlayed_Sound;
	}

	public final void setPlayed_Sound(boolean value) {
		privatePlayed_Sound = value;
	}

	private int privatePos_X;

	public final int getPos_X() {
		return privatePos_X;
	}

	public final void setPos_X(int value) {
		privatePos_X = value;
	}

	private int privatePos_Y;

	public final int getPos_Y() {
		return privatePos_Y;
	}

	public final void setPos_Y(int value) {
		privatePos_Y = value;
	}
}
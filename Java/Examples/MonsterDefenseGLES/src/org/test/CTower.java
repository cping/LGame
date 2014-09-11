package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;

public class CTower {
	private float activateTimer;
	private CAnimObject[] anim;
	public CBullet[] bullets;
	private RectBox clickRect = new RectBox();
	private float currentRotation;
	private float currentShottime;
	private int damage;
	public boolean isActivated;
	public boolean isThere;
	public int level = 1;
	private MainGame mainGame;
	private int maxParticle = 30;
	public float maxRadius;
	public Vector2f origin = new Vector2f();
	private CParticle[][] partilces;
	public Vector2f pos = new Vector2f();
	private LTexture positionCircle;
	private Vector2f positionCircleOrigin = new Vector2f();
	private RectBox positionCircleRect = new RectBox();
	private float radius;
	private RectBox rect = new RectBox();
	private float rotation;
	private float sellMoney;
	public CSound shotSnd;
	private float shotSpeed;
	private float tailTime;
	public CEnemy target;
	public Vector2f tilePos = new Vector2f();
	public CTowerType type;

	public CTower(MainGame game, LTexture positionCircle) {
		this.mainGame = game;
		this.isThere = false;
		this.positionCircle = positionCircle;
		this.bullets = new CBullet[5];
		this.partilces = new CParticle[5][this.maxParticle];
		for (int i = 0; i < 5; i++) {
			this.bullets[i] = new CBullet(game);
		}
	}

	public final void activate() {
		this.isActivated = true;
		this.activateTimer = 0f;
	}

	public final boolean checkClick(RectBox clickPos) {
		if (!this.isThere) {
			return false;
		}
		return this.rect.intersects(clickPos);
	}

	public final boolean checkClick(float x, float y, float w, float h) {
		if (!this.isThere) {
			return false;
		}
		return this.rect.intersects(x, y, w, h);
	}

	public final void clearTarget(CEnemy cleartarget) {
		if (cleartarget == this.target) {
			this.target = null;
		}
		for (int i = 0; i < 5; i++) {
			if (this.bullets[i].isThere && this.bullets[i].homing) {
				this.bullets[i].clearTarget(cleartarget);
			}
		}
	}

	public final void delete() {
		if (this.isThere) {
			this.damage = this.type.damage;
			this.shotSpeed = this.type.shotSpeed;
			this.setRadius(this.type.radius);
			this.level = 1;
			this.isThere = false;
			this.target = null;
			this.mainGame.gameLoopScreen.levelArray[(int) (this.pos.x / this.mainGame.TILESIZE.x)][(int) (this.pos.y / this.mainGame.TILESIZE.x)] = 0;
		}
	}

	public final void disable() {
		this.isActivated = false;
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneLColor) {
		if (this.isThere) {
			batch.draw(this.anim[this.level - 1].getTexture(), this.rect, null,
					defaultSceneLColor,
					MathUtils.toDegrees(-this.currentRotation), this.origin,
					SpriteEffects.None);
		}
	}

	public final void drawBullets(SpriteBatch batch, LColor defaultSceneLColor) {
		if (this.isThere) {
			if (this.type.bulletType[this.level - 1].tail != null) {
				for (int j = 0; j < 5; j++) {
					for (int k = 0; k < this.maxParticle; k++) {
						if (this.partilces[j][k].isThere) {
							batch.draw(
									this.type.bulletType[this.level - 1].tail
											.getTexture(),
									this.partilces[j][k].rectangle,
									null,
									this.partilces[j][k].alpha,
									MathUtils
											.toDegrees(this.partilces[j][k].rotation),
									this.partilces[j][k].origin,
									SpriteEffects.None);
						}
					}
				}
			}
			for (int i = 0; i < 5; i++) {
				if (this.bullets[i].isThere) {
					this.bullets[i].draw(batch, defaultSceneLColor);
				}
			}
		}
	}

	public final void drawcircle(SpriteBatch batch, LColor defaultSceneLColor) {
		if (this.isThere && this.isActivated) {
			this.positionCircleRect.height = this.positionCircleRect.width = (int) ((this.maxRadius * 2f) * this.activateTimer);
			batch.draw(this.positionCircle, this.positionCircleRect, null,
					defaultSceneLColor, 0f, this.positionCircleOrigin,
					SpriteEffects.None);

		}
	}

	public final int getSellMoney() {
		return (int) this.sellMoney;
	}

	public final void init(CTowerType type) {
		this.shotSnd = type.shotSnd;
		this.type = type;
		this.anim = new CAnimObject[type.maxLevel];
		for (int i = 0; i < type.maxLevel; i++) {
			this.anim[i] = new CAnimObject(type.anim[i]);
		}
		this.damage = type.damage;
		this.shotSpeed = type.shotSpeed;
		this.rect = new RectBox(((int) this.pos.x)
				+ (this.anim[0].getTexture().getWidth() / 2),
				((int) this.pos.y)
						+ (this.anim[0].getTexture().getHeight() / 2),
				this.anim[0].getTexture().getWidth(), this.anim[0].getTexture()
						.getHeight());
		this.origin = new Vector2f((float) (this.anim[0].getTexture()
				.getWidth() / 2), (float) (this.anim[0].getTexture()
				.getHeight() / 2));
		this.maxRadius = type.radius;
		this.radius = type.radius * type.radius;
		this.bullets = new CBullet[5];
		if (type.bulletType[0].tail != null) {
			for (int k = 0; k < this.maxParticle; k++) {
				for (int m = 0; m < 5; m++) {
					this.partilces[m][k] = new CParticle(
							1,
							type.bulletType[0].tail.getTexture().getWidth() / 2,
							type.bulletType[0].tail.getTexture().getHeight() / 2);
				}
			}
		}
		for (int j = 0; j < 5; j++) {
			this.bullets[j] = new CBullet(this.mainGame);
		}
		this.sellMoney = ((float) type.cost[0]) / 1.25f;
		this.target = null;
		this.activateTimer = 1f;
		this.positionCircleOrigin.x = this.positionCircleOrigin.y = this.positionCircle
				.getWidth() / 2;
		this.clickRect = new RectBox((int) this.pos.x, (int) this.pos.y,
				this.rect.getWidth(), this.rect.getHeight());
	}

	public final void setPos(float x, float y) {
		this.pos.x = x;
		this.pos.y = y;
		this.rect.x = x + (this.rect.getWidth() / 2);
		this.rect.y = y + (this.rect.getHeight() / 2);
		this.isThere = true;
		this.positionCircleRect.width = this.positionCircleRect.height = ((int) this.maxRadius) * 2;
		this.positionCircleRect.x = x + positionCircleRect.width / 2;
		this.positionCircleRect.y = y + positionCircleRect.height / 2;
		this.tilePos.x = x / this.mainGame.TILESIZE.x;
		this.tilePos.y = y / this.mainGame.TILESIZE.y;
		this.mainGame.gameLoopScreen.levelArray[(int) this.tilePos.x][(int) this.tilePos.y] = 2;
		this.clickRect.x = (int) this.pos.x;
		this.clickRect.y = (int) this.pos.y;
	}

	public final void setRadius(float radius) {
		this.maxRadius = radius;
		this.radius = radius * radius;
		this.positionCircleRect.width = this.positionCircleRect.height = ((int) radius) * 2;
		this.positionCircleRect.x = this.rect.x;
		this.positionCircleRect.y = this.rect.y;
	}

	public final void setTilePos(int x, int y) {
		this.setPos(x * this.mainGame.TILESIZE.x, y * this.mainGame.TILESIZE.y);
	}

	public final void update(float time) {

		if (this.isThere) {
			if (this.activateTimer < 1f) {
				this.activateTimer += time * 8f;
				if (this.activateTimer > 1f) {
					this.activateTimer = 1f;
				}
			}
			this.anim[this.level - 1].update(time);
			for (int i = 0; i < 5; i++) {
				if (this.bullets[i].isThere) {
					this.bullets[i].update(time);
				}
			}
			CEnemy enemy = this.mainGame.gameLoopScreen.getNearestEnemy(
					this.pos.add(this.origin), this.radius * 1.25f);
			if (enemy == this.mainGame.gameLoopScreen.targedEnemy) {
				this.target = enemy;
			} else if (this.target == null) {
				this.target = enemy;
			} else {
				Vector2f vector3 = (this.target.pos.add(this.target.origin))
						.sub(this.pos.add(this.origin));
				if (vector3.len() > (this.radius * 1.25f)) {
					this.target = enemy;
				}
			}
			if (this.target != null) {
				Vector2f vector = (this.pos.add(this.origin))
						.sub(this.target.pos.add(this.target.origin));
				vector.nor();
				this.rotation = (MathUtils.atan2(vector.x, vector.y) - 4.71238898038469f);
				while (this.rotation > 6.283185f) {
					this.rotation -= 6.283185f;
				}
				while (this.rotation < 0f) {
					this.rotation += 6.283185f;
				}
				this.currentRotation = this.rotation;
				if (this.currentShottime <= 0f) {
					this.currentShottime = this.shotSpeed;
					for (int k = 0; k < 5; k++) {
						if (!this.bullets[k].isThere) {
							this.bullets[k].init(this.pos.add(this.origin),
									-(this.currentRotation - 1.570796f),
									this.target, this.damage, this.radius,
									this.type.bulletType[this.level - 1]);
							this.anim[this.level - 1].play(1);
							this.shotSnd.play();
							break;
						}
					}
				} else {
					this.currentShottime -= time;
				}
			} else if (this.currentShottime > 0f) {
				this.currentShottime -= time;
			}
			for (int j = 0; j < 5; j++) {
				if (this.bullets[j].isThere) {
					this.bullets[j].update(time);
				}
			}
			if (this.type.bulletType[this.level - 1].tail != null) {
				this.type.bulletType[this.level - 1].tail.update(time);
				for (int m = 0; m < 5; m++) {
					for (int n = 0; n < this.maxParticle; n++) {
						if (this.partilces[m][n].isThere) {
							this.partilces[m][n].update(time);
						}
					}
				}
				if (this.tailTime <= 0f) {
					for (int num6 = 0; num6 < 5; num6++) {
						if (this.bullets[num6].isThere) {
							for (int num7 = 0; num7 < this.maxParticle; num7++) {
								if (!this.partilces[num6][num7].isThere) {
									Vector2f pos = this.bullets[num6].pos;
									pos.x += ((float) MathUtils
											.sin((double) -this.bullets[num6].rotation)) * 30f;
									pos.y += ((float) MathUtils
											.cos((double) -this.bullets[num6].rotation)) * 30f;
									this.partilces[num6][num7].init(pos);
									break;
								}
							}
						}
					}
					this.tailTime = 0.04f;
				}
				this.tailTime -= time;
			}
		}
	}

	public final void upgrade() {
		this.level++;
		this.upgrade(this.level);
	}

	public final void upgrade(int lvl) {
		this.setRadius(this.type.radius);
		for (int i = 0; i < lvl; i++) {
			this.setRadius(this.maxRadius
					+ ((this.maxRadius * this.type.radiusLevelFactor) * i));
		}
		this.damage = this.type.damage
				+ ((int) (this.type.damage * ((this.type.damageLevelFactor * (lvl - 1)) * 1.1f)));
		this.shotSpeed = this.type.shotSpeed;
		for (int j = 0; j < lvl; j++) {
			this.shotSpeed /= this.type.shotSpeedLevelFactor;
		}
		this.sellMoney = 0f;
		for (int k = 0; k < lvl; k++) {
			this.sellMoney += this.type.cost[k];
		}
		this.sellMoney /= 1.25f;
		this.isActivated = false;
		this.level = lvl;
		if (this.type.bulletType[this.level - 1].tail != null) {
			for (int m = 0; m < this.maxParticle; m++) {
				for (int n = 0; n < 5; n++) {
					this.partilces[n][m] = new CParticle(1,
							this.type.bulletType[this.level - 1].tail
									.getTexture().getWidth() / 2,
							this.type.bulletType[this.level - 1].tail
									.getTexture().getHeight() / 2);
				}
			}
		}
	}
}
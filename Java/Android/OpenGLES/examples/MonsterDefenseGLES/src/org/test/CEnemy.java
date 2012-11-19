package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.utils.MathUtils;

public class CEnemy {
	private CAnimObject anim;
	public int bounty;
	public int currentHealth;
	public float currentRotation;
	public int currentWayPoint;
	private int health;
	private RectBox healthBarRect = new RectBox();
	public int index;
	public boolean isThere;
	private MainGame mainGame;
	public Vector2f origin = new Vector2f();
	public RectBox pointRect = new RectBox();
	public Vector2f pos;
	public RectBox rect = new RectBox();
	public float rotation;
	public Vector2f slowOrigin = new Vector2f();
	public RectBox slowRect = new RectBox();
	public float slowTime;
	public CEnemyType type;
	private float walkSpeed;
	private CWaypoints waypoints;

	public CEnemy(MainGame game) {
		this.mainGame = game;
		this.isThere = false;
	}

	public final void damage(int damage) {
		if (this.isThere) {
			this.currentHealth -= damage;
			this.mainGame.gameLoopScreen.indicateDamage(this.pos
					.add(this.origin));
			if (this.currentHealth <= 0) {
				this.mainGame.gameLoopScreen.reportDestroyEnemy(this, false);
				this.isThere = false;
			}
			this.healthBarRect.width = (int) ((((float) this.currentHealth) / ((float) this.health)) * this.rect.width);
		}
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		if (this.isThere) {
			batch.draw(this.anim.getTexture(), this.rect, null,
					defaultSceneColor,
					MathUtils.toDegrees(-this.currentRotation), this.origin,
					SpriteEffects.None);
			if (this.slowTime > 0f) {
				batch.draw(this.type.speedReducer, this.slowRect, null,
						defaultSceneColor,
						MathUtils.toDegrees(-this.currentRotation),
						this.slowOrigin, SpriteEffects.None);
			}
			batch.draw(this.mainGame.gameLoopScreen.healthBarTexture,
					this.healthBarRect, defaultSceneColor);
		}
	}

	public final void init(CEnemyType type, int index) {
		this.index = index;
		this.waypoints = type.wayPoints;
		this.anim = new CAnimObject(type.anim);
		this.walkSpeed = type.walkSpeed;
		this.health = type.health
				+ ((int) (((type.health * type.healtFactor) * (this.mainGame.gameLoopScreen.wave - 1)) * 1.3f));
		this.currentHealth = this.health;
		this.isThere = true;
		this.bounty = type.bounty;
		this.type = type;
		this.pos = this.waypoints.getWaypoint(0).cpy();
		this.currentWayPoint = 1;
		Vector2f vector = this.waypoints.getWaypoint(this.currentWayPoint).sub(
				this.waypoints.getWaypoint(this.currentWayPoint - 1));
		vector.nor();
		this.rotation = this.currentRotation = (float) (Math.atan2(
				(double) vector.x, (double) vector.y) - 1.5707963267948966);
		this.rect = new RectBox(((int) this.pos.x)
				+ (this.anim.getTexture().getWidth() / 2), ((int) this.pos.y)
				+ this.anim.getTexture().getHeight(), this.anim.getTexture()
				.getWidth(), this.anim.getTexture().getHeight());
		this.slowRect = new RectBox(((int) this.pos.x)
				+ (this.type.speedReducer.getWidth() / 2), ((int) this.pos.y)
				+ this.type.speedReducer.getHeight(),
				this.type.speedReducer.getWidth(),
				this.type.speedReducer.getHeight());
		this.origin.x = ((float) this.anim.getTexture().getWidth()) / 2f;
		this.origin.y = ((float) this.anim.getTexture().getHeight()) / 2f;
		this.pointRect = new RectBox(
				(int) ((this.pos.x + this.origin.x) - (29f * this.mainGame.scalePos.y)),
				(int) ((this.pos.y + this.origin.y) - (29f * this.mainGame.scalePos.y)),
				(int) (58f * this.mainGame.scalePos.y),
				(int) (58f * this.mainGame.scalePos.y));
		this.slowOrigin.x = ((float) this.type.speedReducer.getWidth()) / 2f;
		this.slowOrigin.y = ((float) this.type.speedReducer.getHeight()) / 2f;
		this.healthBarRect = new RectBox((int) this.pos.x, (int) this.pos.y,
				this.rect.getWidth(), 2);
		this.slowTime = 0f;
	}

	public final boolean Intersects(RectBox target) {
		return this.pointRect.intersects(target);
	}

	public final void setCurrentHealth(int health) {
		this.currentHealth = health;
		this.healthBarRect.width = (int) ((((float) this.currentHealth) / ((float) this.health)) * this.rect
				.getWidth());
	}

	public final void setPos(float x, float y) {
		this.pos.x = x;
		this.pos.y = y;
		this.rect.x = (this.pos.x + this.origin.x);
		this.rect.y = (this.pos.y + this.origin.y);
		this.pointRect.x = this.pos.x;
		this.pointRect.y = this.pos.y;
		this.healthBarRect.x = this.pos.x;
		this.healthBarRect.y = this.pos.y;
		this.slowRect.x = this.rect.x;
		this.slowRect.y = this.rect.y;
	}

	public final void slow(int value) {
		if (value > this.slowTime) {
			this.slowTime = value;
		}
	}

	public final void update(float time) {
		if (this.isThere) {
			Vector2f vector = this.waypoints.getWaypoint(this.currentWayPoint)
					.sub(this.waypoints.getWaypoint(this.currentWayPoint - 1));
			vector.nor();
			if (this.rotation != this.currentRotation) {
				if (this.rotation > this.currentRotation) {
					if ((this.rotation - this.currentRotation) > 3.141593f) {
						if (this.rotation > 3.141593f) {
							this.currentRotation += 6.283185f;
						}
						this.currentRotation -= time * 8f;
					} else {
						this.currentRotation += time * 8f;
					}
				} else if (this.rotation < this.currentRotation) {
					if ((this.currentRotation - this.rotation) > 3.141593f) {
						if (this.currentRotation > 3.141593f) {
							this.rotation += 6.283185f;
						}
						this.currentRotation += time * 8f;
					} else {
						this.currentRotation -= time * 8f;
					}
				}
				if ((MathUtils.max(this.currentRotation, this.rotation) - MathUtils
						.min(this.currentRotation, this.rotation)) < 0.2) {
					this.currentRotation = this.rotation;
				}
			}
			float num = (this.pos.sub(this.waypoints
					.getWaypoint(this.currentWayPoint))).len();
			this.pos.addLocal((vector.mul(this.walkSpeed)).mul(time));
			this.rect.x = (this.pos.x + this.origin.x);
			this.rect.y = (this.pos.y + this.origin.y);
			this.pointRect.x = this.rect.x;
			this.pointRect.y = this.rect.y;
			float num2 = (this.pos.sub(this.waypoints
					.getWaypoint(this.currentWayPoint))).len();
			if (num < num2) {
				this.pos.x = this.waypoints.getWaypoint(this.currentWayPoint).x;
				this.pos.y = this.waypoints.getWaypoint(this.currentWayPoint).y;
				this.currentWayPoint++;
				if (this.currentWayPoint == this.waypoints.count()) {
					this.mainGame.gameLoopScreen.reportDestroyEnemy(this, true);
					this.isThere = false;
					if (this.mainGame.gameLoopScreen.lives > 0) {
						this.mainGame.gameLoopScreen.lives -= this.type.deciLives;
					}
				} else {
					vector = this.waypoints.getWaypoint(this.currentWayPoint)
							.sub(this.waypoints
									.getWaypoint(this.currentWayPoint - 1));
					vector.nor();
					this.rotation = (float) (Math.atan2((double) vector.x,
							(double) vector.y) - 1.5707963267948966);
					while (this.rotation < 0f) {
						this.rotation += 6.283185f;
					}
					while (this.rotation > 6.283185f) {
						this.rotation -= 6.283185f;
					}
				}
			}
			this.healthBarRect.x = this.pos.x;
			this.healthBarRect.y = this.pos.y;
			this.anim.update(time);
			if (this.slowTime > 0f) {
				this.walkSpeed = this.type.walkSpeed
						- (this.type.walkSpeed * (this.slowTime / 20f));
				this.slowTime -= time;
				if (this.slowTime < 0f) {
					this.slowTime = 0f;
					this.walkSpeed = this.type.walkSpeed;
				}
				this.slowRect.x = this.rect.x;
				this.slowRect.y = this.rect.y;
			}
		}
	}
}
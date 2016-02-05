package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.MathUtils;

public class CBullet {
	private CAnimObject anim;
	private int damage;
	public boolean homing;
	public boolean homingNoTarget;
	public boolean isThere;
	private MainGame mainGame;
	private Vector2f origin = new Vector2f();
	public Vector2f pos;
	private float radius;
	private RectBox rect;
	public float rotation;
	private float rotationSpeed;
	private float speed;
	private float splashRadius;

	public CEnemy target;
	private Vector2f targetPos;
	private CBulletType type;
	public Vector2f velicity;

	public CBullet(MainGame game) {
		this.mainGame = game;
	}

	public final void clearTarget(CEnemy cleartarget) {
		if (cleartarget == this.target) {
			this.homingNoTarget = true;
			this.target = null;
		}
	}

	public final void draw(SpriteBatch batch, LColor defaultSceneColor) {
		if (this.isThere) {
			batch.draw(this.anim.getTexture(), this.rect, null,
					defaultSceneColor, this.rotation, this.origin,
					SpriteEffects.None);
		}
	}

	public final int getDamage(CEnemy enemy, int damage) {
		if (enemy.type.bulletResistend != null) {
			for (int i = 0; i < enemy.type.bulletResistend.length; i++) {
				if (enemy.type.bulletResistend[i] == this.type) {
					return (int) (((float) damage) / 2f);
				}
			}
		}
		return damage;
	}

	public final void init(Vector2f position, float towerRotation,
			CEnemy target, int damage, float radius, CBulletType type) {
		this.isThere = true;
		this.type = type;
		this.pos = new Vector2f(position.x, position.y);
		this.damage = damage;
		this.speed = type.speed;
		this.anim = new CAnimObject(type.anim);
		this.homingNoTarget = false;
		this.rotationSpeed = type.rotationSpeed;
		this.rotation = towerRotation;
		this.homing = type.homing;
		this.splashRadius = type.splashRadius;
		this.radius = radius;
		this.target = target;
		this.targetPos = new Vector2f(target.pos.x, target.pos.y);
		this.targetPos.addSelf(target.origin);
		this.rect = new RectBox((int) this.pos.x, (int) this.pos.y, this.anim
				.getTexture().getWidth(), this.anim.getTexture().getHeight());
		this.origin.x = ((float) this.anim.getTexture().getWidth()) / 2f;
		this.origin.y = ((float) this.anim.getTexture().getHeight()) / 2f;
		this.velicity = this.targetPos.sub(this.pos);
		this.velicity.nor();
		if (this.splashRadius == -1f) {
			this.rotation = (float) (Math.atan2((double) this.velicity.y,
					(double) this.velicity.x) + 1.5707963705062866);
		}
	}

	public final void update(float time) {
		if (this.isThere) {
			this.anim.update(time);
			if (this.rotationSpeed > 0f) {
				this.rotation += time * this.rotationSpeed;
				if (this.rotation >= 6.283185f) {
					this.rotation = 0f;
				}
			}
			if (this.homing) {
				if (this.homingNoTarget) {
					CEnemy enemy = this.mainGame.gameLoopScreen.getFarestEnemy(
							this.pos, this.radius * 1.25f);
					if (this.target == null) {
						this.target = enemy;
					}
					if (this.target != null) {
						this.homingNoTarget = false;
						this.targetPos.x = this.target.pos.x;
						this.targetPos.y = this.target.pos.y;
						this.targetPos.addSelf(this.target.origin);
					}
				} else {
					this.targetPos.x = this.target.pos.x;
					this.targetPos.y = this.target.pos.y;
					this.targetPos.addSelf(this.target.origin);
				}
			}
			if (this.homingNoTarget) {
				this.pos.x += ((MathUtils.sin((-this.rotation - 3.141593f))) * this.speed)
						* time;
				this.pos.y += ((MathUtils.cos((-this.rotation - 3.141593f))) * this.speed)
						* time;
				this.rotation += time;
				while (this.rotation > 6.283185f) {
					this.rotation -= 6.283185f;
				}
				while (this.rotation < 0f) {
					this.rotation += 6.283185f;
				}
			} else if (this.homing) {
				Vector2f vector = this.targetPos.sub(this.pos);
				vector.nor();
				while (this.rotation > 6.283185f) {
					this.rotation -= 6.283185f;
				}
				while (this.rotation < 0f) {
					this.rotation += 6.283185f;
				}
				float num = (MathUtils.atan2(vector.y, vector.x) + 1.5707963705062866f);
				while (num < 0f) {
					num += 6.283185f;
				}
				while (num > 6.283185f) {
					num -= 6.283185f;
				}
				if (num > this.rotation) {
					if ((num - this.rotation) > 3.141593f) {
						this.rotation -= time * 2f;
					} else {
						this.rotation += time * 2f;
					}
				} else if (num < this.rotation) {
					if ((this.rotation - num) > 3.141593f) {
						this.rotation += time * 2f;
					} else {
						this.rotation -= time * 2f;
					}
				}
				if ((MathUtils.max(num, this.rotation) - MathUtils.min(num,
						this.rotation)) < 0.1) {
					this.rotation = num;
				}
				this.pos.x += ((MathUtils.sin((-this.rotation - 3.141593f))) * this.speed)
						* time;
				this.pos.y += ((MathUtils.cos((-this.rotation - 3.141593f))) * this.speed)
						* time;
			} else {
				if (this.splashRadius != -1f) {
					this.rotation += time;
					if (this.rotation > 6.283185f) {
						this.rotation -= 6.283185f;
					}
				}
				this.pos.addSelf(((this.velicity.mul(this.speed)).mul(time)));
			}
			this.rect.x = (int) this.pos.x;
			this.rect.y = (int) this.pos.y;
			if (this.homing && !this.homingNoTarget) {
				Vector2f vector2 = this.targetPos.sub(this.pos);
				if (vector2.len() < this.mainGame.TILESIZE.x) {
					if (this.splashRadius > 0f) {
						for (int i = 0; i < this.mainGame.gameLoopScreen.currentWaveMaxEnemy; i++) {
							if (this.mainGame.gameLoopScreen.enemy[i].isThere) {
								float num3 = ((this.mainGame.gameLoopScreen.enemy[i].pos
										.add(this.mainGame.gameLoopScreen.enemy[i].origin))
										.sub(this.pos)).len();
								if (num3 <= this.splashRadius) {
									this.mainGame.gameLoopScreen.enemy[i]
											.damage((int) (((this.splashRadius - num3) / this.splashRadius) * this
													.getDamage(
															this.mainGame.gameLoopScreen.enemy[i],
															this.damage)));
								}
							}
						}
					} else {
						this.target.damage(this.getDamage(this.target,
								this.damage));
					}
					this.isThere = false;
				}
			} else if (!this.homing) {
				if (this.splashRadius == 0f) {
					if (((this.pos.x > (this.mainGame.screenSize.x + 100f)) || (this.pos.y > (this.mainGame.screenSize.y + 100f)))
							|| ((this.pos.x < -100f) || (this.pos.y < -100f))) {
						this.isThere = false;
					} else {
						for (int j = 0; j < this.mainGame.gameLoopScreen.currentWaveMaxEnemy; j++) {
							if (this.mainGame.gameLoopScreen.enemy[j].isThere) {
								Vector2f vector4 = (this.mainGame.gameLoopScreen.enemy[j].pos
										.add(this.mainGame.gameLoopScreen.enemy[j].origin))
										.sub(this.pos);
								if (vector4.len() < (this.mainGame.TILESIZE.x - 10f)) {
									this.mainGame.gameLoopScreen.enemy[j]
											.damage(this
													.getDamage(
															this.mainGame.gameLoopScreen.enemy[j],
															this.damage));
									this.isThere = false;
									return;
								}
							}
						}
					}
				} else if (this.splashRadius == -1f) {
					if (((this.pos.x > (this.mainGame.screenSize.x + 100f)) || (this.pos.y > (this.mainGame.screenSize.y + 100f)))
							|| ((this.pos.x < -100f) || (this.pos.y < -100f))) {
						this.isThere = false;
					} else {
						for (int k = 0; k < this.mainGame.gameLoopScreen.currentWaveMaxEnemy; k++) {
							if (this.mainGame.gameLoopScreen.enemy[k].isThere) {
								Vector2f vector5 = (this.mainGame.gameLoopScreen.enemy[k].pos
										.add(this.mainGame.gameLoopScreen.enemy[k].origin))
										.sub(this.pos);
								if (vector5.len() < (this.mainGame.TILESIZE.x - 20f)) {
									this.mainGame.gameLoopScreen.enemy[k]
											.damage(this
													.getDamage(
															this.mainGame.gameLoopScreen.enemy[k],
															this.damage));
								}
							}
						}
					}
				} else if (this.splashRadius == 1f) {
					if (((this.pos.x > (this.mainGame.screenSize.x + 100f)) || (this.pos.y > (this.mainGame.screenSize.y + 100f)))
							|| ((this.pos.x < -100f) || (this.pos.y < -100f))) {
						this.isThere = false;
					} else {
						for (int m = 0; m < this.mainGame.gameLoopScreen.currentWaveMaxEnemy; m++) {
							if (this.mainGame.gameLoopScreen.enemy[m].isThere) {
								Vector2f vector6 = (this.mainGame.gameLoopScreen.enemy[m].pos
										.add(this.mainGame.gameLoopScreen.enemy[m].origin))
										.sub(this.pos);
								if (vector6.len() < (this.mainGame.TILESIZE.x - 10f)) {
									int num7 = this
											.getDamage(
													this.mainGame.gameLoopScreen.enemy[m],
													this.damage);
									this.mainGame.gameLoopScreen.enemy[m]
											.slow(num7);
									this.mainGame.gameLoopScreen.enemy[m]
											.damage(num7);
									this.isThere = false;
								}
							}
						}
					}
				} else {
					Vector2f vector7 = this.targetPos.sub(this.pos);
					if (vector7.len() < (this.mainGame.TILESIZE.x - 10f)) {
						for (int n = 0; n < this.mainGame.gameLoopScreen.currentWaveMaxEnemy; n++) {
							if (this.mainGame.gameLoopScreen.enemy[n].isThere) {
								if (this.splashRadius > 0f) {
									float num9 = ((this.mainGame.gameLoopScreen.enemy[n].pos
											.add(this.mainGame.gameLoopScreen.enemy[n].origin))
											.sub(this.pos)).len();
									if (num9 <= this.splashRadius) {
										int damage = (int) (((this.splashRadius - num9) / this.splashRadius) * this.damage);
										this.mainGame.gameLoopScreen.enemy[n]
												.damage(this
														.getDamage(
																this.mainGame.gameLoopScreen.enemy[n],
																damage));
									}
								} else {
									Vector2f vector9 = (this.mainGame.gameLoopScreen.enemy[n].pos
											.add(this.mainGame.gameLoopScreen.enemy[n].origin))
											.sub(this.pos);
									if (vector9.len() < this.mainGame.TILESIZE.x) {
										this.mainGame.gameLoopScreen.enemy[n]
												.damage(this
														.getDamage(
																this.mainGame.gameLoopScreen.enemy[n],
																this.damage));
									}
								}
							}
						}
						this.mainGame.gameLoopScreen.indicateDamage(this.pos);
						this.isThere = false;
					}
				}
			}
		}
	}
}
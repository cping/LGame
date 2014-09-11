package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;

public class Bunker extends DrawableObject {
	public float AccMultiplier;
	public float aimAngle;
	private float aimAngleMax;
	private float aimAngleMin;
	public float aimAngleOriginal;
	public Vector2f aimingPoint;
	private float alphaRuler;
	public int artilleryCoolDown;
	public float fieldRepair;
	public Weapon freeMercenary;
	private int grenadeDelay;
	public Vector2f grenadeTargetPosition;
	public int iCoolDown;
	private int iGrenadeDelay;
	public boolean isAAGunUsable;
	public boolean isArtilleryEnabled;
	public boolean isArtilleryReady;
	public boolean isFingerTouched;
	public boolean isFreeMerAdded;
	public boolean isGrenadeLaunchable;
	public boolean isLaunchingGrenade;
	public boolean isPlayerControlled;
	public float learningMultiplier;
	public float magSizeMultiplier;
	public Vector2f markerPosition;
	public int numArtilleryHit;
	public float reloadingTimeMultiplier;
	public java.util.ArrayList<Button> skillsGained;
	public LTexture t2DBunkerTop;
	public Zombie target;
	public Vector2f touchCurrent;
	public Vector2f touchOriginal;
	public Weapon weapon;

	public Bunker(LTexture t2DBunkerBottom, LTexture t2DBunkerTop,
			Vector2f position) {
		super(t2DBunkerBottom, position);
		this.aimAngleMax = 1.256637f;
		this.aimAngleMin = -1.256637f;
		this.t2DBunkerTop = t2DBunkerTop;
		this.skillsGained = new java.util.ArrayList<Button>();
		this.isLaunchingGrenade = false;
		this.isGrenadeLaunchable = true;
		this.iGrenadeDelay = 0;
		this.grenadeDelay = 15;
		this.alphaRuler = 1f;
		this.aimingPoint = new Vector2f(500f, 240f);
		this.weapon = Help.currentWeapon;
		this.weapon.isTriggerPulled = false;
		this.markerPosition = position.cpy();
		this.isPlayerControlled = false;
		this.reloadingTimeMultiplier = 1f;
		this.magSizeMultiplier = 1f;
		this.AccMultiplier = 1f;
		this.isAAGunUsable = false;
		this.fieldRepair = 0f;
		this.learningMultiplier = 1f;
		this.isFreeMerAdded = false;
		this.isArtilleryEnabled = false;
		this.isArtilleryReady = true;
		this.numArtilleryHit = 5;
		this.artilleryCoolDown = 300;
		this.iCoolDown = this.artilleryCoolDown;
		this.isFingerTouched = false;
		this.touchOriginal = new Vector2f(700f, 300f);
		this.touchCurrent = this.touchOriginal.cpy();
	}

	public final void Automate(java.util.ArrayList<Zombie> zombieList) {
		if (this.target == null) {
			this.target = zombieList.get((int) ((ScreenGameplay.rand
					.NextDouble() * zombieList.size()) * 0.999));
		} else if (this.target.isDead && (zombieList.size() != 0)) {
			this.target = zombieList.get((int) ((ScreenGameplay.rand
					.NextDouble() * zombieList.size()) * 0.999));
			this.weapon.iDelay = (int) (ScreenGameplay.rand.NextDouble() * 5.0);
		}
		if (zombieList.isEmpty()) {
			this.weapon.isTriggerPulled = false;
		} else if (this.target != null) {
			this.weapon.isTriggerPulled = true;
			this.aimAngle = (float) Math.atan2(
					(this.position.y - this.target.position.y),
					(this.position.x - this.target.position.x));
		}
		this.weapon.Update();
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
		if (this.isPlayerControlled) {
			batch.draw(
					ScreenGameplay.t2DMarker,
					this.markerPosition.add(0f, -5f),
					null,
					LColor.white,
					MathUtils.toDegrees(this.aimAngle
							+ this.weapon.currentAccuracy), 600f, 0f, 1f,
					SpriteEffects.None);
			batch.draw(ScreenGameplay.t2DMarker1,
					this.markerPosition.add(0f, -5f), null, LColor.white,
					MathUtils.toDegrees(this.aimAngle), 350f, 0f, 1f,
					SpriteEffects.None);
			batch.draw(
					ScreenGameplay.t2DMarker,
					this.markerPosition.add(0f, -5f),
					null,
					LColor.white,
					MathUtils.toDegrees(this.aimAngle
							- this.weapon.currentAccuracy), 600f, 0f, 1f,
					SpriteEffects.None);
		}
		if (this.weapon != null) {
			batch.draw(ScreenGameplay.t2DGunInField,
					super.position.add(0f, -5f), null, LColor.white,
					MathUtils.toDegrees(this.aimAngle),

					(ScreenGameplay.t2DGunInField.getWidth() - 10),
					(ScreenGameplay.t2DGunInField.getHeight() / 2), 1f,
					SpriteEffects.None);
		}
		if (super.texture == ScreenGameplay.t2DBunkerBottom) {
			batch.draw(this.t2DBunkerTop, super.position, null, LColor.white,
					0f, (this.t2DBunkerTop.getWidth() / 2),
					(this.t2DBunkerTop.getHeight() / 2), 1f, SpriteEffects.None);
		}
		batch.draw(ScreenGameplay.t2DRuler, 800f, this.position.y
				- (this.aimingPoint.y / 2f), null,
				Global.Pool.getColor(1f, 1f, 1f, 1f * this.alphaRuler), 0f,
				ScreenGameplay.t2DRuler.getWidth(),
				(ScreenGameplay.t2DRuler.getHeight() / 2), 1f,
				SpriteEffects.None);
	}

	public final void Update(java.util.ArrayList<Vector2f> mousePositionList) {
		this.weapon.isTriggerPulled = false;
		for (Vector2f vector : mousePositionList) {
			if (vector.x > 620f) {
				if (!this.isFingerTouched) {
					this.isFingerTouched = true;
					this.touchOriginal = vector.cpy();
					this.aimAngleOriginal = this.aimAngle;
				}
				this.aimingPoint = vector.cpy();
				this.aimAngle = ((-(this.aimingPoint.y - this.touchOriginal.y) / 480f) * this.aimAngleMax)
						+ this.aimAngleOriginal;
				if (this.aimAngle > this.aimAngleMax) {
					this.aimAngle = this.aimAngleMax;
				} else if (this.aimAngle < this.aimAngleMin) {
					this.aimAngle = this.aimAngleMin;
				}
				continue;
			}
			if ((vector.x < 100f) && (vector.y > 350f)) {
				this.weapon.isTriggerPulled = true;
			} else if (((vector.x > 100f) && (vector.y > 200f))
					&& (this.isGrenadeLaunchable && (Help.numGrenade > 0))) {
				this.isLaunchingGrenade = true;
				this.grenadeTargetPosition = vector.cpy();
				this.isGrenadeLaunchable = false;
			}
		}
		this.isFingerTouched = false;
		for (Vector2f vector2 : mousePositionList) {
			if (vector2.x > 650f) {
				this.isFingerTouched = true;
				break;
			}
		}
		if (this.isFingerTouched) {
			this.alphaRuler = 1f;
		} else if (this.alphaRuler > 0f) {
			this.alphaRuler -= 0.03f;
		}
		this.weapon.Update();
		if (!this.isGrenadeLaunchable) {
			this.iGrenadeDelay++;
			if (this.iGrenadeDelay > this.grenadeDelay) {
				this.iGrenadeDelay = 0;
				this.isGrenadeLaunchable = true;
			}
		}
		if (!this.isArtilleryReady) {
			this.iCoolDown++;
			if (this.iCoolDown >= this.artilleryCoolDown) {
				this.isArtilleryReady = true;
			}
		}
	}
}
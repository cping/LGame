package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;

public class ControlPane extends DrawableObject {
	private Vector2f coverPosition;
	private Vector2f coverPositionClosed;
	private Vector2f coverPositionTarget;
	private Vector2f handlePosition;
	private Vector2f handlePositionFinal;
	private Vector2f handlePositionInitial;
	private int handleResetLength;
	private int iHandleReset;
	private boolean isResettingHandle;
	private int numBullet;
	private LTexture t2DBullet;
	private LTexture t2DControlPaneBG;
	private LTexture t2DCover;
	private LTexture t2DHandle;

	public ControlPane(LTexture t2DControlPane, LTexture t2DControlPaneBG,
			LTexture t2DBullet, LTexture t2DCover, LTexture t2DHandle,
			Vector2f position) {
		super(t2DControlPane, position);
		this.t2DBullet = t2DBullet;
		this.t2DCover = t2DCover;
		this.t2DHandle = t2DHandle;
		this.t2DControlPaneBG = t2DControlPaneBG;
		this.isResettingHandle = false;
		this.handleResetLength = 10;
		this.iHandleReset = 0;
		this.numBullet = 0;
		this.coverPositionClosed = new Vector2f(795f, 44f);
		this.coverPosition = this.coverPositionClosed.sub(
				(float) t2DCover.getWidth(), 0f);
		this.handlePositionInitial = new Vector2f(495f, -1f);
		this.handlePositionFinal = new Vector2f(530f, -1f);
		this.handlePosition = this.handlePositionInitial.cpy();
	}

	@Override
	public void Draw(SpriteBatch batch) {
		batch.draw(this.t2DControlPaneBG, super.position, null, LColor.white,
				0f, (this.t2DControlPaneBG.getWidth() / 2),
				(this.t2DControlPaneBG.getHeight() / 2), 1f, SpriteEffects.None);
		batch.draw(this.t2DCover, this.coverPosition, null, LColor.white, 0f,
				0f, 0f, 1f, SpriteEffects.None);
		batch.draw(this.t2DHandle, this.handlePosition, null, LColor.white, 0f,
				0f, 0f, 1f, SpriteEffects.None);
		for (int i = 0; i < this.numBullet; i++) {
			batch.draw(this.t2DBullet, this.coverPositionClosed.sub(
					(float) ((i * (this.t2DBullet.getWidth() - 2)) + 10), 0f),
					null, LColor.white, 0f, 0f, 0f, 1f, SpriteEffects.None);
		}
		super.Draw(batch);
		batch.draw(this.t2DHandle, this.handlePosition, null, LColor.white, 0f,
				0f, 0f, 1f, SpriteEffects.None);
		for (int j = 0; j < Help.numGrenade; j++) {
			batch.draw(ScreenGameplay.t2DGrenade,
					(float) ((j * ScreenGameplay.t2DGrenade.getWidth()) + 650),
					0f, null, LColor.white, 0f, 0f, 0f, 1f, SpriteEffects.None);
		}
	}

	public final void ResetHandle() {
		if (this.iHandleReset < (this.handleResetLength * 0.4)) {
			this.handlePosition.addSelf((this.handlePositionFinal
					.sub(this.handlePositionInitial)
					.div((float) this.handleResetLength)).div(0.4f));
		} else if (this.iHandleReset > (this.handleResetLength * 0.6)) {
			this.handlePosition.subLocal((this.handlePositionFinal
					.sub(this.handlePositionInitial)
					.div((float) this.handleResetLength)).div(0.4f));
		}
		this.iHandleReset++;
		if (this.iHandleReset >= this.handleResetLength) {
			this.iHandleReset = 0;
			this.isResettingHandle = false;
			this.handlePosition = this.handlePositionInitial;
		}
	}

	@Override
	public void Update() {
		super.Update();
	}

	public final void Update1(Weapon weapon, int numGrenade) {
		this.Update();
		this.numBullet = weapon.numBullet;
		this.coverPositionTarget = this.coverPositionClosed
				.sub(((this.numBullet * (this.t2DBullet.getWidth() - 1)) + this.t2DCover.getWidth()),
						0f);
		if (this.coverPositionTarget.x < (0x31b - (2 * this.t2DCover.getWidth()))) {
			this.coverPositionTarget.x = 0x31b - (2 * this.t2DCover.getWidth());
		}
		if ((this.coverPositionTarget.x - this.coverPosition.x) > 2f) {
			this.coverPosition.x += 3f;
		} else if ((this.coverPosition.x - this.coverPositionTarget.x) > 2f) {
			this.coverPosition.x -= 3f;
		}
		if ((weapon.iReload > (0.9 * weapon.currentReloadLength))
				&& !this.isResettingHandle) {
			this.isResettingHandle = true;
		}
		if (this.isResettingHandle) {
			this.ResetHandle();
		}
	}
}
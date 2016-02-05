package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.canvas.LColor;
import loon.geom.Vector2f;

public class Button extends DrawableClickableObject {

	private Help.ButtonID buttonID = Help.ButtonID.values()[0];
	public LTexture buttonTexture;

	public int delayBeforeEffect;
	public String description;
	public int diffLevel;
	public boolean isEffectTaken;
	public boolean isInTransition;
	public boolean isPrerequisiteMet;
	public boolean isTakingEffect;
	public String reqDescription;
	private float rotation;
	public String subDescription;
	private float tranAlpha;
	private Vector2f tranPositionOffset;
	private int tTranCount;
	private int tTranLength;

	public Button(LTexture buttonTexture, Vector2f position, float rotation,
			Help.ButtonID buttonID, int delayBeforeEffect) {
		super(buttonTexture, position);
		this.buttonTexture = buttonTexture;
		super.position = position.cpy();
		this.rotation = rotation;
		this.buttonID = buttonID;
		this.delayBeforeEffect = delayBeforeEffect;
		this.reqDescription = "";
		super.origin = new Vector2f((float) (buttonTexture.getWidth() / 2),
				(float) (buttonTexture.getHeight() / 2));
		this.ButtonInitialize();
		this.diffLevel = 0;
		this.isPrerequisiteMet = true;
	}

	public void ApplyEffect(Bunker player) {
	}

	public final void ButtonInitialize() {
		this.tranPositionOffset = new Vector2f(0f, 0f);
		this.tTranCount = 0;
		this.tTranLength = 15;
		this.tranAlpha = 1f;
		this.isInTransition = false;
		this.isTakingEffect = false;
		this.isEffectTaken = false;
	}

	public void CheckPrerequisite(Bunker player) {
	}

	@Override
	public void Draw(SpriteBatch batch) {
		LColor color = Global.Pool.getColor(1f, 1f, 1f, this.tranAlpha);
		batch.draw(this.buttonTexture, super.position, null, color,
				this.rotation, super.origin, 1f, SpriteEffects.None);
		if (this.isInTransition) {
			batch.draw(this.buttonTexture,
					super.position.add(this.tranPositionOffset), null, color,
					this.rotation, super.origin, 1f, SpriteEffects.None);
			batch.draw(this.buttonTexture,
					super.position.sub(this.tranPositionOffset), null, color,
					this.rotation, super.origin, 1f, SpriteEffects.None);
		}
	}

	@Override
	public boolean IsClicked(Vector2f mousePosition) {
		return super.IsClicked(mousePosition);
	}

	public final void TransAnimation() {
		if (this.tTranCount <= 1f) {
			this.tranAlpha = 0.1f;
		} else {
			this.tranAlpha += 0.5f / ((float) this.tTranLength);
		}
		this.tTranCount++;
		if (this.tTranCount >= this.delayBeforeEffect) {
			this.isTakingEffect = true;
		}
		if (this.tTranCount < this.tTranLength) {
			this.isInTransition = true;
		}
		if ((this.tTranCount > this.delayBeforeEffect)
				&& (this.tTranCount > this.tTranLength)) {
			this.ButtonInitialize();
		}
	}

	public final Help.ButtonID getButtonID() {
		return this.buttonID;
	}

	public final float getTransAlpha() {
		return this.tranAlpha;
	}

	public final int getTTranLength() {
		return this.tTranLength;
	}
}
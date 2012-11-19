package org.test;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.SpriteBatch.SpriteEffects;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.LColor;
import loon.core.graphics.opengl.LTexture;
import loon.core.timer.GameTime;
import loon.utils.MathUtils;

public abstract class GameObject {

	private LColor color = LColor.white;

	private float depth = 0f;

	private SpriteEffects effect = SpriteEffects.None;

	private Vector2f origin = Vector2f.ZERO();

	private Vector2f position = Vector2f.ZERO();

	private boolean remove;

	private float rotation = 0f;

	private float scale = 1f;

	private RectBox source = new RectBox();

	private LTexture texture = null;

	protected GameObject() {
	}

	public void Draw(GameTime gameTime, SpriteBatch batch) {
		batch.draw(this.texture, this.position, this.source, this.color,
				MathUtils.degrees(this.rotation), this.origin, this.scale, this.effect);
	}

	public final Vector2f GetPosition() {
		return this.getPosition();
	}

	public final boolean Removable() {
		return this.getRemove();
	}

	public final void RemoveManuel() {
		this.remove = true;
	}

	public void SetDepth(float depthValue) {
		this.setDepth(depthValue);
	}

	public void SetEffect(SpriteEffects effect) {
		this.effect = effect;
	}

	public void SetPosition(Vector2f newPosition) {
		this.setPosition(newPosition);
	}

	public void SetRotation(float rotation) {
		this.rotation = rotation;
	}

	public void SetScale(float scale) {
		this.setScale(scale);
	}

	public void update(GameTime gameTime) {
		if ((((this.getPosition().x < -400f) || (this.getPosition().x > 880f)) || (this
				.getPosition().y < -400f)) || (this.getPosition().y > 1200f)) {
			this.RemoveManuel();
		}
	}

	protected final LColor getColor() {
		return this.color;
	}

	protected final void setColor(LColor value) {
		this.color = value;
	}

	protected final float getDepth() {
		return this.depth;
	}

	protected final void setDepth(float value) {
		this.depth = value;
	}

	protected final SpriteEffects getEffect() {
		return this.effect;
	}

	protected final void setEffect(SpriteEffects value) {
		this.effect = value;
	}

	protected final Vector2f getOrigin() {
		return this.origin;
	}

	protected final void setOrigin(Vector2f value) {
		this.origin.set(value);
	}

	protected final void setOrigin(float x, float y) {
		this.origin.set(x, y);
	}

	protected final Vector2f getPosition() {
		return this.position;
	}

	protected final void setPosition(Vector2f value) {
		this.position.set(value);
	}

	protected final void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	protected final boolean getRemove() {
		return this.remove;
	}

	protected final void setRemove(boolean value) {
		this.remove = value;
	}

	protected final float getRotation() {
		return this.rotation;
	}

	protected final void setRotation(float value) {
		this.rotation = value;
	}

	protected final float getScale() {
		return this.scale;
	}

	protected final void setScale(float value) {
		this.scale = value;
	}

	protected final RectBox getSource() {
		return this.source;
	}

	protected final void setSource(RectBox value) {
		this.source.setBounds(value);
	}

	protected final void setSource(int x, int y, int w, int h) {
		this.source.setBounds(x, y, w, h);
	}

	protected final LTexture getTexture() {
		return this.texture;
	}

	protected final void setTexture(LTexture value) {
		this.texture = value;
	}
}
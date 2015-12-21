package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class LurWeapon extends DrawableGameComponent implements IGameComponent {

	private int bashRadius;
	private int damage;
	private MainGame game;

	private java.util.ArrayList<Monster> targetMonsters;
	private LTexture texture;
	private Tower tower;

	public LurWeapon(MainGame game, Tower tower,
			java.util.ArrayList<Monster> targetMonsters) {
		super(game);
		this.game = game;
		this.tower = tower;
		this.damage = tower.getDamage();
		this.setHasHitTarget(false);
		this.targetMonsters = targetMonsters;

		this.bashRadius = 20;
		for (Monster monster : targetMonsters) {
			monster.addReservedHitPoints(this.damage);
		}
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		Vector2f vector = this.tower.getPosition().sub(this.bashRadius,
				this.bashRadius);
		batch.draw(this.texture, vector.x, vector.y, this.bashRadius * 2,
				this.bashRadius * 2);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/bash.png");
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.bashRadius >= this.tower.getRange()) {
			this.setHasHitTarget(true);
			for (int i = 0; i < this.targetMonsters.size(); i++) {
				Monster local1 = this.targetMonsters.get(i);
				local1.removeReservedHitPoints(this.damage);
				this.targetMonsters.get(i).Hit(this.damage);
			}
			this.game.Components().remove(this);
		} else {
			this.bashRadius += 2;
		}
	}

	private boolean privateHasHitTarget;

	public final boolean getHasHitTarget() {
		return privateHasHitTarget;
	}

	public final void setHasHitTarget(boolean value) {
		privateHasHitTarget = value;
	}
}
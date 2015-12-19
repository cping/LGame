package com.mygame;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class MonsterToolbar extends DrawableGameComponent implements
		IGameComponent {
	private AnimatedSprite animatedSpriteMonster;
	private Vector2f drawPosition;
	private Vector2f drawPositionFont;
	private LFont font;
	private MainGame game;
	private ProgressBar healthBar;
	private Monster monster;
	private LTexture texture;

	public MonsterToolbar(MainGame game, Monster monster) {
		super(game);
		this.game = game;
		this.monster = monster;
		this.drawPosition = new Vector2f(10f, 420f);
		this.drawPositionFont = this.drawPosition.add(78f, 12f);
		this.animatedSpriteMonster = AnimatedSpriteMonster
				.GetAnimatedSpriteMonsterForMonsterToolbar(game,
						this.drawPosition, monster.getMonsterType());
		this.animatedSpriteMonster.setDrawOrder(0x1d);
		this.animatedSpriteMonster.setAnimationSpeedRatio(3);
		this.animatedSpriteMonster.setObeyGameOpacity(false);
		this.animatedSpriteMonster.setOnlyAnimateIfGameStateStarted(false);
		super.setDrawOrder(1);
		game.Components().add(this.animatedSpriteMonster);
		this.healthBar = new ProgressBar(game, 200, true);
		this.healthBar.setPosition(this.drawPosition.add(75f, 30f));
		this.healthBar.setHeight(8);
		game.Components().add(this.healthBar);
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, LColor.white);
		batch.drawString(this.font, LanguageResources.getRemainingHealth()
				+ " " + this.monster.getHitPoints(), this.drawPositionFont,
				LColor.white);
		super.draw(batch, gameTime);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture("assets/monster_toolbar.png");
		this.font = LFont.getFont(12);
	}

	public final void Remove() {
		this.game.Components().remove(this.animatedSpriteMonster);
		this.game.Components().remove(this.healthBar);
		this.game.Components().remove(this);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		this.healthBar.setCurrentPercent((100 * this.monster.getHitPoints())
				/ this.monster.getStartHitPoints());
	}
}
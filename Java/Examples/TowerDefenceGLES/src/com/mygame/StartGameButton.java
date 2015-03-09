package com.mygame;

import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTextures;
import loon.core.timer.GameTime;

public class StartGameButton extends DrawableGameComponent implements
		IGameComponent {
	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private LTexture texture;
	private String textureFile;

	public StartGameButton(MainGame game) {
		super(game);
		this.drawPosition = new Vector2f(100f, 2f);
		this.textureFile = "assets/start.png";
		this.game = game;
		super.setDrawOrder(40);
	}

	private RectBox rect = new RectBox();

	public final RectBox CentralCollisionArea() {
		rect.setBounds(this.drawPosition.x, this.drawPosition.y,
				this.texture.getWidth(), this.texture.getHeight());
		return rect;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(this.texture, this.drawPosition, this.game
				.getGameplayScreen().getGameOpacity());
		Utils.DrawStringAlignCenter(batch, this.font,
				"" + LanguageResources.getStart(), this.drawPosition.x + 50f,
				this.drawPosition.y + 13f, LColor.white);
		super.draw(batch, gameTime);
	}

	public final void Hide() {
		this.drawPosition.y = -300f;
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.textureFile);
		this.font = LFont.getFont(12);
	}

	public final void Show() {
		this.drawPosition.y = 2f;
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
	}
}
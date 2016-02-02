package org.test.towerdefense;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.SpriteBatch;
import loon.action.sprite.painting.DrawableGameComponent;
import loon.action.sprite.painting.IGameComponent;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class ShowMenuButton extends DrawableGameComponent implements
		IGameComponent {

	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private LTexture texture;
	private String textureFile;

	public ShowMenuButton(MainGame game) {
		super(game);
		this.textureFile = "assets/build_toolbar.png";
		this.game = game;
		this.Show();
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
				"" + LanguageResources.getMenu(),
				this.drawPosition.add(40f, 20f), LColor.white);
		super.draw(batch, gameTime);
	}

	public final void Hide() {
		this.drawPosition = new Vector2f(240f, -300f);
		this.setIsVisible(false);
	}

	@Override
	protected void loadContent() {
		super.loadContent();
		this.texture = LTextures.loadTexture(this.textureFile);
		this.font = LFont.getFont(12);
	}

	public final void Show() {
		this.drawPosition = new Vector2f(240f, 425f);
		this.setIsVisible(true);
	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
	}

	private boolean privateIsVisible;

	public final boolean getIsVisible() {
		return privateIsVisible;
	}

	public final void setIsVisible(boolean value) {
		privateIsVisible = value;
	}
}
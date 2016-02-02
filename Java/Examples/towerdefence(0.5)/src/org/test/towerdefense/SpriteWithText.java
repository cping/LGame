package org.test.towerdefense;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import loon.action.sprite.SpriteBatch;
import loon.canvas.LColor;
import loon.font.LFont;
import loon.geom.Vector2f;
import loon.utils.timer.GameTime;

public class SpriteWithText extends Sprite {
	private Vector2f drawPosition;
	private LFont font;
	private MainGame game;
	private int showMilliseconds;
	private HashMap<Vector2f, String> textAndRelativePosition;
	private double timeLeft;

	public SpriteWithText(MainGame game, String textureFile,
			int showMilliseconds, Vector2f drawPosition,
			HashMap<Vector2f, String> textAndRelativePosition, LFont font) {
		super(game, textureFile, showMilliseconds, drawPosition.cpy());
		this.game = game;
		this.showMilliseconds = showMilliseconds;
		this.timeLeft = showMilliseconds;
		this.drawPosition = drawPosition.cpy();
		this.textAndRelativePosition = new HashMap<Vector2f, String>(
				textAndRelativePosition);
		this.font = font;
	}

	@Override
	public void draw(SpriteBatch batch, GameTime gameTime) {
		batch.draw(super.getTexture(), this.drawPosition, LColor.white);
		Set<Entry<Vector2f, String>> result = textAndRelativePosition
				.entrySet();
		for (Iterator<Entry<Vector2f, String>> it = result.iterator(); it
				.hasNext();) {
			Entry<Vector2f, String> pair = it.next();
			batch.drawString(this.font, pair.getValue(),
					this.drawPosition.add(pair.getKey()), LColor.white);
		}

	}

	@Override
	public void update(GameTime gameTime) {
		super.update(gameTime);
		if (this.showMilliseconds > 0) {
			this.timeLeft -= gameTime.getMilliseconds();
			if (this.timeLeft < 0.0) {
				this.game.Components().remove(this);
			}
		}
	}
}
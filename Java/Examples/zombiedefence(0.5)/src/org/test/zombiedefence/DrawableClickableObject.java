package org.test.zombiedefence;

import loon.LTexture;
import loon.action.sprite.SpriteBatch;
import loon.geom.RectBox;
import loon.geom.Vector2f;

public class DrawableClickableObject extends DrawableObject {
	public RectBox objArea;

	public DrawableClickableObject(LTexture texture, Vector2f position) {
		super(texture, position);
		this.objArea = new RectBox(((int) position.x)
				- (texture.getWidth() / 2), ((int) position.y)
				- (texture.getHeight() / 2), texture.getWidth(),
				texture.getHeight());
	}

	@Override
	public void Draw(SpriteBatch batch) {
		super.Draw(batch);
	}

	public boolean IsClicked(Vector2f mousePosition) {
		int width = 10;
		return this.objArea.intersects(( mousePosition.x)
				- (width / 2), (mousePosition.y) - (width / 2), width,
				width);
	}

	@Override
	public void Update() {
		super.Update();
	}
}
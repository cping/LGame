package com.zombiedefence.free;

import loon.action.sprite.SpriteBatch;
import loon.core.geom.RectBox;
import loon.core.geom.Vector2f;
import loon.core.graphics.opengl.LTexture;

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
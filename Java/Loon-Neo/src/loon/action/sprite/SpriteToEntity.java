package loon.action.sprite;

import loon.LTexture;
import loon.geom.RectBox;

class SpriteToEntity extends Entity {

	private ISprite obj;

	public SpriteToEntity(ISprite spr) {
		this.obj = spr;
	}

	@Override
	public float getWidth() {
		return obj.getWidth();
	}

	@Override
	public float getHeight() {
		return obj.getHeight();
	}

	@Override
	public float getX() {
		return obj.getX();
	}

	@Override
	public float getY() {
		return obj.getY();
	}

	@Override
	public int x() {
		return obj.x();
	}

	@Override
	public int y() {
		return obj.y();
	}

	@Override
	public RectBox getCollisionBox() {
		return obj.getCollisionBox();
	}

	@Override
	public LTexture getBitmap() {
		return obj.getBitmap();
	}

	@Override
	public String getName() {
		return obj.getName();
	}

	@Override
	public Object getTag() {
		return obj.getTag();
	}

}

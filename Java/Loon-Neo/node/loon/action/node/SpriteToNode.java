package loon.action.node;

import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;

public class SpriteToNode extends LNNode {

	private ISprite obj;

	public SpriteToNode(ISprite spr) {
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

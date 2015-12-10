package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.geom.Dimension;
import loon.geom.RectBox;

public abstract class Background extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected Dimension _screen;

	protected boolean _visible = true;
	
	protected LTexture _texture;

	public Background(float x, float y, float w, float h) {
		this.setLocation(x, y);
		this._screen = new Dimension(w, h);
		this._visible = true;
	}

	public float getWidth() {
		return _screen.getWidth();
	}

	public float getHeight() {
		return _screen.getHeight();
	}

	public void setSize(int w, int h) {
		_screen.setSize(w, h);
	}

	public void update(long elapsedTime) {
	}

	@Override
	public void setVisible(boolean v) {
		this._visible = v;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return _texture;
	}

	public Dimension getDimension() {
		return _screen;
	}

	@Override
	public void close() {
		if (_texture != null) {
			_texture.close();
		}
	}

}

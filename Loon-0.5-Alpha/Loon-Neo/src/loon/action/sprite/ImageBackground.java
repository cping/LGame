package loon.action.sprite;

import loon.LTexture;
import loon.LTextures;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class ImageBackground extends Background {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected RectBox _shear;

	public ImageBackground(LTexture tex, float x, float y, float w, float h) {
		super(x, y, w, h);
		_texture = tex;
		_shear = new RectBox(0, 0, _texture.getWidth(), _texture.getHeight());
	}

	public ImageBackground(String path, float x, float y, float w, float h) {
		this(LTextures.loadTexture(path), x, y, w, h);
	}

	public ImageBackground(String path) {
		this(LTextures.loadTexture(path));
	}

	public ImageBackground(LTexture texture) {
		super(0, 0, 1, 1);
		_texture = texture;
		_shear = new RectBox(0, 0, _texture.getWidth(), _texture.getHeight());
		_screen = new Dimension(_texture.getWidth(), _texture.getHeight());
	}

	@Override
	public void createUI(GLEx g) {
		if (!_visible) {
			return;
		}
		if (_alpha <= 0.01) {
			return;
		}
		float tmp = g.alpha();
		g.setAlpha(_alpha);
		if (_texture != null) {
			g.draw(_texture, _location.x, _location.y, _screen.width,
					_screen.height, _shear.x, _shear.y, _shear.width,
					_shear.height, _rotation);

		}
		g.setAlpha(tmp);
	}

	public RectBox getShear() {
		return _shear;
	}

	public void setShear(RectBox s) {
		this._shear.setBounds(s);
	}

	public void setShear(float x, float y, float w, float h) {
		this._shear.setBounds(x, y, w, h);
	}
}

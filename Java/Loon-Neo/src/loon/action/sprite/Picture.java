package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.LTextures;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class Picture extends LObject implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1982153514439690901L;

	private boolean visible;

	private int width, height;

	private LTexture image;

	public Picture(String fileName) {
		this(fileName, 0, 0);
	}

	public Picture(int x, int y) {
		this((LTexture) null, x, y);
	}

	public Picture(String fileName, int x, int y) {
		this(LTextures.loadTexture(fileName), x, y);
	}

	public Picture(LTexture image) {
		this(image, 0, 0);
	}

	public Picture(LTexture image, int x, int y) {
		if (image != null) {
			this.setImage(image);
			this.width = (int) image.width();
			this.height = (int) image.height();
		}
		this.setLocation(x, y);
		this.visible = true;
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			float tmp = g.alpha();
			if (_alpha > 0 && _alpha < 1) {
				g.setAlpha(_alpha);
			}
			g.draw(image, x(), y());
			g.setAlpha(tmp);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Picture) {
			return equals((Picture) o);
		} else {
			return false;
		}
	}

	public boolean equals(Picture p) {
		if (image.equals(p.image)) {
			return true;
		}
		if (this.width == p.width && this.height == p.height) {
			if (image.hashCode() == p.image.hashCode()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public float getHeight() {
		return height;
	}

	@Override
	public float getWidth() {
		return width;
	}

	@Override
	public void update(long timer) {
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void close() {
		if (image != null) {
			image.close();
			image = null;
		}
	}

	public void setImage(String fileName) {
		this.image = LTextures.loadTexture(fileName);
		this.width = (int) image.width();
		this.height = (int) image.height();
	}

	public void setImage(LTexture image) {
		this.image = image;
		this.width = (int) image.width();
		this.height = (int) image.height();
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public LTexture getBitmap() {
		return image;
	}

}

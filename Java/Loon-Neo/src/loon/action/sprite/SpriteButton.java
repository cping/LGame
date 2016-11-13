package loon.action.sprite;

import loon.LTexture;
import loon.Sound;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class SpriteButton extends Entity {

	public interface ButtonFunc {
		public void func(ButtonFunc b);
	}

	protected RectBox rectSrc = null;

	protected String clickse;

	protected String storage;

	protected String target;

	protected boolean countpage;

	protected String exp;

	public ButtonFunc func = null;

	protected boolean downed = false;

	protected Sound sound;

	private int width, height;

	public SpriteButton(LTexture tex, float scale, int width, int height,
			String clickse, Sound sound, String storage, String target,
			boolean countpage, String exp, ButtonFunc func, int size) {
		this.setTexture(tex);
		this.setRepaint(true);
		this.sound = sound;
		if (tex == null) {
			rectSrc = new RectBox(0, 0, width, height);
		} else {
			rectSrc = new RectBox(0, 0, tex.getWidth() / size, tex.getHeight());
		}
		this.width = width;
		this.height = height;
		this.setPos(0, 0);
		this.clickse = clickse;
		this.storage = storage;
		this.target = target;
		this.countpage = countpage;
		this.exp = exp;
		this.func = func;
	}

	static protected final boolean inside(RectBox rect, int x, int y) {
		if (rect != null) {
			if (rect.Left() <= x && x < rect.Right() && rect.Top() <= y
					&& y < rect.Bottom())
				return true;
		}
		return false;
	}

	public void onDown(int x, int y) {
		downed = true;
		if (inside(getCollisionBox(), x, y)) {
			on();
		} else {
			off();
		}
	}

	public void onMove(int x, int y) {
		if (inside(getCollisionBox(), x, y))
			on();
		else
			off();
	}

	public boolean onUp(int x, int y) {
		if (downed && inside(getCollisionBox(), x, y)) {
			if (clickse != null && sound != null) {
				sound.play();
			}
			if (func != null) {
				func.func(func);
				off();
				return true;
			} else if (storage != null || target != null) {
				off();
				return true;
			}
			return false;
		} else {
			off();
			return false;
		}
	}

	public void on() {
		if (_image != null) {
			float width = _image.getWidth();
			rectSrc.setBounds(width / 2, 0, width, _image.getHeight());
		} else {
			float width = getWidth();
			rectSrc.setBounds(width / 2, 0, width, getHeight());
		}
	}

	public void off() {
		if (_image != null) {
			float width = _image.getWidth();
			rectSrc.setBounds(0, 0, width / 2, _image.getHeight());
		} else {
			float width = getWidth();
			rectSrc.setBounds(0, 0, width / 2, getHeight());
		}
	}

	public void setPos(int x, int y) {
		setLocation(x, y);
	}

	public void setFunction(ButtonFunc func) {
		this.func = func;
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (_image == null) {
			g.fillRect(x() + offsetX, y() + offsetY, width, height, _baseColor);
		} else {
			g.draw(_image, x() + offsetX, y() + offsetY, width, height,
					rectSrc.x, rectSrc.y, rectSrc.width, rectSrc.height,
					_baseColor);
		}
	}

}

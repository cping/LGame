package loon.action.sprite;

import loon.LObject;
import loon.LTexture;
import loon.Sound;
import loon.geom.RectBox;
import loon.opengl.GLEx;

public class SpriteButton extends LObject implements ISprite {

	public interface ButtonFunc {
		public void func(ButtonFunc b);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public boolean visible = false;

	protected LTexture image = null;

	protected RectBox rectSrc = null;

	protected String clickse;

	protected String storage;

	protected String target;

	protected boolean countpage;

	protected String exp;

	public ButtonFunc func = null;

	protected boolean downed = false;

	protected float scale = 1;

	protected Sound sound;

	private int width, height;

	public SpriteButton(LTexture texture, float scale, int width, int height,
			String clickse, Sound sound, String storage, String target,
			boolean countpage, String exp, ButtonFunc func, int size) {
		this.image = texture;
		this.scale = scale;
		this.sound = sound;
		if (image == null) {
			rectSrc = new RectBox(0, 0, width, height);
		} else {
			rectSrc = new RectBox(0, 0, image.getWidth() / size,
					image.getHeight());
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

	public void clear() {
		if (image != null) {
			image.close();
			image = null;
		}
	}

	public void on() {
		int width = image.getWidth();
		rectSrc.setBounds(width / 2, 0, width, image.getHeight());
	}

	public void off() {
		int width = image.getWidth();
		rectSrc.setBounds(0, 0, width / 2, image.getHeight());
	}

	public void setPos(int x, int y) {
		setLocation(x, y);
	}

	public void setFunction(ButtonFunc func) {
		this.func = func;
	}

	@Override
	public void close() {
		if (image != null) {
			image.close();
			image = null;
		}
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return this.visible;
	}

	@Override
	public void createUI(GLEx g) {
		if (visible) {
			float old = getAlpha();
			if (alpha > 0f && alpha < 1f) {
				setAlpha(alpha);
			}
			g.draw(image, x(), y(), width * scale, height * scale, rectSrc.x,
					rectSrc.y, rectSrc.width, rectSrc.height);
			if (alpha != 1f) {
				setAlpha(old);
			}
		}
	}

	@Override
	public void update(long elapsedTime) {

	}

	@Override
	public RectBox getCollisionBox() {
		return super.getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return image;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

}

package loon.action.sprite.effect;

import loon.LObject;
import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 0.3.2起新增类，百叶窗特效 0--竖屏,1--横屏
 */
public class CrossEffect extends LObject implements BaseEffect, ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int width, height;

	private boolean visible, completed;

	private LTexture otexture, ntexture;

	private LTimer timer;

	private int count, code;

	private int maxcount = 16;

	private int part;

	private int left;

	private int right;

	private LTexture tmp;

	public CrossEffect(int c, String fileName) {
		this(c, LTextures.loadTexture(fileName));
	}

	public CrossEffect(int c, String file1, String file2) {
		this(c, LTextures.loadTexture(file1), LTextures.loadTexture(file2));
	}

	public CrossEffect(int c, LTexture o) {
		this(c, o, null);
	}

	public CrossEffect(int c, LTexture o, LTexture n) {
		this.code = c;
		this.otexture = o;
		this.ntexture = n;
		this.width = (int) o.width();
		this.height = (int) o.height();
		if (width > height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.timer = new LTimer(160);
		this.visible = true;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public boolean isCompleted() {
		return completed;
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
	public void update(long elapsedTime) {
		if (completed) {
			return;
		}
		if (this.count > this.maxcount) {
			this.completed = true;
		}
		if (timer.action(elapsedTime)) {
			count++;
		}
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		float old = g.alpha();
		try {
			if (completed) {
				if (ntexture != null) {
					if (_alpha > 0 && _alpha < 1) {
						g.setAlpha(_alpha);
					}
					g.draw(ntexture, x(), y());
				}
				return;
			}
			if (_alpha > 0 && _alpha < 1) {
				g.setAlpha(_alpha);
			}
			part = 0;
			left = 0;
			right = 0;
			tmp = null;
			switch (code) {
			default:
				part = width / this.maxcount / 2;
				for (int i = 0; i <= this.maxcount; i++) {
					if (i <= this.count) {
						tmp = this.ntexture;
						if (tmp == null) {
							continue;
						}
					} else {
						tmp = this.otexture;
					}
					left = i * 2 * part;
					right = width - ((i + 1) * 2 - 1) * part;
					g.draw(tmp, x() + left, y(), part, height, left, 0, left
							+ part, height);
					g.draw(tmp, x() + right, y(), part, height, right, 0, right
							+ part, height);
				}
				break;
			case 1:
				part = height / this.maxcount / 2;
				for (int i = 0; i <= this.maxcount; i++) {
					if (i <= this.count) {
						tmp = this.ntexture;
						if (tmp == null) {
							continue;
						}
					} else {
						tmp = this.otexture;
					}
					int up = i * 2 * part;
					int down = height - ((i + 1) * 2 - 1) * part;
					g.draw(tmp, 0, up, width, part, 0, up, width, up + part);
					g.draw(tmp, 0, down, width, part, 0, down, width, down
							+ part);
				}
				break;
			}
		} finally {
			g.setAlpha(old);
		}

	}

	public void reset() {
		this.completed = false;
		this.count = 0;
	}

	@Override
	public LTexture getBitmap() {
		return otexture;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	@Override
	public void close() {

		visible = false;
		completed = true;

		if (otexture != null) {
			otexture.close();
			otexture = null;
		}
		if (ntexture != null) {
			ntexture.close();
			ntexture = null;
		}

	}

}

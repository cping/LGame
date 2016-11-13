package loon.action.sprite.effect;

import loon.LTexture;
import loon.LTextures;
import loon.action.sprite.Entity;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 0.3.2起新增类，百叶窗特效 0--竖屏,1--横屏
 */
public class CrossEffect extends Entity implements BaseEffect {

	private boolean completed;

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
		_width = (int) o.width();
		_height = (int) o.height();
		if (_width > _height) {
			maxcount = 16;
		} else {
			maxcount = 8;
		}
		this.timer = new LTimer(160);
		this.setRepaint(true);
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
	public void onUpdate(long elapsedTime) {
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
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completed) {
			if (ntexture != null) {
				g.draw(ntexture, x() + offsetX, y() + offsetY);
			}
			return;
		}
		part = 0;
		left = 0;
		right = 0;
		tmp = null;
		switch (code) {
		default:
			part = (int) (_width / this.maxcount / 2);
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
				right = (int) (_width - ((i + 1) * 2 - 1) * part);
				g.draw(tmp, x() + left + offsetX, y() + offsetY, part, _height,
						left, 0, left + part, _height);
				g.draw(tmp, x() + right + offsetX, y() + offsetY, part,
						_height, right, 0, right + part, _height);
			}
			break;
		case 1:
			part = (int) (_height / this.maxcount / 2);
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
				int down = (int) (_height - ((i + 1) * 2 - 1) * part);
				g.draw(tmp, offsetX, up, _width, part, 0, up, _width, up + part);
				g.draw(tmp, offsetY, down, _width, part, 0, down, _width, down
						+ part);
			}
			break;
		}

	}

	public void reset() {
		super.reset();
		this.completed = false;
		this.count = 0;
	}

	@Override
	public LTexture getBitmap() {
		return otexture;
	}

	public int getMaxCount() {
		return maxcount;
	}

	public void setMaxCount(int maxcount) {
		this.maxcount = maxcount;
	}

	@Override
	public void close() {

		super.close();
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

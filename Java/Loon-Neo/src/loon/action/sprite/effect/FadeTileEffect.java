package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

public class FadeTileEffect extends LObject<ISprite> implements BaseEffect,
		ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final int twidth = 64;
	private final int hwidth = 32;

	private int width, height;

	private boolean visible, completed;

	private int count;

	private int speed = 1;

	private int tmpflag = 0;

	private boolean[][] conversions;
	private boolean[][] temp;

	private boolean usefore = false;

	private LColor back = LColor.black;
	private LColor fore = LColor.white;

	private int type;

	private LTimer timer;

	public FadeTileEffect(int type, LColor c) {
		this(type, 1, 1, c, LColor.white);
	}

	public FadeTileEffect(int type) {
		this(type, 1, 1, LColor.black, LColor.white);
	}

	public FadeTileEffect(int type, int count, int speed, LColor back,
			LColor fore) {
		this.type = type;
		this.count = count;
		this.speed = speed;
		this.timer = new LTimer(60);
		this.width = ((LSystem.viewSize.getWidth() / twidth)) + 1;
		this.height = ((LSystem.viewSize.getHeight() / hwidth)) + 1;
		this.conversions = new boolean[width][height];
		this.temp = new boolean[width][height];
		this.back = back;
		this.fore = fore;
		this.visible = true;
		this.reset();
	}

	private boolean filledObject(int x, int y) {
		if (x > 0) {
			if (conversions[x - 1][y]) {
				return true;
			}
		} else if (x < width - 1) {
			if (conversions[x + 1][y]) {
				return true;
			}
		} else if (y > 0) {
			if (conversions[x][y - 1]) {
				return true;
			}
		} else if (y < height - 1) {
			if (conversions[x][y + 1]) {
				return true;
			}
		}
		return false;
	}

	public float getDelay() {
		return timer.getDelay();
	}

	public void setDelay(int delay) {
		timer.setDelay(delay);
	}

	public boolean isCompleted() {
		return completed;
	}

	@Override
	public float getHeight() {
		return getContainerWidth();
	}

	@Override
	public float getWidth() {
		return getContainerHeight();
	}

	@Override
	public void update(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			int count = 0;
			if (ISprite.TYPE_FADE_OUT == type) {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							temp[x][y] = false;
						}
					}
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							if (!temp[x][y] && conversions[x][y]) {
								temp[x][y] = true;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x - 1][y]) {
										conversions[x - 1][y] = true;
										temp[x - 1][y] = true;
									}
								}
								if (x < width - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x + 1][y]) {
										conversions[x + 1][y] = true;
										temp[x + 1][y] = true;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y - 1]) {
										conversions[x][y - 1] = true;
										temp[x][y - 1] = true;
									}
								}
								if (y < height - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (!conversions[x][y + 1]) {
										conversions[x][y + 1] = true;
										temp[x][y + 1] = true;
									}
								}

							}
						}
					}
				}

				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (!conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (count == 0) {
					completed = true;
				}
			} else {
				for (int i = 0; i < speed; i++) {
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							temp[x][y] = true;
						}
					}
					for (int x = 0; x < width; x++) {
						for (int y = 0; y < height; y++) {
							if (temp[x][y] && !conversions[x][y]) {
								temp[x][y] = false;
								if (x > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x - 1][y]) {
										conversions[x - 1][y] = false;
										temp[x - 1][y] = false;
									}
								}
								if (x < width - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x + 1][y]) {
										conversions[x + 1][y] = false;
										temp[x + 1][y] = false;
									}
								}
								if (y > 0 && !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y - 1]) {
										conversions[x][y - 1] = false;
										temp[x][y - 1] = false;
									}
								}
								if (y < height - 1
										&& !(MathUtils.random(1, 2) == 1)) {
									if (conversions[x][y + 1]) {
										conversions[x][y + 1] = false;
										temp[x][y + 1] = false;
									}
								}

							}
						}
					}
				}
				for (int x = 0; x < width; x++) {
					for (int y = 0; y < height; y++) {
						if (!conversions[x][y]) {
							count++;
							break;
						}
					}
				}
				if (tmpflag >= height) {
					completed = true;
				}
				if (count >= width) {
					tmpflag++;
				}

			}
		}
	}

	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (completed) {
			return;
		}
		if (!visible) {
			return;
		}
		int tmp = g.color();
		g.setColor(back);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if (usefore) {
					if (conversions[x][y]) {
						g.setColor(back);
						g.fillRect((x * twidth) + offsetX, (y * hwidth)
								+ offsetY, twidth, hwidth);
					} else if (!conversions[x][y] && filledObject(x, y)) {
						g.setColor(fore);
						g.fillRect((x * twidth) + offsetX, (y * hwidth)
								+ offsetY, twidth, hwidth);
					}
				} else {
					if (conversions[x][y]) {
						g.fillRect((x * twidth) + offsetX, (y * hwidth)
								+ offsetY, twidth, hwidth);
					}
				}
			}
		}
		g.setColor(tmp);
	}

	public void reset() {
		this.completed = false;
		this.tmpflag = 0;
		if (ISprite.TYPE_FADE_OUT == type) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					conversions[x][y] = false;
					temp[x][y] = false;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, width) - 1][MathUtils.random(1,
						height) - 1] = true;
			}
		} else {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					conversions[x][y] = true;
					temp[x][y] = true;
				}
			}
			for (int i = 0; i < count; i++) {
				conversions[MathUtils.random(1, width) - 1][MathUtils.random(1,
						height) - 1] = false;
			}
		}
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public int getFadeType() {
		return type;
	}

	public int getCount() {
		return count;
	}

	@Override
	public void close() {
		visible = false;
		completed = true;
		conversions = null;
		temp = null;
	}

}

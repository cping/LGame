package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.action.sprite.ISprite;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

public class FadeSpiralEffect extends LObject implements BaseEffect,ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private boolean visible;

	private boolean finished;

	private final int twidth = 64;
	private final int hwidth = 32;

	private int tilewidth;
	private int tileheight;
	private int speed;
	private int tilescovered = 0;

	private boolean[][] conversions;
	private int cx = 0;
	private int cy = 0;

	private int state = 1;

	private int type;

	private LColor color;

	private LTimer timer;

	public FadeSpiralEffect(int type) {
		this(type, 1, LColor.black);
	}

	public FadeSpiralEffect(int type, LColor c) {
		this(type, 1, c);
	}

	public FadeSpiralEffect(int type, int speed, LColor c) {
		this.type = type;
		this.speed = speed;
		this.color = c;
		this.timer = new LTimer(30);
		this.tilewidth = LSystem.viewSize.getWidth() / twidth + 1;
		this.tileheight = LSystem.viewSize.getHeight() / hwidth + 1;
		this.conversions = new boolean[tilewidth][tileheight];
		this.visible = true;
		this.reset();
	}

	public void reset() {
		if (type == ISprite.TYPE_FADE_IN) {
			for (int x = 0; x < tilewidth; x++) {
				for (int y = 0; y < tileheight; y++) {
					conversions[x][y] = true;
				}
			}
		} else {
			for (int x = 0; x < tilewidth; x++) {
				for (int y = 0; y < tileheight; y++) {
					conversions[x][y] = false;
				}
			}
		}
		state = 1;
		cx = 0;
		cy = 0;
		tilescovered = 0;
	}

	public float getDelay() {
		return timer.getDelay();
	}

	public void setDelay(int delay) {
		timer.setDelay(delay);
	}

	public boolean finished() {
		return tilescovered >= (tilewidth * tileheight);
	}

	@Override
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public void createUI(GLEx g) {
		if (!visible) {
			return;
		}
		if (finished) {
			return;
		}
		int tmp = g.color();
		g.setColor(color);
		for (int x = 0; x < tilewidth; x++) {
			for (int y = 0; y < tileheight; y++) {
				if (conversions[x][y]) {
					g.fillRect(x * twidth, y * hwidth, twidth, hwidth);
				}
			}
		}
		g.setColor(tmp);
	}

	@Override
	public RectBox getCollisionBox() {
		return getCollisionArea();
	}

	@Override
	public LTexture getBitmap() {
		return null;
	}

	public boolean isCompleted() {
		return finished;
	}

	@Override
	public void update(long elapsedTime) {
		if (finished) {
			return;
		}
		if (timer.action(elapsedTime)) {
			if (type == ISprite.TYPE_FADE_IN) {
				for (int i = 0; i < speed; i++) {
					if (conversions[cx][cy]) {
						conversions[cx][cy] = false;
						tilescovered++;
					}
					switch (state) {
					case 0:
						cy--;
						if (cy <= -1 || (!conversions[cx][cy])) {
							cy++;
							state = 2;
						}
						break;
					case 1:
						cy++;
						if (cy >= tileheight || (!conversions[cx][cy])) {
							cy--;
							state = 3;
						}
						break;
					case 2:
						cx--;
						if (cx <= -1 || (!conversions[cx][cy])) {
							cx++;
							state = 1;
						}
						break;
					case 3:
						cx++;
						if (cx >= tilewidth || (!conversions[cx][cy])) {
							cx--;
							state = 0;
						}
						break;
					}
				}
			} else {
				for (int i = 0; i < speed; i++) {
					if (!conversions[cx][cy]) {
						conversions[cx][cy] = true;
						tilescovered++;
					}
					switch (state) {
					case 0:
						cy--;
						if (cy <= -1 || (conversions[cx][cy])) {
							cy++;
							state = 2;
						}
						break;
					case 1:
						cy++;
						if (cy >= tileheight || (conversions[cx][cy])) {
							cy--;
							state = 3;
						}
						break;
					case 2:
						cx--;
						if (cx <= -1 || (conversions[cx][cy])) {
							cx++;
							state = 1;
						}
						break;
					case 3:
						cx++;
						if (cx >= tilewidth || (conversions[cx][cy])) {
							cx--;
							state = 0;
						}
						break;
					}
				}
			}
			if (finished()) {
				finished = true;
			}
		}
	}

	@Override
	public int getWidth() {
		return getContainerWidth();
	}

	@Override
	public int getHeight() {
		return getContainerHeight();
	}

	@Override
	public void close() {
		visible = false;
		finished = true;
		conversions = null;
	}
}

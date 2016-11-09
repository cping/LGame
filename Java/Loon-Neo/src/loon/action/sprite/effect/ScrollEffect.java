package loon.action.sprite.effect;

import loon.LObject;
import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.sprite.ISprite;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 0.3.2版新增类，用以实现特定图像的滚动播放(循环展示)
 */
public class ScrollEffect extends LObject<ISprite> implements BaseEffect, ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int backgroundLoop;

	private int count;

	private int width, height;

	private LTexture texture;

	private boolean visible, completed;

	private LTimer timer;

	private int code;

	public ScrollEffect(String fileName) {
		this(LTextures.loadTexture(fileName));
	}

	public ScrollEffect(String fileName, RectBox rect) {
		this(Config.DOWN, LTextures.loadTexture(fileName), rect);
	}

	public ScrollEffect(LTexture tex2d) {
		this(Config.DOWN, tex2d, LSystem.viewSize.getRect());
	}

	public ScrollEffect(int d, String fileName) {
		this(d, LTextures.loadTexture(fileName));
	}

	public ScrollEffect(int d, LTexture tex2d) {
		this(d, tex2d, LSystem.viewSize.getRect());
	}

	public ScrollEffect(int d, String fileName, RectBox limit) {
		this(d, LTextures.loadTexture(fileName), limit);
	}

	public ScrollEffect(int d, LTexture tex2d, RectBox limit) {
		this(d, tex2d, limit.x, limit.y, limit.width, limit.height);
	}

	public ScrollEffect(int d, LTexture tex2d, float x, float y, int w, int h) {
		this.setLocation(x, y);
		this.texture = tex2d;
		this.width = w;
		this.height = h;
		this.count = 1;
		this.timer = new LTimer(10);
		this.visible = true;
		this.code = d;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}

	public void update(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			switch (code) {
			case Config.DOWN:
			case Config.TDOWN:
			case Config.UP:
			case Config.TUP:
				this.backgroundLoop = ((backgroundLoop + count) % height);
				break;
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TLEFT:
			case Config.TRIGHT:
				this.backgroundLoop = ((backgroundLoop + count) % width);
				break;
			}
		}
	}

	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}
	
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!visible) {
			return;
		}
		float tmp = g.alpha();
		if (_alpha > 0 && _alpha < 1) {
			g.setAlpha(_alpha);
		}
		switch (code) {
		case Config.DOWN:
		case Config.TDOWN:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.draw(texture, x() + (j * width) + offsetX, y()
							+ (i * height + backgroundLoop) + offsetY, width,
							height, 0, 0, width, height);
				}
			}
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.draw(texture, x() + (j * width + backgroundLoop)
							+ offsetX, y() + (i * height) + offsetY, width,
							height, 0, 0, width, height);
				}
			}
			break;
		case Config.UP:
		case Config.TUP:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.draw(texture, x() + (j * width) + offsetX, y()
							- (i * height + backgroundLoop) + offsetY, width,
							height, 0, 0, width, height);
				}
			}
			break;
		case Config.LEFT:
		case Config.TLEFT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.draw(texture, x() - (j * width + backgroundLoop)
							+ offsetX, y() + (i * height) + offsetY, width,
							height, 0, 0, width, height);
				}
			}
			break;
		}
		g.setAlpha(tmp);
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public LTexture getBitmap() {
		return texture;
	}

	public void setStop(boolean completed) {
		this.completed = completed;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	public RectBox getCollisionBox() {
		return getRect(x(), y(), width, height);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void close() {
		visible = false;
		completed = true;
		if (texture != null) {
			texture.close();
			texture = null;
		}
	}

}

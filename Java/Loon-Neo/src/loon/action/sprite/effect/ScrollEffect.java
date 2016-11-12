package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.LTextures;
import loon.action.map.Config;
import loon.action.sprite.Entity;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.timer.LTimer;

/**
 * 0.3.2版新增类，用以实现特定图像的滚动播放(循环展示)
 */
public class ScrollEffect extends Entity implements BaseEffect {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int backgroundLoop;

	private int count;

	private boolean completed;

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
		this.setTexture(tex2d);
		this.setRepaint(true);
		this.setSize(w, h);
		this.count = 1;
		this.timer = new LTimer(10);
		this.code = d;
	}

	public void setDelay(long delay) {
		timer.setDelay(delay);
	}

	public long getDelay() {
		return timer.getDelay();
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			switch (code) {
			case Config.DOWN:
			case Config.TDOWN:
			case Config.UP:
			case Config.TUP:
				this.backgroundLoop = (int) ((backgroundLoop + count) % _height);
				break;
			case Config.LEFT:
			case Config.RIGHT:
			case Config.TLEFT:
			case Config.TRIGHT:
				this.backgroundLoop = (int) ((backgroundLoop + count) % _width);
				break;
			}
		}
	}

	
	public void repaint(GLEx g, float offsetX, float offsetY) {
		switch (code) {
		case Config.DOWN:
		case Config.TDOWN:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.draw(_image, x() + (j * _width) + offsetX, y()
							+ (i * _height + backgroundLoop) + offsetY, _width,
							_height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.RIGHT:
		case Config.TRIGHT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.draw(_image, x() + (j * _width + backgroundLoop)
							+ offsetX, y() + (i * _height) + offsetY, _width,
							_height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.UP:
		case Config.TUP:
			for (int i = -1; i < 1; i++) {
				for (int j = 0; j < 1; j++) {
					g.draw(_image, x() + (j * _width) + offsetX, y()
							- (i * _height + backgroundLoop) + offsetY, _width,
							_height, 0, 0, _width, _height);
				}
			}
			break;
		case Config.LEFT:
		case Config.TLEFT:
			for (int j = -1; j < 1; j++) {
				for (int i = 0; i < 1; i++) {
					g.draw(_image, x() - (j * _width + backgroundLoop)
							+ offsetX, y() + (i * _height) + offsetY, _width,
							_height, 0, 0, _width, _height);
				}
			}
			break;
		}
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setStop(boolean completed) {
		this.completed = completed;
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

}

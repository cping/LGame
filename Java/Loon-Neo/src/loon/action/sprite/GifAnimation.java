package loon.action.sprite;

import loon.BaseIO;
import loon.LObject;
import loon.LTexture;
import loon.geom.Dimension;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.GifDecoder;

public class GifAnimation extends LObject<ISprite> implements ISprite {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private GifDecoder _gifDecoder;

	private int _width, _height;

	private Animation _animation;

	private boolean _visible = true;

	public GifAnimation(ArrayByte bytes) {
		loadData(bytes);
	}

	public GifAnimation(String path) {
		loadData(BaseIO.loadArrayByte(path));
	}

	public Animation loadData(ArrayByte bytes) {
		this._animation = new Animation();
		this._gifDecoder = new GifDecoder();
		this._gifDecoder.readStatus(bytes);
		Dimension d = _gifDecoder.getFrameSize();
		this._width = d.getWidth();
		this._height = d.getHeight();
		for (int i = 0; i < _gifDecoder.getFrameCount(); i++) {
			int delay = _gifDecoder.getDelay(i);
			_animation.addFrame(_gifDecoder.getFrame(i).texture(),
					delay == 0 ? 100 : delay);
		}
		return _animation;
	}

	public void setRunning(boolean runing) {
		_animation.setRunning(runing);
	}

	@Override
	public void update(long elapsedTime) {
		if (_visible) {
			_animation.update(elapsedTime);
		}
	}

	public Animation getAnimation() {
		return _animation;
	}

	@Override
	public boolean isVisible() {
		return _visible;
	}

	@Override
	public void setVisible(boolean visible) {
		this._visible = visible;
	}

	public GifDecoder getGifDecoder() {
		return _gifDecoder;
	}

	@Override
	public RectBox getCollisionBox() {
		return getRect(x(), y(), _width, _height);
	}

	@Override
	public float getWidth() {
		return _height;
	}

	@Override
	public float getHeight() {
		return _width;
	}

	@Override
	public void createUI(GLEx g) {
		createUI(g, 0, 0);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (_visible) {
			g.draw(_animation.getSpriteImage(), getX() + offsetX, getY()
					+ offsetY);
		}

	}

	@Override
	public LTexture getBitmap() {
		return _animation.getSpriteImage();
	}

	@Override
	public void close() {
		if (_gifDecoder != null) {
			_gifDecoder = null;
		}

	}

}

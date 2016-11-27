package loon.action.sprite;

import loon.BaseIO;
import loon.LTexture;
import loon.geom.Dimension;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.GifDecoder;

public class GifAnimation extends Entity {

	private GifDecoder _gifDecoder;

	private Animation _animation;

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
		setRepaint(true);
		return _animation;
	}

	public void setRunning(boolean runing) {
		_animation.setRunning(runing);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		_animation.update(elapsedTime);
		setTexture(_animation.getSpriteImage());
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		g.draw(_animation.getSpriteImage(), getX() + offsetX, getY() + offsetY);
	}

	@Override
	public LTexture getBitmap() {
		return _animation.getSpriteImage();
	}

	public Animation getAnimation() {
		return _animation;
	}

	public GifDecoder getGifDecoder() {
		return _gifDecoder;
	}

	@Override
	public void close() {
		super.close();
		if (_gifDecoder != null) {
			_gifDecoder = null;
		}
	}

}

package loon.action.avg;

import loon.LTexture;
import loon.canvas.Canvas;
import loon.canvas.LColor;
import loon.opengl.GLEx;

public class NovelLayer {

	public interface FadeInCallBack {
		public void onFinish();
	}

	public interface BlackOutCallBack {
		public void onFinish();
	}

	protected int fade_count;
	protected int blackout_count;
	protected final int FADE_MAX_COUNT = 20;
	protected final int BLACKOUT_MAX_COUNT = 20;
	protected FadeInCallBack _fadeInCallBack;
	protected BlackOutCallBack _bOutCallBack;

	protected LTexture _ibitmap;
	protected LTexture _nbitmap;

	public int display;
	public String name;
	public int alpha;

	protected int _width, _height;

	protected NovelLayer() {
		_width = _height = 0;
		fade_count = FADE_MAX_COUNT + 1;
		blackout_count = BLACKOUT_MAX_COUNT + 1;
	}

	public void draw(GLEx g) {
		_width = g.getWidth();
		_height = g.getHeight();
		if (fade_count < FADE_MAX_COUNT) {
			fade_count++;
		} else if (fade_count == FADE_MAX_COUNT) {
			if (_fadeInCallBack != null) {
				_fadeInCallBack.onFinish();
			}
			fade_count++;
			_ibitmap = _nbitmap;
			_nbitmap = null;
			blackout_count++;
		}
		if (blackout_count < BLACKOUT_MAX_COUNT) {
			blackout_count++;
		} else if (blackout_count == BLACKOUT_MAX_COUNT) {
			_ibitmap = _nbitmap;
			_nbitmap = null;
			blackout_count++;
			if (_bOutCallBack != null)
				_bOutCallBack.onFinish();

		}
	}

	public void draw(Canvas g) {
		_width = (int) g.width;
		_height = (int) g.height;
		if (fade_count < FADE_MAX_COUNT) {
			fade_count++;
		} else if (fade_count == FADE_MAX_COUNT) {
			if (_fadeInCallBack != null) {
				_fadeInCallBack.onFinish();
			}
			fade_count++;
			_ibitmap = _nbitmap;
			_nbitmap = null;
			blackout_count++;
		}
		if (blackout_count < BLACKOUT_MAX_COUNT) {
			blackout_count++;
		} else if (blackout_count == BLACKOUT_MAX_COUNT) {
			_ibitmap = _nbitmap;
			_nbitmap = null;
			blackout_count++;
			if (_bOutCallBack != null)
				_bOutCallBack.onFinish();

		}
	}

	protected void drawBitmap(GLEx g, LTexture tex, int left, int top,
			int width, int height) {

	}

	protected void drawBitmap(GLEx g, LTexture tex, int left, int top,
			int width, int height, LColor color) {

	}

	public void startFadeIn(LTexture b, FadeInCallBack cb) {
		this.fade_count = 0;
		this._nbitmap = b;
		this._fadeInCallBack = cb;
	}

	public void startBlackOut(LTexture b, BlackOutCallBack cb) {
		this.blackout_count = 0;
		this._nbitmap = b;
		this._bOutCallBack = cb;
	}

	protected boolean isFade() {
		if (fade_count <= FADE_MAX_COUNT)
			return true;
		return false;
	}

	protected boolean isBOut() {
		if (blackout_count <= BLACKOUT_MAX_COUNT) {
			return true;
		}
		return false;
	}

}

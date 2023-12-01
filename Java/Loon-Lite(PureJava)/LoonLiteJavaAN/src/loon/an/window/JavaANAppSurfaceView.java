package loon.an.window;

import android.content.Context;
import android.graphics.*;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.concurrent.atomic.AtomicBoolean;

import loon.LSystem;
import loon.an.JavaANGame;
import loon.an.JavaANImage;
import loon.an.JavaANSetting;
import loon.canvas.Image;

public class JavaANAppSurfaceView extends SurfaceView implements JavaANHolderCallback {

	private JavaANAppLoop _loop;

	private JavaANGame _game;

	private AtomicBoolean _running = new AtomicBoolean(false);

	private JavaANSetting _setting;

	private int _frameCount;

	private boolean _doubleDraw;

	private boolean _pause;

	private float _ppiX = 0f;

	private float _ppiY = 0f;

	private float _ppcX = 0f;

	private float _ppcY = 0f;

	private float _density = 1f;

	public JavaANAppSurfaceView(final Context context, final JavaANGame game) {
		super(context);
		getHolder().addCallback(this);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this._game = game;
		this._setting = (JavaANSetting) _game.setting;
		this._doubleDraw = this._setting.doubleBuffer;
		setOnKeyListener(_game.input());
		setOnTouchListener(_game.input());
		setBackgroundColor(Color.BLACK);
		setZOrderOnTop(true);
		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
	}

	public boolean isPause() {
		return _pause;
	}

	public JavaANAppSurfaceView pause() {
		return setPause(true);
	}

	public JavaANAppSurfaceView resume() {
		return setPause(false);
	}

	public JavaANAppSurfaceView setPause(boolean p) {
		this._pause = p;
		return this;
	}

	@SuppressWarnings("deprecation")
	protected void updatePpi() {
		DisplayMetrics metrics = new DisplayMetrics();

		WindowManager wm = _game.getMainPlatform().getResWindowManager();
		if (wm != null) {
			Display display = wm.getDefaultDisplay();
			if (display != null) {
				display.getMetrics(metrics);
				_ppiX = metrics.xdpi;
				_ppiY = metrics.ydpi;
				_ppcX = metrics.xdpi / 2.54f;
				_ppcY = metrics.ydpi / 2.54f;
				_density = metrics.density;
			}
		}
	}

	public JavaANAppSurfaceView start() {
		if (_running.get()) {
			return this;
		}
		this._loop = new JavaANAppLoop(_game, this, _setting.fps);
		this._running.set(true);
		this._loop.start();
		return this;
	}

	public JavaANAppSurfaceView stop() {
		if (!_running.get()) {
			return this;
		}
		_running.set(false);
		return this;
	}

	@Override
	public JavaANLoop set(boolean r) {
		_running.set(r);
		return this;
	}

	public JavaANGame getGame() {
		return _game;
	}

	public int getFrameCount() {
		return this._frameCount;
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		updatePpi();
		start();
		if (_game != null) {
			LSystem.resetTextureRes(_game);
			if (_game.display() != null) {
				_game.display().GL().update();
			}
			LSystem.d("Created Renderer View");
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width, int height) {
		updatePpi();
		if (_game != null) {
			_game.graphics().onSizeChanged(width, height);
			if (!_running.get()) {
				start();
			}
			_pause = false;
			LSystem.d("Update Renderer View");
		}
	}

	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		stop();
	}

	public Bitmap snap() {
		Bitmap bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		android.view.ViewGroup.LayoutParams layouts = getLayoutParams();
		layout(0, 0, layouts.width, layouts.height);
		draw(canvas);
		return bmp;
	}

	@Override
	public boolean get() {
		return _running.get();
	}

	public float getPpiX() {
		return _ppiX;
	}

	public float getPpiY() {
		return _ppiY;
	}

	public float getPpcX() {
		return _ppcX;
	}

	public float getPpcY() {
		return _ppcY;
	}

	public float getDensity() {
		return _density;
	}

	@Override
	public void process(final boolean active) {
		if (_pause) {
			return;
		}
		SurfaceHolder holder = getHolder();
		synchronized (holder) {
			Canvas canvas = holder.lockCanvas();
			if (canvas != null) {
				if (_doubleDraw) {
					_game.process(active);
					Image img = _game.getCanvas().getImage();
					if (img != null) {
						canvas.drawBitmap(((JavaANImage) img).anImage(), 0, 0, null);
					}
				} else {
					_game.getCanvas().updateContext(canvas);
					_game.process(active);
				}
			}
			holder.unlockCanvasAndPost(canvas);
		}
		this._frameCount++;
	}

	public void onPause() {
		this.pause();
	}

	public void onResume() {
		this.resume();
	}

	@Override
	public void close() {
		stop();
		if (_game != null) {
			_game.shutdown();
		}
	}

}

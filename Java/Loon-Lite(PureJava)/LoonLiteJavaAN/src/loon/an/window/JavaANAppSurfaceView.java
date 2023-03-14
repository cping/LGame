package loon.an.window;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.util.concurrent.atomic.AtomicBoolean;

import loon.LRelease;
import loon.an.JavaANCanvas;
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

    public JavaANAppSurfaceView(final Context context, final JavaANGame game) {
        super(context);
        getHolder().addCallback(this);
        _game = game;
        this._setting = (JavaANSetting) _game.setting;
        this._doubleDraw = this._setting.doubleBuffer;
        _loop = new JavaANAppLoop(_game, this, _setting.fps);
        setBackgroundColor(Color.BLACK);
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

    public JavaANAppSurfaceView start() {
        if (_running.get()) {
            return this;
        }
        _running.set(true);
        _loop.start();
        return this;
    }

    public JavaANAppSurfaceView stop() {
        if (!_running.get()) {
            return this;
        }
        _running.set(false);
        _loop.terminate();
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
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        start();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int format, int width, int height) {
        _game.graphics().onSizeChanged(width, height);
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        close();
    }

    @Override
    public boolean get() {
        return _running.get();
    }

    @Override
    public void process(final boolean active) {
        if (_pause) {
            return;
        }
        SurfaceHolder holder = getHolder();
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
        this._frameCount++;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return _game.input().onTouch(event);
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

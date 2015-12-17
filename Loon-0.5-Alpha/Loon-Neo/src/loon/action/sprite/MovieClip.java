package loon.action.sprite;

import loon.LRelease;
import loon.LSystem;
import loon.canvas.LColor;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.utils.res.TextureData;
import loon.utils.res.SpriteSheet;

public class MovieClip extends DisplayObject implements LRelease {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private TextureData _ssd = null;

	private SpriteSheet _sheet = null;

	private LColor color = new LColor(LColor.white);

	private int _interval = 0;

	private int _playIndex = 0;

	private long _changeTime = 0;

	private boolean _isStop = false;

	private boolean _isLoop = false;

	public boolean isLoop() {
		return _isLoop;
	}

	public void setIsLoop(boolean v) {
		if (_isLoop != v) {
			_isLoop = v;
		}
	}

	private boolean _autoDispose = false;

	public boolean autoDispsoe() {
		return _autoDispose;
	}

	public void setAutoDispose(boolean v) {
		if (_autoDispose != v) {
			_autoDispose = v;
		}
	}

	public MovieClip(SpriteSheet sheet, int interval) {
		init(sheet, interval, DisplayObject.ANCHOR_TOP_LEFT);
	}

	public MovieClip(SpriteSheet sheet, int interval, int anchor) {
		init(sheet, interval, anchor);
	}

	private void init(SpriteSheet sheet, int interval, int anchor) {
		_sheet = sheet;
		_interval = interval;
		setAnchor(anchor);
	}

	public void nextFrame() {
		_playIndex++;
		if (_playIndex == _sheet.datas().length) {
			this.dispatchEvent(EventType.EVENT_MOVIE_CLIP_RESTART);

			if (_autoDispose) {
				if (this.getParent() != null
						&& (this.getParent() instanceof MovieSprite)) {
					((MovieSprite) this.getParent()).removeChild(this);
				}
				return;
			}

			if (!_isLoop) {
				_isStop = true;
				return;
			}

			_playIndex = 0;

		}

		TextureData ssd = _sheet.datas()[_playIndex];
		setSSD(ssd);
	}

	private void setSSD(TextureData ssd) {
		_ssd = ssd;
		_width = ssd.sourceW();
		_height = ssd.sourceH();
	}

	@Override
	protected void enterFrame(long time) {
		if (time >= _changeTime && false == _isStop) {
			_changeTime = time + _interval;
			nextFrame();
		}
	}

	public int currentFrame() {
		return _playIndex;
	}

	public String currentLabel() {
		if (_ssd != null) {
			return _ssd.name();
		}
		return null;
	}

	public void gotoAndStop(String label) {
		if (null != _sheet) {
			TextureData ssd = _sheet.getSSD(label);
			if (ssd != null) {
				setSSD(ssd);
				_isStop = true;
			}
		}
	}

	public void gotoAndStop(int frame) {
		if (null != _sheet && frame >= 0 && frame < _sheet.datas().length) {
			TextureData ssd = _sheet.datas()[frame];
			setSSD(ssd);
			_isStop = true;
		}
	}

	public void gotoAndPlay(String label) {
		if (null != _sheet) {
			TextureData ssd = _sheet.getSSD(label);
			if (ssd != null) {
				setSSD(ssd);
				_isStop = false;
			}
		}
	}

	public void gotoAndPlay(int frame) {
		if (null != _sheet && frame >= 0 && frame < _sheet.datas().length) {
			TextureData ssd = _sheet.datas()[frame];
			setSSD(ssd);
			_isStop = false;
		}
	}

	public void play() {
		_isStop = true;
	}

	public void stop() {
		_isStop = false;
	}

	protected void onScaleChange(float scaleX, float scaleY) {
		setScale(scaleX, scaleY);
	}

	@Override
	protected void addedToStage() {
		if (LSystem.getProcess() != null
				&& LSystem.getProcess().getScreen() != null) {
			LSystem.getProcess().getScreen().add(this);
		}
	}

	@Override
	protected void removedFromStage() {
		if (LSystem.getProcess() != null
				&& LSystem.getProcess().getScreen() != null) {
			LSystem.getProcess().getScreen().remove(this);
		}
	}

	@Override
	public void update(long elapsedTime) {
       enterFrame(elapsedTime);
	}

	private RectBox tempRect;

	@Override
	public void createUI(GLEx g) {
		if (!_visible) {
			return;
		}
		if (_alpha <= 0.01f) {
			return;
		}
		if (_ssd == null) {
			return;
		}
		float x = _location.x + _ssd.offX();
		float y = _location.y + _ssd.offY();
		if (_anchor == DisplayObject.ANCHOR_CENTER) {
			x -= _ssd.sourceW() >> 1;
			y -= _ssd.sourceH() >> 1;
		} else {
			x -= _anchorX;
			y -= _anchorY;
		}
		PointF p = local2Global(x, y);
		if (tempRect == null) {
			tempRect = new RectBox(p.x, p.y, _ssd.w(), _ssd.h());
		} else {
			tempRect.setBounds(p.x, p.y, _ssd.w(), _ssd.h());
		}
		RectBox rect = null;
		if (LSystem.getProcess() != null
				&& LSystem.getProcess().getScreen() != null) {
			rect = LSystem.getProcess().getScreen().getBox();
		} else {
			rect = LSystem.viewSize.getRect();
		}
		RectBox drawRect = RectBox.getIntersection(tempRect, rect);
		if (drawRect != null) {
			int clipX = (int) (_ssd.x() + drawRect.x - p.x);
			int clipY = (int) (_ssd.y() + drawRect.y - p.y);
			int destX = (int) (drawRect.x() * DisplayObject.morphX);
			int destY = (int) (drawRect.y() * DisplayObject.morphY);
			g.drawRegion(_sheet.sheet(), clipX, clipY, drawRect.width,
					drawRect.height, _trans, destX, destY, 0, color);
		}

	}

	public LColor getColor() {
		return color;
	}

	public void setColor(LColor color) {
		this.color = color;
	}

	@Override
	public void close() {

	}

}

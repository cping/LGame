/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * 
 * @project loon
 * @author cping
 * @email：javachenpeng@yahoo.com
 * @version 0.5
 */
package loon.action.sprite;

import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.LTrans;
import loon.PlayerUtils;
import loon.action.ActionTween;
import loon.geom.PointF;
import loon.geom.RectBox;
import loon.opengl.GLEx;
import loon.opengl.GLEx.Direction;
import loon.utils.res.TextureData;
import loon.utils.res.MovieSpriteSheet;
import loon.utils.timer.LTimer;

/**
 * as中同名对象的仿写,干什么用大家都懂……
 */
public class MovieClip extends DisplayObject {

	private boolean _autoDispose = false;

	private TextureData _ssd = null;

	private MovieSpriteSheet _sheet = null;

	private int _playIndex = 0;

	private LTimer _delay;

	private boolean _isStop = false;

	private boolean _isLoop = false;

	private RectBox _tempRect;

	public MovieClip(MovieSpriteSheet sheet, int interval) {
		init(sheet, interval, DisplayObject.ANCHOR_TOP_LEFT);
	}

	public MovieClip(MovieSpriteSheet sheet, int interval, int anchor) {
		init(sheet, interval, anchor);
	}

	private void init(MovieSpriteSheet sheet, int interval, int anchor) {
		_sheet = sheet;
		_delay = new LTimer(interval);
		setAnchor(anchor);
	}

	public MovieClip nextFrame() {
		_playIndex++;
		if (_playIndex == _sheet.datas().length) {
			this.dispatchEvent(EventType.EVENT_MOVIE_CLIP_RESTART);

			if (_autoDispose) {
				if (this.getParent() != null && (this.getParent() instanceof MovieSprite)) {
					((MovieSprite) this.getParent()).removeChild(this);
				}
				return this;
			}

			if (!_isLoop) {
				_isStop = true;
				return this;
			}

			_playIndex = 0;

		}

		TextureData ssd = _sheet.datas()[_playIndex];
		setSSD(ssd);
		return this;
	}

	private void setSSD(TextureData ssd) {
		_ssd = ssd;
		_width = ssd.sourceW();
		_height = ssd.sourceH();
	}

	@Override
	protected void enterFrame(long time) {
		if (_delay.action(time) && !_isStop) {
			nextFrame();
		}
	}

	public MovieClip reset() {
		_isStop = false;
		_delay.refresh();
		return this;
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

	public MovieClip gotoAndStop(String label) {
		if (null != _sheet) {
			TextureData ssd = _sheet.getSSD(label);
			if (ssd != null) {
				setSSD(ssd);
				_isStop = true;
			}
		}
		return this;
	}

	public MovieClip gotoAndStop(int frame) {
		if (null != _sheet && frame >= 0 && frame < _sheet.datas().length) {
			TextureData ssd = _sheet.datas()[frame];
			setSSD(ssd);
			_isStop = true;
		}
		return this;
	}

	public MovieClip gotoAndPlay(String label) {
		if (null != _sheet) {
			TextureData ssd = _sheet.getSSD(label);
			if (ssd != null) {
				setSSD(ssd);
				_isStop = false;
			}
		}
		return this;
	}

	public MovieClip gotoAndPlay(int frame) {
		if (null != _sheet && frame >= 0 && frame < _sheet.datas().length) {
			TextureData ssd = _sheet.datas()[frame];
			setSSD(ssd);
			_isStop = false;
		}
		return this;
	}

	public MovieClip play() {
		_isStop = true;
		return this;
	}

	public MovieClip stop() {
		_isStop = false;
		return this;
	}

	@Override
	protected void onScaleChange(float scaleX, float scaleY) {
		setScale(scaleX, scaleY);
	}

	@Override
	protected void addedToStage() {
		if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
			LSystem.getProcess().getScreen().add(this);
		}
	}

	@Override
	protected void removedFromStage() {
		if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
			LSystem.getProcess().getScreen().remove(this);
		} else if (_sprites != null) {
			_sprites.remove(this);
		}
	}

	@Override
	public void update(long elapsedTime) {
		enterFrame(elapsedTime);
	}

	@Override
	public void createUI(GLEx g) {
		this.createUI(g, 0f, 0f);
	}

	@Override
	public void createUI(GLEx g, float offsetX, float offsetY) {
		if (!_visible) {
			return;
		}
		if (_objectAlpha < 0.01f) {
			return;
		}
		if (_ssd == null) {
			return;
		}
		float x = _objectLocation.x + _ssd.offX() + offsetX + _offset.x;
		float y = _objectLocation.y + _ssd.offY() + offsetY + _offset.y;
		if (_anchor == DisplayObject.ANCHOR_CENTER) {
			x -= _ssd.sourceW() >> 1;
			y -= _ssd.sourceH() >> 1;
		}
		PointF p = local2Global(x, y);
		if (_tempRect == null) {
			_tempRect = new RectBox(p.x, p.y, _width, _height);
		} else {
			_tempRect.setBounds(p.x, p.y, _width, _height);
		}
		RectBox rect = null;
		if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
			rect = LSystem.getProcess().getScreen().getBox();
		} else {
			rect = LSystem.viewSize.getRect();
		}
		RectBox drawRect = RectBox.getIntersection(_tempRect, rect);
		if (drawRect != null) {
			int destX = (int) (drawRect.x() * _morphX);
			int destY = (int) (drawRect.y() * _morphY);

			float rotate = 0;
			Direction dir = Direction.TRANS_NONE;

			switch (_trans) {
			case LTrans.TRANS_NONE: {
				break;
			}
			case LTrans.TRANS_ROT90: {
				rotate = 90;
				break;
			}
			case LTrans.TRANS_ROT180: {
				rotate = 180;
				break;
			}
			case LTrans.TRANS_ROT270: {
				rotate = 270;
				break;
			}
			case LTrans.TRANS_MIRROR: {
				dir = Direction.TRANS_MIRROR;
				break;
			}
			case LTrans.TRANS_MIRROR_ROT90: {
				dir = Direction.TRANS_MIRROR;
				rotate = -90;
				break;
			}
			case LTrans.TRANS_MIRROR_ROT180: {
				dir = Direction.TRANS_MIRROR;
				rotate = -180;
				break;
			}
			case LTrans.TRANS_MIRROR_ROT270: {
				dir = Direction.TRANS_MIRROR;
				rotate = -270;
				break;
			}
			default:
				throw new LSysException("Bad transform");
			}
			float r = rotate + _objectRotation;
			if (r > 360) {
				rotate = r - 360;
			} else {
				rotate += _objectRotation;
			}
			g.draw(_sheet.sheet(), destX, destY, drawRect.width, drawRect.height, _ssd.x(), _ssd.y(), _ssd.w(),
					_ssd.h(), _baseColor, rotate, _scaleX, _scaleY, _anchorValue, _pivotValue, dir);

		}

	}

	@Override
	public LTexture getBitmap() {
		return _sheet.sheet();
	}
	
	@Override
	public ActionTween selfAction() {
		return PlayerUtils.set(this);
	}

	@Override
	public boolean isActionCompleted() {
		return PlayerUtils.isActionCompleted(this);
	}

	public boolean isLoop() {
		return _isLoop;
	}

	public MovieClip setLoop(boolean v) {
		if (_isLoop != v) {
			_isLoop = v;
		}
		return this;
	}

	public boolean autoDispsoe() {
		return _autoDispose;
	}

	public MovieClip setAutoDispose(boolean v) {
		if (_autoDispose != v) {
			_autoDispose = v;
		}
		return this;
	}

	public boolean isClosed() {
		return isDisposed();
	}

	@Override
	public void close() {
		if (_sheet != null) {
			_sheet.close();
		}
		setState(State.DISPOSED);
		removeActionEvents(this);
	}

}

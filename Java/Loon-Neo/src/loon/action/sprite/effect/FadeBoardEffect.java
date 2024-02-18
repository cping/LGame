/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.action.sprite.effect;

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;
import loon.utils.timer.Duration;

/**
 * 瓦片(从左向右逐渐减少或增加)淡入淡出效果
 */
public class FadeBoardEffect extends BaseAbstractEffect {

	public final static int START_LEFT = 0;

	public final static int START_RIGHT = 1;

	private static class Block {

		float _x, _y, _width, _height;

		float _scaleX, _scaleY;

		float _deltaScaleX, _deltaScaleY;

		float _currentScaleX, _currentScaleY;

		float _angle;

		float _delayTimer;

		float _delayCount;

		float _alpha;

		float _currentDelta;

		boolean _finished;

		FadeBoardEffect _effect;

		Block(FadeBoardEffect effect, float x, float y, float width, float height, float angle, float scaleX,
				float scaleY, float alpha, float delay) {
			this._effect = effect;
			this._x = x;
			this._y = y;
			this._width = width;
			this._height = height;
			this._angle = angle;
			this._scaleX = scaleX;
			this._scaleY = scaleY;
			if (_effect.fadeType == TYPE_FADE_OUT) {
				this._deltaScaleX = _effect.targetScaleX - _scaleX;
				this._deltaScaleY = _effect.targetScaleY - _scaleY;
			} else {
				this._deltaScaleX = _effect.targetScaleX + _scaleX;
				this._deltaScaleY = _effect.targetScaleY + _scaleY;
				this._currentScaleX = scaleX;
				this._currentScaleY = scaleY;
			}
			this._alpha = alpha;
			this._delayTimer = delay;
		}

		boolean completed() {
			return _finished;
		}

		void update(long elapsedTime) {
			if (_finished) {
				return;
			}
			_delayCount += elapsedTime;
			if (_delayCount >= _delayTimer) {
				_currentDelta += MathUtils.max(Duration.toS(elapsedTime), LSystem.MIN_SECONE_SPEED_FIXED);
				float delta = MathUtils.sin(_currentDelta / _effect.blocDuration * 1.5707964f);
				if (_effect.fadeType == TYPE_FADE_OUT) {
					_angle += (delta * 100f);
					_alpha += delta;
					if (_alpha > 1f) {
						_alpha = 1f;
					}
					_currentScaleX += (_scaleX + (_deltaScaleX * delta));
					_currentScaleY += (_scaleY + (_deltaScaleY * delta));
					if (_currentScaleX > _effect.targetScaleX) {
						_currentScaleX = _effect.targetScaleX;
					}
					if (_currentScaleY > _effect.targetScaleY) {
						_currentScaleY = _effect.targetScaleY;
					}
					if (_currentScaleX >= _effect.targetScaleX && _currentScaleY >= _effect.targetScaleY && _alpha == 1f
							&& _angle >= _effect.targetAngle) {
						_finished = true;
					}
				} else {
					_angle -= (delta * 100f);
					if (_angle < 0f) {
						_angle = 0f;
					}
					_alpha -= delta;
					if (_alpha < 0f) {
						_alpha = 0f;
					}
					_currentScaleX -= (_deltaScaleX * delta);
					_currentScaleY -= (_deltaScaleY * delta);
					if (_currentScaleX < 0f) {
						_currentScaleX = 0f;
					}
					if (_currentScaleY < 0f) {
						_currentScaleY = 0f;
					}
					if (_currentScaleX <= 0f && _currentScaleY <= 0f && _alpha == 0f && _angle == 0f) {
						_finished = true;
					}
				}
			}
		}

		void paint(GLEx g, float offX, float offY) {
			g.draw(_effect.blockTexture, offX + _x, offY + _y, _width, _height, _effect._baseColor.setAlpha(_alpha),
					_angle, _currentScaleX, _currentScaleY, false, false);
		}

	}

	private int _startDirection = START_LEFT;

	private int countCompleted;

	private LTexture blockTexture;

	private int cellWidth, cellHeight;

	private final float targetAngle;

	private final float targetScaleX, targetScaleY;

	private TArray<Block> paintBlocks;

	private long blocDuration;

	private long blockDelay;

	private int fadeType;

	private boolean _dirty;

	public FadeBoardEffect(int model, LColor c) {
		this(model, START_LEFT, c);
	}

	public FadeBoardEffect(int model, int dir, LColor c) {
		this(model, dir, c, MathUtils.ifloor(LSystem.viewSize.getTileWidthSize()),
				MathUtils.ifloor(LSystem.viewSize.getTileHeightSize()));
	}

	public FadeBoardEffect(int model, LColor c, int cellW, int cellH) {
		this(model, START_LEFT, c, cellW, cellH);
	}

	public FadeBoardEffect(int model, int dir, LColor c, int cellW, int cellH) {
		this(model, dir, c, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), cellW, cellH);
	}

	public FadeBoardEffect(int model, LColor c, int x, int y, int width, int height, int cellW, int cellH) {
		this(model, START_LEFT, c, x, y, width, height, cellW, cellH);
	}

	public FadeBoardEffect(int model, int dir, LColor c, int x, int y, int width, int height, int cellW, int cellH) {
		this(model, dir, c, x, y, width, height, cellW, cellH, 1.5f, 1.5f, 50, 100);
	}

	public FadeBoardEffect(int model, int dir, LColor c, int x, int y, int width, int height, int cellW, int cellH,
			float sx, float sy, long delay, long duration) {
		this.setLocation(x, y);
		this.setSize(width, height);
		this.setColor(c == null ? LColor.black : c);
		this.setRepaint(true);
		this.fadeType = model;
		this.cellWidth = cellW;
		this.cellHeight = cellH;
		this.blockDelay = delay;
		this.blocDuration = duration;
		this.targetScaleX = sx;
		this.targetScaleY = sy;
		this.targetAngle = 360f;
		_startDirection = dir;
		_dirty = true;
	}

	public FadeBoardEffect pack() {
		if (_dirty || paintBlocks == null) {
			paintBlocks = createBlocks(0, 0, width(), height());
			_dirty = false;
		}
		return this;
	}

	protected TArray<Block> createBlocks(int newX, int newY, int boardWidth, int boardHeight) {
		this.blockTexture = LSystem.base().graphics().finalColorTex();
		final int blockWidth = (boardWidth / cellWidth);
		final int blockHeight = (boardHeight / cellHeight);
		final int size = blockWidth * blockHeight;
		final TArray<Block> blocks = new TArray<Block>(size);
		switch (_startDirection) {
		default:
		case START_LEFT:
			for (int x = 0; x < blockWidth; x++) {
				for (int y = 0; y < blockHeight; y++) {
					if (fadeType == TYPE_FADE_OUT) {
						blocks.add(new Block(this, newX + (x * cellWidth), newY + (y * cellHeight), cellWidth,
								cellHeight, 0f, 0f, 0f, 0f, x * blockDelay));
					} else {
						blocks.add(new Block(this, newX + (x * cellWidth), newY + (y * cellHeight), cellWidth,
								cellHeight, targetAngle, targetScaleX, targetScaleY, 1f, x * blockDelay));
					}
				}
			}
			break;
		case START_RIGHT:
			for (int x = blockWidth; x > -1; x--) {
				for (int y = blockHeight; y > -1; y--) {
					if (fadeType == TYPE_FADE_OUT) {
						blocks.add(new Block(this, newX + (boardWidth - (x * cellWidth)),
								newY + (boardHeight - (y * cellHeight)), cellWidth, cellHeight, 0f, 0f, 0f, 0f,
								x * blockDelay));
					} else {
						blocks.add(new Block(this, newX + (boardWidth - (x * cellWidth)),
								newY + (boardHeight - (y * cellHeight)), cellWidth, cellHeight, targetAngle,
								targetScaleX, targetScaleY, 1f, x * blockDelay));
					}
				}
			}
			break;
		}
		this._dirty = true;
		return blocks;
	}

	@Override
	public FadeBoardEffect setDelay(long delay) {
		blockDelay = delay;
		return this;
	}

	public long getDelay() {
		return blockDelay;
	}

	@Override
	public FadeBoardEffect setDelayS(float s) {
		return setDelay(Duration.ofS(s));
	}

	public float getDelayS() {
		return Duration.toS(blockDelay);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_dirty) {
			pack();
			return;
		}
		for (int i = 0; i < paintBlocks.size; i++) {
			Block block = paintBlocks.get(i);
			block.update(elapsedTime);
			if (block.completed()) {
				countCompleted++;
			}
		}
		if (countCompleted >= paintBlocks.size) {
			_completed = true;
		}
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		if (completedAfterBlackScreen(g, offsetX, offsetY)) {
			return;
		}
		if (fadeType == TYPE_FADE_OUT && _completed) {
			g.fillRect(drawX(offsetX), drawY(offsetY), _width, _height, _baseColor);
			return;
		}
		if (fadeType == TYPE_FADE_IN && _completed) {
			return;
		}
		if (_dirty) {
			pack();
			return;
		}
		for (int i = 0; i < paintBlocks.size; i++) {
			paintBlocks.get(i).paint(g, drawX(offsetX), drawY(offsetY));
		}
	}

	public int getStartDirection() {
		return _startDirection;
	}

	public FadeBoardEffect setStartDirection(int d) {
		this._startDirection = d;
		return this;
	}

	@Override
	public FadeBoardEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	public FadeBoardEffect reset() {
		super.reset();
		this._dirty = true;
		return this;
	}

	@Override
	public void close() {
		super.close();
		this._dirty = true;
		if (paintBlocks != null) {
			this.paintBlocks.clear();
			this.paintBlocks = null;
		}
	}

}
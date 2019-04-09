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
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;
import loon.utils.TArray;

/**
 * 瓦片(从左向右逐渐减少或增加)淡入淡出效果
 */
public class FadeBoardEffect extends Entity implements BaseEffect {

	private int countCompleted;

	private LTexture blockTexture;

	private int cellWidth, cellHeight;

	private final float targetAngle;

	private final float targetScaleX, targetScaleY;

	private TArray<Block> paintBlocks;

	private long blocDuration;

	private long blockDelay;

	private int fadeType;

	private boolean _completed;

	private boolean _dirty;

	class Block {

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

		Block(float x, float y, float width, float height, float angle, float scaleX, float scaleY, float alpha,
				float delay) {
			this._x = x;
			this._y = y;
			this._width = width;
			this._height = height;
			this._angle = angle;
			this._scaleX = scaleX;
			this._scaleY = scaleY;
			if (fadeType == TYPE_FADE_OUT) {
				this._deltaScaleX = targetScaleX - _scaleX;
				this._deltaScaleY = targetScaleY - _scaleY;
			} else {
				this._deltaScaleX = targetScaleX + _scaleX;
				this._deltaScaleY = targetScaleY + _scaleY;
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
				_currentDelta += MathUtils.max(elapsedTime / 1000f, 0.01f);
				float delta = MathUtils.sin(_currentDelta / blocDuration * 1.5707964f);
				if (fadeType == TYPE_FADE_OUT) {
					_angle += (delta * 100f);
					_alpha += delta;
					if (_alpha > 1f) {
						_alpha = 1f;
					}
					_currentScaleX += (_scaleX + (_deltaScaleX * delta));
					_currentScaleY += (_scaleY + (_deltaScaleY * delta));
					if (_currentScaleX > targetScaleX) {
						_currentScaleX = targetScaleX;
					}
					if (_currentScaleY > targetScaleY) {
						_currentScaleY = targetScaleY;
					}
					if (_currentScaleX >= targetScaleX && _currentScaleY >= targetScaleY && _alpha == 1f
							&& _angle >= targetAngle) {
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
			g.draw(blockTexture, offX + _x, offY + _y, _width, _height, _baseColor.setAlpha(_alpha), _angle,
					_currentScaleX, _currentScaleY, false, false);
		}

	}

	public FadeBoardEffect(int model, LColor c) {
		this(model, c, 32, 32);
	}

	public FadeBoardEffect(int model, LColor c, int cellW, int cellH) {
		this(model, c, 0, 0, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight(), cellW, cellH);
	}

	public FadeBoardEffect(int model, LColor c, int x, int y, int width, int height, int cellW, int cellH) {
		this(model, c, x, y, width, height, cellW, cellH, 1.5f, 1.5f, 50, 100);
	}

	public FadeBoardEffect(int model, LColor c, int x, int y, int width, int height, int cellW, int cellH, float sx,
			float sy, long delay, long duration) {
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
		_dirty = true;
	}

	public void pack() {
		if (_dirty || paintBlocks == null) {
			paintBlocks = createBlocks(0, 0, width(), height());
			_dirty = false;
		}
	}

	protected TArray<Block> createBlocks(int newX, int newY, int boardWidth, int boardHeight) {
		this.blockTexture = LSystem.base().graphics().finalColorTex();
		int blockWidth = (boardWidth / cellWidth);
		int blockHeight = (boardHeight / cellHeight);
		int size = blockWidth * blockHeight;
		TArray<Block> blocks = new TArray<Block>(size);
		for (int x = 0; x < blockWidth; x++) {
			for (int y = 0; y < blockHeight; y++) {
				if (fadeType == TYPE_FADE_OUT) {
					blocks.add(new Block(newX + (x * cellWidth), newY + (y * cellHeight), cellWidth, cellHeight, 0f, 0f,
							0f, 0f, x * blockDelay));
				} else {
					blocks.add(new Block(newX + (x * cellWidth), newY + (y * cellHeight), cellWidth, cellHeight,
							targetAngle, targetScaleX, targetScaleY, 1f, x * blockDelay));
				}
			}
		}
		this._dirty = true;
		return blocks;
	}

	public void setDelay(long delay) {
		blockDelay = delay;
	}

	public long getDelay() {
		return blockDelay;
	}

	@Override
	public boolean isCompleted() {
		return _completed;
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (_completed) {
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
		if (_completed) {
			return;
		}
		if (_dirty) {
			pack();
			return;
		}
		for (int i = 0; i < paintBlocks.size; i++) {
			paintBlocks.get(i).paint(g, offsetX + _location.x, offsetY + _location.y);
		}
	}

	@Override
	public void reset() {
		super.reset();
		this._dirty = true;
		this._completed = false;
	}

	@Override
	public void close() {
		super.close();
		this._completed = true;
		this._dirty = true;
		
		if (blockTexture != null) {
			blockTexture.close();
		}
	}

}
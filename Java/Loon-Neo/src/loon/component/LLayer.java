/**
 * Copyright 2008 - 2010
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
 * @version 0.1
 */
package loon.component;

import loon.LSystem;
import loon.LTexture;
import loon.action.map.Field2D;
import loon.action.sprite.ISprite;
import loon.action.sprite.Sprites;
import loon.canvas.Canvas;
import loon.canvas.Image;
import loon.canvas.LColor;
import loon.geom.RectBox;
import loon.geom.Vector2f;
import loon.opengl.GLEx;
import loon.utils.ArrayMap;
import loon.utils.LIterator;
import loon.utils.MathUtils;
import loon.utils.timer.LTimer;

/**
 * 一个Layer类,用于同Actor类合作渲染游戏
 */
public class LLayer extends ActorLayer {

	private Sprites _layerSprites;

	private int _lastDropX;

	private int _lastDropY;

	private float _colorAlpha;

	private float _actorX;

	private float _actorY;

	private float _actorWidth;

	private float _actorHeight;

	protected boolean _actorDrag, _pressed;

	private Actor _dragActor;

	private LTimer _currentLayerTimer = new LTimer(0);

	private Actor _thing = null;

	private boolean _currentDragging = false;

	private boolean _currentLayerListener = false;

	private boolean _currentLayerTouchClick = false;

	private boolean _currentLayerVSync = false;

	private int _paintSeq = 0;

	public LLayer(int w, int h) {
		this(0, 0, w, h);
	}

	public LLayer(int w, int h, int size) {
		this(0, 0, w, h, true, size);
	}

	public LLayer(int w, int h, boolean bounded) {
		this(0, 0, w, h, bounded);
	}

	public LLayer(int x, int y, int w, int h) {
		this(x, y, w, h, true);
	}

	public LLayer(int x, int y, int w, int h, boolean bounded) {
		this(x, y, w, h, bounded, 1);
	}

	public LLayer(int x, int y, int w, int h, boolean bounded, int size) {
		super(x, y, w, h, size, bounded);
		this.setLocation(x, y);
		this._isLimitMove = true;
		this._actorDrag = true;
		this._currentLayerVSync = true;
		this._currentLayerTouchClick = true;
		this.customRendering = true;
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(-10000);
	}

	private void allocateSprites() {
		if (_layerSprites == null) {
			this._layerSprites = new Sprites(getScreen() == null ? LSystem.getProcess().getScreen() : getScreen(),
					getWidth(), getHeight());
		}
	}

	public LLayer addSprite(ISprite s) {
		allocateSprites();
		_layerSprites.add(s);
		return this;
	}

	public LLayer addSprite(ISprite... s) {
		for (int i = 0; i < s.length; i++) {
			addSprite(s[i]);
		}
		return this;
	}

	public LLayer addSpriteAt(ISprite s, float x, float y) {
		allocateSprites();
		_layerSprites.addAt(s, x, y);
		return this;
	}

	public LLayer removeSprite(ISprite s) {
		allocateSprites();
		_layerSprites.remove(s);
		return this;
	}

	public LLayer removeSprite(int idx) {
		allocateSprites();
		_layerSprites.remove(idx);
		return this;
	}

	public LLayer removeSpriteName(String name) {
		allocateSprites();
		_layerSprites.removeName(name);
		return this;
	}

	public LLayer removeSpriteAll() {
		allocateSprites();
		_layerSprites.removeAll();
		return this;
	}

	public LLayer setVSync(boolean vsync) {
		this._currentLayerVSync = vsync;
		return this;
	}

	public boolean isVSync() {
		return _currentLayerVSync;
	}

	public void downClick(int x, int y) {
		if (_click != null) {
			_click.DownClick(this, x, y);
		}
	}

	public void upClick(int x, int y) {
		if (_click != null) {
			_click.UpClick(this, x, y);
		}
	}

	public void dragClick(int x, int y) {
		if (_click != null) {
			_click.DragClick(this, x, y);
		}
	}

	public void downKey() {
	}

	public void upKey() {
	}

	/**
	 * 设定动作触发延迟时间
	 * 
	 * @param delay
	 */
	public LLayer setDelay(long delay) {
		_currentLayerTimer.setDelay(delay);
		return this;
	}

	/**
	 * 返回动作触发延迟时间
	 * 
	 * @return
	 */
	public long getDelay() {
		return _currentLayerTimer.getDelay();
	}

	public LLayer setDelayS(float s) {
		_currentLayerTimer.setDelayS(s);
		return this;
	}

	public float getDelayS() {
		return _currentLayerTimer.getDelayS();
	}

	/**
	 * 动作处理
	 * 
	 * @param elapsedTime
	 */
	@Override
	public void action(long elapsedTime) {

	}

	@Override
	public void update(final long elapsed) {
		if (_component_visible) {
			super.update(elapsed);
			if (_currentLayerTimer.action(this.elapsedTime = elapsed)) {
				action(elapsed);
				if (!_currentLayerVSync) {
					LIterator<Actor> it = objects.iterator();
					for (; it.hasNext();) {
						_thing = (Actor) it.next();
						if (!_thing.visible) {
							continue;
						}
						_thing.update(elapsed);
					}
				}
				if (_layerSprites != null) {
					_layerSprites.update(elapsed);
				}
			}
		}
	}

	@Override
	public void createCustomUI(final GLEx g, final int x, final int y, final int w, final int h) {
		if (!_component_visible) {
			return;
		}
		final int tint = g.color();
		paintObjects(g, x, y, x + w, y + h);
		if (x == 0 && y == 0) {
			paint(g);
			if (_layerSprites != null) {
				_layerSprites.paint(g, x, y, w, h);
			}
		} else {
			try {
				g.translate(x, y);
				paint(g);
				if (_layerSprites != null) {
					_layerSprites.paint(g, x, y, w, h);
				}
			} finally {
				g.translate(-x, -y);
			}
		}
		g.setTint(tint);
	}

	public void paint(GLEx g) {

	}

	public void paintObjects(final GLEx g, final int minX, final int minY, final int maxX, final int maxY) {
		synchronized (objects) {
			final LIterator<Actor> it = objects.iterator();
			for (; it.hasNext();) {
				_thing = it.next();
				if (!_thing.visible) {
					continue;
				}
				_currentLayerListener = (_thing.actorListener != null);
				if (_currentLayerVSync) {
					if (_currentLayerListener) {
						_thing.actorListener.update(elapsedTime);
					}
					_thing.update(elapsedTime);
				}

				_actorX = minX + _thing.getX();
				_actorY = minY + _thing.getY();
				_actorWidth = _thing.pixelWidth();
				_actorHeight = _thing.pixelHeight();
				if (_component_elastic && (_actorX + _actorWidth < minX || _actorX > maxX
						|| _actorY + _actorHeight < minY || _actorY > maxY)) {
					continue;
				}
				final int tint = g.color();
				final float alpha = g.alpha();
				LTexture actorImage = _thing.getImage();
				if (actorImage != null) {
					final float width = _thing.pixelWidth();
					final float height = _thing.pixelHeight();
					_thing.setLastPaintSeqNum(_paintSeq++);
					float oldAlpha = g.alpha();
					_colorAlpha = _thing.getAlpha();
					if (_colorAlpha != oldAlpha) {
						g.setAlpha(_colorAlpha);
					}
					g.draw(actorImage, _actorX, _actorY, width, height,
							_colorTemp
									.setColor(_component_baseColor == null ? LColor.getColorARGBInt(_thing.filterColor)
											: LColor.combine(_component_baseColor, _thing.filterColor)),
							_thing.getRotation(), _thing.scaleX, _thing.scaleY, _thing.flipX, _thing.flipY);
					if (_colorAlpha != oldAlpha) {
						g.setAlpha(oldAlpha);
					}
				}
				if (_thing.isConsumerDrawing) {
					if (_actorX == 0 && _actorY == 0) {
						_thing.draw(g);
						if (_currentLayerListener) {
							_thing.actorListener.draw(g);
						}
					} else {
						try {
							g.saveTx();
							g.translate(_actorX, _actorY);
							_thing.draw(g);
							if (_currentLayerListener) {
								_thing.actorListener.draw(g);
							}
						} finally {
							g.translate(-_actorX, -_actorY);
							g.restoreTx();
						}
					}
				}
				g.setAlpha(alpha);
				g.setTint(tint);
			}

		}
	}

	public LLayer moveCamera(Actor actor) {
		moveCamera(actor.x(), actor.y());
		return this;
	}

	public LLayer centerOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, getHeight() / 2 - object.getHeight() / 2);
		return this;
	}

	public LLayer topOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, 0);
		return this;
	}

	public LLayer leftOn(final Actor object) {
		object.setLocation(0, getHeight() / 2 - object.getHeight() / 2);
		return this;
	}

	public LLayer rightOn(final Actor object) {
		object.setLocation(getWidth() - object.getWidth(), getHeight() / 2 - object.getHeight() / 2);
		return this;
	}

	public LLayer bottomOn(final Actor object) {
		object.setLocation(getWidth() / 2 - object.getWidth() / 2, getHeight() - object.getHeight());
		return this;
	}

	public LLayer setField2DBackground(Field2D field, ArrayMap pathMap) {
		setField2DBackground(field, pathMap, null);
		return this;
	}

	public LLayer setField2DBackground(Field2D field, ArrayMap pathMap, String fileName) {
		setField2D(field);
		Image background = null;
		if (fileName != null) {
			Image tmp = Image.createImage(fileName);
			background = setTileBackground(tmp, true);
			if (tmp != null) {
				tmp.close();
				tmp = null;
			}
		} else {
			background = Image.createImage(getWidth(), getHeight());
		}
		if (background != null) {
			Canvas g = background.getCanvas();
			for (int i = 0; i < field.getWidth(); i++) {
				for (int j = 0; j < field.getHeight(); j++) {
					int index = field.getTileType(i, j);
					Object o = pathMap.get(index);
					if (o != null) {
						if (o instanceof Image) {
							g.draw(((Image) o), field.tilesToWidthPixels(i), field.tilesToHeightPixels(j));
						} else if (o instanceof Actor) {
							addObject(((Actor) o), field.tilesToWidthPixels(i), field.tilesToHeightPixels(j));
						}
					}
				}
			}
			g.close();
			setBackground(background.texture());
			if (background != null) {
				background.close();
				background = null;
			}
		}
		return this;
	}

	public LLayer setTileBackground(String fileName) {
		setTileBackground(Image.createImage(fileName));
		return this;
	}

	public LLayer setTileBackground(Image image) {
		setTileBackground(image, false);
		return this;
	}

	public Image setTileBackground(Image image, boolean isReturn) {
		if (image == null) {
			return null;
		}
		int layerWidth = width();
		int layerHeight = height();
		int tileWidth = image.getWidth();
		int tileHeight = image.getHeight();

		Image tempImage = Image.createImage(layerWidth, layerHeight);
		Canvas g = tempImage.getCanvas();
		if (g != null) {
			for (int x = 0; x < layerWidth; x += tileWidth) {
				for (int y = 0; y < layerHeight; y += tileHeight) {
					g.draw(image, x, y);
				}
			}
			g.close();
		}
		if (isReturn) {
			return tempImage;
		}
		setBackground(tempImage.texture());
		if (tempImage != null) {
			tempImage.close();
			tempImage = null;
		}
		return null;
	}

	public int getScroll(RectBox visibleRect, int orientation, int direction) {
		int cellSize = this.getCellSize();
		float scrollPos = 0f;
		if (orientation == 0) {
			if (direction < 0) {
				scrollPos = visibleRect.getMinX();
			} else if (direction > 0) {
				scrollPos = visibleRect.getMaxX();
			}
		} else if (direction < 0) {
			scrollPos = visibleRect.getMinY();
		} else if (direction > 0) {
			scrollPos = visibleRect.getMaxY();
		}
		int increment = MathUtils.abs((int) MathUtils.IEEEremainder(scrollPos, cellSize));
		if (increment == 0) {
			increment = cellSize;
		}
		return increment;
	}

	public Actor getClickActor() {
		return _dragActor;
	}

	@Override
	protected void processTouchEntered() {
		this._pressed = true;
	}

	@Override
	protected void processTouchExited() {
		this._pressed = false;
	}

	@Override
	protected void processKeyPressed() {
		if (this.isSelected()) {
			this.downKey();
		}
	}

	@Override
	protected void processKeyReleased() {
		if (this.isSelected()) {
			this.upKey();
		}
	}

	@Override
	protected void processTouchPressed() {
		if (!_input.isMoving()) {
			super.processTouchPressed();
			final Vector2f pos = getUITouchXY();
			int dx = MathUtils.floor(pos.x);
			int dy = MathUtils.floor(pos.y);
			_dragActor = getSynchronizedObject(dx, dy);
			if (_dragActor != null) {
				if (!_dragActor.isClick()) {
					_dragActor.downClick(dx, dy);
					if (_dragActor.actorListener != null) {
						_dragActor.actorListener.downClick(dx, dy);
					}
					_dragActor.clicked = true;
				}
			}
			try {
				this.downClick(dx, dy);
			} catch (Throwable e) {
				LSystem.error("Layer downClick() exception", e);
			}
			_currentLayerTouchClick = true;
		}
	}

	@Override
	protected void processTouchReleased() {
		if (!_currentLayerTouchClick) {
			return;
		}
		if (!_input.isMoving()) {
			super.processTouchReleased();
			final Vector2f pos = getUITouchXY();
			int dx = MathUtils.floor(pos.x);
			int dy = MathUtils.floor(pos.y);
			_dragActor = getSynchronizedObject(dx, dy);
			if (_dragActor != null) {
				if (_dragActor.isClick()) {
					_dragActor.upClick(dx, dy);
					if (_dragActor.actorListener != null) {
						_dragActor.actorListener.upClick(dx, dy);
					}
					_dragActor.clicked = false;
				}
			}
			try {
				this.upClick(dx, dy);
			} catch (Throwable e) {
				LSystem.error("Layer upClick() exception", e);
			}
			this._dragActor = null;
			this._currentDragging = false;
			this._currentLayerTouchClick = false;
		}
	}

	@Override
	protected void processTouchDragged() {
		if (!_currentLayerTouchClick) {
			return;
		}
		if (_input.isMoving()) {
			int dropX = 0;
			int dropY = 0;
			if (!_dragLocked) {
				boolean moveActor = false;
				if (_actorDrag) {
					moveActor = checkDragActor();
				}
				if (!moveActor) {
					synchronized (_input) {
						validatePosition();
						dropX = this._input.getTouchDX();
						dropY = this._input.getTouchDY();
						if (isNotMoveInScreen(dropX + x(), dropY + y())) {
							return;
						}
						if (getContainer() != null) {
							getContainer().sendToFront(this);
						}
						try {
							this.move(dropX, dropY);
							this.dragClick(dropX, dropY);
						} catch (Throwable e) {
							LSystem.error("Layer drag() exception", e);
						}
					}
				}
			} else {
				if (!_actorDrag) {
					return;
				}
				checkDragActor();
			}
			try {
				super.dragClick();
			} catch (Throwable e) {
				LSystem.error("Layer dragClick() exception", e);
			}
			_currentDragging = true;
		}
	}

	protected boolean checkDragActor() {
		if (!_currentDragging) {
			return false;
		}
		int dropX = 0;
		int dropY = 0;
		boolean moveActor = false;
		if (_actorDrag) {
			synchronized (objects) {
				final Vector2f pos = getUITouchXY();
				dropX = MathUtils.floor(pos.x);
				dropY = MathUtils.floor(pos.y);
				if (_lastDropX == dropX && _lastDropY == dropY) {
					return (_dragActor != null && _dragActor.isDrag());
				}
				if (_dragActor == null) {
					_dragActor = getSynchronizedObject(dropX, dropY);
				}
				if (_dragActor != null && _dragActor.isDrag() && _dragActor.isClick()) {
					synchronized (_dragActor) {
						objects.sendToFront(_dragActor);
						RectBox rect = _dragActor.getBoundingRect();
						int dx = dropX - (rect.width / 2);
						int dy = dropY - (rect.height / 2);
						if (_dragActor.getLLayer() != null) {
							_dragActor.setLocation(dx, dy);
							_dragActor.drag(dropX, dropY);
							if (_dragActor.actorListener != null) {
								_dragActor.actorListener.drag(dropX, dropY);
							}
						}
						moveActor = true;
					}
				}
			}
		}
		_lastDropX = dropX;
		_lastDropY = dropY;
		return moveActor;
	}

	public boolean isCurrentDragging() {
		return _currentDragging;
	}

	public boolean isTouchPressed() {
		return this._pressed;
	}

	public boolean isActorDrag() {
		return _actorDrag;
	}

	public LLayer setActorDrag(boolean d) {
		this._actorDrag = d;
		return this;
	}

	public boolean isLimitMove() {
		return _isLimitMove;
	}

	public LLayer setLimitMove(boolean isLimitMove) {
		this._isLimitMove = isLimitMove;
		return this;
	}

	public boolean isTouchClick() {
		return _currentLayerTouchClick;
	}

	public LLayer setTouchClick(boolean t) {
		this._currentLayerTouchClick = t;
		return this;
	}

	public float getLayerTouchX() {
		return getUITouchX();
	}

	public float getLayerTouchY() {
		return getUITouchY();
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public String getUIName() {
		return "Layer";
	}

	@Override
	public void destory() {
		if (collisionChecker != null) {
			collisionChecker.dispose();
			collisionChecker = null;
		}
		if (objects != null) {
			Object[] o = objects.toActors();
			for (int i = 0; i < o.length; i++) {
				Actor actor = (Actor) o[i];
				if (actor != null) {
					actor.close();
					actor = null;
				}
			}
		}
		if (_layerSprites != null) {
			_layerSprites.close();
		}
		_layerSprites = null;
	}
}

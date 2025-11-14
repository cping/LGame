/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.component;

import loon.LTexture;
import loon.LSystem;
import loon.canvas.LColor;
import loon.component.skin.MessageSkin;
import loon.component.skin.SkinManager;
import loon.geom.Vector2f;
import loon.geom.XY;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 一个允许滚动显示其中内容的LContainer
 */
public class LScrollContainer extends LContainer {

	public static LScrollContainer createVerticalScrollContainer(LComponent comp, float x, float y, float h) {
		final int newX = MathUtils.ifloor(x);
		final int newY = MathUtils.ifloor(y);
		int newW = comp == null ? 18 : comp.width();
		int newH = MathUtils.ifloor(h);
		LScrollContainer container = null;
		if (comp == null) {
			container = new LScrollContainer(newX, newY, newW, newH);
			container.setScrollHorizontalbarVisible(false);
			return container;
		}
		if (comp.width() >= newW) {
			newW = comp.width() + 18;
		}
		if (comp.height() <= newH) {
			newH = MathUtils.iceil(comp.height() * 0.67f);
		}
		container = new LScrollContainer(newX, newY, newW, newH);
		container.setScrollHorizontalbarVisible(false);
		return container;
	}

	public static LScrollContainer createHorizontalScrollContainer(LComponent comp, float x, float y, float w) {
		final int newX = MathUtils.ifloor(x);
		final int newY = MathUtils.ifloor(y);
		int newW = MathUtils.ifloor(w);
		int newH = comp == null ? 18 : comp.height();
		LScrollContainer container = null;
		if (comp == null) {
			container = new LScrollContainer(newX, newY, newW, newH);
			container.setScrollVerticalbarVisible(false);
			return container;
		}
		if (comp.width() <= newW) {
			newW = MathUtils.iceil(comp.width() * 0.67f);
		}
		if (comp.height() >= newH) {
			newH = comp.height() + 18;
		}
		container = new LScrollContainer(newX, newY, newW, newH);
		container.setScrollVerticalbarVisible(false);
		return container;
	}

	private float _scrollTime, _scrollAmountTimer;

	private float _velocityX, _velocityY;

	private float _minAutoScrollX, _minAutoScrollY;

	private float _maxAutoScrollX, _maxAutoScrollY;

	private int _verticalScrollType = LScrollBar.RIGHT;

	private int _horizontalScrollType = LScrollBar.BOTTOM;

	private boolean _scrollX;

	private boolean _scrollY;

	private boolean _visibleBackground;

	private boolean _verticalVisible;

	private boolean _horizontalVisible;

	private float _boxScrollX;

	private float _boxScrollY;

	private LScrollBar _verticalScrollbar;

	private LScrollBar _horizontalScrollbar;

	private boolean _allowHorizontalScroll = true;

	private boolean _allowVerticalScrollbar = true;

	private boolean _accumulate = false;

	public boolean _showScroll = true;

	public LScrollContainer(int x, int y, int w, int h) {
		this(SkinManager.get().getMessageSkin().getBackgroundTexture(), x, y, w, h);
	}

	public LScrollContainer(String path, int x, int y, int w, int h) {
		this(LSystem.loadTexture(path), x, y, w, h);
	}

	public LScrollContainer(MessageSkin skin, int x, int y, int w, int h) {
		this(skin.getBackgroundTexture(), x, y, w, h);
	}

	public LScrollContainer(LTexture texture, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.onlyBackground(texture);
		this.setElastic(true);
		this._minAutoScrollX = _minAutoScrollY = -1f;
		this._maxAutoScrollX = _maxAutoScrollY = -1f;
		this._velocityX = _velocityY = -1f;
		this._scrollX = _scrollY = true;
		this._visibleBackground = true;
		this._verticalVisible = this._horizontalVisible = true;
	}

	@Override
	public void createUI(GLEx g) {
		if (_destroyed) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}
		final LComponent[] childs = super._childs;
		synchronized (childs) {
			try {
				g.saveTx();
				if (_visibleBackground) {
					if (_background == null) {
						g.fillRect(getScreenX(), getScreenY(), getWidth(), getHeight(),
								_colorTemp.setColor(_component_baseColor == null ? LColor.gray.getARGB()
										: LColor.combine(_component_baseColor, LColor.gray)));
					} else {
						g.draw(_background, getScreenX(), getScreenY(), getWidth(), getHeight(), _component_baseColor);
					}
				}
				g.translate(-_boxScrollX, -_boxScrollY);
				super.createUI(g);
				g.translate(_boxScrollX, _boxScrollY);
				if (_showScroll) {
					if (_verticalScrollbar != null) {
						_verticalScrollbar.paint(g);
					}
					if (_horizontalScrollbar != null) {
						_horizontalScrollbar.paint(g);
					}
				}
			} finally {
				g.restoreTx();
			}
		}
	}

	public boolean isVerticalScrolling() {
		return (_verticalScrollbar == null) ? false : _verticalScrollbar._scrolling;
	}

	public boolean isHorizontalScrolling() {
		return (_horizontalScrollbar == null) ? false : _horizontalScrollbar._scrolling;
	}

	@Override
	public void createUI(GLEx g, int x, int y) {

	}

	@Override
	public LScrollContainer scrollBy(float x, float y) {
		if (_scrollX) {
			this._boxScrollX += x;
		}
		if (_scrollY) {
			this._boxScrollY += y;
		}
		this._scrolling = x != 0f && y != 0f;
		return this;
	}

	@Override
	public LScrollContainer scrollTo(float x, float y) {
		scrollX(x);
		scrollY(y);
		return this;
	}

	@Override
	public float scrollX() {
		return this._boxScrollX;
	}

	@Override
	public float scrollY() {
		return this._boxScrollY;
	}

	@Override
	public LScrollContainer scrollX(float x) {
		this._scrolling = !MathUtils.equal(x, this._boxScrollX);
		if (_scrollX) {
			this._boxScrollX = x;
		}
		return this;
	}

	@Override
	public LScrollContainer scrollY(float y) {
		this._scrolling = !MathUtils.equal(y, this._boxScrollY);
		if (_scrollY) {
			this._boxScrollY = y;
		}
		return this;
	}

	public LScrollContainer moveScrollX(float newScrollX) {
		if (!_scrollX) {
			return this;
		}
		validatePosition();
		if (_accumulate) {
			_boxScrollX += MathUtils.max(0f, newScrollX);
		} else {
			_boxScrollX = MathUtils.max(0f, newScrollX);
		}
		int size = getMaxWidth() - width();
		if (_boxScrollX >= size) {
			_boxScrollX = size;
		}
		this._scrolling = true;
		return this;
	}

	public LScrollContainer moveScrollY(float newScrollY) {
		if (!_scrollY) {
			return this;
		}
		validatePosition();
		if (_accumulate) {
			_boxScrollY += MathUtils.max(0f, newScrollY);
		} else {
			_boxScrollY = MathUtils.max(0f, newScrollY);
		}
		int size = getMaxHeight() - height();
		if (_boxScrollY >= size) {
			_boxScrollY = size;
		}
		this._scrolling = true;
		return this;
	}

	public float getBoxScrollX() {
		return _boxScrollX;
	}

	public float getBoxScrollY() {
		return _boxScrollY;
	}

	@Override
	public LComponent add(LComponent comp) {
		super.add(comp);
		scrollContainerRealSizeChanged();
		return this;
	}

	@Override
	public LComponent add(LComponent comp, int index) {
		super.add(comp, index);
		scrollContainerRealSizeChanged();
		return this;
	}

	@Override
	public LComponent add(LComponent comp, Vector2f pos) {
		super.add(comp, pos.x(), pos.y());
		scrollContainerRealSizeChanged();
		return this;
	}

	@Override
	public LComponent add(LComponent comp, int x, int y) {
		super.add(comp, x, y);
		scrollContainerRealSizeChanged();
		return this;
	}

	@Override
	public void setWidth(float width) {
		float scrollBarWidth = _verticalScrollbar == null ? 0 : _verticalScrollbar.getWidth();
		super.setWidth(width - scrollBarWidth);
		fitScrollBarSize();
	}

	@Override
	public void setHeight(float height) {
		float scrollbarHeight = _horizontalScrollbar == null ? 0 : _horizontalScrollbar.getHeight();
		super.setHeight(height - scrollbarHeight);
		fitScrollBarSize();
	}

	public LScrollContainer setScrollVerticalbarVisible(boolean visible) {
		if (_verticalScrollbar != null) {
			_verticalScrollbar.setVisible(visible);
		}
		_verticalVisible = visible;
		return this;
	}

	public LScrollContainer setScrollHorizontalbarVisible(boolean visible) {
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.setVisible(visible);
		}
		_horizontalVisible = visible;
		return this;
	}

	public LScrollContainer setScrollbarsVisible(boolean visible) {
		setScrollVerticalbarVisible(visible);
		setScrollHorizontalbarVisible(visible);
		return this;
	}

	public boolean isScrollbarVisible() {
		return isVerticalScrollbarVisible() && isHorizontalScrollbarVisible();
	}

	public boolean isVerticalScrollbarVisible() {
		return (_verticalScrollbar != null) ? _verticalScrollbar.isVisible() : _verticalVisible;
	}

	public boolean isHorizontalScrollbarVisible() {
		return (_horizontalScrollbar != null) ? _horizontalScrollbar.isVisible() : _horizontalVisible;
	}

	private void fitScrollBarSize() {
		if (_verticalScrollbar != null) {
			_verticalScrollbar.adjustScrollbar();
		}
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.adjustScrollbar();
		}
	}

	public LScrollContainer adjustSlider(int sliderWidth, int sliderHeight) {
		if (_verticalScrollbar != null) {
			_verticalScrollbar.adjustSlider(sliderWidth, sliderHeight);
		}
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.adjustSlider(sliderWidth, sliderHeight);
		}
		return this;
	}

	public boolean isClickSlider() {
		return isClickSlider(null);
	}

	public boolean isClickSlider(XY pos) {
		if (pos == null) {
			pos = getUITouchXY();
		}
		return isClickSlider(pos.getX(), pos.getY());
	}

	public boolean isClickSlider(float posX, float posY) {
		boolean selected = isClickVerticalSlider(posX, posY);
		if (!selected) {
			selected = isClickHorizontalSlider(posX, posY);
		}
		return selected;
	}

	public boolean isClickVerticalSlider(float posX, float posY) {
		if (_verticalScrollbar == null) {
			return false;
		}
		return _verticalScrollbar.contains(getScalePixelX() + posX, getScalePixelY() + posY);
	}

	public boolean isClickHorizontalSlider(float posX, float posY) {
		if (_horizontalScrollbar == null) {
			return false;
		}
		return _horizontalScrollbar.contains(getScalePixelX() + posX, getScalePixelY() + posY);
	}

	public LScrollContainer scrollContainerRealSizeChanged() {
		checkIfScrollbarIsNecessary();
		fitScrollBarSize();
		return this;
	}

	private void checkIfScrollbarIsNecessary() {
		if (_allowHorizontalScroll) {
			int maxX = getInnerWidth();
			if (maxX > getWidth()) {
				if (_horizontalScrollbar != null) {
					addScrollbar(_horizontalScrollbar);
				} else {
					_horizontalScrollbar = new LScrollBar(_horizontalScrollType);
					addScrollbar(_horizontalScrollbar);
				}
				if (_scrollAmountTimer > 0f) {
					_horizontalScrollbar.autoScroll(_scrollAmountTimer, _velocityX, _velocityY);
				}
				_horizontalScrollbar.setLimitAutoScroll(_minAutoScrollX, _minAutoScrollY, _maxAutoScrollX,
						_maxAutoScrollY);
				_horizontalScrollbar.setVisible(_horizontalVisible);
			}
		} else {
			_horizontalScrollbar = null;
		}
		if (_allowVerticalScrollbar) {
			int maxY = getInnerHeight();
			if (maxY > getHeight()) {
				if (_verticalScrollbar != null) {
					addScrollbar(_verticalScrollbar);
				} else {
					_verticalScrollbar = new LScrollBar(_verticalScrollType);
					addScrollbar(_verticalScrollbar);
				}
			}
			if (_verticalScrollbar != null) {
				if (_scrollAmountTimer > 0f) {
					_verticalScrollbar.autoScroll(_scrollAmountTimer, _velocityX, _velocityY);
				}
				_verticalScrollbar.setLimitAutoScroll(_minAutoScrollX, _minAutoScrollY, _maxAutoScrollX,
						_maxAutoScrollY);
				_verticalScrollbar.setVisible(_verticalVisible);
			}
		} else {
			_verticalScrollbar = null;
		}
	}

	public int getMaxWidth() {
		int maxX = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxX = MathUtils.floor(MathUtils.max(super._childs[i].getX() + super._childs[i].getWidth(), maxX));
		}
		return maxX;
	}

	public int getInnerWidth() {
		int maxX = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxX = MathUtils.floor(MathUtils.max(x() + super._childs[i].getX() + super._childs[i].getWidth(), maxX));
		}
		return maxX;
	}

	public int getMaxHeight() {
		int maxY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxY = MathUtils.floor(MathUtils.max(super._childs[i].getY() + super._childs[i].getHeight(), maxY));
		}
		return maxY;
	}

	public int getInnerHeight() {
		int maxY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxY = MathUtils.floor(MathUtils.max(y() + super._childs[i].getY() + super._childs[i].getHeight(), maxY));
		}
		return maxY;
	}

	public LScrollContainer addScrollbar(LScrollBar scrollBar) {
		if (scrollBar.getOrientation() == LScrollBar.LEFT || scrollBar.getOrientation() == LScrollBar.RIGHT) {
			if (_verticalScrollbar != null) {
				remove(_verticalScrollbar);
			}
			_verticalScrollbar = scrollBar;
			scrollBar.setScrollContainer(this);
			return this;
		}
		if (_horizontalScrollbar != null) {
			remove(_horizontalScrollbar);
		}
		_horizontalScrollbar = scrollBar;
		scrollBar.setScrollContainer(this);
		return this;
	}

	@Override
	public void process(final long elapsedTime) {
		if (!isAutoScroll()) {
			final Vector2f pos = getUITouchXY();
			if (_verticalVisible && _verticalScrollbar != null && isClickVerticalSlider(pos.x, pos.y)) {
				_verticalScrollbar.process(elapsedTime);
				_verticalScrollbar.processCheckClicked(pos);
			}
			if (_horizontalVisible && _horizontalScrollbar != null && isClickHorizontalSlider(pos.x, pos.y)) {
				_horizontalScrollbar.process(elapsedTime);
				_horizontalScrollbar.processCheckClicked(pos);
			}
		} else {
			if (_verticalScrollbar != null) {
				_verticalScrollbar.process(elapsedTime);
			}
			if (_horizontalScrollbar != null) {
				_horizontalScrollbar.process(elapsedTime);
			}
		}
	}

	@Override
	protected void processTouchDragged() {
		final Vector2f pos = getUITouchXY();
		if (_verticalVisible && _verticalScrollbar != null) {
			if (_verticalScrollbar.isAllowTouch() && isClickVerticalSlider(pos.x, pos.y)) {
				_verticalScrollbar.processTouchDragged();
			}
		}
		if (_horizontalVisible && _horizontalScrollbar != null) {
			if (_horizontalScrollbar.isAllowTouch() && isClickHorizontalSlider(pos.x, pos.y)) {
				_horizontalScrollbar.processTouchDragged();
			}
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		final Vector2f pos = getUITouchXY();
		if (_verticalVisible && _verticalScrollbar != null) {
			if (_verticalScrollbar.isAllowTouch() && isClickVerticalSlider(pos.x, pos.y)) {
				_verticalScrollbar.processTouchPressed();
			}
		}
		if (_horizontalVisible && _horizontalScrollbar != null) {
			if (_horizontalScrollbar.isAllowTouch() && isClickHorizontalSlider(pos.x, pos.y)) {
				_horizontalScrollbar.processTouchPressed();
			}
		}
		super.processTouchPressed();
	}

	@Override
	protected void processTouchReleased() {
		final Vector2f pos = getUITouchXY();
		if (_verticalVisible && _verticalScrollbar != null) {
			if (_verticalScrollbar.isAllowTouch() && isClickVerticalSlider(pos.x, pos.y)) {
				_verticalScrollbar.processTouchReleased();
			}
		}
		if (_horizontalVisible && _horizontalScrollbar != null) {
			if (_horizontalScrollbar.isAllowTouch() && isClickHorizontalSlider(pos.x, pos.y)) {
				_horizontalScrollbar.processTouchReleased();
			}
		}
		super.processTouchReleased();
	}

	public LScrollBar getVerticalScrollbar() {
		if (_verticalScrollbar == null) {
			scrollContainerRealSizeChanged();
		}
		return _verticalScrollbar;
	}

	public LScrollContainer setVerticalScrollbar(LScrollBar v) {
		this._verticalScrollbar = v;
		return this;
	}

	public LScrollBar getHorizontalScrollbar() {
		if (_horizontalScrollbar == null) {
			scrollContainerRealSizeChanged();
		}
		return _horizontalScrollbar;
	}

	public LScrollContainer setHorizontalScrollbar(LScrollBar h) {
		this._horizontalScrollbar = h;
		return this;
	}

	public boolean isAccumulate() {
		return _accumulate;
	}

	public LScrollContainer setAccumulate(boolean accumulate) {
		this._accumulate = accumulate;
		return this;
	}

	public boolean isShowScroll() {
		return _showScroll;
	}

	public LScrollContainer setShowScroll(boolean showScroll) {
		this._showScroll = showScroll;
		return this;
	}

	public boolean isAllowHorizontalScroll() {
		return _allowHorizontalScroll;
	}

	public LScrollContainer setAllowHorizontalScroll(boolean a) {
		this._allowHorizontalScroll = a;
		return this;
	}

	public boolean isAllowVerticalScrollbar() {
		return _allowVerticalScrollbar;
	}

	public LScrollContainer setAllowVerticalScrollbar(boolean a) {
		this._allowVerticalScrollbar = a;
		return this;
	}

	public LScrollContainer setScroll(boolean x, boolean y) {
		setScrollX(x);
		setScrollY(y);
		return this;
	}

	public boolean isScrollX() {
		return _scrollX;
	}

	public LScrollContainer setScrollX(boolean x) {
		this._scrollX = x;
		return this;
	}

	public boolean isScrollY() {
		return _scrollY;
	}

	public LScrollContainer setScrollY(boolean y) {
		this._scrollY = y;
		return this;
	}

	public LScrollContainer setScrollType(int v, int h) {
		this.setVerticalScrollType(v);
		this.setHorizontalScrollType(h);
		return this;
	}

	public int getVerticalScrollType() {
		return _verticalScrollType;
	}

	public LScrollContainer setVerticalScrollType(int v) {
		this._verticalScrollType = v;
		return this;
	}

	public int getHorizontalScrollType() {
		return _horizontalScrollType;
	}

	public LScrollContainer setHorizontalScrollType(int h) {
		this._horizontalScrollType = h;
		return this;
	}

	public boolean isAutoScroll() {
		boolean result = false;
		if (_horizontalVisible && _horizontalScrollbar != null) {
			result = _horizontalScrollbar.isAutoScroll();
		}
		if (_verticalVisible && _verticalScrollbar != null) {
			result = _verticalScrollbar.isAutoScroll();
		}
		return result && _scrollAmountTimer > 0f;
	}

	public boolean isAutoVelocity() {
		return this._velocityX != -1f || this._velocityY != -1f;
	}

	public float getScrollTime() {
		return _scrollTime;
	}

	public LScrollContainer setScrollTime(float time) {
		this._scrollTime = time;
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.setScrollTime(time);
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.setScrollTime(time);
		}
		return this;
	}

	public LScrollContainer autoScroll(float time) {
		return autoScroll(time, 1f, 1f);
	}

	public LScrollContainer autoScroll(float time, float x, float y) {
		this._scrollAmountTimer = MathUtils.max(time, 0.1f);
		this._velocityX = MathUtils.max(x, 0.1f);
		this._velocityY = MathUtils.max(y, 0.1f);
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.autoScroll(time, x, y);
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.autoScroll(time, x, y);
		}
		return this;
	}

	public LScrollContainer setVelocityX(float x) {
		this._velocityX = x;
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.setVelocityX(x);
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.setVelocityX(x);
		}
		return this;
	}

	public float getVelocityX() {
		return _velocityX;
	}

	public LScrollContainer setVelocityY(float y) {
		this._velocityY = y;
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.setVelocityY(y);
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.setVelocityY(y);
		}
		return this;
	}

	public float getVelocityY() {
		return _velocityY;
	}

	public LScrollContainer setLimitAutoScroll(float x, float y, float w, float h) {
		this.setMinAutoScrollX(x);
		this.setMinAutoScrollY(y);
		this.setMaxAutoScrollX(w);
		this.setMaxAutoScrollY(h);
		return this;
	}

	public float getMinAutoScrollX() {
		return _minAutoScrollX;
	}

	public LScrollContainer setMinAutoScrollX(float x) {
		if (x == -1) {
			return this;
		}
		this._minAutoScrollX = MathUtils.max(0f, x);
		return this;
	}

	public float getMinAutoScrollY() {
		return _minAutoScrollY;
	}

	public LScrollContainer setMinAutoScrollY(float y) {
		if (y == -1) {
			return this;
		}
		this._minAutoScrollY = MathUtils.max(0f, y);
		return this;
	}

	public float getMaxAutoScrollX() {
		return _maxAutoScrollX;
	}

	public LScrollContainer setMaxAutoScrollX(float x) {
		if (x == -1) {
			return this;
		}
		this._maxAutoScrollX = MathUtils.min(getWidth(), x);
		return this;
	}

	public float getMaxAutoScrollY() {
		return _maxAutoScrollY;
	}

	public LScrollContainer setMaxAutoScrollY(float y) {
		if (y == -1) {
			return this;
		}
		this._maxAutoScrollY = MathUtils.min(getHeight(), y);
		return this;
	}

	public boolean isVisibleBackground() {
		return _visibleBackground;
	}

	public LScrollContainer setVisibleBackground(boolean v) {
		this._visibleBackground = v;
		return this;
	}

	public LScrollContainer resetAutoScroll() {
		this._scrollTime = 1f;
		this._scrollAmountTimer = 0f;
		this._velocityX = _velocityY = -1f;
		this._minAutoScrollX = _minAutoScrollY = -1f;
		this._maxAutoScrollX = _maxAutoScrollY = -1f;
		return this;
	}

	@Override
	public String getUIName() {
		return "ScrollContainer";
	}

	@Override
	public void destory() {

	}

}

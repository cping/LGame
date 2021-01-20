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
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 一个允许滚动显示其中内容的LContainer
 */
public class LScrollContainer extends LContainer {

	private int _scrollX;

	private int _scrollY;

	private LScrollBar _verticalScrollbar;

	private LScrollBar _horizontalScrollbar;

	private boolean allowHorizontalScroll = true;

	private boolean allowVerticalScrollbar = true;

	private boolean accumulate = false;

	public boolean showScroll = true;

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
	}

	@Override
	public void createUI(GLEx g) {
		if (_component_isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}

		LComponent[] childs = super._childs;
		synchronized (childs) {
			try {
				g.saveTx();
				if (_background == null) {
					g.fillRect(getScreenX(), getScreenY(), getWidth(), getHeight(),
							_component_baseColor == null ? LColor.gray : _component_baseColor.mul(LColor.gray));
				} else {
					g.draw(_background, getScreenX(), getScreenY(), getWidth(), getHeight(), _component_baseColor);
				}
				g.translate(-_scrollX, -_scrollY);
				super.createUI(g);
				g.translate(_scrollX, _scrollY);

				if (showScroll) {
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

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {

	}

	public LScrollContainer moveScrollX(int newScrollX) {
		if (accumulate) {
			_scrollX += newScrollX;
		} else {
			_scrollX = newScrollX;
		}
		if (_verticalScrollbar == null) {
			if (_scrollX > width()) {
				_scrollX = width();
			}
		} else {
			int size = _verticalScrollbar.width();
			if (_scrollX > width() + size) {
				_scrollX = width() + size;
			}
		}
		return this;
	}

	public LScrollContainer moveScrollY(int newScrollY) {
		if (accumulate) {
			_scrollY += newScrollY;
		} else {
			_scrollY = newScrollY;
		}
		if (_horizontalScrollbar == null) {
			if (_scrollY > height()) {
				_scrollY = height();
			}
		} else {
			int size = (_horizontalScrollbar.height() + _horizontalScrollbar.getSliderHeight()
					+ _horizontalScrollbar.getSliderMargin());
			if (_scrollY > height() - size * 2) {
				_scrollY = height() - size * 2;
			}
		}
		return this;
	}

	public int getScrollX() {
		return _scrollX;
	}

	public int getScrollY() {
		return _scrollY;
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

	public LScrollContainer scrollContainerRealSizeChanged() {
		checkIfScrollbarIsNecessary();
		fitScrollBarSize();
		return this;
	}

	private void checkIfScrollbarIsNecessary() {
		if (allowHorizontalScroll) {
			int maxX = getInnerWidth();
			if (maxX > getWidth()) {
				if (_horizontalScrollbar != null) {
					addScrollbar(new LScrollBar(LScrollBar.BOTTOM));
				} else {
					_horizontalScrollbar = new LScrollBar(LScrollBar.BOTTOM);
					addScrollbar(_horizontalScrollbar);
				}
			}
		} else {
			_horizontalScrollbar = null;
		}
		if (allowVerticalScrollbar) {
			int maxY = getInnerHeight();
			if (maxY > getHeight()) {
				if (_verticalScrollbar != null) {
					addScrollbar(new LScrollBar(LScrollBar.RIGHT));
				} else {
					_verticalScrollbar = new LScrollBar(LScrollBar.RIGHT);
					addScrollbar(_verticalScrollbar);
				}
			}
		} else {
			_verticalScrollbar = null;
		}
	}

	public int getInnerWidth() {
		int maxX = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxX = (int) MathUtils.max(x() + super._childs[i].getX() + super._childs[i].getWidth(), maxX);
		}
		return maxX;
	}

	public int getInnerHeight() {
		int maxY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxY = (int) MathUtils.max(y() + super._childs[i].getY() + super._childs[i].getHeight(), maxY);
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
	protected void processTouchDragged() {
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.processTouchDragged();
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.processTouchDragged();
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		super.processKeyPressed();
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.processTouchPressed();
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (_horizontalScrollbar != null) {
			_horizontalScrollbar.processTouchReleased();
		}
		if (_verticalScrollbar != null) {
			_verticalScrollbar.processTouchReleased();
		}
	}

	public LScrollBar getVerticalScrollbar() {
		return _verticalScrollbar;
	}

	public LScrollContainer setVerticalScrollbar(LScrollBar v) {
		this._verticalScrollbar = v;
		return this;
	}

	public LScrollBar getHorizontalScrollbar() {
		return _horizontalScrollbar;
	}

	public LScrollContainer setHorizontalScrollbar(LScrollBar h) {
		this._horizontalScrollbar = h;
		return this;
	}

	public boolean isAccumulate() {
		return accumulate;
	}

	public LScrollContainer setAccumulate(boolean accumulate) {
		this.accumulate = accumulate;
		return this;
	}

	public boolean isShowScroll() {
		return showScroll;
	}

	public LScrollContainer setShowScroll(boolean showScroll) {
		this.showScroll = showScroll;
		return this;
	}

	public boolean isAllowHorizontalScroll() {
		return allowHorizontalScroll;
	}

	public LScrollContainer setAllowHorizontalScroll(boolean a) {
		this.allowHorizontalScroll = a;
		return this;
	}

	public boolean isAllowVerticalScrollbar() {
		return allowVerticalScrollbar;
	}

	public LScrollContainer setAllowVerticalScrollbar(boolean a) {
		this.allowVerticalScrollbar = a;
		return this;
	}

	@Override
	public String getUIName() {
		return "ScrollContainer";
	}

}

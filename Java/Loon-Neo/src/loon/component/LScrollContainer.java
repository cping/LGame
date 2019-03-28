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
import loon.LTextures;
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

	private int scrollX;

	private int scrollY;

	private LScrollBar verticalScrollbar;

	private LScrollBar horizontalScrollbar;

	private boolean allowHorizontalScroll = true;

	private boolean allowVerticalScrollbar = true;

	private boolean accumulate = false;

	public boolean showScroll = true;

	public LScrollContainer(int x, int y, int w, int h) {
		this(SkinManager.get().getMessageSkin().getBackgroundTexture(), x, y, w, h);
	}

	public LScrollContainer(String path, int x, int y, int w, int h) {
		this(LTextures.loadTexture(path), x, y, w, h);
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
				g.translate(-scrollX, -scrollY);
				super.createUI(g);
				g.translate(scrollX, scrollY);

				if (showScroll) {
					if (verticalScrollbar != null) {
						verticalScrollbar.paint(g);
					}
					if (horizontalScrollbar != null) {
						horizontalScrollbar.paint(g);
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
			scrollX += newScrollX;
		} else {
			scrollX = newScrollX;
		}
		if (verticalScrollbar == null) {
			if (scrollX > width()) {
				scrollX = width();
			}
		} else {
			int size = verticalScrollbar.width();
			if (scrollX > width() + size) {
				scrollX = width() + size;
			}
		}
		return this;
	}

	public LScrollContainer moveScrollY(int newScrollY) {
		if (accumulate) {
			scrollY += newScrollY;
		} else {
			scrollY = newScrollY;
		}
		if (horizontalScrollbar == null) {
			if (scrollY > height()) {
				scrollY = height();
			}
		} else {
			int size = (horizontalScrollbar.height() + horizontalScrollbar.getSliderHeight()
					+ horizontalScrollbar.getSliderMargin());
			if (scrollY > height() - size * 2) {
				scrollY = height() - size * 2;
			}
		}
		return this;
	}

	public int getScrollX() {
		return scrollX;
	}

	public int getScrollY() {
		return scrollY;
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
		float scrollBarWidth = verticalScrollbar == null ? 0 : verticalScrollbar.getWidth();
		super.setWidth(width - scrollBarWidth);
		fitScrollBarSize();
	}

	@Override
	public void setHeight(float height) {
		float scrollbarHeight = horizontalScrollbar == null ? 0 : horizontalScrollbar.getHeight();
		super.setHeight(height - scrollbarHeight);
		fitScrollBarSize();
	}

	private void fitScrollBarSize() {
		if (verticalScrollbar != null) {
			verticalScrollbar.adjustScrollbar();
		}
		if (horizontalScrollbar != null) {
			horizontalScrollbar.adjustScrollbar();
		}
	}

	public LScrollContainer adjustSlider(int sliderWidth, int sliderHeight) {
		if (verticalScrollbar != null) {
			verticalScrollbar.adjustSlider(sliderWidth, sliderHeight);
		}
		if (horizontalScrollbar != null) {
			horizontalScrollbar.adjustSlider(sliderWidth, sliderHeight);
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
				if (horizontalScrollbar != null) {
					addScrollbar(new LScrollBar(LScrollBar.BOTTOM));
				} else {
					horizontalScrollbar = new LScrollBar(LScrollBar.BOTTOM);
					addScrollbar(horizontalScrollbar);
				}
			}
		} else {
			horizontalScrollbar = null;
		}
		if (allowVerticalScrollbar) {
			int maxY = getInnerHeight();
			if (maxY > getHeight()) {
				if (verticalScrollbar != null) {
					addScrollbar(new LScrollBar(LScrollBar.RIGHT));
				} else {
					verticalScrollbar = new LScrollBar(LScrollBar.RIGHT);
					addScrollbar(verticalScrollbar);
				}
			}
		} else {
			verticalScrollbar = null;
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
			if (verticalScrollbar != null) {
				remove(verticalScrollbar);
			}
			verticalScrollbar = scrollBar;
			scrollBar.setScrollContainer(this);
			return this;
		}
		if (horizontalScrollbar != null) {
			remove(horizontalScrollbar);
		}
		horizontalScrollbar = scrollBar;
		scrollBar.setScrollContainer(this);
		return this;
	}

	@Override
	protected void processTouchDragged() {
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchDragged();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchDragged();
		}
		super.processTouchDragged();
	}

	@Override
	protected void processTouchPressed() {
		super.processKeyPressed();
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchPressed();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchPressed();
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchReleased();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchReleased();
		}
	}

	public LScrollBar getVerticalScrollbar() {
		return verticalScrollbar;
	}

	public LScrollContainer setVerticalScrollbar(LScrollBar verticalScrollbar) {
		this.verticalScrollbar = verticalScrollbar;
		return this;
	}

	public LScrollBar getHorizontalScrollbar() {
		return horizontalScrollbar;
	}

	public LScrollContainer setHorizontalScrollbar(LScrollBar horizontalScrollbar) {
		this.horizontalScrollbar = horizontalScrollbar;
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

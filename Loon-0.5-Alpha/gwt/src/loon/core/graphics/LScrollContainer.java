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
package loon.core.graphics;

import loon.core.graphics.component.DefUI;
import loon.core.graphics.device.LColor;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.MathUtils;

public class LScrollContainer extends LContainer {

	private int scrollX;

	private int scrollY;

	private LScrollBar verticalScrollbar;

	private LScrollBar horizontalScrollbar;

	private LTexture backgroundTexture;

	private boolean accumulate = false;

	public boolean showScroll = true;

	public LScrollContainer(int x, int y, int w, int h) {
		this(DefUI.getDefaultTextures(8), x, y, w, h);
	}

	public LScrollContainer(LTexture texture, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.backgroundTexture = texture;
		this.setElastic(true);
		this.setLayer(100);
	}

	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}

		LComponent[] childs = getComponents();
		synchronized (childs) {
			if (backgroundTexture == null) {
				g.setColor(LColor.gray);
				g.fillRect(x(), y(), getWidth(), getHeight());
			} else {
				g.drawTexture(backgroundTexture, x(), y(), getWidth(),
						getHeight());
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
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	public void moveScrollX(int newScrollX) {
		if (accumulate) {
			scrollX += newScrollX;
		} else {
			scrollX = newScrollX;
		}
	}

	public void moveScrollY(int newScrollY) {
		if (accumulate) {
			scrollY += newScrollY;
		} else {
			scrollY = newScrollY;
		}
	}

	public int getScrollX() {
		return scrollX;
	}

	public int getScrollY() {
		return scrollY;
	}

	public void add(LComponent comp) {
		super.add(comp);
		scrollContainerRealSizeChanged();
	}

	public synchronized void add(LComponent comp, int index) {
		super.add(comp, index);
		scrollContainerRealSizeChanged();
	}

	@Override
	public void setWidth(int width) {
		int scrollBarWidth = verticalScrollbar == null ? 0 : verticalScrollbar
				.getWidth();
		super.setWidth(width - scrollBarWidth);
		fitScrollBarSize();
	}

	@Override
	public void setHeight(int height) {
		int scrollbarHeight = horizontalScrollbar == null ? 0
				: horizontalScrollbar.getHeight();
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

	public void scrollContainerRealSizeChanged() {
		checkIfScrollbarIsNecessary();
		fitScrollBarSize();
	}

	private void checkIfScrollbarIsNecessary() {
		int maxX = getInnerWidth();
		if (maxX > getWidth()) {
			if (horizontalScrollbar != null) {
				addScrollbar(new LScrollBar(LScrollBar.BOTTOM));
			} else {
				horizontalScrollbar = new LScrollBar(LScrollBar.BOTTOM);
				addScrollbar(horizontalScrollbar);
			}
		}
		int maxY = getInnerHeight();
		if (maxY > getHeight()) {
			if (verticalScrollbar != null) {
				addScrollbar(new LScrollBar(LScrollBar.RIGHT));
			} else {
				verticalScrollbar = new LScrollBar(LScrollBar.RIGHT);
				addScrollbar(verticalScrollbar);
			}
		}
	}

	public int getInnerWidth() {
		int maxX = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxX = (int) MathUtils.max(x() + getComponents()[i].getX()
					+ getComponents()[i].getWidth(), maxX);
		}
		return maxX;
	}

	public int getInnerHeight() {
		int maxY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxY = (int) MathUtils.max(y() + getComponents()[i].getY()
					+ getComponents()[i].getHeight(), maxY);
		}
		return maxY;
	}

	public void addScrollbar(LScrollBar scrollBar) {
		if (scrollBar.getOrientation() == LScrollBar.LEFT
				|| scrollBar.getOrientation() == LScrollBar.RIGHT) {
			if (verticalScrollbar != null) {
				remove(verticalScrollbar);
			}
			verticalScrollbar = scrollBar;
			scrollBar.setScrollContainer(this);
			return;
		}
		if (horizontalScrollbar != null) {
			remove(horizontalScrollbar);
		}
		horizontalScrollbar = scrollBar;
		scrollBar.setScrollContainer(this);
	}

	protected void processTouchDragged() {
		super.processTouchDragged();
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchDragged();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchDragged();
		}
	}

	protected void processTouchPressed() {
		super.processKeyPressed();
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchPressed();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchPressed();
		}
	}

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

	public void setVerticalScrollbar(LScrollBar verticalScrollbar) {
		this.verticalScrollbar = verticalScrollbar;
	}

	public LScrollBar getHorizontalScrollbar() {
		return horizontalScrollbar;
	}

	public void setHorizontalScrollbar(LScrollBar horizontalScrollbar) {
		this.horizontalScrollbar = horizontalScrollbar;
	}

	public boolean isAccumulate() {
		return accumulate;
	}

	public void setAccumulate(boolean accumulate) {
		this.accumulate = accumulate;
	}

	public boolean isShowScroll() {
		return showScroll;
	}

	public void setShowScroll(boolean showScroll) {
		this.showScroll = showScroll;
	}

	@Override
	public String getUIName() {
		return "ScrollContainer";
	}

}

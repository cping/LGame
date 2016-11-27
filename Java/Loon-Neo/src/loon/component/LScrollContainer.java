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
import loon.opengl.GLEx;
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

	public LScrollContainer(String path, int x, int y, int w, int h) {
		this(LTextures.loadTexture(path), x, y, w, h);
	}

	public LScrollContainer(LTexture texture, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.backgroundTexture = texture;
		this.setElastic(true);
		this.setLayer(100);
	}

	@Override
	public void createUI(GLEx g) {
		if (isClose) {
			return;
		}
		if (!this.isVisible()) {
			return;
		}

		LComponent[] childs = super._childs;
		synchronized (childs) {
			try {
				g.saveTx();
				if (backgroundTexture == null) {
					g.fillRect(getScreenX(), getScreenY(), getWidth(),
							getHeight(), baseColor == null ? LColor.gray
									: baseColor.mul(LColor.gray));
				} else {
					g.draw(backgroundTexture, getScreenX(), getScreenY(),
							getWidth(), getHeight(), baseColor);
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

	@Override
	public void add(LComponent comp) {
		super.add(comp);
		scrollContainerRealSizeChanged();
	}

	@Override
	public synchronized void add(LComponent comp, int index) {
		super.add(comp, index);
		scrollContainerRealSizeChanged();
	}

	@Override
	public void setWidth(float width) {
		float scrollBarWidth = verticalScrollbar == null ? 0
				: verticalScrollbar.getWidth();
		super.setWidth(width - scrollBarWidth);
		fitScrollBarSize();
	}

	@Override
	public void setHeight(float height) {
		float scrollbarHeight = horizontalScrollbar == null ? 0
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
			maxX = (int) MathUtils.max(x() + super._childs[i].getX()
					+ super._childs[i].getWidth(), maxX);
		}
		return maxX;
	}

	public int getInnerHeight() {
		int maxY = 0;
		for (int i = 0; i < getComponentCount(); i++) {
			maxY = (int) MathUtils.max(y() + super._childs[i].getY()
					+ super._childs[i].getHeight(), maxY);
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

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		if (horizontalScrollbar != null) {
			horizontalScrollbar.processTouchDragged();
		}
		if (verticalScrollbar != null) {
			verticalScrollbar.processTouchDragged();
		}
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

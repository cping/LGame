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
import loon.component.skin.ScrollBarSkin;
import loon.component.skin.SkinManager;
import loon.events.SysTouch;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

/**
 * 滚轴边角的UI
 */
public class LScrollBar extends LComponent {

	private final static float SCROLL_SCALE_VALUE = 0.07f;

	public static final int RIGHT = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int TOP = 3;

	protected LScrollContainer _scrollContainer;

	protected int orientation;

	protected int sliderWidth;

	protected int sliderHeight;

	protected int sliderX;

	protected int sliderY;

	protected int relativeClickX;

	protected int relativeClickY;

	protected boolean _scrolling;

	private LTexture scrollBar;

	private LTexture slider;

	protected LColor scrollBarColor = LColor.white.darker();

	protected LColor sliderColor = LColor.black.darker();

	protected int sliderMargin = 3;

	protected float offsetX, offsetY;

	protected final int MAX_SLIDER_SIZE = 20;

	public LScrollBar(int orientation) {
		this(orientation, 0, 0, 150, 30);
	}

	public LScrollBar(int orientation, int x, int y, int width, int height) {
		this(SkinManager.get().getScrollBarSkin().getScrollBarTexture(),
				SkinManager.get().getScrollBarSkin().getSliderTexture(), orientation, x, y, width, height);
	}

	public LScrollBar(String patha, String pathb, int orientation, int x, int y, int width, int height) {
		this(LSystem.loadTexture(patha), LSystem.loadTexture(pathb), orientation, x, y, width, height);
	}

	public LScrollBar(ScrollBarSkin skin, int orientation, int x, int y, int width, int height) {
		this(skin.getScrollBarTexture(), skin.getSliderTexture(), x, y, orientation, width, height);
	}

	public LScrollBar(LTexture a, LTexture b, int orientation, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.orientation = orientation;
		this.scrollBar = a;
		this.slider = b;
		freeRes().add(a, b);
	}

	public LScrollBar setScrollContainer(LScrollContainer sc) {
		this._scrollContainer = sc;
		adjustScrollbar();
		return this;
	}

	public LScrollBar adjustScrollbar() {
		switch (getOrientation()) {
		case LScrollBar.TOP:
			initHorizontalTopScrollBar();
			break;
		case LScrollBar.RIGHT:
			initVerticalRightScrollBar();
			break;
		case LScrollBar.BOTTOM:
			initHorizontalBottomScrollBar();
			break;
		case LScrollBar.LEFT:
			initVerticalLeftScrollBar();
			break;
		}
		return this;
	}

	public LScrollBar adjustSlider() {
		switch (getOrientation()) {
		case LScrollBar.TOP:
			adjustHorizontalTopSlider();
			break;
		case LScrollBar.RIGHT:
			adjustVerticalRightSlider();
			break;
		case LScrollBar.BOTTOM:
			adjustHorizontalBottomSlider();
			break;
		case LScrollBar.LEFT:
			adjustVerticalLeftSlider();
			break;
		}
		return this;
	}

	public LScrollBar adjustSlider(int sliderX, int sliderY, int sliderWidth, int sliderHeight) {
		setSliderX(sliderX);
		setSliderY(sliderY);
		setSliderWidth(sliderWidth);
		setSliderHeight(sliderHeight);
		return this;
	}

	public LScrollBar adjustSlider(int sliderWidth, int sliderHeight) {
		adjustSlider(x() + getSliderMargin(), y() + getSliderMargin(), sliderWidth, sliderHeight);
		return this;
	}

	private void initVerticalLeftScrollBar() {
		if (_scrollContainer == null) {
			return;
		}
		int width = MathUtils.floor(_scrollContainer.getWidth() * SCROLL_SCALE_VALUE);
		width = width > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : width;
		setHeight(_scrollContainer.getHeight());
		setWidth(width);
		setX(_scrollContainer.getX());
		setY(_scrollContainer.getY());
		adjustVerticalLeftSlider();
	}

	private void adjustVerticalLeftSlider() {
		if (_scrollContainer == null) {
			return;
		}
		setSliderWidth(MathUtils.floor(getWidth() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioHeight = _scrollContainer.getHeight() / _scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight(MathUtils.floor((ratioHeight * getHeight()) - (getSliderMargin() * 2)) + 1);
	}

	private void initVerticalRightScrollBar() {
		if (_scrollContainer == null) {
			return;
		}
		int width = width();
		width = width > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : width;
		setWidth(width);
		setX(_scrollContainer.getX() + _scrollContainer.getWidth() - width);
		setY(_scrollContainer.getY());
		setHeight(_scrollContainer.getHeight());
		adjustVerticalRightSlider();
	}

	private void adjustVerticalRightSlider() {
		if (_scrollContainer == null) {
			return;
		}
		setSliderWidth(MathUtils.floor(getWidth() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioHeight = getHeight() / _scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight(MathUtils.floor((ratioHeight * getHeight()) - (getSliderMargin() * 2)) + 1);
	}

	private void initHorizontalTopScrollBar() {
		if (_scrollContainer == null) {
			return;
		}
		int height = MathUtils.floor(_scrollContainer.getHeight() * SCROLL_SCALE_VALUE);
		height = height > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : height;
		setWidth(_scrollContainer.getWidth());
		setHeight(height);
		setX(_scrollContainer.getX());
		setY(_scrollContainer.getY());
		adjustHorizontalTopSlider();
	}

	private void adjustHorizontalTopSlider() {
		if (_scrollContainer == null) {
			return;
		}
		setSliderHeight(MathUtils.floor(getHeight() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioWidth = getWidth() / _scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth(MathUtils.floor((ratioWidth * getWidth()) - (getSliderMargin() * 2)) + 1);
	}

	private void initHorizontalBottomScrollBar() {
		if (_scrollContainer == null) {
			return;
		}
		int height = MathUtils.floor(_scrollContainer.getWidth() * SCROLL_SCALE_VALUE);
		height = height > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : height;
		setHeight(height);
		setX(_scrollContainer.getX());
		setY(_scrollContainer.getY() + _scrollContainer.getHeight() - height);
		setWidth(_scrollContainer.getWidth());
		adjustHorizontalBottomSlider();
	}

	private void adjustHorizontalBottomSlider() {
		if (_scrollContainer == null) {
			return;
		}
		setSliderHeight(MathUtils.floor(getHeight() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioWidth = getWidth() / _scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth(MathUtils.floor((ratioWidth * getWidth()) - (getSliderMargin() * 2)) + 1);
	}

	@Override
	public LScrollBar setSize(float w, float h) {
		super.setSize(w, h);
		adjustSlider();
		return this;
	}

	public int getOrientation() {
		return orientation;
	}

	public int getSliderWidth() {
		return sliderWidth;
	}

	protected void setSliderWidth(int sliderWidth) {
		this.sliderWidth = sliderWidth;
	}

	public int getSliderHeight() {
		return sliderHeight;
	}

	public LScrollBar setSliderHeight(int sliderHeight) {
		this.sliderHeight = sliderHeight;
		return this;
	}

	public int getSliderX() {
		return sliderX;
	}

	protected void setSliderX(int sliderX) {
		this.sliderX = sliderX;
	}

	public int getSliderY() {
		return sliderY;
	}

	protected void setSliderY(int sliderY) {
		this.sliderY = sliderY;
	}

	public LColor getScrollBarColor() {
		return scrollBarColor.cpy();
	}

	public LScrollBar setScrollBarColor(LColor scrollBarColor) {
		this.scrollBarColor = scrollBarColor;
		return this;
	}

	public LColor getSliderColor() {
		return sliderColor.cpy();
	}

	public LScrollBar setSliderColor(LColor sliderColor) {
		this.sliderColor = sliderColor;
		return this;
	}

	public int getSliderMargin() {
		return sliderMargin;
	}

	public LScrollBar setSliderMargin(int sliderMargin) {
		this.sliderMargin = sliderMargin;
		return this;
	}

	public boolean isScrolling() {
		return _scrolling;
	}

	protected void setScroll(boolean sc) {
		this._scrolling = sc;
	}

	@Override
	protected void processTouchDragged() {
		super.processTouchDragged();
		if (isPointInUI(getTouchX(), getTouchY())) {
			touchDragged(getUITouchX(), getUITouchY());
		}
	}

	@Override
	protected void processTouchPressed() {
		super.processKeyPressed();
		if (isPointInUI(getTouchX(), getTouchY())) {
			touchDown(getUITouchX(), getUITouchY());
		}
	}

	@Override
	protected void processTouchReleased() {
		super.processTouchReleased();
		if (isPointInUI(getTouchX(), getTouchY())) {
			touchUp(getUITouchX(), getUITouchY());
		}
	}

	public boolean touchDown(float screenX, float screenY) {
		if (_scrollContainer != null) {
			relativeClickX = MathUtils.floor(screenX - (getX() + getSliderX() + _scrollContainer.getX()));
			relativeClickY = MathUtils.floor(screenY - (getY() + getSliderY() + _scrollContainer.getY()));
			return true;
		}
		return false;
	}

	public boolean touchUp(float screenX, float screenY) {
		if (_scrollContainer != null) {
			relativeClickX = MathUtils.floor(screenX);
			relativeClickY = MathUtils.floor(screenY);
			setScroll(false);
			return true;
		}
		return false;
	}

	public boolean touchDragged(float screenX, float screenY) {
		if (_scrollContainer != null) {
			int rClickX = MathUtils.floor(screenX - (getX() + _scrollContainer.getX()));
			int rClickY = MathUtils.floor(screenY - (getY() + _scrollContainer.getY()));
			rClickX -= relativeClickX;
			rClickY -= relativeClickY;
			moveSlider(rClickX, rClickY);
			return true;
		}
		return false;
	}

	protected void moveSlider(int dX, int dY) {
		if (orientation == LEFT || orientation == RIGHT) {
			moveVerticalSlider(dY);
		} else {
			moveHorizontalSlider(dX);
		}
	}

	protected void moveVerticalSlider(int newY) {
		int minY = y() + getSliderMargin();
		int maxY = MathUtils.floor(y() + getHeight() - (getSliderHeight() + getSliderMargin()));
		if (newY < minY) {
			setSliderY(minY);
		} else if (newY > maxY) {
			setSliderY(maxY);
		} else {
			setSliderY(newY);
		}
		updateScrollContainerY();
	}

	protected void updateScrollContainerY() {
		if (_scrollContainer == null) {
			return;
		}
		float ratio = ((_scrollContainer.getInnerHeight()) - _scrollContainer.getHeight())
				/ (getHeight() - getSliderHeight() - (getSliderMargin() * 2f));
		float relativeSliderY = getSliderY() - (getY() + getSliderMargin());
		_scrollContainer.moveScrollY(relativeSliderY * ratio);
	}

	protected void moveHorizontalSlider(int newX) {
		int minX = x() + getSliderMargin();
		int maxX = MathUtils.floor(x() + getWidth() - (getSliderWidth() + getSliderMargin()));
		if (newX < minX) {
			setSliderX(minX);
		} else if (newX > maxX) {
			setSliderX(maxX);
		} else {
			setSliderX(newX);
		}
		updateScrollContainerX();
	}

	protected void updateScrollContainerX() {
		if (_scrollContainer == null) {
			return;
		}
		float ratio = ((_scrollContainer.getInnerWidth()) - getWidth())
				/ (getWidth() - getSliderWidth() - (getSliderMargin() * 2f));
		float relativeSliderX = getSliderX() - (getX() + getSliderMargin());
		_scrollContainer.moveScrollX(relativeSliderX * ratio);
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		adjustSlider();
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		adjustSlider();
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		adjustSlider();
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		adjustSlider();
	}

	@Override
	public void process(final long elapsedTime) {
		if (SysTouch.isDrag()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				touchDragged(getUITouchX(), getUITouchY());
			}
		}
		if (SysTouch.isDown()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				touchDown(getUITouchX(), getUITouchY());
			}
		} else if (SysTouch.isDown()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				touchUp(getUITouchX(), getUITouchY());
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component, LTexture[] buttonImage) {
		if (scrollBar == null || slider == null) {
			g.fillRect(x - 1 + offsetX, y - 1 + offsetY, getWidth() + 2, getHeight() + 2, scrollBarColor);
			g.fillRect(sliderX - 1 + offsetX, sliderY - 1 + offsetY, sliderWidth, sliderHeight, sliderColor);
		} else {
			g.draw(scrollBar, x - 1 + offsetX, y - 1 + offsetY, getWidth() + 2, getHeight() + 2, _component_baseColor);
			g.draw(slider, sliderX - 1 + offsetX, sliderY - 1 + offsetY, sliderWidth, sliderHeight,
					_component_baseColor);
		}
	}

	public void paint(GLEx g) {
		createUI(g, x(), y(), null, null);
	}

	public float getBoxOffsetX() {
		return offsetX;
	}

	public LScrollBar setBoxOffsetX(float offsetX) {
		this.offsetX = offsetX;
		return this;
	}

	public float getBoxOffsetY() {
		return offsetY;
	}

	public LScrollBar setBoxOffsetY(float offsetY) {
		this.offsetY = offsetY;
		return this;
	}

	@Override
	public String getUIName() {
		return "ScrollBar";
	}

	@Override
	public void destory() {

	}

}

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

/**
 * 滚轴边角的UI
 */
public class LScrollBar extends LComponent {

	public static final int RIGHT = 0;
	public static final int BOTTOM = 1;
	public static final int LEFT = 2;
	public static final int TOP = 3;

	protected LScrollContainer scrollContainer;

	protected int orientation;

	protected int sliderWidth;

	protected int sliderHeight;

	protected int sliderX;

	protected int sliderY;

	protected int relativeClickX;

	protected int relativeClickY;

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

	public void setScrollContainer(LScrollContainer scrollContainer) {
		this.scrollContainer = scrollContainer;
		adjustScrollbar();
	}

	public void adjustScrollbar() {
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
	}

	public void adjustSlider() {
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
	}

	private void initVerticalLeftScrollBar() {
		setHeight(scrollContainer.getHeight());

		int width = (int) (scrollContainer.getWidth() * 0.07);
		setWidth(width > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : width);

		setX(scrollContainer.getX() - getWidth());
		setY(scrollContainer.getY());

		adjustVerticalLeftSlider();
	}

	public void adjustSlider(int sliderX, int sliderY, int sliderWidth, int sliderHeight) {
		setSliderX(sliderX);
		setSliderY(sliderY);
		setSliderWidth(sliderWidth);
		setSliderHeight(sliderHeight);
	}

	public void adjustSlider(int sliderWidth, int sliderHeight) {
		adjustSlider(x() + getSliderMargin(), y() + getSliderMargin(), sliderWidth, sliderHeight);
	}

	private void adjustVerticalLeftSlider() {
		setSliderWidth((int) (getWidth() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());

		float ratioHeight = scrollContainer.getHeight() / scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight((int) ((ratioHeight * getHeight()) - (getSliderMargin() * 2)) + 1);
	}

	private void initVerticalRightScrollBar() {
		int width = (int) (scrollContainer.getWidth() * 0.07);
		setWidth(width > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : width);
		setX(scrollContainer.getX() + scrollContainer.getWidth() - width);
		setY(scrollContainer.getY());
		setHeight(scrollContainer.getHeight());
		adjustVerticalRightSlider();
	}

	private void adjustVerticalRightSlider() {
		setSliderWidth((int) (getWidth() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioHeight = getHeight() / scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight((int) ((ratioHeight * getHeight()) - (getSliderMargin() * 2)) + 1);
	}

	private void initHorizontalTopScrollBar() {
		setWidth(scrollContainer.getWidth());
		int height = (int) (scrollContainer.getWidth() * 0.07);
		setHeight(height > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : height);
		setX(scrollContainer.getX());
		setY(scrollContainer.getY() - getHeight());
		adjustHorizontalTopSlider();
	}

	private void adjustHorizontalTopSlider() {
		setSliderHeight((int) (getHeight() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		float ratioWidth = getWidth() / scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth((int) ((ratioWidth * getWidth()) - (getSliderMargin() * 2)) + 1);
	}

	private void initHorizontalBottomScrollBar() {
		int height = (int) (scrollContainer.getWidth() * 0.07);
		setHeight(height > MAX_SLIDER_SIZE ? MAX_SLIDER_SIZE : height);

		setX(scrollContainer.getX());
		setY(scrollContainer.getY() + scrollContainer.getHeight() - getHeight());

		setWidth(scrollContainer.getWidth());

		adjustHorizontalBottomSlider();
	}

	private void adjustHorizontalBottomSlider() {
		setSliderHeight((int) (getHeight() - (getSliderMargin() * 2)) + 1);
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());

		float ratioWidth = getWidth() / scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth((int) ((ratioWidth * getWidth()) - (getSliderMargin() * 2)) + 1);
	}

	@Override
	public LScrollBar setSize(float w, float h) {
		super.setSize(w, h);
		adjustSlider();
		return this;
	}

	@Override
	public void update(final long elapsedTime) {
		super.update(elapsedTime);
		if (SysTouch.isDrag()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				touchDragged(getUITouchX(), getUITouchY());
			}
		}
		if (SysTouch.isDown()) {
			if (isPointInUI(getTouchX(), getTouchY())) {
				touchDown(getUITouchX(), getUITouchY());
			}
		}
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

	public void setSliderHeight(int sliderHeight) {
		this.sliderHeight = sliderHeight;
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

	public void setScrollBarColor(LColor scrollBarColor) {
		this.scrollBarColor = scrollBarColor;
	}

	public LColor getSliderColor() {
		return sliderColor.cpy();
	}

	public void setSliderColor(LColor sliderColor) {
		this.sliderColor = sliderColor;
	}

	public int getSliderMargin() {
		return sliderMargin;
	}

	public void setSliderMargin(int sliderMargin) {
		this.sliderMargin = sliderMargin;
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
			touchDown(getUITouchX(), getUITouchY());
		}
	}

	public boolean touchDown(float screenX, float screenY) {
		if (scrollContainer != null) {
			relativeClickX = (int) (screenX - (x() + getSliderX() + scrollContainer.x()));
			relativeClickY = (int) (screenY - (y() + getSliderY() + scrollContainer.y()));
			return true;
		}
		return false;
	}

	public boolean touchDragged(float screenX, float screenY) {
		if (scrollContainer != null) {
			int rClickX = (int) (screenX - (x() + scrollContainer.x()));
			int rClickY = (int) (screenY - (y() + scrollContainer.y()));
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
		int maxY = (int) (y() + getHeight() - (getSliderHeight() + getSliderMargin()));
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
		float ratio = (float) ((scrollContainer.getInnerHeight()) - scrollContainer.getHeight())
				/ (getHeight() - getSliderHeight() - (getSliderMargin() * 2));
		if (Float.isNaN(ratio) || Float.isInfinite(ratio)) {
			ratio = 0f;
		}
		int relativeSliderY = getSliderY() - (y() + getSliderMargin());
		scrollContainer.moveScrollY((int) (relativeSliderY * ratio));
	}

	protected void moveHorizontalSlider(int newX) {
		int minX = x() + getSliderMargin();
		int maxX = (int) (x() + getWidth() - (getSliderWidth() + getSliderMargin()));
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
		float ratio = ((scrollContainer.getInnerWidth()) - getWidth())
				/ (getWidth() - getSliderWidth() - (getSliderMargin() * 2));
		if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
			ratio = 0.0f;
		}
		int relativeSliderX = getSliderX() - (x() + getSliderMargin());
		scrollContainer.moveScrollX((int) (relativeSliderX * ratio));
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

}

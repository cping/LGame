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

	protected final int MAX_SLIDER_SIZE = 20;

	public LScrollBar(int orientation) {
		this(orientation, 0, 0, 150, 30);
	}

	public LScrollBar(int orientation, int x, int y, int width, int height) {
		this(DefUI.getDefaultTextures(2), DefUI.getDefaultTextures(8),
				orientation, x, y, width, height);
	}

	public LScrollBar(LTexture a, LTexture b, int orientation, int x, int y,
			int width, int height) {
		super(x, y, width, height);
		this.orientation = orientation;
		this.scrollBar = a;
		this.slider = b;
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

	private void adjustVerticalLeftSlider() {
		setSliderWidth(getWidth() - (getSliderMargin() * 2));
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());

		double ratioHeight = (double) scrollContainer.getHeight()
				/ scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight((int) ((ratioHeight * getHeight()) - (getSliderMargin() * 2)));
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
		setSliderWidth(getWidth() - (getSliderMargin() * 2));
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		double ratioHeight = (double) getHeight()
				/ scrollContainer.getInnerHeight();
		ratioHeight = ratioHeight > 1 ? 1 : ratioHeight;
		setSliderHeight((int) ((ratioHeight * getHeight()) - (getSliderMargin() * 2)));
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
		setSliderHeight(getHeight() - (getSliderMargin() * 2));
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());
		double ratioWidth = (double) getWidth()
				/ scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth((int) ((ratioWidth * getWidth()) - (getSliderMargin() * 2)));
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
		setSliderHeight(getHeight() - (getSliderMargin() * 2));
		setSliderX(x() + getSliderMargin());
		setSliderY(y() + getSliderMargin());

		double ratioWidth = (double) getWidth()
				/ scrollContainer.getInnerWidth();
		ratioWidth = ratioWidth > 1 ? 1 : ratioWidth;
		setSliderWidth((int) ((ratioWidth * getWidth()) - (getSliderMargin() * 2)));
	}

	public void setSize(int w, int h) {
		super.setSize(w, h);
		adjustSlider();
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
		return scrollBarColor;
	}

	public void setScrollBarColor(LColor scrollBarColor) {
		this.scrollBarColor = scrollBarColor;
	}

	public LColor getSliderColor() {
		return sliderColor;
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

	protected void processTouchDragged() {
		super.processTouchDragged();
		touchDragged(getTouchX(), getTouchY());
	}

	protected void processTouchPressed() {
		super.processKeyPressed();
		touchDown(getTouchX(), getTouchY());
	}

	protected void processTouchReleased() {
		super.processTouchReleased();
		touchDown(getTouchX(), getTouchY());
	}

	public boolean touchDown(float screenX, float screenY) {
		if (scrollContainer != null) {
			relativeClickX = (int) (screenX - (x() + getSliderX() + scrollContainer
					.x()));
			relativeClickY = (int) (screenY - (y() + getSliderY() + scrollContainer
					.y()));
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
		int maxY = y() + getHeight() - (getSliderHeight() + getSliderMargin());
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
		double ratio = (double) ((scrollContainer.getInnerHeight()) - scrollContainer
				.getHeight())
				/ (getHeight() - getSliderHeight() - (getSliderMargin() * 2));
		if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
			ratio = 0.0;
		}
		int relativeSliderY = getSliderY() - (y() + getSliderMargin());
		scrollContainer.moveScrollY((int) (relativeSliderY * ratio));
	}

	protected void moveHorizontalSlider(int newX) {
		int minX = x() + getSliderMargin();
		int maxX = x() + getWidth() - (getSliderWidth() + getSliderMargin());
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
		double ratio = (double) ((scrollContainer.getInnerWidth()) - getWidth())
				/ (getWidth() - getSliderWidth() - (getSliderMargin() * 2));
		if (Double.isNaN(ratio) || Double.isInfinite(ratio)) {
			ratio = 0.0;
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

	public void setWidth(int width) {
		super.setWidth(width);
		adjustSlider();
	}

	public void setHeight(int height) {
		super.setHeight(height);
		adjustSlider();
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (scrollBar == null || slider == null) {
			g.fillRect(x, y, getWidth()+2, getHeight()+2, scrollBarColor);
			g.fillRect(sliderX, sliderY, sliderWidth, sliderHeight, sliderColor);
		} else {
			g.fillRect(x, y, getWidth(), getHeight(), scrollBarColor);
			g.fillRect(sliderX, sliderY, sliderWidth, sliderHeight, sliderColor);
		}
	}

	public void paint(GLEx g) {
		createUI(g, x(), y(), null, null);
	}

	@Override
	public String getUIName() {
		return "ScrollBar";
	}

}

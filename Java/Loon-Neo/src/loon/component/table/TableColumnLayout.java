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
package loon.component.table;

import loon.canvas.LColor;
import loon.component.LComponent;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class TableColumnLayout {

	public static final int HORIZONTAL_ALIGN_CENTER = 1;
	public static final int HORIZONTAL_ALIGN_RIGHT = 2;
	public static final int HORIZONTAL_ALIGN_LEFT = 3;

	public static final int VERTICAL_ALIGN_TOP = 4;
	public static final int VERTICAL_ALIGN_CENTER = 5;
	public static final int VERTICAL_ALIGN_BOTTOM = 6;

	private int verticalAlignment = VERTICAL_ALIGN_CENTER;

	private int horizontalAlignment = HORIZONTAL_ALIGN_CENTER;

	private int leftMargin = 0;

	private int rightMargin = 0;

	private int topMargin = 0;

	private int bottomMargin = 0;

	private int x;

	private int y;

	private double width;

	private int height;

	private LComponent component;

	public TableColumnLayout(LComponent component, int x, int y, int width, int height) {
		this.component = component;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getHeight() {
		return height;
	}

	public TableColumnLayout setHeight(int height) {
		this.height = height;
		adjustComponent();
		return this;
	}

	public int getWidth() {
		return MathUtils.floorInt(width);
	}

	public double getWidthf() {
		return width;
	}

	public TableColumnLayout setWidth(double width) {
		this.width = width;
		adjustComponent();
		return this;
	}

	public int getX() {
		return x;
	}

	public TableColumnLayout setX(int x) {
		this.x = x;
		adjustComponent();
		return this;
	}

	public int getY() {
		return y;
	}

	public TableColumnLayout setY(int y) {
		this.y = y;
		adjustComponent();
		return this;
	}

	public int getVerticalAlignment() {
		return verticalAlignment;
	}

	public TableColumnLayout setVerticalAlignment(int verticalAlignment) {
		this.verticalAlignment = verticalAlignment;
		adjustComponent();
		return this;
	}

	public int getHorizontalAlignment() {
		return horizontalAlignment;
	}

	public TableColumnLayout setHorizontalAlignment(int horizontalAlignment) {
		this.horizontalAlignment = horizontalAlignment;
		adjustComponent();
		return this;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public TableColumnLayout setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
		adjustComponent();
		return this;
	}

	public int getRightMargin() {
		return rightMargin;
	}

	public TableColumnLayout setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
		adjustComponent();
		return this;
	}

	public int getTopMargin() {
		return topMargin;
	}

	public TableColumnLayout setTopMargin(int topMargin) {
		this.topMargin = topMargin;
		adjustComponent();
		return this;
	}

	public int getBottomMargin() {
		return bottomMargin;
	}

	public TableColumnLayout setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
		adjustComponent();
		return this;
	}

	public TableColumnLayout setMargin(int marginLeft, int marginRight, int marginTop, int marginBottom) {
		setLeftMargin(marginLeft);
		setRightMargin(marginRight);
		setTopMargin(marginTop);
		setBottomMargin(marginBottom);
		return this;
	}

	public TableColumnLayout setComponent(LComponent component) {
		this.component = component;
		adjustComponent();
		return this;
	}

	public LComponent getComponent() {
		return component;
	}

	public boolean canWidthShrink(int newWidth) {
		return newWidth > getMinWidth();
	}

	public int getMinWidth() {
		if (component != null) {
			return (int) (leftMargin + rightMargin + component.getWidth());
		}
		return 1;
	}

	public boolean canHeightShrink(int newHeight) {
		return newHeight > getMinHeight();
	}

	public int getMinHeight() {
		if (component != null) {
			return (int) (topMargin + bottomMargin + component.getHeight());
		}
		return 1;
	}

	public TableColumnLayout adjustComponent() {
		if (component != null) {
			switch (horizontalAlignment) {
			case HORIZONTAL_ALIGN_LEFT:
				component.setX(getX() + leftMargin);
				break;
			case HORIZONTAL_ALIGN_CENTER:
				component.setX(getX() + (getWidth() / 2 - component.getWidth() / 2));
				break;
			case HORIZONTAL_ALIGN_RIGHT:
				component.setX((getX() + getWidth()) - (component.getWidth() + rightMargin));
				break;
			}
			switch (verticalAlignment) {
			case VERTICAL_ALIGN_TOP:
				component.setY(getY() + topMargin);
				break;
			case VERTICAL_ALIGN_CENTER:
				component.setY(getY() + (getHeight() / 2 - component.getHeight() / 2));
				break;
			case VERTICAL_ALIGN_BOTTOM:
				component.setY((getY() + getHeight()) - (component.getHeight() + bottomMargin));
			}
		}
		return this;
	}

	public void paint(GLEx g) {
		g.drawRect(getX(), getY(), getWidth(), getHeight(), LColor.white);
		if (component != null && component.getContainer() != null && component.getContainer() instanceof TableLayout) {
			if (((TableLayout) component.getContainer()).isGrid()) {
				g.drawRect(component.getContainer().getX() + component.getX() - leftMargin,
						component.getContainer().getY() + component.getY() - topMargin,
						component.getWidth() + rightMargin, component.getHeight() + bottomMargin, LColor.red);
			}
		}
	}

}

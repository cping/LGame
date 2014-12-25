package loon.core.graphics;

import loon.core.graphics.component.DefUI;
import loon.core.graphics.component.LScrollBar;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;

public class LScrollContainer extends LContainer {

	private int scrollX;

	private int scrollY;

	private LScrollBar verticalScrollbar;

	private LScrollBar horizontalScrollbar;

	private LTexture background;

	public LScrollContainer(int x, int y, int w, int h) {
		this(DefUI.getDefaultTextures(8), x, y, w, h);
	}

	public LScrollContainer(LTexture texture, int x, int y, int w, int h) {
		super(x, y, w, h);
		this.background = texture;
		this.setElastic(true);
		this.setLayer(0);
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
			if (background == null) {
				g.setColor(LColor.blue);
				g.fillRect(x(), y(), getWidth(), getHeight());
			} else {
				g.drawTexture(background, x(), y(), getWidth(), getHeight());
			}
			g.translate(scrollX, scrollY);
			super.createUI(g);
			if (this.elastic) {
				g.setClip(this.getScreenX(), this.getScreenY(),
						this.getWidth(), this.getHeight());
			}
			this.renderComponents(g);
			g.translate(-scrollX, -scrollY);
			if (verticalScrollbar != null) {
				verticalScrollbar.paint(g);
			}
			if (horizontalScrollbar != null) {
				horizontalScrollbar.paint(g);
			}
			if (this.elastic) {
				g.clearClip();
			}
		}
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {

	}

	public void moveScrollX(int newScrollX) {
		scrollX += newScrollX;
	}

	public void moveScrollY(int newScrollY) {
		scrollY += newScrollY;
	}

	public int getScrollX() {
		return scrollX;
	}

	public int getScrollY() {
		return scrollY;
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
			}
		}
		int maxY = getInnerHeight();
		if (maxY > getHeight()) {
			if (verticalScrollbar != null) {
				addScrollbar(new LScrollBar(LScrollBar.RIGHT));
			}
		}
	}

	public int getInnerWidth() {
		int maxX = 0;
		for (int i = 0; i < getContainer().getComponentCount(); i++) {
			maxX = (int) Math.max(getContainer().getComponents()[i].getX()
					+ getContainer().getComponents()[i].getWidth(), maxX);
		}
		return maxX;
	}

	public int getInnerHeight() {
		int maxY = 0;
		for (int i = 0; i < getContainer().getComponentCount(); i++) {
			maxY = (int) Math.max(getContainer().getComponents()[i].getY()
					+ getContainer().getComponents()[i].getHeight(), maxY);
		}
		return maxY;
	}

	public void addScrollbar(LScrollBar scrollBar) {
		if (scrollBar.getOrientation() == LScrollBar.LEFT
				|| scrollBar.getOrientation() == LScrollBar.RIGHT) {
			if (verticalScrollbar != null) {
				getContainer().remove(verticalScrollbar);
			}
			verticalScrollbar = scrollBar;
			scrollBar.setScrollContainer(this);
			return;
		}
		if (horizontalScrollbar != null) {
			getContainer().remove(horizontalScrollbar);
		}
		horizontalScrollbar = scrollBar;
		scrollBar.setScrollContainer(this);
	}

	@Override
	public String getUIName() {
		return "ScrollContainer";
	}

}

package loon.component.layout;

import loon.LObject;
import loon.LSystem;
import loon.Screen;
import loon.component.LClickButton;
import loon.component.LComponent;
import loon.component.LContainer;
import loon.event.ClickListener;
import loon.geom.SizeValue;
import loon.utils.TArray;

public abstract class LayoutManager {

	public final static void elements(final Screen root,
			final TArray<LObject<?>> objs, int sx, int sy, int bwidth,
			int bheight, int maxHeight) {
		final int offsetX = 2;
		final int offsetY = 2;
		elements(root, objs, sx, sy, bwidth, bheight, offsetX, offsetY,
				maxHeight);
	}

	public final static void elements(final Screen root,
			final TArray<LObject<?>> objs, int sx, int sy, int bwidth, int bheight) {
		final int offsetX = 2;
		final int offsetY = 2;
		elements(root, objs, sx, sy, bwidth, bheight, offsetX, offsetY,
				(int) (LSystem.viewSize.height - bheight - offsetY));
	}

	public final static void elements(final Screen root,
			final TArray<LObject<?>> objs, int sx, int sy, int bwidth,
			int bheight, int offsetX, int offsetY, int maxHeight) {
		int x = sx;
		int y = sy;
		for (int i = 0; i < objs.size; i++) {
			LObject<?> obj = objs.get(i);
			obj.setLocation(x + offsetX, y + offsetY);
			root.add(obj);
			y += bheight + offsetY;
			if (y >= maxHeight) {
				y = sy;
				x += bwidth + offsetX;
			}
		}
	}

	public final static TArray<LClickButton> elementButtons(final Screen root,
			final String[] names, int sx, int sy, int bwidth, int bheight,
			ClickListener listener, int maxHeight) {
		final int offsetX = 2;
		final int offsetY = 2;
		return elementButtons(root, names, sx, sy, bwidth, bheight, offsetX,
				offsetY, listener, maxHeight);
	}

	public final static TArray<LClickButton> elementButtons(final Screen root,
			final String[] names, int sx, int sy, int bwidth, int bheight,
			ClickListener listener) {
		final int offsetX = 2;
		final int offsetY = 2;
		return elementButtons(root, names, sx, sy, bwidth, bheight, offsetX,
				offsetY, listener, (int) (LSystem.viewSize.height - bheight
						- offsetY - sy));
	}

	public final static TArray<LClickButton> elementButtons(final Screen root,
			final String[] names, int sx, int sy, int bwidth, int bheight,
			int offsetX, int offsetY, ClickListener listener, int maxHeight) {
		int x = sx;
		int y = sy;
		TArray<LClickButton> clicks = new TArray<LClickButton>(names.length);
		for (int i = 0; i < names.length; i++) {
			LClickButton click = LClickButton.make(names[i], bwidth, bheight);
			click.setLocation(x + offsetX, y + offsetY);
			click.SetClick(listener);
			root.add(click);
			clicks.add(click);
			y += bheight + offsetY;
			if (y >= maxHeight) {
				y = sy;
				x += bwidth + offsetX;
			}
		}
		return clicks;
	}

	protected boolean _allow = true;

	public final LayoutManager setChangeSize(boolean allow) {
		this._allow = allow;
		return this;
	}

	public final boolean isAllowChangeSize() {
		return _allow;
	}

	public final void layoutElements(final Screen root,
			final LComponent... children) {
		int size = children.length;
		LayoutPort[] ports = new LayoutPort[size];
		for (int i = 0; i < size; i++) {
			ports[i] = children[i].getLayoutPort();
		}
		layoutElements(root.getLayoutPort(), ports);
	}
	
	public final void layoutElements(final LContainer root,
			final LComponent... children) {
		int size = children.length;
		LayoutPort[] ports = new LayoutPort[size];
		for (int i = 0; i < size; i++) {
			ports[i] = children[i].getLayoutPort();
		}
		layoutElements(root.getLayoutPort(), ports);
	}
	
	public abstract void layoutElements(LayoutPort root, LayoutPort... children);

	abstract SizeValue calculateConstraintWidth(LayoutPort root,
			TArray<LayoutPort> children);

	abstract SizeValue calculateConstraintHeight(LayoutPort root,
			TArray<LayoutPort> children);
}

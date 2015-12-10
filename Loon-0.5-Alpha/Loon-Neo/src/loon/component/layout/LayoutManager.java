package loon.component.layout;

import loon.Screen;
import loon.component.LComponent;
import loon.geom.SizeValue;
import loon.utils.TArray;

public abstract class LayoutManager {

	protected boolean _allow = true;

	public final void setChangeSize(boolean allow) {
		this._allow = allow;
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

	public abstract void layoutElements(LayoutPort root, LayoutPort... children);

	abstract SizeValue calculateConstraintWidth(LayoutPort root,
			TArray<LayoutPort> children);

	abstract SizeValue calculateConstraintHeight(LayoutPort root,
			TArray<LayoutPort> children);
}

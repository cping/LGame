package loon.component.layout;

import loon.Screen;
import loon.component.LComponent;
import loon.geom.SizeValue;
import loon.utils.TArray;

public abstract class LayoutManager {

	public final void layoutElements(final Screen root,
			final LComponent... children) {
		int size = children.length;
		LayoutPort[] ports = new LayoutPort[size];
		for (int i = 0; i < size; i++) {
			ports[i] = children[i].getLayoutPort();
		}
		layoutElements(root.getLayoutPort(), ports);
	}

	abstract void layoutElements(LayoutPort root, LayoutPort... children);

	abstract SizeValue calculateConstraintWidth(LayoutPort root,
			TArray<LayoutPort> children);

	abstract SizeValue calculateConstraintHeight(LayoutPort root,
			TArray<LayoutPort> children);
}

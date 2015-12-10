package loon.component.layout;

import loon.geom.SizeValue;
import loon.utils.TArray;

public class OverlayLayout extends LayoutManager {
	
	public final void layoutElements(final LayoutPort rootElement,
			final LayoutPort... elements) {

		if (rootElement == null || elements == null || elements.length == 0) {
			return;
		}

		BoxSize rootBox = rootElement.getBox();

		for (int i = 0; i < elements.length; i++) {
			LayoutPort p = elements[i];
			BoxSize box = p.getBox();
			box.setX(rootBox.getX());
			box.setY(rootBox.getY());
			box.setWidth(rootBox.getWidth());
			box.setHeight(rootBox.getHeight());
		}
	}

	public final SizeValue calculateConstraintWidth(final LayoutPort root,
			final TArray<LayoutPort> children) {
		return null;
	}

	public final SizeValue calculateConstraintHeight(final LayoutPort root,
			final TArray<LayoutPort> children) {
		return null;
	}
}

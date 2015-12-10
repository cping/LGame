package loon.component.layout;

import loon.geom.RectBox;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class OverlayLayout implements LayoutManager {

	public final void layoutElements(final LayoutPort rootElement,
			final TArray<LayoutPort> elements) {

		if (rootElement == null || elements == null || elements.size == 0) {
			return;
		}

		RectBox rootBox = rootElement.getBox();

		for (int i = 0; i < elements.size; i++) {
			LayoutPort p = elements.get(i);
			RectBox box = p.getBox();
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

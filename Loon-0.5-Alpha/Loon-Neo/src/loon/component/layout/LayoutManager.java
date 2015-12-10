package loon.component.layout;

import loon.geom.SizeValue;
import loon.utils.TArray;

public interface LayoutManager {

	void layoutElements(LayoutPort root, TArray<LayoutPort> children);

	SizeValue calculateConstraintWidth(LayoutPort root,
			TArray<LayoutPort> children);

	SizeValue calculateConstraintHeight(LayoutPort root,
			TArray<LayoutPort> children);
}

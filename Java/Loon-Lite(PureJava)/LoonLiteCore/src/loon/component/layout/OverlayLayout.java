/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
 * @version 0.5
 */
package loon.component.layout;

import loon.geom.BoxSize;
import loon.geom.SizeValue;
import loon.utils.TArray;

public class OverlayLayout extends LayoutManager {

	public final static OverlayLayout at() {
		return new OverlayLayout();
	}

	@Override
	public final LayoutManager layoutElements(final LayoutPort rootElement, final LayoutPort... elements) {

		if (rootElement == null || elements == null || elements.length == 0) {
			return this;
		}

		BoxSize rootBox = rootElement.getBox();

		for (int i = 0; i < elements.length; i++) {
			LayoutPort p = elements[i];
			BoxSize box = p.getBox();
			box.setX(rootBox.getX());
			box.setY(rootBox.getY());
			if (_allow) {
				box.setWidth(rootBox.getWidth());
				box.setHeight(rootBox.getHeight());
			}
		}
		return this;
	}

	final SizeValue calculateConstraintWidth(final LayoutPort root, final TArray<LayoutPort> children) {
		return null;
	}

	final SizeValue calculateConstraintHeight(final LayoutPort root, final TArray<LayoutPort> children) {
		return null;
	}
}

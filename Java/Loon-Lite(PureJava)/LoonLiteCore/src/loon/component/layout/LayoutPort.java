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
import loon.geom.RectBox;
import loon.geom.SizeValue;
import loon.utils.StrBuilder;

public class LayoutPort {

	private BoxSize box;

	private LayoutConstraints boxConstraints;

	public LayoutPort() {
		this.box = new RectBox();
		this.boxConstraints = new LayoutConstraints();
	}

	public LayoutPort(final BoxSize newBox, final LayoutConstraints newBoxConstraints) {
		this.box = newBox;
		this.boxConstraints = newBoxConstraints;
	}

	public LayoutPort(final LayoutPort src) {
		this.box = src.getBox();
		this.boxConstraints = new LayoutConstraints(src.getBoxConstraints());
	}

	public static void updateLayoutPart(final LayoutPort layoutPart, final int width, final int height) {
		BoxSize box = layoutPart.getBox();
		box.setWidth(width);
		box.setHeight(height);
		LayoutConstraints boxConstraints = layoutPart.getBoxConstraints();
		boxConstraints.setWidth(new SizeValue(width + "px"));
		boxConstraints.setHeight(new SizeValue(height + "px"));
	}

	public final BoxSize getBox() {
		return box;
	}

	public final LayoutConstraints getBoxConstraints() {
		return boxConstraints;
	}

	@Override
	public String toString() {
		StrBuilder result = new StrBuilder();
		result.append("box [" + box.getX() + ", " + box.getY() + ", " + box.getWidth() + ", " + box.getHeight()
				+ "] with constraints [" + boxConstraints.getX() + ", " + boxConstraints.getY() + ", "
				+ boxConstraints.getWidth() + ", " + boxConstraints.getHeight() + "]");
		return result.toString();
	}
}

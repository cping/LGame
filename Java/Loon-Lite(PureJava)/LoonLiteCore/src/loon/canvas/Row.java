/**
 * Copyright 2008 - 2019 The Loon Game Engine Authors
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
package loon.canvas;

import loon.geom.RectI;
import loon.utils.MathUtils;

public class Row implements Comparable<Row> {

	public static enum RowType {
		FIX, // 固定
		HORIZONTALPATCH, // 水平拉伸
		VERTICALPATCH, // 垂直拉伸
		TILEPATCH // 平铺
	}

	private RectI rect;

	private RowType type;

	public Row(RectI rect, RowType type) {
		this.rect = rect;
		this.type = type;
	}

	public RectI getRect() {
		return rect;
	}

	public RowType getTypeCode() {
		return type;
	}

	@Override
	public int compareTo(Row o) {
		return MathUtils.compare(getRect().x, o.getRect().x);
	}

	@Override
	public String toString() {
		return "row [row=" + rect + ", type=" + type + "]";
	}

}

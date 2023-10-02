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
package loon.action.collision;

import loon.geom.PointF;
import loon.geom.RectF;
import loon.utils.MathUtils;

public class CollisionGrid {

	public static interface TraverseCallback {

		public void onTraverse(float cx, float cy);
	}

	private final PointF traverse_c1 = new PointF();
	private final PointF traverse_c2 = new PointF();
	private final PointF traverse_initStepX = new PointF();
	private final PointF traverse_initStepY = new PointF();
	private final PointF cellRect_cxy = new PointF();

	public static void toWorld(float cellSizeX, float cellSizeY, float cx, float cy, PointF point) {
		point.set((cx - 1) * cellSizeX, (cy - 1) * cellSizeY);
	}

	public static void toCell(float cellSizeX, float cellSizeY, float x, float y, PointF point) {
		point.set(MathUtils.floor(x / cellSizeX) + 1, MathUtils.floor(y / cellSizeY) + 1);
	}

	public static int initStep(float cellSizeX, float cellSizeY, float ct, float t1, float t2, PointF point) {
		float v = t2 - t1;
		if (v > 0) {
			point.set(cellSizeX / v, ((ct + v) * cellSizeY - t1) / v);
			return 1;
		} else if (v < 0) {
			point.set(-cellSizeX / v, ((ct + v - 1) * cellSizeY - t1) / v);
			return -1;
		} else {
			point.set(Float.MAX_VALUE, Float.MAX_VALUE);
			return 0;
		}
	}

	public void traverse(float cellSizeX, float cellSizeY, float x1, float y1, float x2, float y2, TraverseCallback f) {
		toCell(cellSizeX, cellSizeY, x1, y1, traverse_c1);
		float cx1 = traverse_c1.x;
		float cy1 = traverse_c1.y;
		toCell(cellSizeX, cellSizeY, x2, y2, traverse_c2);
		float cx2 = traverse_c2.x;
		float cy2 = traverse_c2.y;
		int stepX = initStep(cellSizeX, cellSizeY, cx1, x1, x2, traverse_initStepX);
		int stepY = initStep(cellSizeX, cellSizeY, cy1, y1, y2, traverse_initStepY);
		float dx = traverse_initStepX.x;
		float tx = traverse_initStepX.y;
		float dy = traverse_initStepY.x;
		float ty = traverse_initStepY.y;
		float cx = cx1, cy = cy1;

		f.onTraverse(cx, cy);

		while (MathUtils.abs(cx - cx2) + MathUtils.abs(cy - cy2) > 1) {
			if (tx < ty) {
				tx = tx + dx;
				cx = cx + stepX;
				f.onTraverse(cx, cy);
			} else {
				if (tx == ty) {
					f.onTraverse(cx + stepX, cy);
				}
				ty = ty + dy;
				cy = cy + stepY;
				f.onTraverse(cx, cy);
			}
		}

		if (cx != cx2 || cy != cy2) {
			f.onTraverse(cx2, cy2);
		}
	}

	public RectF toCellRect(float cellSizeX, float cellSizeY, float x, float y, float w, float h, RectF rect) {
		toCell(cellSizeX, cellSizeY, x, y, cellRect_cxy);
		float cx = cellRect_cxy.x;
		float cy = cellRect_cxy.y;

		float cr = MathUtils.ceil((x + w) / cellSizeX);
		float cb = MathUtils.ceil((y + h) / cellSizeY);

		rect.set(cx, cy, cr - cx + 1, cb - cy + 1);
		return rect;
	}
}

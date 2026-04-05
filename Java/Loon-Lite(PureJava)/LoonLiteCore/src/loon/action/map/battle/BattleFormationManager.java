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
package loon.action.map.battle;

import loon.action.map.Direction;
import loon.geom.PointI;
import loon.utils.MathUtils;
import loon.utils.TArray;

public class BattleFormationManager {

	public static enum FormationType {
		// 方阵
		SQUARE,
		// 横排
		LINE_H,
		// 竖排
		LINE_V,
		// 楔形
		WEDGE,
		// V形
		V_SHAPE,
		// 环形
		CIRCLE
	}

	private int spacing = 1;

	private final BattleMap battleMap;

	public BattleFormationManager(BattleMap iso) {
		this.battleMap = iso;
	}

	public void setSpacing(int tileSpacing) {
		this.spacing = tileSpacing;
	}

	public PointI getOffset(FormationType type, int index, int total, Direction faceDir) {
		PointI base = getBaseOffset(type, index, total);
		return rotateByDirection(base, faceDir);
	}

	private PointI getBaseOffset(FormationType type, int index, int total) {
		switch (type) {
		default:
		case SQUARE:
			return square(index, total);
		case LINE_H:
			return lineH(index);
		case LINE_V:
			return lineV(index);
		case WEDGE:
			return wedge(index);
		case V_SHAPE:
			return vShape(index, total);
		case CIRCLE:
			return circle(index, total);
		}
	}

	private PointI square(int idx, int total) {
		int s = MathUtils.ceil(MathUtils.sqrt(total));
		int x = idx % s - s / 2;
		int y = idx / s - s / 2;
		return new PointI(x * spacing, y * spacing);
	}

	private PointI lineH(int idx) {
		return new PointI((idx - 2) * spacing, 0);
	}

	private PointI lineV(int idx) {
		return new PointI(0, (idx - 2) * spacing);
	}

	private PointI wedge(int idx) {
		int r = (int) ((MathUtils.sqrt(8 * idx + 1) - 1) / 2);
		int c = idx - r * (r + 1) / 2;
		int x = c - r / 2;
		int y = r;
		return new PointI(x * spacing, y * spacing);
	}

	private PointI vShape(int idx, int total) {
		int mid = total / 2;
		int x = (idx - mid) * spacing;
		int y = MathUtils.abs(idx - mid) * spacing;
		return new PointI(x, y);
	}

	private PointI circle(int idx, int total) {
		float a = 2 * MathUtils.PI * idx / total;
		int r = MathUtils.max(2, total / 3);
		int x = (int) (r * MathUtils.cos(a) * spacing);
		int y = (int) (r * MathUtils.sin(a) * spacing);
		return new PointI(x, y);
	}

	private PointI rotateByDirection(PointI o, Direction d) {
		if (d == Direction.UP) {
			new PointI(o.y, -o.x);
		} else if (d == Direction.DOWN) {
			new PointI(-o.y, o.x);
		} else if (d == Direction.LEFT) {
			new PointI(-o.x, -o.y);
		} else if (d == Direction.RIGHT) {
			new PointI(o.x, o.y);
		} else if (d == Direction.UP_LEFT) {
			new PointI(o.x + o.y, o.y);
		} else if (d == Direction.UP_RIGHT) {
			new PointI(o.x, o.x + o.y);
		} else if (d == Direction.DOWN_LEFT) {
			new PointI(-o.x, -o.x - o.y);
		} else if (d == Direction.DOWN_RIGHT) {
			new PointI(-o.x - o.y, -o.y);
		}
		return new PointI(0, 0);
	}

	public TArray<PointI> offsetPath(TArray<PointI> path, PointI offset) {
		TArray<PointI> res = new TArray<PointI>();
		for (PointI p : path) {
			res.add(new PointI(p.x + offset.x, p.y + offset.y));
		}
		return res;
	}

	public int getSpacing() {
		return spacing;
	}

	public BattleMap getBattleMap() {
		return battleMap;
	}
}

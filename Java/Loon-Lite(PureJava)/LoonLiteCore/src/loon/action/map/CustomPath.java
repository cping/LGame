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
package loon.action.map;

import loon.LRelease;
import loon.LSystem;
import loon.geom.Vector2f;
import loon.utils.StringKeyValue;
import loon.utils.StringUtils;
import loon.utils.TArray;

/**
 * 交给用户自定义行走路径的对象移动路径存储用类,此类没有寻径能力,仅仅是用来记录一组固定的行走路线
 */
public class CustomPath implements LRelease {

	protected TArray<Vector2f> steps = new TArray<Vector2f>();

	private CustomPathMove pathMove;

	private float scaleX;

	private float scaleY;

	private String name;

	public CustomPath() {
		this(LSystem.UNKNOWN);
	}

	public CustomPath(String n) {
		this(n, 1f);
	}

	public CustomPath(TArray<Vector2f> list) {
		this(list, 1f);
	}

	public CustomPath(TArray<Vector2f> list, float scale) {
		this(list, scale, scale);
	}

	public CustomPath(TArray<Vector2f> list, float sx, float sy) {
		this(LSystem.UNKNOWN, list, sx, sy);
	}

	public CustomPath(String n, float scale) {
		this(n, scale, scale);
	}

	public CustomPath(String n, float sx, float sy) {
		this(n, null, sx, sy);
	}

	public CustomPath(String n, TArray<Vector2f> list, float sx, float sy) {
		this.name = n;
		this.scaleX = sx;
		this.scaleY = sy;
		if (list != null) {
			add(list);
		}
	}

	public CustomPathMove getMovePath() {
		if (this.pathMove == null) {
			this.pathMove = new CustomPathMove(this);
		}
		return this.pathMove;
	}

	public int size() {
		return steps.size;
	}

	public Vector2f getStep(int index) {
		if (index < 0 || index >= steps.size) {
			return null;
		}
		return steps.get(index).mul(scaleX, scaleY);
	}

	public Vector2f first() {
		return getStep(0);
	}

	public Vector2f last() {
		return getStep(steps.size < 1 ? 0 : steps.size - 1);
	}

	public Vector2f get(int index) {
		return getStep(index);
	}

	public float getX(int index) {
		return getStep(index).getX();
	}

	public float getY(int index) {
		return getStep(index).getY();
	}

	public CustomPath append(float x, float y) {
		steps.add(new Vector2f(x, y));
		return this;
	}

	public CustomPath prepend(float x, float y) {
		steps.unshift(new Vector2f(x, y));
		return this;
	}

	public CustomPath remove(Vector2f step) {
		steps.removeValue(step, true);
		return this;
	}

	public CustomPath removeIndex(int idx) {
		steps.removeIndex(idx);
		return this;
	}

	public Vector2f pop() {
		return steps.pop();
	}

	public TArray<Vector2f> getSteps() {
		return this.steps;
	}

	public CustomPath reverse() {
		steps.reverse();
		return this;
	}

	public CustomPath add(CustomPath path) {
		if (path != null) {
			steps.addAll(path.steps);
		}
		return this;
	}

	public CustomPath addLoop(float startX, float startY, float endX, float endY, int count) {
		return addLoop(new Vector2f(startX, startY), new Vector2f(endX, endY), count);
	}

	public CustomPath addLoop(Vector2f start, Vector2f end, int count) {
		if (start == null || end == null) {
			return this;
		}
		for (int i = 0; i < count; i++) {
			steps.add(start.cpy());
			steps.add(end.cpy());
		}
		return this;
	}

	public CustomPath loop(int count) {
		TArray<Vector2f> tmp = new TArray<Vector2f>();
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < steps.size; j++) {
				Vector2f loc = steps.get(j);
				if (loc != null) {
					tmp.add(loc.cpy());
				}
			}
		}
		steps.clear();
		steps.addAll(tmp);
		return this;
	}

	public CustomPath add(Vector2f... pos) {
		if (pos == null) {
			return this;
		}
		for (int i = 0; i < pos.length; i++) {
			Vector2f loc = pos[i];
			if (loc != null) {
				steps.add(loc);
			}
		}
		return this;
	}

	public CustomPath add(TArray<Vector2f> v) {
		if (v == null) {
			return this;
		}
		for (int i = 0; i < v.size; i++) {
			Vector2f loc = v.get(i);
			if (loc != null) {
				steps.add(loc);
			}
		}
		return this;
	}

	public CustomPath add(Vector2f step) {
		if (step != null) {
			steps.add(step);
		}
		return this;
	}

	public CustomPath cpy() {
		return new CustomPath(this.name, this.scaleX, this.scaleY).add(this);
	}

	public CustomPath cpyReverse() {
		return cpy().reverse();
	}

	public boolean contains(int x, int y) {
		return steps.contains(new Vector2f(x, y), false);
	}

	public boolean containsScale(int x, int y) {
		return steps.contains(new Vector2f(x, y).mul(scaleX, scaleY), false);
	}

	public boolean isScale() {
		return scaleX != 1f && scaleY != 1f;
	}

	public CustomPath setTileSize(float size) {
		return setScale(size);
	}

	public CustomPath setTileSize(float tileWidth, float tileHeight) {
		return setScale(tileWidth, tileHeight);
	}

	public CustomPath setScale(float s) {
		return setScale(s, s);
	}

	public CustomPath setScale(float sx, float sy) {
		this.scaleX = sx;
		this.scaleY = sy;
		return this;
	}

	public float getTileWidth() {
		return getScaleX();
	}

	public float getTileHeight() {
		return getScaleY();
	}

	public float getScaleX() {
		return scaleX;
	}

	public CustomPath setTileWidth(float tileWidth) {
		return setScaleX(tileWidth);
	}

	public CustomPath setScaleX(float scaleX) {
		this.scaleX = scaleX;
		return this;
	}

	public float getScaleY() {
		return scaleY;
	}

	public CustomPath setTileHeight(float tileHeight) {
		return setScaleY(tileHeight);
	}

	public CustomPath setScaleY(float scaleY) {
		this.scaleY = scaleY;
		return this;
	}

	public CustomPath setName(String n) {
		if (!StringUtils.isEmpty(n)) {
			this.name = n;
		}
		return this;
	}

	public String getName() {
		return name;
	}

	public boolean isEmpty() {
		return steps.isEmpty();
	}

	public CustomPath clear() {
		steps.clear();
		return this;
	}

	@Override
	public boolean equals(Object other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (other instanceof CustomPath) {
			CustomPath path = (CustomPath) other;
			return path.scaleX == this.scaleX && path.scaleY == this.scaleY && steps.equals(path.steps);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = LSystem.unite(result, scaleX);
		result = LSystem.unite(result, scaleY);
		result = LSystem.unite(result, steps.hashCode());
		return prime * result;
	}

	@Override
	public String toString() {
		StringKeyValue builder = new StringKeyValue("CustomPath");
		builder.kv("name", name).comma().kv("scaleX", scaleX).comma().kv("scaleY", scaleY).comma().kv("steps",
				steps.toString());
		return builder.toString();
	}

	@Override
	public void close() {
		clear();
	}

}

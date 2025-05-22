/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.canvas;

import loon.LRelease;
import loon.LSystem;
import loon.utils.IntMap;

/**
 * 颜色对象缓存池
 */
public class LColorPool implements LRelease {

	private static LColorPool _colorPool;

	public static void freeStatic() {
		_colorPool = null;
	}

	public static LColorPool get() {
		synchronized (LColorPool.class) {
			if (_colorPool == null) {
				_colorPool = new LColorPool();
			}
			return _colorPool;
		}
	}

	private final LColor _alphaColor = new LColor(0f, 0f, 0f, 0f, true);

	private IntMap<LColor> _colorMap = new IntMap<LColor>();

	private boolean _closed;

	private String _name;

	private LColorPool() {
		this(LSystem.UNKNOWN);
	}

	public LColorPool(String name) {
		this._name = name;
	}

	public LColor getColor(int pixel) {
		final float[] rgba = LColor.toRGBA(pixel);
		return getColor(rgba[0], rgba[1], rgba[2], rgba[3]);
	}

	public LColor getColor(float r, float g, float b, float a) {
		if (a <= 0.1f) {
			return _alphaColor;
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, r);
		hashCode = LSystem.unite(hashCode, g);
		hashCode = LSystem.unite(hashCode, b);
		hashCode = LSystem.unite(hashCode, a);
		LColor color = _colorMap.get(hashCode);
		if (color == null) {
			color = new LColor(r, g, b, a, true);
			_colorMap.put(hashCode, color);
		}
		return color;
	}

	public LColor getColor(float r, float g, float b) {
		return getColor(r, g, b, 1f);
	}

	public LColor getColor(int r, int g, int b, int a) {
		if (a <= 10) {
			return _alphaColor;
		}
		int hashCode = 1;
		hashCode = LSystem.unite(hashCode, r);
		hashCode = LSystem.unite(hashCode, g);
		hashCode = LSystem.unite(hashCode, b);
		hashCode = LSystem.unite(hashCode, a);
		LColor color = _colorMap.get(hashCode);
		if (color == null) {
			color = new LColor(r, g, b, a, true);
			_colorMap.put(hashCode, color);
		}
		return color;
	}

	public LColor getColor(int r, int g, int b) {
		return getColor(r, g, b, 1f);
	}

	public Iterable<LColor> getAllColors() {
		return _colorMap.values();
	}

	@Override
	public void close() {
		if (_colorMap != null) {
			_colorMap.clear();
		}
		_closed = true;
	}

	public boolean isClosed() {
		return _closed;
	}

	public String getName() {
		return _name;
	}

}

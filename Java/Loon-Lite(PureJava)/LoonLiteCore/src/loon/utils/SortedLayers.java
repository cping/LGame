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
package loon.utils;

import loon.LSystem;

public class SortedLayers {

	private IntMap<String> _nameMap = new IntMap<String>();

	private IntMap<Integer> _indexMap = new IntMap<Integer>();

	public String getLayerName(int layer) {
		String name = LSystem.EMPTY;
		if (this._nameMap.containsKey(layer)) {
			name = this._nameMap.get(layer);
		}
		return name;
	}

	public int getLayerIndex(int layer) {
		int index = 0;
		if (this._indexMap.containsKey(layer)) {
			index = this._indexMap.get(layer);
		}
		return index;
	}

	public int getLayerByName(String name) {
		int count = this._nameMap.size;
		int[] keys = this._nameMap.keys();
		int key = 0;
		for (int i = 0; i < count; i++) {
			key = keys[i];
			if (name.equals(this._nameMap.get(key))) {
				return key;
			}
		}
		return 0;
	}

	public int getLayerIndexByName(String name) {
		int id = this.getLayerByName(name);
		return this.getLayerIndex(id);
	}

	public SortedLayers putLayer(int layer, int layerIndex, String layerName) {
		this._nameMap.put(layer, layerName);
		this._indexMap.put(layer, Integer.valueOf(layerIndex));
		return this;
	}

	public SortedLayers clear() {
		this._indexMap.clear();
		this._nameMap.clear();
		return this;
	}
}

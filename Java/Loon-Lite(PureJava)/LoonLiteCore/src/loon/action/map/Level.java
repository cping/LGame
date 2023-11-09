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
import loon.utils.TArray;

public class Level implements LRelease {

	private String _name;

	private int _currentIndex = 0;

	private final TArray<Field2D> _maps;

	public Level() {
		this(LSystem.UNKNOWN);
	}

	public Level(String name) {
		_name = name;
		_maps = new TArray<Field2D>();
	}

	public Level(int tw, int th, String[]... maps) {
		this(LSystem.UNKNOWN, tw, th, maps);
	}

	public Level(String name, int tw, int th, String[]... mapArrays) {
		final int size = mapArrays.length;
		_name = name;
		_maps = new TArray<Field2D>(size);
		for (int i = 0; i < mapArrays.length; i++) {
			_maps.add(new Field2D(mapArrays[i], tw, th));
		}
	}

	public Level(int tw, int th, int[][]... maps) {
		this(LSystem.UNKNOWN, tw, th, maps);
	}

	public Level(String name, int tw, int th, int[][]... mapArrays) {
		final int size = mapArrays.length;
		_name = name;
		_maps = new TArray<Field2D>(size);
		for (int i = 0; i < mapArrays.length; i++) {
			_maps.add(new Field2D(mapArrays[i], tw, th));
		}
	}

	public Level(Field2D... maps) {
		this(LSystem.UNKNOWN, maps);
	}

	public Level(String name, Field2D... maps) {
		_name = name;
		_maps = new TArray<Field2D>(maps);
	}

	public Level addMap(int tw, int th, String... s) {
		_maps.add(new Field2D(s, tw, th));
		return this;
	}

	public Level addMap(int tw, int th, int[][] map) {
		_maps.add(new Field2D(map, tw, th));
		return this;
	}

	public Level addMap(Field2D map) {
		_maps.add(new Field2D(map));
		return this;
	}

	public Field2D removeMap(int idx) {
		return _maps.removeIndex(idx);
	}

	public Field2D getMap() {
		return getMap(_currentIndex);
	}

	public Field2D getMap(int idx) {
		return _maps.get(idx);
	}

	public int getCurrentIndex() {
		return _currentIndex;
	}

	public Level setCurrentIndex(int c) {
		this._currentIndex = c;
		return this;
	}

	public TileMap toTileMap() {
		return toTileMap(_currentIndex);
	}

	public TileMap toTileMap(int idx) {
		return new TileMap(getMap(idx));
	}

	public String getName() {
		return this._name;
	}

	@Override
	public void close() {
		_maps.close();
		_currentIndex = 0;
	}

}

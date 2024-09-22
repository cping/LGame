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
package loon.action.map.ldtk;

import loon.BaseIO;
import loon.Json;
import loon.LRelease;
import loon.LSysException;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

/**
 * LDTK2D地图的解释与渲染器（编辑器官网：https://github.com/deepnight/ldtk）
 */
public class LDTKMap implements LRelease {

	private final IntMap<LDTKLevel> _leveltoIds;

	private final ObjectMap<String, Integer> _levelNamesToIds;

	private int _worldGridWidth, _worldGridHeight;

	private int _maxWidth, _maxHeight;

	private float _defaultPivotX;

	private float _defaultPivotY;

	private float _posX;

	private float _posY;

	private int _defaultLevelWidth;

	private int _defaultLevelHeight;

	private int _defaultGridSize;

	private int _defaultEntityWidth;

	private int _defaultEntityHeight;

	private LDTKTypes _types;

	private String _path;

	private String _dirPath;

	private boolean _dirty;

	public LDTKMap() {
		this(new LDTKTypes());
	}

	public LDTKMap(String path) {
		this(new LDTKTypes(), path);
	}

	public LDTKMap(LDTKTypes types) {
		this._leveltoIds = new IntMap<LDTKLevel>();
		this._levelNamesToIds = new ObjectMap<String, Integer>();
		this._types = types;
		this._dirty = true;
	}

	public LDTKMap(LDTKTypes types, String path) {
		this(types);
		this._path = path;
		this._dirPath = PathUtils.normalizeDir(_path);
	}

	public void reset() {
		for (int i = _leveltoIds.size - 1; i > -1; i--) {
			_leveltoIds.get(i).close();
		}
		this._leveltoIds.clear();
		this._levelNamesToIds.clear();
		this._dirty = true;
	}

	public void parse() {
		if (_dirty) {
			if (!StringUtils.isEmpty(_path)) {
				load((Json.Object) BaseIO.loadJsonObject(_path));
			}
			_dirty = false;
		}
	}

	public String getPath() {
		return this._path;
	}

	public String getDir() {
		return this._dirPath;
	}

	private void load(Json.Object root) {
		if (root == null || root.isNull("levels")) {
			throw new LSysException("The file " + _path + " does not exist or is not an ldtk map file !");
		}
		this._worldGridWidth = root.getInt("worldGridWidth");
		this._worldGridHeight = root.getInt("worldGridHeight");
		this._defaultLevelWidth = root.getInt("defaultLevelWidth");
		this._defaultLevelHeight = root.getInt("defaultLevelHeight");
		this._defaultPivotX = root.getNumber("defaultPivotX");
		this._defaultPivotY = root.getNumber("defaultPivotY");
		this._defaultGridSize = root.getInt("defaultGridSize");
		this._defaultEntityWidth = root.getInt("defaultEntityWidth");
		this._defaultEntityHeight = root.getInt("defaultEntityHeight");
		Json.Array levelRoot = root.getArray("levels");
		for (int i = 0; i < levelRoot.length(); i++) {
			parseLevel(levelRoot.getObject(i));
		}
	}

	private void parseLevel(Json.Object levelRoot) {
		LDTKLevel level = new LDTKLevel(levelRoot, _types, this);
		_leveltoIds.put(level.getUid(), level);
		_levelNamesToIds.put(level.getIdentifier(), level.getUid());
		if (level.getX() + level.getWidth() > _maxWidth) {
			_maxWidth = MathUtils.iceil(level.getX() + level.getWidth());
		}
		if (level.getY() + level.getHeight() > _maxHeight) {
			_maxHeight = MathUtils.iceil(level.getY() + level.getHeight());
		}
	}

	public int getGridSize(LDTKLevel level, int idx) {
		parse();
		int gridSize = 0;
		for (int j = 0; j < _leveltoIds.size; j++) {
			LDTKLevel le = _leveltoIds.get(j);
			if (le.getLayerType() == LDTKLayerType.Entities) {
				gridSize = le.getGridSize(idx);
			}
		}
		return gridSize;
	}

	public LDTKLevel getLevel(String name) {
		parse();
		return _leveltoIds.get(_levelNamesToIds.get(name));
	}

	public LDTKLevel getLevel(int id) {
		parse();
		return _leveltoIds.get(id);
	}

	public int[] getLevelKeys() {
		parse();
		return _leveltoIds.keys();
	}

	public IntMap<LDTKLevel> getLevels() {
		parse();
		return _leveltoIds;
	}

	public float getPosX() {
		return _posX;
	}

	public LDTKMap setPosX(float x) {
		this._posX = x;
		return this;
	}

	public float getPosY() {
		return _posY;
	}

	public LDTKMap setPosY(float y) {
		this._posY = y;
		return this;
	}

	public int getMaxWidth() {
		return _maxWidth;
	}

	public int getMaxHeight() {
		return _maxHeight;
	}

	public int getWorldGridWidth() {
		return _worldGridWidth;
	}

	public int getWorldGridHeight() {
		return _worldGridHeight;
	}

	public float getDefaultPivotX() {
		return _defaultPivotX;
	}

	public float getDefaultPivotY() {
		return _defaultPivotY;
	}

	public int getDefaultLevelWidth() {
		return _defaultLevelWidth;
	}

	public int getDefaultLevelHeight() {
		return _defaultLevelHeight;
	}

	public int getDefaultGridSize() {
		return _defaultGridSize;
	}

	public int getDefaultEntityWidth() {
		return _defaultEntityWidth;
	}

	public int getDefaultEntityHeight() {
		return _defaultEntityHeight;
	}

	public LDTKTypes getLDTKTypes() {
		return this._types;
	}

	@Override
	public void close() {
		reset();
	}

}

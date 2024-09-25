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
import loon.LSysException;
import loon.LSystem;
import loon.LTexture;
import loon.action.map.Config;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.geom.PointI;
import loon.opengl.GLEx;
import loon.utils.IntArray;
import loon.utils.IntMap;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.PathUtils;
import loon.utils.StringUtils;

/**
 * LDTK2D地图的解释与渲染器（编辑器官网：https://github.com/deepnight/ldtk）
 */
public class LDTKMap extends Entity implements Config {

	private final IntMap<LDTKLevel> _leveltoIds;

	private final ObjectMap<String, Integer> _levelNamesToIds;

	private final IntArray _drawLevels;

	private int _worldGridWidth, _worldGridHeight;

	private int _maxWidth, _maxHeight;

	private float _defaultPivotX;

	private float _defaultPivotY;

	private int _defaultLevelWidth;

	private int _defaultLevelHeight;

	private int _defaultGridSize;

	private int _defaultEntityWidth;

	private int _defaultEntityHeight;

	private LColor _defaultLevelBgColor;

	private LColor _bgColor;

	private LDTKWorldLayoutType _worldLayout;

	private LDTKTypes _types;

	private String _path;

	private String _dirPath;

	private boolean _dirty;

	public LDTKMap(String path) {
		this(path, 0f, 0f, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public LDTKMap(String path, float x, float y) {
		this(new LDTKTypes(), path, x, y, LSystem.viewSize.getWidth(), LSystem.viewSize.getHeight());
	}

	public LDTKMap(String path, float x, float y, float w, float h) {
		this(new LDTKTypes(), path, x, y, w, h);
	}

	public LDTKMap(LDTKTypes types, String path, float x, float y, float w, float h) {
		super((LTexture) null, x, y, w, h);
		this._leveltoIds = new IntMap<LDTKLevel>();
		this._levelNamesToIds = new ObjectMap<String, Integer>();
		this._drawLevels = new IntArray();
		this._worldLayout = LDTKWorldLayoutType.WorldLayoutFree;
		this._types = types;
		this._dirty = true;
		this._path = path;
		this._dirPath = PathUtils.normalizeDir(_path);
		this._repaintDraw = true;
	}

	public LDTKWorldLayoutType getWorldLayoutType() {
		return this._worldLayout;
	}

	public LDTKMap setWorldLayoutType(LDTKWorldLayoutType t) {
		this._worldLayout = t;
		return this;
	}

	@Override
	protected void repaint(GLEx g, float offsetX, float offsetY) {
		for (int i = _drawLevels.length - 1; i > -1; i--) {
			int idx = _drawLevels.get(i);
			LDTKLevel level = _leveltoIds.get(idx);
			if (level != null) {
				level.draw(g, drawX(offsetX), drawY(offsetY));
			}
		}
	}

	public void freeMap() {
		for (int i = _leveltoIds.size - 1; i > -1; i--) {
			_leveltoIds.get(i).close();
		}
		this._leveltoIds.clear();
		this._levelNamesToIds.clear();
		this._drawLevels.clear();
		this._dirty = true;
	}

	@Override
	public LDTKMap reset() {
		super.reset();
		this.freeMap();
		return this;
	}

	public void parse() {
		if (_dirty) {
			if (!StringUtils.isEmpty(_path)) {
				load((Json.Object) BaseIO.loadJsonObject(_path));
			}
			_dirty = false;
		}
	}

	public int getWorldX() {
		return MathUtils.iceil(this.getX());
	}

	public int getWorldY() {
		return MathUtils.iceil(this.getY());
	}

	public int getWorldWidth() {
		return MathUtils.iceil(this.getWidth());
	}

	public int getWorldHeight() {
		return MathUtils.iceil(this.getHeight());
	}

	public String getPath() {
		return this._path;
	}

	public String getDir() {
		return this._dirPath;
	}

	private void convertLayout(Json.Object o) {
		if (o != null) {
			String wl = o.getString("worldLayout", null);
			if (wl != null) {
				wl = wl.toLowerCase();
				if ("linearvertical".equals(wl)) {
					this._worldLayout = LDTKWorldLayoutType.WorldLayoutVertical;
				} else if ("linearhorizontal".equals(wl)) {
					this._worldLayout = LDTKWorldLayoutType.WorldLayoutHorizontal;
				} else if ("gridvania".equals(wl)) {
					this._worldLayout = LDTKWorldLayoutType.WorldLayoutGridVania;
				} else if ("free".equals(wl)) {
					this._worldLayout = LDTKWorldLayoutType.WorldLayoutFree;
				} else {
					this._worldLayout = null;
				}
			}
		}
	}

	private void load(Json.Object root) {
		if (root == null || root.isNull("levels")) {
			throw new LSysException("The file " + _path + " does not exist or is not an ldtk map file !");
		}
		this.convertLayout(root);
		this._worldGridWidth = root.getInt("worldGridWidth");
		this._worldGridHeight = root.getInt("worldGridHeight");
		this._defaultLevelWidth = root.getInt("defaultLevelWidth");
		this._defaultLevelHeight = root.getInt("defaultLevelHeight");
		this._defaultPivotX = root.getNumber("defaultPivotX");
		this._defaultPivotY = root.getNumber("defaultPivotY");
		this._defaultGridSize = root.getInt("defaultGridSize");
		this._defaultEntityWidth = root.getInt("defaultEntityWidth");
		this._defaultEntityHeight = root.getInt("defaultEntityHeight");
		this._defaultLevelBgColor = new LColor(root.getString("defaultLevelBgColor"));
		this._bgColor = new LColor(root.getString("bgColor"));
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

	public PointI getGridSize(LDTKLevel level, int idx) {
		parse();
		PointI gridSize = new PointI();
		for (int j = 0; j < _leveltoIds.size; j++) {
			LDTKLevel le = _leveltoIds.get(j);
			if (le.getLayerType() == LDTKLayerType.Entities) {
				gridSize = le.getGridSize(idx);
			}
		}
		return gridSize;
	}

	public LDTKMap setDrawLevelNames(String... names) {
		parse();
		String[] list = names;
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				Integer id = _levelNamesToIds.get(names[i]);
				if (id != null) {
					_drawLevels.add(id);
				}
			}
		}
		return this;
	}

	public LDTKMap setDrawLevelIds(int... ids) {
		parse();
		int[] list = ids;
		if (list != null) {
			for (int i = 0; i < list.length; i++) {
				int id = ids[i];
				if (_leveltoIds.containsKey(id)) {
					_drawLevels.add(id);
				}
			}
		}
		return this;
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

	public LColor getDefaultLevelBgColor() {
		parse();
		return _defaultLevelBgColor;
	}

	public LColor getBgColor() {
		parse();
		return _bgColor;
	}

	public float getPosX() {
		return getX();
	}

	public float getPosY() {
		return getY();
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
		super.close();
		freeMap();
	}

}

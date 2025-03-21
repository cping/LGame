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
package loon.action.sprite.bone;

import loon.BaseIO;
import loon.LSysException;
import loon.LSystem;
import loon.geom.RectBox;
import loon.utils.ArrayByteReader;
import loon.utils.FloatArray;
import loon.utils.MathUtils;
import loon.utils.ObjectMap;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class SkeletonLoader {

	private String _fileName;

	private String[] _paths;

	private ObjectMap<String, String> _kvs = new ObjectMap<String, String>();

	private boolean _actUpdated;

	private String _poseName;

	private FloatArray _baseValues = new FloatArray();

	private TArray<Bone> _bones = new TArray<Bone>();

	private TArray<Skeleton> _skeletons = new TArray<Skeleton>();

	public SkeletonLoader(String fileName, String... paths) {
		this(fileName, true, paths);
	}

	public SkeletonLoader(String fileName, boolean loaded, String... paths) {
		this._fileName = fileName;
		this._paths = paths;
		if (loaded) {
			load(fileName, paths);
		}
	}

	public SkeletonLoader load() {
		return load(_fileName, _paths);
	}

	public SkeletonLoader load(String fileName, String... paths) {
		reset();
		TArray<BoneSheet> boneSheet = new TArray<BoneSheet>();
		try {
			for (String path : paths) {
				boneSheet.add(new BoneSheet(path));
			}
			loadData(fileName, boneSheet);
		} catch (Exception e) {
			throw new LSysException("The skeleton file " + fileName + " not found !");
		}
		return this;
	}

	public SkeletonLoader loadData(String fileName, TArray<BoneSheet> boneSheets) {
		String result = LSystem.EMPTY;
		ArrayByteReader reader = BaseIO.loadArrayByteReader(fileName);
		while ((result = reader.readLine()) != null) {
			parseLine(result.toLowerCase().trim(), boneSheets);
		}
		clean();
		reader.close();
		return this;
	}

	public void parseLine(String line, TArray<BoneSheet> boneSheets) {
		if (StringUtils.isEmpty(line)) {
			submitBones(boneSheets, _actUpdated);
		} else if (line.startsWith("model") || line.startsWith("pose") || line.startsWith("act")) {
			int idx = line.indexOf(LSystem.COLON);
			if (idx != -1) {
				String newPoseName = line.substring(idx + 1, line.length()).trim();
				if (!newPoseName.equals(_poseName)) {
					submitBones(boneSheets, _actUpdated);
					_poseName = newPoseName;
					_actUpdated = true;
				}
			}
		} else {
			if (_poseName != null && _baseValues.size() == (BoneFlags.CLIP_INDEX + 1)) {
				_baseValues.set(BoneFlags.ANGLE, _baseValues.get(BoneFlags.ANGLE) * MathUtils.DEG_FULL / 10000f);
				_bones.add(new Bone(_poseName, _baseValues, boneSheets));
				submitBones(boneSheets, _actUpdated);
				_baseValues.clear();
			}
			filterValues(line);
			_actUpdated = false;
		}
	}

	private void submitBones(TArray<BoneSheet> boneSheets, boolean update) {
		if (update) {
			_skeletons.add(new Skeleton(_bones, boneSheets));
			_bones = new TArray<Bone>();
		}
	}

	public void filterValues(String line) {
		final String[] list = StringUtils.toList(line, ",");
		for (int i = 0; i < list.length; i++) {
			final String v = list[i].trim();
			if (MathUtils.isNan(v)) {
				_baseValues.add(Float.valueOf(v));
			} else {
				String result = _kvs.get(v);
				if (result != null) {
					filterValues(result);
				}
			}
		}
	}

	public SkeletonLoader setClip(String key, float x, float y, float w, float h) {
		_kvs.put(key, StringUtils.join(",", x, y, w, h));
		return this;
	}

	public RectBox getClip(String key) {
		String result = _kvs.get(key);
		if (result != null) {
			return RectBox.at(result);
		}
		return null;
	}

	public String getFileName() {
		return _fileName;
	}

	public static float toScale(float x) {
		return x / 1000f;
	}

	public void clean() {
		_baseValues.clear();
		_bones.clear();
		_actUpdated = false;
	}

	public void reset() {
		_skeletons = new TArray<Skeleton>();
		_actUpdated = false;
	}

	public TArray<Skeleton> getSkeletons() {
		return _skeletons;
	}

	public SkeletonAnimation getSkeletonAnimation() {
		return getSkeletonAnimation(LSystem.UNKNOWN);
	}

	public SkeletonAnimation getSkeletonAnimation(String name) {
		return new SkeletonAnimation(name, _skeletons);
	}
}

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
package loon.core;

import java.util.ArrayList;

import loon.utils.CollectionUtils;

//0.3.3版新增的单例资源加载器，默认只有同步加载，建议使用时与多线程配合(比如丢到Screen的onLoad函数下，效果上就等于异步了)。
public class AssetManager {

	private static ArrayList<Asset> _assetList = new ArrayList<Asset>(
			CollectionUtils.INITIAL_CAPACITY);

	private static int _loadedIndex = 0;

	public static void reset() {
		_loadedIndex = 0;
	}

	public static String getCurrentAssetName() {
		if (_loadedIndex < _assetList.size()) {
			return _assetList.get(_loadedIndex).AssetName;
		}
		return "LoadComplete";
	}

	public static int getPercentLoaded() {
		return ((100 * _loadedIndex) / _assetList.size());
	}

	public static boolean hasLoaded() {
		return (_loadedIndex >= _assetList.size());
	}

	public static boolean loadAllAssets() {
		while (!loadOneAsset()) {
		}
		return true;
	}

	public static boolean loadOneAsset() {
		if (hasLoaded()) {
			return true;
		}
		Asset asset = _assetList.get(_loadedIndex);
		if (asset != null) {
			asset.load();
		}
		_loadedIndex++;
		return false;
	}

	public static void prepareAsset(Asset asset) {
		if (asset != null) {
			_assetList.add(asset);
		}
	}
}

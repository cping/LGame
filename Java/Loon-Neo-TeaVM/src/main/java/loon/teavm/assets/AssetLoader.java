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
package loon.teavm.assets;

import loon.teavm.TeaBlob;
import loon.teavm.assets.AssetPreloader.FileType;

public interface AssetLoader {

	void preload(String assetFileUrl, AssetLoaderListener<Void> preloadListener);

	String getAssetUrl();

	String getScriptUrl();

	boolean isAssetInQueueOrDownloading(String path);

	boolean isAssetLoaded(FileType fileType, String path);

	void loadAsset(String path, AssetType assetType, FileType fileType);

	void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<TeaBlob> listener);

	void loadAsset(String path, AssetType assetType, FileType fileType, AssetLoaderListener<TeaBlob> listener, boolean overwrite);

	void loadScript(String path);

	void loadScript(String path, AssetLoaderListener<String> listener);

	int getQueue();

	int getDownloadingCount();

	boolean isDownloading();
}
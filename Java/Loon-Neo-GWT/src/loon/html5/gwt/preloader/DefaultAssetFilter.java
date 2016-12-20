/**
 * Copyright 2008 - 2015 The Loon Game Engine Authors
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
package loon.html5.gwt.preloader;

import loon.LSystem;

public class DefaultAssetFilter implements AssetFilter {

	public final static char special_symbols = 'φ';

	private String extension(String file) {
		String name = file;
		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return "";
		return name.substring(dotIndex + 1);
	}

	@Override
	public boolean accept(String file, boolean isDirectory) {
		if (isDirectory && file.endsWith(".svn")) {
			return false;
		}
		return true;
	}

	@Override
	public AssetType getType(String file) {
		String extension = extension(file).toLowerCase();
		if (LSystem.isImage(extension)) {
			return AssetType.Image;
		}
		if (LSystem.isAudio(extension)) {
			return AssetType.Audio;
		}
		if (LSystem.isText(extension)) {
			return AssetType.Text;
		}
		return AssetType.Binary;
	}

	@Override
	public String getBundleName(String file) {
		return "assets";
	}
}

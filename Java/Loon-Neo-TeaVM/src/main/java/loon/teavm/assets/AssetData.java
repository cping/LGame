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

public class AssetData {

	public static final int TYPE_DIRECTORY = 1;
	public static final int TYPE_FILE = 2;

	private final String path;
	private final byte[] bytes;
	private final int type;

	public AssetData(String path) {
		this(path, TYPE_DIRECTORY, null);
	}

	public AssetData(String path, byte[] bytes) {
		this(path, TYPE_FILE, bytes);
	}

	public AssetData(String path, int type, byte[] bytes) {
		if (bytes != null && path.endsWith("/")) {
			int length = path.length();
			path = path.substring(0, length - 1);
		}
		this.path = path;
		this.bytes = bytes;
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public boolean isDirectory() {
		return type == TYPE_DIRECTORY;
	}

	public int getType() {
		return type;
	}

	public int getBytesSize() {
		return bytes != null ? bytes.length : 0;
	}
}

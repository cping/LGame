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
package loon.utils.res.loaders;

public class ContextAssetLoader extends AssetAbstractLoader<String> {

	private String _context;

	public ContextAssetLoader(String text, String context) {
		this(text, null, context);
	}

	public ContextAssetLoader(String text, String nickname, String context) {
		this.set(text, nickname);
		this._context = context;
	}

	@Override
	public String get() {
		return _context;
	}

	@Override
	public boolean completed() {
		return true;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Context;
	}

	@Override
	public void close() {
	}

}

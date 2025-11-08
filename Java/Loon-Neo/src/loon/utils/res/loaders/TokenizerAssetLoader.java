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

import loon.utils.parse.StrTokenizer;
import loon.utils.res.TextResource;

public class TokenizerAssetLoader extends AssetAbstractLoader<StrTokenizer> {

	private StrTokenizer _tokenizer;

	private String _delimiters;

	public TokenizerAssetLoader(String path, String nickname, String delimiters) {
		this.set(path, delimiters);
		this._delimiters = delimiters;
	}

	@Override
	public StrTokenizer get() {
		return _tokenizer;
	}

	public String getDelimiters() {
		return _delimiters;
	}

	@Override
	public boolean isLoaded() {
		return _tokenizer != null;
	}

	@Override
	public void loadData() {
		close();
		_tokenizer = TextResource.get().loadStrTokenizer(_path, _delimiters);
	}

	@Override
	public boolean completed() {
		return _tokenizer != null;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Tokenizer;
	}

	@Override
	public void close() {
		_tokenizer = null;
	}

}

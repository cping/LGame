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

import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLListener;
import loon.utils.xml.XMLParser;

public class XmlAssetLoader extends AssetAbstractLoader<XMLDocument> {

	private XMLListener _listener;

	private XMLDocument _xmlDoc;

	public XmlAssetLoader(String path, String nickname, XMLListener listener) {
		this.set(path, nickname);
		this._listener = listener;
	}

	@Override
	public XMLDocument get() {
		return _xmlDoc;
	}

	@Override
	public boolean completed() {
		return (_xmlDoc = XMLParser.parse(_path, _listener)) != null;
	}

	@Override
	public PreloadItem item() {
		return PreloadItem.Xml;
	}

	@Override
	public void close() {
		if (_xmlDoc != null) {
			_xmlDoc.close();
			_xmlDoc = null;
		}
	}

}

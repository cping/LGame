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
package loon.action.map.tmx;

import loon.Json;
import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.utils.PathUtils;
import loon.utils.xml.XMLElement;

public class TMXImage {

	public static enum Format {
		PNG, GIF, JPG, BMP, OTHER
	}

	// 瓦片色彩格式
	private Format _format;

	// 瓦片图像源
	private String _source;

	// 过滤色
	private LColor _trans;

	private int _width;
	private int _height;

	private String _location;

	private LTexture _image = null;

	public void parse(Json.Object element, String tmxPath) {
		_location = tmxPath;
		_source = element.getString("image", LSystem.EMPTY).trim();
		_width = element.getInt("imagewidth", 0);
		_height = element.getInt("imageheight", 0);
		if (element.containsKey("trans")) {
			_trans = new LColor(element.getString("trans", LSystem.EMPTY).trim());
		} else if (element.containsKey("transparentcolor")) {
			_trans = new LColor(element.getString("transparentcolor", LSystem.EMPTY).trim());
		} else {
			_trans = new LColor(LColor.TRANSPARENT);
		}
		loadImage();
	}

	public void parse(XMLElement element, String tmxPath) {
		_location = tmxPath;
		String sourcePath = element.getAttribute("source", LSystem.EMPTY);
		_source = sourcePath.trim();
		_width = element.getIntAttribute("width", 0);
		_height = element.getIntAttribute("height", 0);
		if (element.hasAttribute("trans")) {
			_trans = new LColor(element.getAttribute("trans", LSystem.EMPTY).trim());
		} else if (element.hasAttribute("transparentcolor")) {
			_trans = new LColor(element.getAttribute("transparentcolor", LSystem.EMPTY).trim());
		} else {
			_trans = new LColor(LColor.TRANSPARENT);
		}
		loadImage();
	}

	protected void loadImage() {
		if (_image == null || _image.isClosed() || _width == 0 || _height == 0) {
			_image = LSystem.loadTexture(PathUtils.normalizeCombinePaths(_location, _source));
			if (_width == 0) {
				_width = _image.getWidth();
			}
			if (_height == 0) {
				_height = _image.getWidth();
			}
		}
	}

	public TMXImage setFormat(Format f) {
		this._format = f;
		return this;
	}

	public Format getFormat() {
		return _format;
	}

	public String getLocation() {
		return _location;
	}

	public String getSource() {
		return _source;
	}

	public LTexture getImage() {
		loadImage();
		return _image;
	}

	public int getWidth() {
		return _width;
	}

	public int getHeight() {
		return _height;
	}

	public LColor getTrans() {
		return _trans;
	}

}

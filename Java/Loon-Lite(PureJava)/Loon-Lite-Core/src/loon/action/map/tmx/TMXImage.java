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

import loon.LSystem;
import loon.LTexture;
import loon.canvas.LColor;
import loon.utils.xml.XMLElement;

public class TMXImage {
	
	public static enum Format {
		PNG, GIF, JPG, BMP, OTHER
	}

	// 瓦片色彩格式
	private Format format;

	// 瓦片图像源
	private String source;

	// 过滤色
	private LColor trans;

	private int width;
	private int height;

	public void parse(XMLElement element, String tmxPath) {
		String sourcePath = element.getAttribute("source", LSystem.EMPTY);
		source = sourcePath.trim();
		width = element.getIntAttribute("width", 0);
		height = element.getIntAttribute("height", 0);
		trans = new LColor(LColor.TRANSPARENT);
		if (element.hasAttribute("trans")) {
			String color = element.getAttribute("trans", LSystem.EMPTY).trim();
			if (color.startsWith("#")) {
				color = color.substring(1);
			}
			trans = new LColor(Integer.parseInt(color, 16));
		}
		if (width == 0 || height == 0) {
			LTexture image = LSystem.loadTexture(source);
			if (width == 0) {
				width = image.getWidth();
			}
			if (height == 0) {
				height = image.getWidth();
			}
		}
	}

	public Format getFormat() {
		return format;
	}

	public String getSource() {
		return source;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public LColor getTrans() {
		return trans;
	}

}

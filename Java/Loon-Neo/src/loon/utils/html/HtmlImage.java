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
package loon.utils.html;

public class HtmlImage {

	protected String src;

	protected String alt;

	protected int width;

	protected int height;

	protected HtmlImage(HtmlElement ele) {
		src = ele.getAttribute("src", "");
		alt = ele.getAttribute("alt", "");
		width = ele.getIntAttribute("width", 0);
		height = ele.getIntAttribute("height", 0);
	}

	public String getSrc() {
		return src;
	}

	public String getAlt() {
		return alt;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

}

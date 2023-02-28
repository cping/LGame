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
package loon.utils.html.css;

public class CssUnit {

	public final static CssUnit PX() {
		return new CssUnit(0);
	}

	public final static CssUnit PT() {
		return new CssUnit(1);
	}

	int code;

	public CssUnit(int c) {
		this.code = c;
	}

	@Override
	public String toString() {
		switch (code) {
		case 0:
			return "px";
		default:
			return "pt";
		}
	}

}

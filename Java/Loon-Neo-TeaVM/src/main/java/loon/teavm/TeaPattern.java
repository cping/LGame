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
package loon.teavm;

import org.teavm.jso.canvas.CanvasPattern;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.html.HTMLImageElement;

import loon.canvas.Pattern;

public class TeaPattern extends Pattern {

	private final HTMLImageElement _patimg;
	
	private CanvasPattern _pattern;

	TeaPattern(HTMLImageElement patimg, boolean repeatX, boolean repeatY) {
		super(repeatX, repeatY);
		this._patimg = patimg;
	}

	public CanvasPattern pattern(CanvasRenderingContext2D ctx) {
		if (_pattern == null) {
			String repeat;
			if (repeatX) {
				if (repeatY)
					repeat = "repeat";
				else
					repeat = "repeat-x";
			} else if (repeatY) {
				repeat = "repeat-y";
			} else {
				repeat = null;
			}
			_pattern = ctx.createPattern(_patimg, repeat);
		}
		return _pattern;
	}
}

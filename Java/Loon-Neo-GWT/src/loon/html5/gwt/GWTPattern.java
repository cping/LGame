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
package loon.html5.gwt;

import loon.canvas.Pattern;

import com.google.gwt.canvas.dom.client.CanvasPattern;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;

class GWTPattern extends Pattern {

	private final ImageElement patimg;
	private CanvasPattern pattern;

	GWTPattern(ImageElement patimg, boolean repeatX, boolean repeatY) {
		super(repeatX, repeatY);
		this.patimg = patimg;
	}

	public CanvasPattern pattern(Context2d ctx) {
		if (pattern == null) {
			Context2d.Repetition repeat;
			if (repeatX) {
				if (repeatY)
					repeat = Context2d.Repetition.REPEAT;
				else
					repeat = Context2d.Repetition.REPEAT_X;
			} else if (repeatY) {
				repeat = Context2d.Repetition.REPEAT_Y;
			} else {
				repeat = null;
			}
			pattern = ctx.createPattern(patimg, repeat);
		}
		return pattern;
	}
}

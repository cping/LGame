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

import loon.canvas.LColor;
import loon.component.Print;
import loon.opengl.GLEx;
import loon.utils.html.css.CssDimensions.Rect;

public class CssTextCmd extends CssCmd {

	public Rect rect;

	public String text;

	public float offset;

	private boolean dirty;

	private String tempText;

	public CssTextCmd(float w, float h) {
		super(w, h);
		dirty = true;
		offset = 4;
	}

	@Override
	public void paint(GLEx g, float x, float y) {
		if (rect != null) {
			if (x + rect.x + g.getFont().stringWidth(text) + offset < rect.height) {
				g.drawString(text, x + rect.x + offset, y + rect.y + offset, LColor.white);
			} else {
				if (dirty) {
					tempText = Print.prepareString(g.getFont(), text,
							rect.width - offset - rect.x - offset - g.getFont().getSize() * 3);
					dirty = false;
				}
				g.drawString(tempText, x + rect.x + offset, y + rect.y + offset, LColor.white);
			}
		}

	}

}

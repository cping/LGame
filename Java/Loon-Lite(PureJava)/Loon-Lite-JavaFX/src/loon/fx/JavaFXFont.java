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
package loon.fx;

import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import loon.font.Font.Style;
import loon.utils.StringUtils;

class JavaFXFont {

	public static final JavaFXFont DEFAULT = new JavaFXFont(Font.getDefault(), 14);

	public final Font typeface;
	public final float size;
	protected Paint paint;

	public JavaFXFont(Font typeface) {
		this(typeface, (float) typeface.getSize());
	}

	public JavaFXFont(Font typeface, float size) {
		this.typeface = typeface;
		this.size = size;
	}

	public static Font create(loon.font.Font font) {
		String name = font.name;
		if (StringUtils.isEmpty(name)) {
			name = "monospace";
		}
		FontWeight weight = (font.style == Style.BOLD) ? FontWeight.BOLD : FontWeight.NORMAL;
		FontPosture posture = (font.style == Style.ITALIC) ? FontPosture.ITALIC : FontPosture.REGULAR;
		if (font.style == Style.BOLD_ITALIC) {
			weight = FontWeight.BOLD;
			posture = FontPosture.ITALIC;
		}
		if (font.style == Style.PLAIN) {
			weight = FontWeight.NORMAL;
			posture = FontPosture.REGULAR;
		}
		return Font.font(name, weight, posture, font.size);
	}

}

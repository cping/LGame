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

import java.text.CharacterIterator;

import javafx.geometry.Bounds;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import loon.font.Font;
import loon.font.Font.Style;
import loon.geom.RectBox;

public class JavaFXFontMetrics {

	public final Font font;

	public final float height;

	public final float emwidth;

	public JavaFXFontMetrics(Font font, float height, float emwidth) {
		this.font = font;
		this.height = height;
		this.emwidth = emwidth;
	}

	public RectBox getStringBounds(CharacterIterator ci, int beginIndex, int limit) {
		char[] arr = new char[limit - beginIndex];
		ci.setIndex(beginIndex);
		for (int idx = 0; idx < arr.length; idx++) {
			arr[idx] = ci.current();
			ci.next();
		}
		return getStringBounds(arr, beginIndex, limit);
	}

	public RectBox getStringBounds(char[] chars, int beginIndex, int limit) {
		String str = new String(chars, beginIndex, limit - beginIndex);
		return getStringBounds(str);
	}

	public RectBox getStringBounds(String str, int beginIndex, int limit) {
		String substr = str.substring(beginIndex, limit);
		return getStringBounds(substr);
	}

	public RectBox getStringBounds(String str) {
		Text text = new Text(str);
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
		javafx.scene.text.Font fxfont = javafx.scene.text.Font.font(font.name, weight, posture, font.size);
		text.setFont(fxfont);
		Bounds b = text.getLayoutBounds();	
		return new RectBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	public int stringWidth(String str) {
		return (int) getStringBounds(str).getWidth();
	}

	public float ascent() {
		return 0.7f * height;
	}

	public float descent() {
		return height - ascent();
	}

	public float leading() {
		return 0.1f * height;
	}

	public float adjustWidth(float width) {
		switch (font.style) {
		case ITALIC:
			return width + emwidth / 8;
		case BOLD_ITALIC:
			return width + emwidth / 6;
		default:
			return width;
		}
	}
}

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

import javafx.geometry.Bounds;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import loon.font.Font;
import loon.font.Font.Style;
import loon.geom.RectBox;
import loon.utils.CharUtils;
import loon.utils.MathUtils;

public class JavaFXFontMetrics {

	public final Font font;

	private final float fascent;

	private final float fdescent;

	private final float fheight;

	private final float leading;

	protected final float emwidth;

	protected javafx.scene.text.Font fxfont;

	protected Text fxtext;

	protected Bounds fxbound;

	public JavaFXFontMetrics(Font font) {
		this(font, 0, 0);
	}

	public JavaFXFontMetrics(Font font, float height, float ewidth) {
		this.font = font;
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
		this.fxfont = javafx.scene.text.Font.font(font.name, weight, posture, font.size);
		this.fxtext = new Text();
		this.fxtext.setFont(fxfont);
		this.fxbound = fxtext.getLayoutBounds();
		this.fheight = MathUtils.max(height, (float) fxbound.getHeight());
		this.emwidth = MathUtils.max(ewidth, (float) fxbound.getWidth());
		this.fascent = (float) -fxbound.getMinY();
		this.fdescent = (float) fxbound.getMaxY();
		this.leading = fheight;
	}

	public Bounds getBounds() {
		return this.fxbound;
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
		fxtext.setText(str);
		Bounds b = fxtext.getLayoutBounds();
		return new RectBox(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
	}

	public int stringHeight(String str) {
		return (int) getStringBounds(str).getHeight();
	}

	public int stringWidth(String str) {
		return (int) getStringBounds(str).getWidth();
	}

	public int charWidth(char ch) {
		int width = stringWidth(String.valueOf(ch));
		if (CharUtils.isAlpha(ch)) {
			if (width < 5) {
				return width + 3;
			}
			if (width < 10) {
				return width + 2;
			}
			if (width < 15) {
				return width + 1;
			}
			return width;
		}
		if (CharUtils.isDigitCharacter(ch)) {
			return width < 10 ? width + 1 : width;
		}

		return width;
	}

	public int charHeight(char ch) {
		return stringHeight(String.valueOf(ch));
	}

	public int getHeight() {
		return (int) fheight;
	}

	public float ascent() {
		return fascent;
	}

	public float descent() {
		return fdescent;
	}

	public float leading() {
		return leading;
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

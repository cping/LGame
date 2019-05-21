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

import java.util.ArrayList;

import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.text.Font;
import loon.font.TextFormat;
import loon.font.TextWrap;
import loon.geom.RectBox;

public class JavaFXTextLayout extends loon.font.TextLayout {

	public static JavaFXTextLayout layoutText(JavaFXGraphics gfx, String text, TextFormat format) {
		JavaFXFontMetrics metrics = new JavaFXFontMetrics(format.font);
		return new JavaFXTextLayout(text, metrics, format);
	}

	public static JavaFXTextLayout[] layoutText(JavaFXGraphics gfx, String text, TextFormat format, TextWrap wrap) {
		JavaFXFontMetrics metrics = new JavaFXFontMetrics(format.font);
		String ltext = text.length() == 0 ? " " : text;
		ArrayList<JavaFXTextLayout> layouts = new ArrayList<JavaFXTextLayout>();
		text = normalizeEOL(ltext);
		for (String line : text.split("\\n")) {
			String[] words = line.split("\\s");
			for (int idx = 0; idx < words.length;) {
				idx = measureLine(metrics, format, wrap, words, idx, layouts);
			}
		}
		return layouts.toArray(new JavaFXTextLayout[layouts.size()]);
	}

	static int measureLine(JavaFXFontMetrics metrics, TextFormat format, TextWrap wrap, String[] words, int idx,
			ArrayList<JavaFXTextLayout> layouts) {
		String line = words[idx++];
		int startIdx = idx;
		for (; idx < words.length; idx++) {
			String nline = line + " " + words[idx];
			if (nline.length() * metrics.emwidth > wrap.width) {
				break;
			}
			line = nline;
		}
		float lineWidth = metrics.stringWidth(line);
		if (lineWidth < wrap.width) {
			for (; idx < words.length; idx++) {
				String nline = line + " " + words[idx];
				float nlineWidth = metrics.stringWidth(line);
				if (nlineWidth > wrap.width) {
					break;
				}
				line = nline;
				lineWidth = nlineWidth;
			}
		}

		while (lineWidth > wrap.width && idx > (startIdx + 1)) {
			line = line.substring(0, line.length() - words[--idx].length() - 1);
			lineWidth = metrics.stringWidth(line);
		}

		if (lineWidth > wrap.width) {
			StringBuilder remainder = new StringBuilder();
			while (lineWidth > wrap.width && line.length() > 1) {
				int lastIdx = line.length() - 1;
				remainder.insert(0, line.charAt(lastIdx));
				line = line.substring(0, lastIdx);
				lineWidth = metrics.stringWidth(line);
			}
			words[--idx] = remainder.toString();
		}

		layouts.add(new JavaFXTextLayout(line, metrics, format));
		return idx;
	}

	JavaFXFontMetrics metrics;

	void stroke(GraphicsContext context, float x, float y) {
		configContext(context, format);
		context.strokeText(text, x, y);
	}

	void fill(GraphicsContext context, float x, float y) {
		configContext(context, format);
		context.fillText(text, x, y);
	}

	static void configContext(GraphicsContext context, TextFormat format) {
		Font font = getFont(format);
		context.setFont(font);
		context.setTextBaseline(VPos.TOP);
	}

	static Font getFont(TextFormat format) {
		return (format.font == null) ? JavaFXFont.DEFAULT.typeface : JavaFXFont.create(format.font);
	}

	JavaFXTextLayout(String text, JavaFXFontMetrics metrics, TextFormat format) {
		super(text, format, computeBounds(metrics), metrics.getHeight());
		this.metrics = metrics;
	}

	private static RectBox computeBounds(JavaFXFontMetrics metrics) {
		Bounds bounds = metrics.getBounds();
		return new RectBox((float) bounds.getMinX(), (float) bounds.getMinY() + metrics.ascent(),
				(float) bounds.getWidth(), (float) bounds.getHeight());
	}

	@Override
	public float ascent() {
		return metrics.ascent();
	}

	@Override
	public float descent() {
		return metrics.descent();
	}

	@Override
	public float leading() {
		return metrics.leading();
	}

	@Override
	public int stringWidth(String message) {
		return metrics.stringWidth(message);
	}

	@Override
	public int getHeight() {
		return metrics.getHeight();
	}

	@Override
	public int charWidth(char ch) {
		return metrics.charWidth(ch);
	}

}

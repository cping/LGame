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
package loon.lwjgl;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;

import loon.font.TextFormat;
import loon.font.TextWrap;
import loon.geom.RectBox;

class Lwjgl3TextLayout extends loon.font.TextLayout {

	public static Lwjgl3TextLayout layoutText(Lwjgl3ImplGraphics gfx, String text,
			TextFormat format) {
		AttributedString astring = new AttributedString(
				text.length() == 0 ? " " : text);
		if (format.font != null) {
			astring.addAttribute(TextAttribute.FONT,
					gfx.resolveFont(format.font));
		}
		FontRenderContext frc = format.antialias ? gfx.aaFontContext
				: gfx.aFontContext;
		return new Lwjgl3TextLayout(text, format, new TextLayout(
				astring.getIterator(), frc));
	}

	public static Lwjgl3TextLayout[] layoutText(Lwjgl3ImplGraphics gfx,
			String text, TextFormat format, TextWrap wrap) {
		text = normalizeEOL(text);
		String ltext = text.length() == 0 ? " " : text;

		AttributedString astring = new AttributedString(ltext);
		if (format.font != null) {
			astring.addAttribute(TextAttribute.FONT,
					gfx.resolveFont(format.font));
		}

		List<Lwjgl3TextLayout> layouts = new ArrayList<Lwjgl3TextLayout>();
		FontRenderContext frc = format.antialias ? gfx.aaFontContext
				: gfx.aFontContext;
		LineBreakMeasurer measurer = new LineBreakMeasurer(
				astring.getIterator(), frc);
		int lastPos = ltext.length(), curPos = 0;
		char eol = '\n';
		while (curPos < lastPos) {
			int nextRet = ltext.indexOf(eol, measurer.getPosition() + 1);
			if (nextRet == -1) {
				nextRet = lastPos;
			}
			TextLayout layout = measurer.nextLayout(wrap.width, nextRet, false);
			int endPos = measurer.getPosition();
			while (curPos < endPos && ltext.charAt(curPos) == eol) {
				curPos += 1;
			}
			layouts.add(new Lwjgl3TextLayout(ltext.substring(curPos, endPos),
					format, layout));
			curPos = endPos;
		}
		return layouts.toArray(new Lwjgl3TextLayout[layouts.size()]);
	}

	private final TextLayout layout;

	private final Graphics2D g2d;

	private FontMetrics fontMetrics;

	Lwjgl3TextLayout(String text, TextFormat format, TextLayout layout) {
		super(text, format, computeBounds(layout), layout.getAscent()
				+ layout.getDescent());
		this.g2d = (Graphics2D) new BufferedImage(1, 1,
				BufferedImage.TYPE_INT_ARGB).getGraphics();
		this.fontMetrics = g2d.getFontMetrics(new java.awt.Font(
				format.font.name,
				Lwjgl3ImplGraphics.STYLE_TO_JAVA[format.font.style.ordinal()],
				(int) format.font.size));
		this.layout = layout;
	}

	@Override
	public float ascent() {
		return layout.getAscent();
	}

	@Override
	public float descent() {
		return layout.getDescent();
	}

	@Override
	public float leading() {
		return layout.getLeading();
	}

	void stroke(Graphics2D gfx, float x, float y) {
		paint(gfx, x, y, true);
	}

	void fill(Graphics2D gfx, float x, float y) {
		paint(gfx, x, y, false);
	}

	void paint(Graphics2D gfx, float x, float y, boolean stroke) {
		Object ohint = gfx.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		try {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					format.antialias ? RenderingHints.VALUE_ANTIALIAS_ON
							: RenderingHints.VALUE_ANTIALIAS_OFF);
			float yoff = y + layout.getAscent();
			if (stroke) {
				gfx.translate(x, yoff);
				gfx.draw(layout.getOutline(null));
				gfx.translate(-x, -yoff);
			} else {
				layout.draw(gfx, x, yoff);
			}

		} finally {
			gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, ohint);
		}
	}

	private static RectBox computeBounds(TextLayout layout) {
		Rectangle2D bounds = layout.getBounds();
		return new RectBox((float) bounds.getX(), (float) bounds.getY()
				+ layout.getAscent(), (float) bounds.getWidth(),
				(float) bounds.getHeight());
	}

	public int charWidth(char ch) {
		return fontMetrics.charWidth(ch);
	}

	@Override
	public int stringWidth(String message) {
		return fontMetrics.stringWidth(message);
	}

	@Override
	public int getHeight() {
		return fontMetrics.getHeight();
	}
}

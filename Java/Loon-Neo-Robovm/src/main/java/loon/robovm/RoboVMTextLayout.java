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
package loon.robovm;

import loon.font.TextFormat;
import loon.font.TextLayout;
import loon.font.TextWrap;
import loon.geom.RectBox;
import loon.jni.CTFrame;
import loon.jni.CTFramesetter;

import org.robovm.apple.corefoundation.CFArray;
import org.robovm.apple.corefoundation.CFRange;
import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coregraphics.CGBitmapContext;
import org.robovm.apple.coregraphics.CGPath;
import org.robovm.apple.coregraphics.CGRect;
import org.robovm.apple.coretext.CTFont;
import org.robovm.apple.coretext.CTLine;
import org.robovm.apple.foundation.NSAttributedString;
import org.robovm.apple.uikit.NSAttributedStringAttributes;
import org.robovm.apple.uikit.UIColor;
import org.robovm.apple.uikit.UIFont;

class RoboVMTextLayout extends TextLayout {

	public static RoboVMTextLayout layoutText(RoboVMGraphics gfx, final String text, TextFormat format) {
		final CTFont font = RoboVMFont.resolveFont(gfx.game.assets(), format.font);
		NSAttributedStringAttributes attribs = createAttribs(font);
		CTLine line = CTLine.create(new NSAttributedString(text, attribs));
		return new RoboVMTextLayout(gfx, text, format, font, line);
	}

	public static RoboVMTextLayout[] layoutText(RoboVMGraphics gfx, String text, TextFormat format, TextWrap wrap) {
		text = normalizeEOL(text);
		final CTFont font = RoboVMFont.resolveFont(gfx.game.assets(), format.font);
		NSAttributedStringAttributes attribs = createAttribs(font);
		CFArray lines = wrapLines(new NSAttributedString(text, attribs), wrap.width);
		RoboVMTextLayout[] layouts = new RoboVMTextLayout[(int) lines.size()];
		for (int ii = 0; ii < layouts.length; ii++) {
			CTLine line = lines.get(ii, CTLine.class);
			CFRange range = line.getStringRange();
			String ltext = text.substring((int) range.getLocation(), (int) (range.getLocation() + range.getLength()));
			layouts[ii] = new RoboVMTextLayout(gfx, ltext, format, font, line);
		}
		return layouts;
	}

	private static NSAttributedStringAttributes createAttribs(CTFont font) {
		NSAttributedStringAttributes attribs = new NSAttributedStringAttributes();
		attribs.setFont(font.as(UIFont.class));
		return attribs;
	}

	private static void addStroke(NSAttributedStringAttributes attribs, CTFont font, float strokeWidth,
			int strokeColor) {
		double strokePct = 100 * strokeWidth / font.getSize();
		attribs.setStrokeWidth(strokePct);
		attribs.setStrokeColor(toUIColor(strokeColor));
	}

	private static CFArray wrapLines(NSAttributedString astring, float wrapWidth) {
		CTFramesetter fs = CTFramesetter.create(astring);
		try {
			CGPath path = CGPath.createWithRect(new CGRect(0, 0, wrapWidth, Float.MAX_VALUE / 2),
					CGAffineTransform.Identity());
			CTFrame frame = fs.createFrame(new CFRange(0, 0), path, null);
			return frame.getLines();
		} finally {
			fs.dispose();
		}
	}

	private final CTFont font;
	private CTLine fillLine;
	private int fillColor;
	private CTLine strokeLine;
	private float strokeWidth;
	private int strokeColor;

	private RoboVMTextLayout(RoboVMGraphics gfx, String text, TextFormat format, CTFont font, CTLine fillLine) {
		super(text, format, computeBounds(font, fillLine.getImageBounds(gfx.scratchCtx)),
				(float) (font.getAscent() + font.getDescent()));
		this.font = font;
		this.fillLine = fillLine;
	}

	@Override
	public float ascent() {
		return (float) font.getAscent();
	}

	@Override
	public float descent() {
		return (float) font.getDescent();
	}

	@Override
	public float leading() {
		return (float) font.getLeading();
	}

	void stroke(CGBitmapContext bctx, float x, float y, float strokeWidth, int strokeColor) {
		if (strokeLine == null || strokeWidth != this.strokeWidth || strokeColor != this.strokeColor) {
			this.strokeWidth = strokeWidth;
			this.strokeColor = strokeColor;
			NSAttributedStringAttributes attribs = createAttribs(font);
			addStroke(attribs, font, strokeWidth, strokeColor);
			strokeLine = CTLine.create(new NSAttributedString(text, attribs));

		}
		paint(bctx, strokeLine, x, y);
	}

	void fill(CGBitmapContext bctx, float x, float y, int fillColor) {
		if (this.fillColor != fillColor) {
			this.fillColor = fillColor;
			NSAttributedStringAttributes attribs = createAttribs(font);
			attribs.setForegroundColor(toUIColor(fillColor));
			fillLine = CTLine.create(new NSAttributedString(text, attribs));
		}
		paint(bctx, fillLine, x, y);
	}

	private void paint(CGBitmapContext bctx, CTLine line, float x, float y) {
		bctx.saveGState();
		bctx.translateCTM(x, y + ascent());
		bctx.scaleCTM(1, -1);
		bctx.setShouldAntialias(format.antialias);
		bctx.setTextPosition(0, 0);
		line.draw(bctx);
		bctx.restoreGState();
	}

	private static UIColor toUIColor(int color) {
		float blue = (color & 0xFF) / 255f;
		color >>= 8;
		float green = (color & 0xFF) / 255f;
		color >>= 8;
		float red = (color & 0xFF) / 255f;
		color >>= 8;
		float alpha = (color & 0xFF) / 255f;
		return new UIColor(red, green, blue, alpha);
	}

	private static RectBox computeBounds(CTFont font, CGRect bounds) {
		float ascent = (float) font.getAscent();
		return new RectBox((float) bounds.getMinX(), ascent - (float) (bounds.getHeight() + bounds.getMinY()),
				(float) bounds.getWidth(), (float) bounds.getHeight());
	}

	@Override
	public int stringWidth(String message) {
		NSAttributedStringAttributes attribs = createAttribs(font);
		CTLine line = CTLine.create(new NSAttributedString(message, attribs));
		return (int) line.getWidth();
	}

	@Override
	public int getHeight() {
		return (int) font.getSize();
	}

	@Override
	public int charWidth(char ch) {
		NSAttributedStringAttributes attribs = createAttribs(font);
		CTLine line = CTLine.create(new NSAttributedString(String.valueOf(ch), attribs));
		return (int) line.getWidth();
	}
}

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
package loon.cport;

import loon.canvas.Canvas;
import loon.canvas.Canvas.Composite;
import loon.canvas.Canvas.LineCap;
import loon.canvas.Canvas.LineJoin;
import loon.canvas.Pixmap;
import loon.canvas.PixmapComposite;

public class CCanvasState {

	int fillColor;
	int strokeColor;
	CGradient fillGradient;
	CPattern fillPattern;
	float strokeWidth;
	LineCap lineCap;
	LineJoin lineJoin;
	float miterLimit;
	Composite composite;
	float alpha;

	CCanvasState() {
		this(0xff000000, 0xffffffff, null, null, 1.0f, LineCap.SQUARE, LineJoin.MITER, 10.0f, Composite.SRC_OVER, 1);
	}

	CCanvasState(CCanvasState toCopy) {
		this(toCopy.fillColor, toCopy.strokeColor, toCopy.fillGradient, toCopy.fillPattern, toCopy.strokeWidth,
				toCopy.lineCap, toCopy.lineJoin, toCopy.miterLimit, toCopy.composite, toCopy.alpha);
	}

	CCanvasState(int fillColor, int strokeColor, CGradient fillGradient, CPattern fillPattern, float strokeWidth,
			LineCap lineCap, LineJoin lineJoin, float miterLimit, Composite composite, float alpha) {
		this.fillColor = fillColor;
		this.strokeColor = strokeColor;
		this.fillGradient = fillGradient;
		this.fillPattern = fillPattern;
		this.strokeWidth = strokeWidth;
		this.lineCap = lineCap;
		this.lineJoin = lineJoin;
		this.miterLimit = miterLimit;
		this.composite = composite;
		this.alpha = alpha;
	}

	void prepareStroke(Pixmap gfx) {
		gfx.setColor(strokeColor);
		gfx.setComposite(convertComposite(composite));
	}

	void prepareFill(Pixmap gfx) {
		gfx.setColor(fillColor);
		gfx.setComposite(convertComposite(composite));
	}

	private final static int convertComposite(Canvas.Composite composite) {
		switch (composite) {
		case DST_ATOP:
			return PixmapComposite.SCREEN;
		case DST_IN:
			return PixmapComposite.ADD;
		case DST_OUT:
			return PixmapComposite.CLEAR;
		case DST_OVER:
			return PixmapComposite.DIFFERENCE;
		case SRC:
			return PixmapComposite.SRC_IN;
		case SRC_ATOP:
			return PixmapComposite.SRC_ATOP;
		case SRC_IN:
			return PixmapComposite.SRC_IN;
		case SRC_OUT:
			return PixmapComposite.SRC_OUT;
		case SRC_OVER:
			return PixmapComposite.SRC_OVER;
		case XOR:
			return PixmapComposite.EXCLUSION;
		case MULTIPLY:
			return PixmapComposite.MULTIPLY;
		default:
			return PixmapComposite.SRC_OVER;
		}
	}

}

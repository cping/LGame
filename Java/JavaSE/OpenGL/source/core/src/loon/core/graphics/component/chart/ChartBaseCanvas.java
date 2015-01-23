/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.core.graphics.component.chart;

import loon.core.graphics.device.Align;
import loon.core.graphics.device.Bitmap;
import loon.core.graphics.device.Canvas;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.device.LGraphics;
import loon.core.graphics.device.LImage;
import loon.core.graphics.device.Paint;
import loon.core.graphics.device.Path;

public class ChartBaseCanvas {

	int p_width = 0;
	int p_height = 0;
	int p_paddtop = 8;
	int p_paddright = 8;
	int p_paddbottom = 8;
	int p_paddleft = 8;
	
	boolean p_xscale_auto = true;
	boolean p_yscale_auto = true;
	boolean p_border_vis = true;
	boolean p_grid_vis = true;
	boolean p_axis_vis = true;
	boolean p_xtext_vis = true;
	boolean p_ytext_vis = true;
	boolean p_xtext_bottom = true;
	boolean p_ytext_left = true;
	boolean p_grid_aa = true;

	int p_background_color = LColor.white.getARGB();
	int p_border_color = LColor.white.getARGB();
	int p_grid_color = LColor.white.getARGB();
	int p_axis_color = LColor.white.getARGB();
	int p_text_color = LColor.white.getARGB();

	float p_border_width = dipToPixel(1.0f);
	float p_grid_width = dipToPixel(1.0f);
	float p_axis_width = dipToPixel(1.0f);
	float p_text_size = dipToPixel(12.0f);

	boolean bRedraw = false;

	float sX, sY, dX, dY, eX, eY;

	float mXmin, mXmax, mYmin, mYmax;

	float mXminGrid, mXmaxGrid, mYminGrid, mYmaxGrid, mXdivGrid, mYdivGrid;
	int mXgridNum, mYgridNum;

	float aX, bX, aY, bY;

	public float offsetX = -5, offsetY = 0;

	Canvas mCnv = null;
	Bitmap mBmp = null;
	Paint mPntBorder = new Paint();
	Paint mPntGrid = new Paint();
	Paint mPntAxis = new Paint();
	Paint mPntText = new Paint();

	Path mPath = new Path();

	private final LImage _myImage;

	private final Canvas _myCanvas;

	private LColor _background = LColor.gray.darker();
	
	public ChartBaseCanvas(int w, int h) {
		this._myImage = new LImage(w, h, true);
		this._myCanvas = new Canvas(_myImage.getLGraphics());
		this.p_width = w;
		this.p_height = h;
		initPaint();
	}

	public int getWidth() {
		return p_width;
	}

	public int getHeight() {
		return p_height;
	}

	protected void initPaint() {
		mPntBorder.setStyle(Paint.Style.STROKE);
		mPntBorder.setColor(p_border_color);
		mPntBorder.setStrokeWidth(p_border_width);
		mPntBorder.setAntiAlias(p_grid_aa);
		mPntGrid.setStyle(Paint.Style.STROKE);
		mPntGrid.setColor(p_grid_color);
		mPntGrid.setStrokeWidth(p_grid_width);
	//	mPntGrid.setPathEffect(new DashPathEffect(new float[] { 2, 2 }, 0));
		mPntGrid.setAntiAlias(p_grid_aa);
		mPntAxis.setStyle(Paint.Style.STROKE);
		mPntAxis.setColor(p_axis_color);
		mPntAxis.setStrokeWidth(p_axis_width);
		mPntAxis.setAntiAlias(p_grid_aa);
		mPntText.setColor(p_text_color);
		mPntText.setTypeface(LFont.getFont(14));
		mPntText.setTextSize(p_text_size);
		mPntText.setStyle(Paint.Style.FILL);
		mPntText.setAntiAlias(true);
		setBackgroundColor(p_background_color);
	}

	public void reset() {
			mBmp = Bitmap.createBitmap(p_width, p_height);
			mCnv = new Canvas(mBmp);
	}
	
	public void setAxisVis(boolean a){
		p_axis_vis = a;
	}
	
	public void paint(LGraphics g) {
		if (_myImage != null) {
			synchronized (_myImage) {
				_myCanvas.drawClear(_background, p_width, p_height);
				draw(_myCanvas);
				g.drawImage(_myImage, 0, 0);
			}
		}
	}
	
	public void draw(Canvas cnv) {

		if ((mBmp == null) || (bRedraw)) {

			getViewSizes();
			getXYminmax();
			if (p_xscale_auto) {
				calcXgridRange();
			}
			if (p_yscale_auto) {
				calcYgridRange();
			}
			calcXYcoefs();
			reset();

			if (p_grid_vis) {
				drawGrid();
			}
			if (p_xtext_vis) {
				drawXlabel();
			}
			if (p_ytext_vis) {
				drawYlabel();
			}
			if (p_border_vis) {
				drawBorder();
			}
			if (p_axis_vis) {
				drawAxis();
			}
			bRedraw = false;
		}
		cnv.drawBitmap(mBmp, 0, 0);
	}

	public void setPadding(int pad) {
		p_paddtop = pad;
		p_paddright = pad;
		p_paddbottom = pad;
		p_paddleft = pad;
	}

	public void setPaddingDip(int pad) {
		p_paddtop = (int) dipToPixel(pad);
		p_paddright = (int) dipToPixel(pad);
		p_paddbottom = (int) dipToPixel(pad);
		p_paddleft = (int) dipToPixel(pad);
	}

	public void setPadding(int paddtop, int padright, int paddbot, int padleft) {
		p_paddtop = paddtop;
		p_paddright = padright;
		p_paddbottom = paddbot;
		p_paddleft = padleft;
	}

	public void setPaddingDip(int paddtop, int padright, int paddbot,
			int padleft) {
		p_paddtop = (int) dipToPixel(paddtop);
		p_paddright = (int) dipToPixel(padright);
		p_paddbottom = (int) dipToPixel(paddbot);
		p_paddleft = (int) dipToPixel(padleft);
	}

	public void setBackgroundColor(int color) {
		p_background_color = color;
	}

	public void setXgrid(boolean autoXscale, float xmin, float xmax, int num) {
		p_xscale_auto = autoXscale;
		if (!autoXscale) {
			mXminGrid = xmin;
			mXmaxGrid = xmax;
			mXgridNum = num;
			mXdivGrid = (xmax - xmin) / num;
		}

	}

	public void setYgrid(boolean autoYscale, float ymin, float ymax, int num) {
		p_yscale_auto = autoYscale;
		if (!autoYscale) {
			mYminGrid = ymin;
			mYmaxGrid = ymax;
			mYgridNum = num;
			mYdivGrid = (ymax - ymin) / num;
		}

	}

	public void setGridVis(boolean bBorderShow, boolean bGridShow,
			boolean bAxisShow) {
		p_border_vis = bBorderShow;
		p_grid_vis = bGridShow;
		p_axis_vis = bAxisShow;
		bRedraw = true;

	}

	public void setGridColor(int borderColor, int gridColor, int axisColor) {
		p_border_color = borderColor;
		p_grid_color = gridColor;
		p_axis_color = axisColor;
		initPaint();
		bRedraw = true;

	}

	public void setGridWidth(float borderWidth, float gridWidth, float axisWidth) {
		p_border_width = borderWidth;
		p_grid_width = gridWidth;
		p_axis_width = axisWidth;
		initPaint();
		bRedraw = true;

	}

	public void setGridWidthDip(float borderWidth, float gridWidth,
			float axisWidth) {
		p_border_width = dipToPixel(borderWidth);
		p_grid_width = dipToPixel(gridWidth);
		p_axis_width = dipToPixel(axisWidth);
		initPaint();
		bRedraw = true;

	}

	public void setGridAA(boolean antialias) {
		p_grid_aa = antialias;
		initPaint();
		bRedraw = true;

	}

	public void setTextVis(boolean xtext, boolean ytext, boolean xbottom,
			boolean yleft) {
		p_xtext_vis = xtext;
		p_ytext_vis = ytext;
		p_xtext_bottom = xbottom;
		p_ytext_left = yleft;
		bRedraw = true;

	}

	public void setTextStyle(int color, float size) {
		p_text_color = color;
		p_text_size = dipToPixel(size);
		initPaint();
		bRedraw = true;

	}

	protected void getViewSizes() {
		p_width = getWidth();
		p_height = getHeight();
		sX = p_paddleft;
		sY = p_paddtop;
		eX = p_width - p_paddright;
		eY = p_height - p_paddbottom;
		if (p_ytext_vis && p_ytext_left) {
			sX += 3 * p_text_size;
		}
		if (p_ytext_vis && !p_ytext_left) {
			eX -= 3 * p_text_size;
		}
		if (p_xtext_vis && p_xtext_bottom) {
			eY -= p_text_size + 2;
		}
		if (p_xtext_vis && !p_xtext_bottom) {
			sY += p_text_size + 2;
		}
		dX = eX - sX;
		dY = eY - sY;
	}

	protected void getXYminmax() {
		mXmin = -9;
		mXmax = 9;
		mYmin = -90;
		mYmax = 90;
	}

	public void setLeft(int p){
		this.p_paddleft = p;
	}

	public void setTop(int p){
		this.p_paddtop = p;
	}
	
	
	public void setBottom(int p){
		this.p_paddbottom = p;
	}

	public void setRight(int p){
		this.p_paddright = p;
	}
	
	protected void calcXgridRange() {
		mXdivGrid = (float) Math.pow(10,
				Math.floor(Math.log10(Math.abs(mXmax - mXmin))));
		mXminGrid = (float) (mXdivGrid * Math.floor(mXmin / mXdivGrid));
		mXmaxGrid = (float) (mXdivGrid * Math.ceil(mXmax / mXdivGrid));
		mXgridNum = (int) ((mXmaxGrid - mXminGrid) / mXdivGrid);
		if ((dX / dY) < 1.2) {
			if (mXgridNum <= 2) {
				mXgridNum *= 5;
			} else if (mXgridNum == 3) {
				mXgridNum *= 3;
			} else if (mXgridNum <= 5) {
				mXgridNum *= 2;
			}
		} else {
			if (mXgridNum <= 2) {
				mXgridNum *= 6;
			} else if (mXgridNum == 3) {
				mXgridNum *= 4;
			} else if (mXgridNum == 4) {
				mXgridNum *= 3;
			} else if (mXgridNum <= 6) {
				mXgridNum *= 2;
			}
		}
	}

	protected void calcYgridRange() {
		mYdivGrid = (float) Math.pow(10,
				Math.floor(Math.log10(Math.abs(mYmax - mYmin))));
		mYminGrid = (float) (mYdivGrid * Math.floor(mYmin / mYdivGrid));
		mYmaxGrid = (float) (mYdivGrid * Math.ceil(mYmax / mYdivGrid));
		mYgridNum = (int) ((mYmaxGrid - mYminGrid) / mYdivGrid);
		if ((dY / dX) < 1.2) {
			if (mYgridNum <= 2) {
				mYgridNum *= 5;
			} else if (mYgridNum <= 3) {
				mYgridNum *= 3;
			} else if (mYgridNum <= 5) {
				mYgridNum *= 2;
			}
		} else {
			if (mYgridNum <= 2) {
				mYgridNum *= 6;
			} else if (mYgridNum == 3) {
				mYgridNum *= 4;
			} else if (mYgridNum == 4) {
				mYgridNum *= 3;
			} else if (mYgridNum <= 6) {
				mYgridNum *= 2;
			}
		}
	}

	protected void calcXYcoefs() {
		aX = (float) dX / Math.abs(mXmaxGrid - mXminGrid);
		bX = (float) mXminGrid;
		aY = (float) dY / Math.abs(mYmaxGrid - mYminGrid);
		bY = (float) mYminGrid;
	}

	protected void drawGrid() {
		
		mPath.reset();
		for (int ii = 1; ii < mXgridNum; ii++) {
			mPath.moveTo(sX + ii * (dX / mXgridNum), sY);
			mPath.lineTo(sX + ii * (dX / mXgridNum), eY);
		}
		for (int ii = 1; ii < mYgridNum; ii++) {
			mPath.moveTo(sX, sY + ii * (dY / mYgridNum));
			mPath.lineTo(eX, sY + ii * (dY / mYgridNum));
		}
		mCnv.drawPath(mPath, mPntGrid);
	}

	protected void drawXlabel() {
		mPntText.setTextAlign(Align.CENTER);
		mPath.reset();
		if (p_xtext_bottom) {
			for (int ii = 1; ii < mXgridNum; ii++) {
				mPath.moveTo(sX + ii * (dX / mXgridNum), eY - 3);
				mPath.lineTo(sX + ii * (dX / mXgridNum), eY + 3);
				float ff = mXminGrid + ii * (mXmaxGrid - mXminGrid) / mXgridNum;
				mCnv.drawText(String.format("%.1f", ff), sX + ii
						* (dX / mXgridNum), eY + p_text_size + 2, mPntText);
			}
		} else {
			for (int ii = 1; ii < mXgridNum; ii++) {
				mPath.moveTo(sX + ii * (dX / mXgridNum), sY - 3);
				mPath.lineTo(sX + ii * (dX / mXgridNum), sY + 3);
				float ff = mXminGrid + ii * (mXmaxGrid - mXminGrid) / mXgridNum;
				mCnv.drawText(String.format("%.1f", ff), sX + ii
						* (dX / mXgridNum), sY - p_text_size + 2, mPntText);
			}
		}
		mCnv.drawPath(mPath, mPntAxis);
	}

	private String mYLabelFlag = null;

	public void setYLabelFlag(String label){
		this.mYLabelFlag = label;
	}
	
	protected void drawYlabel() {
		if (p_ytext_left) {
			mPntText.setTextAlign(Align.RIGHT);
		} else {
			mPntText.setTextAlign(Align.LEFT);
		}
		mPath.reset();
		if (p_ytext_left) {
			for (int ii = 1; ii < mYgridNum; ii++) {
				mPath.moveTo(sX - 3, eY - ii * (dY / mYgridNum));
				mPath.lineTo(sX + 3, eY - ii * (dY / mYgridNum));
				float ff = mYminGrid + ii * (mYmaxGrid - mYminGrid) / mYgridNum;
				if (mYLabelFlag == null) {
					mCnv.drawText(String.format("%s", (int) ff), sX - 6, eY
							- ii * (dY / mYgridNum) + p_text_size / 2, mPntText);
				} else {
					mCnv.drawText(String.format("%s%s", (int) ff, mYLabelFlag),
							sX - 6, eY - ii * (dY / mYgridNum) + p_text_size
									/ 2, mPntText);
				}
			}
			
		} else {
			for (int ii = 1; ii < mYgridNum; ii++) {
				mPath.moveTo(eX - 3, eY - ii * (dY / mYgridNum));
				mPath.lineTo(eX + 3, eY - ii * (dY / mYgridNum));
				float ff = mYminGrid + ii * (mYmaxGrid - mYminGrid) / mYgridNum;
				if (mYLabelFlag == null) {
					mCnv.drawText(String.format("%s%s", (int) ff, mYLabelFlag),
							eX + 6, eY - ii * (dY / mYgridNum) + p_text_size
									/ 2, mPntText);
				} else {
					mCnv.drawText(String.format("%s%s", (int) ff, mYLabelFlag),
							eX + 6, eY - ii * (dY / mYgridNum) + p_text_size
									/ 2, mPntText);
				}
			}
		}
		mCnv.drawPath(mPath, mPntAxis);
	}

	protected void drawBorder() {
		mPath.reset();
		mPath.moveTo(sX, sY);
		mPath.lineTo(eX, sY);
		mPath.lineTo(eX, eY);
		mPath.lineTo(sX, eY);
		mPath.lineTo(sX, sY);
		mCnv.drawPath(mPath, mPntBorder);
	}

	protected void drawAxis() {
		mPath.reset();
		mPath.moveTo(sX - bX * aX, sY);
		mPath.lineTo(sX - bX * aX, eY);
		mPath.moveTo(sX, eY + bY * aY);
		mPath.lineTo(eX, eY + bY * aY);
		mCnv.drawPath(mPath, mPntAxis);
	}

	public LColor getBackground() {
		return _background;
	}

	public void setBackground(LColor b) {
		this._background = b;
	}

	protected float dipToPixel(float dips) {
		return dips;
	}
}

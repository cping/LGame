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

import java.util.ArrayList;

import loon.core.graphics.device.LColor;

public class ChartPointSerie {

	public ArrayList<ChartPoint> mPointList = new ArrayList<ChartPoint>();

	public int mColor = 0xFF000000;
	public float mWidth = 2;
	public boolean mUseDip = true;

	public float mXmin = 0, mXmax = 1, mYmin = 0, mYmax = 1;

	private boolean mShow = true;
	private boolean mOrderonx = false;
	private boolean mAutoXminmax = true;
	private boolean mAutoYminmax = true;

	public ChartPointSerie() {
	}

	public ChartPointSerie(int color) {
		mColor = color;
	}

	public ChartPointSerie(int color, float width) {
		mColor = color;
		mWidth = width;
	}

	public ChartPointSerie(LColor color, float width) {
		mColor = color.getARGB();
		mWidth = width;
	}

	public ChartPointSerie(int color, float width, boolean usedip) {
		mColor = color;
		mWidth = width;
		mUseDip = usedip;
	}

	public ArrayList<ChartPoint> getPointList() {
		return mPointList;
	}

	public void setPointList(ArrayList<ChartPoint> points) {
		this.mPointList = points;
	}

	public void clearPointList() {
		this.mPointList.clear();
	}

	public void addPoint(ChartPoint point) {
		if (mAutoXminmax) {
			if (mPointList.size() > 0) {
				if (point.x > mXmax)
					mXmax = point.x;
				else if (point.x < mXmin)
					mXmin = point.x;
			} else
				mXmin = mXmax = point.x;
		}
		if (mAutoYminmax) {
			if (mPointList.size() > 0) {
				if (point.y > mYmax)
					mYmax = point.y;
				else if (point.y < mYmin)
					mYmin = point.y;
			} else
				mYmin = mYmax = point.y;
		}
		if (!mOrderonx) {
			mPointList.add(point);
			return;
		}
		ChartPoint p;
		int i;
		for (i = 0; i < mPointList.size(); i++) {
			p = mPointList.get(i);
			if (point.x < p.x) {
				mPointList.add(i, point);
				return;
			}
		}
		mPointList.add(point);
	}

	public void shiftPoint(ChartPoint point, int max) {
		addPoint(point);
		while (mPointList.size() > max)
			mPointList.remove(0);
		if (mAutoXminmax || mAutoYminmax)
			calcRanges();
	}

	public void removePoint(ChartPoint point) {
		mPointList.remove(point);
		if (mAutoXminmax || mAutoYminmax)
			calcRanges();
	}

	public ChartPoint getPoint(int index) {
		return mPointList.get(index);
	}

	public int getSize() {
		return mPointList.size();
	}

	private void calcRanges() {
		int i;
		if (mPointList.size() == 0)
			return;
		if (mAutoXminmax) {
			mXmin = mPointList.get(0).x;
			mXmax = mPointList.get(0).x;
			for (i = 1; i < mPointList.size(); i++) {
				if (mPointList.get(i).x > mXmax)
					mXmax = mPointList.get(i).x;
				else if (mPointList.get(i).x < mXmin)
					mXmin = mPointList.get(i).x;
			}
		}
		if (mAutoYminmax) {
			mYmin = mPointList.get(0).y;
			mYmax = mPointList.get(0).y;
			for (i = 1; i < mPointList.size(); i++) {
				if (mPointList.get(i).y > mYmax)
					mYmax = mPointList.get(i).y;
				else if (mPointList.get(i).y < mYmin)
					mYmin = mPointList.get(i).y;
			}
		}
	}

	public void setAutoMinmax(boolean bAutoX, boolean bAutoY) {
		this.mAutoXminmax = bAutoX;
		this.mAutoYminmax = bAutoY;
		if (bAutoX || bAutoY)
			calcRanges();
	}

	public void setAutoMinmax(boolean bAutoX, boolean bAutoY, float fXmin,
			float fXmax, float fYmin, float fYmax) {
		this.mAutoXminmax = bAutoX;
		this.mAutoYminmax = bAutoY;
		if (!bAutoX) {
			this.mXmin = fXmin;
			this.mXmax = fXmax;
		}
		if (!bAutoY) {
			this.mYmin = fYmin;
			this.mYmax = fYmax;
		}
		if (bAutoX || bAutoY)
			calcRanges();
	}

	public void setOrderOnX(boolean bOrderonx) {
		if (mPointList.size() > 0)
			return;
		this.mOrderonx = bOrderonx;
	}

	public void setVisible(boolean bShow) {
		this.mShow = bShow;
	}

	public boolean isVisible() {
		return this.mShow;
	}

	public void setStyle(int iColor, float fWidth) {
		mColor = iColor;
		mWidth = fWidth;
	}

	public void setStyle(LColor color, float fWidth) {
		mColor = color.getARGB();
		mWidth = fWidth;
	}
	
	public void setStyle(int iColor, float fWidth, boolean bUsedip) {
		mColor = iColor;
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

	public void setStyle(LColor color, float fWidth, boolean bUsedip) {
		mColor = color.getARGB();
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

}

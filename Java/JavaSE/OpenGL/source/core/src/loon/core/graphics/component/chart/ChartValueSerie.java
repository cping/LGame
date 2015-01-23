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


public class ChartValueSerie {

	public ArrayList<ChartValue> mPointList = new ArrayList<ChartValue>();

	public int mColor = LColor.red.getARGB();
	public int mFillColor = LColor.darkGray.getARGB();
	public float mWidth = 2;
	public boolean mUseDip = true;

	public float mYmin = 0, mYmax = 1;

	private boolean mShow = true;
	private boolean mAutoYminmax = true;

	public ChartValueSerie() {
	}

	public ChartValueSerie(int color) {
		mColor = color;
		mFillColor = color;
	}

	public ChartValueSerie(LColor color) {
		mColor = color.getARGB();
		mFillColor = mColor;
	}

	public ChartValueSerie(LColor color, float width) {
		mColor = color.getARGB();
		mFillColor = mColor;
		mWidth = width;
	}

	public ChartValueSerie(int color, float width) {
		mColor = color;
		mFillColor = color;
		mWidth = width;
	}

	public ChartValueSerie(int color, float width, boolean usedip) {
		mColor = color;
		mFillColor = color;
		mWidth = width;
		mUseDip = usedip;
	}

	public ArrayList<ChartValue> getPointList() {
		return mPointList;
	}

	public void setPointList(ArrayList<ChartValue> points) {
		this.mPointList = points;
	}

	public void clearPointList() {
		if (mPointList.size() > 0) {
			this.mPointList.clear();
		}
	}

	public void addPoint(ChartValue point) {
		if (mAutoYminmax) {
			if (mPointList.size() > 0) {
				if (point.y > mYmax) {
					mYmax = point.y;
				} else if (point.y < mYmin) {
					mYmin = point.y;
				}
			} else
				mYmin = mYmax = point.y;
		}
		mPointList.add(point);
	}

	public void shiftPoint(ChartValue point, int max) {
		addPoint(point);
		while (mPointList.size() > max) {
			mPointList.remove(0);
		}
		if (mAutoYminmax) {
			calcRanges();
		}
	}

	public void removePoint(ChartValue point) {
		mPointList.remove(point);
		if (mAutoYminmax)
			calcRanges();
	}

	public ChartValue getPoint(int index) {
		return mPointList.get(index);
	}

	public void updatePoint(int index, float y) {
		mPointList.get(index).y = y;
		if (mAutoYminmax)
			calcRanges();
	}

	public int getSize() {
		return mPointList.size();
	}

	private void calcRanges() {
		int i;
		if (mPointList.size() == 0) {
			return;
		}
		if (mAutoYminmax) {
			mYmin = mPointList.get(0).y;
			mYmax = mPointList.get(0).y;
			for (i = 1; i < mPointList.size(); i++) {
				if (mPointList.get(i).y > mYmax) {
					mYmax = mPointList.get(i).y;
				} else if (mPointList.get(i).y < mYmin) {
					mYmin = mPointList.get(i).y;
				}
			}
		}
	}

	public void setAutoMinmax(boolean bAutoY) {
		this.mAutoYminmax = bAutoY;
		if (bAutoY) {
			calcRanges();
		}
	}

	public void setAutoMinmax(boolean bAutoY, float fYmin, float fYmax) {
		this.mAutoYminmax = bAutoY;
		if (!bAutoY) {
			this.mYmin = fYmin;
			this.mYmax = fYmax;
		}
		if (bAutoY) {
			calcRanges();
		}
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

	public void setStyle(int iColor, float fWidth, boolean bUsedip) {
		mColor = iColor;
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

	public void setStyle(int iColor, int iFillColor, float fWidth,
			boolean bUsedip) {
		mColor = iColor;
		mFillColor = iFillColor;
		mWidth = fWidth;
		mUseDip = bUsedip;
	}

}

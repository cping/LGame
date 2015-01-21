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

public class StyledChartPointSerie {

	public ArrayList<StyledChartPoint> mPointList = new ArrayList<StyledChartPoint>();

	public float mWidth = 2;
	public boolean mUseDip = true;

	public float mXmin = 0, mXmax = 1, mYmin = 0, mYmax = 1;

	private boolean mShow = true;
	private boolean mOrderonx = false;
	private boolean mAutoXminmax = true;
	private boolean mAutoYminmax = true;

	public StyledChartPointSerie() {
	}

	public StyledChartPointSerie(float width) {
		mWidth = width;
	}

	public StyledChartPointSerie(float width, boolean usedip) {
		mWidth = width;
		mUseDip = usedip;
	}

	public ArrayList<StyledChartPoint> getPointList() {
		return mPointList;
	}

	public void setPointList(ArrayList<StyledChartPoint> points) {
		this.mPointList = points;
	}

	public void clearPointList() {
		this.mPointList.clear();
	}

	public void addPoint(StyledChartPoint point) {
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
		StyledChartPoint p;
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

	public void shiftPoint(StyledChartPoint point, int max) {
		addPoint(point);
		while (mPointList.size() > max) {
			mPointList.remove(0);
		}
		if (mAutoXminmax || mAutoYminmax) {
			calcRanges();
		}
	}

	public void removePoint(StyledChartPoint point) {
		mPointList.remove(point);
		if (mAutoXminmax || mAutoYminmax) {
			calcRanges();
		}
	}

	public StyledChartPoint getPoint(int index) {
		return mPointList.get(index);
	}

	public int getSize() {
		return mPointList.size();
	}

	private void calcRanges() {
		int i;
		if (mPointList.size() == 0) {
			return;
		}
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

	public void setStyle(float fWidth) {
		mWidth = fWidth;
	}

	public void setStyle(float fWidth, boolean bUsedip) {
		mWidth = fWidth;
		mUseDip = bUsedip;
	}
}

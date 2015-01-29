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

import loon.core.graphics.device.LColor;

public class StyledChartPoint {
	public float x = 0;
	public float y = 0;
	public int lineColor = 0xff000000;
	public int fillColor = 0x00000000;
	public int markColor = 0x00000000;
	public float markSize = 0;

	public StyledChartPoint() {
	}

	public StyledChartPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public StyledChartPoint(float x, float y, int lineColor) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor;
	}

	public StyledChartPoint(float x, float y, LColor lineColor) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor.getARGB();
	}

	public StyledChartPoint(float x, float y, int lineColor, int fillColor) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
	}

	public StyledChartPoint(float x, float y, LColor lineColor,
			LColor fillColor, LColor markColor, float markSize) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor.getARGB();
		this.fillColor = fillColor.getARGB();
		this.markColor = markColor.getARGB();
		this.markSize = markSize;
	}

	public StyledChartPoint(float x, float y, LColor lineColor,
			LColor fillColor, int markColor, float markSize) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor.getARGB();
		this.fillColor = fillColor.getARGB();
		this.markColor = markColor;
		this.markSize = markSize;
	}

	public StyledChartPoint(float x, float y, int lineColor, int fillColor,
			int markColor, float markSize) {
		this.x = x;
		this.y = y;
		this.lineColor = lineColor;
		this.fillColor = fillColor;
		this.markColor = markColor;
		this.markSize = markSize;
	}

}

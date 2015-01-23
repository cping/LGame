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

public class ChartValue {
	   public String t = "";
	   public float y = 0;
	   public int color = -1;
	   
	   public ChartValue() {   
	   }

	   public ChartValue(String t,float y,LColor color) {
	      this.t = t;
	      this.y = y;
	      this.color = color.getARGB();
	   }
	   
	   public ChartValue(String t,float y,int color) {
	      this.t = t;
	      this.y = y;
	      this.color = color;
	   }
	   
	   public ChartValue(String t,float y) {
	      this.t = t;
	      this.y = y;
	   }
	   
}

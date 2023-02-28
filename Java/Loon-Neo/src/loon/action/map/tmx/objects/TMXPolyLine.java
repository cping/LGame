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
package loon.action.map.tmx.objects;

import loon.LSystem;
import loon.utils.StringUtils;
import loon.utils.TArray;
import loon.utils.xml.XMLElement;

public class TMXPolyLine {

	private TArray<TMXPoint> points;

	public TMXPolyLine() {
		points = new TArray<>();
	}

	public TMXPoint getPoint(int index) {
		return points.get(index);
	}

	public TArray<TMXPoint> getPoints() {
		return points;
	}

	public int getNumPoints() {
		return points.size;
	}

	public void parse(XMLElement element) {
		String pointsLine = element.getAttribute("points", LSystem.EMPTY).trim();

		for (String token : pointsLine.split(" ")) {
			String[] subTokens = token.split(",");

			TMXPoint point = new TMXPoint();
			point.x = Integer.parseInt(subTokens[0].trim());
			point.y = Integer.parseInt(subTokens[1].trim());

			points.add(point);
		}
	}

	@Override
	public String toString() {
		return StringUtils.format("TMXPolyLine [" + points.toString() + "]");
	}
}

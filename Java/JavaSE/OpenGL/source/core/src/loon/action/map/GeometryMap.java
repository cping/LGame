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
 * @email javachenpeng@yahoo.com
 * @version 0.4.2
 */
package loon.action.map;

import java.util.List;


public interface GeometryMap {

	public boolean contains(int[] position);

	public Geometry coordinate(int[] position);

	public int[] decoordinate(int x, int y);

	public int distance(int[] start, int[] end);

	public int orientate(int[] start, int[] end);

	public int[][] adjacent(int[] position);

	public int[] adjacent(int[] position, int orientation);

	public List<int[]> lineRegion(int[] start, int[] end);

	public List<int[]> circleRegion(int[] center, int radius);

}

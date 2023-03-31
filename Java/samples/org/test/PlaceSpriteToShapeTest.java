/**
 * Copyright 2008 - 2023 The Loon Game Engine Authors
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
package org.test;

import loon.Stage;
import loon.geom.Triangle2f;

public class PlaceSpriteToShapeTest extends Stage {

	@Override
	public void create() {
		//window 800x600
		//addEntityGroup("ball.png", 16).getSprites().circle(new Circle(400, 280, 260), 0, 360);
		//addEntityGroup("ball.png", 24).getSprites().ellipse(new Ellipse(400, 300, 200, 500));
		//addEntityGroup("ball.png", 16).getSprites().line(new Line(100, 200, 600, 400));
		//addEntityGroup("ball.png", 16).getSprites().rect(new RectBox(100, 100, 256, 256));
		addEntityGroup("ball.png", 32).getSprites().triangle(Triangle2f.left(200, 400, 300, 200));
	}

}

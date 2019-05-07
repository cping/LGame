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
package loon.test;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class TestCanvas extends Canvas {

	private GraphicsContext gc;

	public TestCanvas(float width, float height) {
		super(width, height);
		gc = getGraphicsContext2D();
		paint(gc);
	}
	
	public void paint(GraphicsContext g){
		g.setFill(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
	}

	public GraphicsContext getGC() {
		return this.gc;
	}

}

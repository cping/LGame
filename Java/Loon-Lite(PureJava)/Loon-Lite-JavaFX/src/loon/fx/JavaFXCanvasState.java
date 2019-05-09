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
package loon.fx;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;

public class JavaFXCanvasState {

	boolean stateSaved = false;
	float lineWidth;
	Font font;
	Affine transform;

	Paint fillPaint;
	Paint strokePaint;
	GraphicsContext gfx;

	
	JavaFXCanvasState(GraphicsContext gfx) {
		this.gfx = gfx;
		this.save();
	}

	public void save() {
		gfx.save();
		lineWidth = (float) gfx.getLineWidth();
		font = gfx.getFont();
		transform = gfx.getTransform();
		fillPaint = gfx.getFill();
		strokePaint = gfx.getStroke();
		stateSaved = true;
	}

	public void restore() {
		gfx.restore();
		gfx.setLineWidth(lineWidth);
		gfx.setFont(font);
		gfx.setTransform(transform);
		gfx.setFill(fillPaint);
		gfx.setStroke(strokePaint);
		stateSaved = false;
	}

}

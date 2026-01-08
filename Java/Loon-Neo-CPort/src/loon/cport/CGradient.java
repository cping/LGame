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
package loon.cport;

import loon.canvas.Gradient;
import loon.canvas.Pixmap;
import loon.canvas.PixmapGradientPaint;
import loon.canvas.PixmapGradientPaint.CycleMethod;
import loon.canvas.PixmapGradientPaint.GradientType;

public class CGradient extends Gradient {

	public CGradient(Pixmap pix, Gradient.Config g) {
		if (g instanceof Linear) {
			Linear linear = (Linear) g;
			new PixmapGradientPaint(pix, GradientType.LINEAR, linear.x0, linear.y0, linear.x1, linear.y1,
					pix.getWidth() / 2f, pix.getHeight() / 2f, linear.colors, CycleMethod.REPEAT, null);
		} else if (g instanceof Radial) {
			Radial radial = (Radial) g;
			new PixmapGradientPaint(pix, GradientType.RADIAL, radial.x, radial.y, radial.x + radial.r,
					radial.y + radial.r, radial.r, radial.r, radial.colors, CycleMethod.REPEAT, null);
		}
	}

}

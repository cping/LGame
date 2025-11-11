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
package loon.turtle;

import loon.canvas.LColor;
import loon.opengl.GLEx;
import loon.utils.MathUtils;

public class GotoCommand extends MoveCommand {

	public GotoCommand(String name, boolean fix, float x, float y, float endX, float endY, float angle, LColor color,
			float width, float time) {
		super(name, x, y, endX, endY, angle, color, width, time);
		this._fixDrawline = fix;
	}

	@Override
	public void drawLine(GLEx g, float sx, float sy, float ex, float ey, float progress) {
		if (!_inited) {
			return;
		}
		final float initEx = ex;
		final float initEy = ey;
		if ("posx".equals(_turleName)) {
			if (ex > sx) {
				ex = ex * progress;
				if (ex < sx) {
					ex = sx;
				}
			} else if (ex < 0) {
				ex = sx * (1f - progress);
			}
		} else if ("posy".equals(_turleName)) {
			if (ey > sy) {
				ey = ey * progress;
				if (ey < sy) {
					ey = sy;
				}
			} else if (ey < 0) {
				ey = sy * (1f - progress);
			}
		} else {
			if (_fixDrawline) {
				ex = ex * progress;
				if (ex < sx) {
					ex = sx;
				}
				ey = ey * progress;
				if (ey < sy) {
					ey = sy;
				}
			} else {
				if (ex < sx) {
					ex = sx * (1f - progress);
					if (ex < initEx) {
						ex = initEx;
					}
				}
				if (ey < sy) {
					ey = sy * (1f - progress);
					if (ey < initEy) {
						ey = initEy;
					}
				}
			}
		}
		g.drawLine(MathUtils.ifloor(sx), MathUtils.ifloor(sy), MathUtils.ifloor(ex), MathUtils.ifloor(ey),
				_currentLineWidth, _currentColor);

	}

}

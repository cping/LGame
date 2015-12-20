/**
 * 
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
 * @version 0.4.1
 */
package loon.core.graphics.component;

import loon.core.graphics.LComponent;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LFont;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.utils.collection.Array;

/**
 * 单纯文字显示用组件(无边框或背景图,但是加入了自动定是替换，并且可以注入多个信息)
 */
public class LLabels extends LComponent {

	public LLabels(int x, int y, int width, int height) {
		this(LFont.getDefaultFont(), x, y, width, height);
	}

	public LLabels(LFont font, int x, int y, int width, int height) {
		super(x, y, width, height);
		this.font = font;
	}

	class Info {

		LColor color;

		String message;

		float x;
		float y;

		float stateTime;
		float length;

		float speed;
	}

	private LFont font;

	public Array<Info> labels = new Array<Info>();

	private float speed = 0;

	public void update(long elapsedTime) {
		if (!visible) {
			return;
		}
		super.update(elapsedTime);
		this.speed = elapsedTime / 1000f;

	}

	public void draw(GLEx g, int x, int y) {
		for (int i = 0; i < labels.size(); i++) {
			Info label = labels.get(i);
			if (label.length == -1) {
				g.drawString(label.message, x + label.x,
						y + label.y + font.getHeight() / 2 + 5, label.color);
			} else {
				label.stateTime += speed;
				if (label.stateTime > label.length) {
					labels.remove(label);
				} else {
					g.drawString(label.message, x + label.x,
							y + label.y + font.getHeight() / 2 + 5, label.color);
				}
			}
		}
	}

	public void addLabel(String message, LColor color) {
		addLabel(0, 0, message, -1, color, -1);
	}

	public void addLabel(float x, float y, String message, LColor color) {
		addLabel(x, y, message, -1, color, -1);
	}

	public void addLabel(float x, float y, String message, float length,
			LColor color, float speed) {
		Info label = new Info();
		label.x = x;
		label.y = y;
		label.message = message;
		label.color = color;
		label.length = length;
		label.speed = speed;
		labels.add(label);
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		draw(g, x, y);
	}

	@Override
	public String getUIName() {
		return "Labels";
	}


}

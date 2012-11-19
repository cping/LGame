package org.loon.framework.android.game.core.graphics.component;

import org.loon.framework.android.game.action.sprite.WaitAnimation;
import org.loon.framework.android.game.core.graphics.LColor;
import org.loon.framework.android.game.core.graphics.LComponent;
import org.loon.framework.android.game.core.graphics.LContainer;
import org.loon.framework.android.game.core.graphics.LImage;
import org.loon.framework.android.game.core.graphics.device.LGraphics;
import org.loon.framework.android.game.core.timer.LTimer;
import org.loon.framework.android.game.utils.GraphicsUtils;

import android.view.KeyEvent;

/**
 * Copyright 2008 - 2009
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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public class LPicture extends LContainer {

	private LTimer timer;

	private LImage image;

	private WaitAnimation wait;

	private int states;

	private Thread thread;

	public LPicture(int x, int y, int w, int h) {
		super(x, y, w, h);
		this.timer = new LTimer(100);
		this.wait = new WaitAnimation(0, w, h);
		this.setBackground(LColor.white);
		this.wait.setRunning(true);
		this.customRendering = true;
		this.setElastic(true);
		this.setLocked(true);
		this.setLayer(100);
	}

	public void notBackground() {
		setBackground((LImage) null);
	}

	public void loadImage(final String fileName, final boolean transparency) {
		if (thread == null) {
			thread = new Thread(){
				public void run() {
					image = GraphicsUtils.loadImage(fileName, transparency);
				}
			};
			thread.start();
			thread = null;
		}
	}

	public void loadImage(final LImage img) {
		this.image = img;
	}

	public LImage getImage() {
		return image;
	}

	public void doClick() {

	}

	public void dispose() {
		super.dispose();
		timer = null;
		wait = null;
		if (image != null) {
			image.dispose();
			image = null;
		}
	}

	protected void processTouchPressed() {
		if (this.input.isTouchClick()) {
			this.doClick();
		}
	}

	protected void processKeyPressed() {
		if (this.input.getKeyPressed() == KeyEvent.KEYCODE_ENTER) {
			this.doClick();
		}
	}

	public void update(long elapsedTime) {
		if (visible) {
			super.update(elapsedTime);
			if (timer.action(elapsedTime) && image == null) {
				wait.next();
			}
		}
	}

	protected void createCustomUI(LGraphics g, int x, int y, int w, int h) {
		if (visible) {
			if (image == null) {
				wait.draw(g, x, y, w, h);
			} else {
				g.drawSize(image, x, y, w, h);
			}
		}
	}

	protected void processTouchDragged() {
		if (!locked) {
			if (getContainer() != null) {
				getContainer().sendToFront(this);
			}
			this.move(this.input.getTouchDX(), this.input.getTouchDY());
		}
	}

	public int getStates() {
		return states;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public String getUIName() {
		return "Picture";
	}

	public void createUI(LGraphics g, int x, int y, LComponent component,
			LImage[] buttonImage) {
	
	}

}
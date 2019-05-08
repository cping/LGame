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
package loon.action.sprite.effect;

import java.util.Iterator;

import loon.LSystem;
import loon.action.sprite.Entity;
import loon.canvas.LColor;
import loon.event.LTouchArea;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

/**
 * 指定位置出现波纹,可以选择椭圆或方形乃至三角扩散
 */
public class RippleEffect extends Entity implements LTouchArea, BaseEffect {

	public static RippleEffect at(Model model) {
		return new RippleEffect(model);
	}

	public enum Model {
		OVAL, RECT, TRIANGLE;
	}

	private TArray<RippleKernel> ripples;

	private LTimer timer;

	private boolean completed;

	private Model model;

	private int existTime;

	public RippleEffect() {
		this(Model.OVAL, LColor.blue);
	}

	public RippleEffect(LColor c) {
		this(Model.OVAL, c);
	}

	public RippleEffect(Model model) {
		this(model, LColor.blue);
	}

	public RippleEffect(Model m, LColor c) {
		this(m, c, 25);
	}

	public RippleEffect(Model m, LColor c, int time) {
		this.model = m;
		this.ripples = new TArray<RippleKernel>();
		this.existTime = time;
		this.timer = new LTimer(60);
		this.setColor(c);
		setRepaint(true);
	}

	public void setDelay(long delay) {
		this.timer.setDelay(delay);
	}

	public long getDelay() {
		return this.timer.getDelay();
	}

	public boolean addRipplePoint(final float x, final float y) {
		final RippleKernel ripple = new RippleKernel(x, y, existTime);
		final RealtimeProcess update = new RealtimeProcess() {

			@Override
			public void run(LTimerContext time) {
				RippleKernel rippleOther = new RippleKernel(x, y, existTime);
				ripples.add(rippleOther);
				kill();
			}
		};
		update.setDelay(LSystem.SECOND / 5);
		RealtimeProcessManager.get().addProcess(update);
		ripples.add(ripple);
		return true;
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (completed) {
			return;
		}
		int tmp = g.color();
		g.setColor(_baseColor);
		for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
			RippleKernel ripple = it.next();
			ripple.draw(g, model, drawX(sx), drawY(sy));
		}
		g.setColor(tmp);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (completed) {
			return;
		}
		if (timer.action(elapsedTime)) {
			for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
				RippleKernel ripple = it.next();
				if (ripple.isExpired()) {
					it.remove();
				}
			}
		}
		if (completed) {
			if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
				LSystem.getProcess().getScreen().remove(this);
			}
			if (getSprites() != null) {
				getSprites().remove(this);
			}
		}
	}

	public boolean checkCompleted() {
		return completed = (ripples.size == 0);
	}

	@Override
	public boolean isCompleted() {
		return completed;
	}

	@Override
	public void close() {
		super.close();
		completed = true;
	}

	public int getExistTime() {
		return existTime;
	}

	public void setExistTime(int existTime) {
		this.existTime = existTime;
	}

	@Override
	public boolean contains(float x, float y) {
		return LSystem.viewSize.contains(x, y);
	}

	@Override
	public void onAreaTouched(Event e, float touchX, float touchY) {
		if (e == Event.DOWN) {
			addRipplePoint(touchX, touchY);
		}
	}

}

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
import loon.canvas.LColor;
import loon.events.ActionKey;
import loon.events.LTouchArea;
import loon.opengl.GLEx;
import loon.utils.TArray;
import loon.utils.processes.GameProcessType;
import loon.utils.processes.RealtimeProcess;
import loon.utils.processes.RealtimeProcessManager;
import loon.utils.timer.LTimerContext;

/**
 * 指定位置出现波纹,可以选择椭圆或方形乃至三角扩散
 */
public class RippleEffect extends BaseAbstractEffect implements LTouchArea {

	private static class RippleProcess extends RealtimeProcess {

		RippleEffect rippleEffect;

		RippleKernel rippleOther;

		float dstX;

		float dstY;

		int existTime;

		public RippleProcess(float x, float y, int time, RippleEffect effect) {
			this.dstX = x;
			this.dstY = y;
			this.existTime = time;
			this.rippleEffect = effect;
			this.setProcessType(GameProcessType.View);
		}

		@Override
		public void run(LTimerContext time) {
			rippleOther = new RippleKernel(dstX, dstY, existTime);
			rippleEffect.ripples.add(rippleOther);
			kill();
		}
	}

	public static RippleEffect at(Model model) {
		return new RippleEffect(model);
	}

	public enum Model {
		OVAL, RECT, TRIANGLE, RHOMBUS;
	}

	private ActionKey touchLocked = new ActionKey();

	private RippleProcess lastProcess;

	private TArray<RippleKernel> ripples;

	private TArray<RippleProcess> processArray;

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
		this.processArray = new TArray<RippleProcess>();
		this.existTime = time;
		this.setDelay(60);
		this.setColor(c);
		setRepaint(true);
	}

	public boolean addRipplePoint(final float x, final float y) {

		this.ripples.add(new RippleKernel(x, y, existTime));

		this.lastProcess = new RippleProcess(x, y, existTime, this);
		this.lastProcess.setDelay(LSystem.SECOND / 5);
		this.processArray.add(lastProcess);

		RealtimeProcessManager.get().addProcess(lastProcess);

		return true;
	}

	@Override
	public void repaint(GLEx g, float sx, float sy) {
		if (completedAfterBlackScreen(g, sx, sy)) {
			return;
		}
		final int tmp = g.color();
		g.setColor(_baseColor);
		for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
			RippleKernel ripple = it.next();
			ripple.draw(g, model, drawX(sx), drawY(sy));
		}
		g.setColor(tmp);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		if (checkAutoRemove()) {
			return;
		}
		if (_timer.action(elapsedTime)) {
			for (Iterator<RippleKernel> it = ripples.iterator(); it.hasNext();) {
				RippleKernel ripple = it.next();
				if (ripple.isExpired()) {
					it.remove();
				}
			}
		}
	}

	public boolean checkCompleted() {
		return _completed = (ripples.size == 0);
	}

	public int getExistTime() {
		return existTime;
	}

	public RippleEffect setExistTime(int existTime) {
		this.existTime = existTime;
		return this;
	}

	@Override
	public boolean contains(float x, float y) {
		if (getSprites() != null && getSprites().getScreen() != null) {
			return getSprites().getScreen().contains(x, y);
		}
		if (LSystem.getProcess() != null && LSystem.getProcess().getScreen() != null) {
			return LSystem.getProcess().getScreen().contains(x, y);
		}
		return LSystem.viewSize.contains(x, y);
	}

	@Override
	public void onAreaTouched(Event e, float touchX, float touchY) {
		if (e == Event.DOWN) {
			if (!touchLocked.isPressed() || (lastProcess != null && lastProcess.isDead())) {
				if (lastProcess != null) {
					lastProcess.kill();
					ripples.remove(lastProcess.rippleOther);
				}
				addRipplePoint(touchX, touchY);
				touchLocked.press();
			}
		} else if (e == Event.UP) {
			touchLocked.release();
		}
	}

	@Override
	public RippleEffect setAutoRemoved(boolean autoRemoved) {
		super.setAutoRemoved(autoRemoved);
		return this;
	}

	@Override
	public void close() {
		super.close();
		if (processArray != null) {
			for (RippleProcess process : processArray) {
				if (process != null) {
					process.close();
					process = null;
				}
			}
			processArray.clear();
		}
		touchLocked.release();
	}

}

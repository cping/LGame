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
package loon;

import loon.utils.reply.Act;
import loon.utils.reply.Port;
import loon.utils.timer.LTimerContext;

public abstract class LSystemView extends BaseIO {

	public final Act<LTimerContext> update = Act.create();

	public final Act<LTimerContext> paint = Act.create();

	private final LTimerContext updateClock = new LTimerContext();
	private final LTimerContext paintClock = new LTimerContext();

	private final LGame _game;

	private final long updateRate;
	
	private long nextUpdate;
	
	public LSystemView(LGame g, long updateRate) {
		this.updateRate = updateRate;
		this._game = g;
		_game.checkBaseGame(g);
		_game.frame.connect(new Port<LGame>() {
			@Override
			public void onEmit(LGame game) {
				onFrame();
			}
		});
	}

	public void update(LTimerContext clock) {
		update.emit(clock);
	}

	public void paint(LTimerContext clock) {
		paint.emit(clock);
	}

	private void onFrame() {
		if (!LSystem._auto_repaint) {
			return;
		}
		final int updateTick = _game.tick();
		final LSetting setting = _game.setting;
		final long paintLoop = setting.fixedPaintLoopTime;
		final long updateLoop = setting.fixedUpdateLoopTime;
		
		long nextUpdate = this.nextUpdate;

		if (updateTick >= nextUpdate) {
			long updateRate = this.updateRate;
			long updates = 0;
			while (updateTick >= nextUpdate) {
				nextUpdate += updateRate;
				updates++;
			}
			this.nextUpdate = nextUpdate;
			long updateDt = updates * updateRate;
			updateClock.tick += updateDt;
			if (updateLoop == -1) {
				updateClock.timeSinceLastUpdate = updateDt;
			} else {
				updateClock.timeSinceLastUpdate = updateLoop;
			}
			update(updateClock);
		}
		long paintTick = _game.tick();
		if (paintLoop == -1) {
			paintClock.timeSinceLastUpdate = paintTick - paintClock.tick;
		} else {
			paintClock.timeSinceLastUpdate = paintLoop;
		}
		paintClock.tick = paintTick;
		paintClock.alpha = 1f - (nextUpdate - paintTick) / (float) updateRate;
		paint(paintClock);
	}

	public final LTimerContext getUpdate() {
		return updateClock;
	}

	public final LTimerContext getPaint() {
		return paintClock;
	}

	public LGame getGame() {
		return _game;
	}

}

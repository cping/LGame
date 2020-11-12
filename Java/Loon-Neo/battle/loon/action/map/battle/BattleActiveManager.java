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
package loon.action.map.battle;

import loon.LRelease;
import loon.LSystem;
import loon.Screen;
import loon.utils.reply.Port;
import loon.utils.timer.LTimer;
import loon.utils.timer.LTimerContext;

public class BattleActiveManager implements LRelease {

	private LTimer timer;

	private BattleActive battleTick;

	private Port<LTimerContext> timePort;

	private Screen stage;

	public BattleActiveManager(long d) {
		this(LSystem.getProcess().getScreen(), 100, d);
	}

	public BattleActiveManager(int activeTime, long d) {
		this(LSystem.getProcess().getScreen(), activeTime, d);
	}

	public BattleActiveManager(Screen s, int activeTime, long d) {
		this.battleTick = new BattleActive(activeTime);
		this.timer = new LTimer(d);
		this.stage = s;
	}

	public void launch(int start) {
		battleTick.setCurrent(start);
		if (stage != null && timePort == null) {
			timePort = new Port<LTimerContext>() {

				@Override
				public void onEmit(LTimerContext event) {
					if (timer.action(event)) {
						battleTick.action(stage);
					}
				}
			};
			stage.add(timePort, true);
		}
	}

	public void pause() {
		battleTick.pause(true);
	}

	public void resume() {
		battleTick.pause(false);
	}

	public int getCurrent() {
		return battleTick.getCurrent();
	}

	public void stop() {
		timer.stop();
	}

	public void reset() {
		battleTick.reset();
	}

	public LTimer getTimer() {
		return timer;
	}

	public BattleActive getBattleTick() {
		return battleTick;
	}

	@Override
	public void close() {
		if (stage != null && timePort != null) {
			stage.remove(timePort, true);
		}
	}

}

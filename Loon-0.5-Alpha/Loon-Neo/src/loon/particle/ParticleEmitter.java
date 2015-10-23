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
package loon.particle;

import java.util.ArrayList;
import java.util.List;

import loon.opengl.BaseBatch;
import loon.opengl.GLEx;
import loon.opengl.Painter;
import loon.opengl.ParticleBatch;
import loon.particle.ParticleBuffer.Effector;
import loon.particle.ParticleBuffer.Initializer;
import loon.stage.Player;
import loon.stage.PlayerUtils;
import loon.utils.reply.Act;
import loon.utils.reply.Port;
import loon.utils.reply.UnitPort;
import loon.utils.timer.GameClock;

public class ParticleEmitter {

	protected final ParticleBuffer _buffer;
	
	protected float _time;
	
	public final Player layer;

	public ParticleGenerator generator;

	public final List<Initializer> initters = new ArrayList<Initializer>();

	public final List<Effector> effectors = new ArrayList<Effector>();

	public final Act<ParticleEmitter> onExhausted = Act.create();

	public final Act<ParticleEmitter> onEmpty = Act.create();

	public ParticleEmitter(final ParticleBatch batch, final Act<GameClock> paint,
			final int maxParticles, final Painter p) {
		this.layer = new Player() {
			@Override
			protected void paintImpl(GLEx gl) {
				BaseBatch bbatch = gl.pushBatch(batch);
				_buffer.paint(batch.load(p, maxParticles), p.width(),
						p.height());
				gl.popBatch(bbatch);
			}

			@Override
			public void update(long elapsedTime) {

			}
		};
		_buffer = new ParticleBuffer(maxParticles);

		PlayerUtils.bind(layer, paint, new Port<GameClock>() {
			public void onEmit(GameClock clock) {
				paint(clock);
			}
		});
	}

	public void addParticles(int count) {
		if (_buffer.isFull()){
			return;
		}
		for (int ii = 0, ll = initters.size(); ii < ll; ii++){
			initters.get(ii).willInit(count);
		}
		_buffer.add(count, _time, initters);
	}

	public void destroyOnEmpty() {
		onEmpty.connect(new UnitPort() {
			@Override
			public void onEmit() {
				layer.close();
			}
		});
	}

	protected void paint(GameClock clock) {
		float dt = clock.dt / 1000f, now = _time + dt;
		_time = now;
		if (generator != null && generator.generate(this, now, dt)) {
			generator = null;
			onExhausted.emit(this);
		}
		if (_buffer.apply(effectors, now, dt) == 0 && generator == null) {
			onEmpty.emit(this);
		}
	}

}

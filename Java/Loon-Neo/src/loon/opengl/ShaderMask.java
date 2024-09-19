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
package loon.opengl;

import loon.LRelease;
import loon.LSystem;
import loon.events.EventActionN;

public class ShaderMask implements LRelease {

	protected ShaderSource _shaderSource;

	protected BaseBatch _maskBatch;

	protected BaseBatch _oldBatch;

	private EventActionN _onLoad;

	private boolean _closed;

	private int _blend;

	public ShaderMask(EventActionN update) {
		this(BlendMethod.MODE_ALPHA, update);
	}

	public ShaderMask(int b, EventActionN update) {
		this._blend = b;
		this._onLoad = update;
	}

	public ShaderMask setShaderSource(ShaderSource ss) {
		this._shaderSource = ss;
		return this;
	}

	public ShaderSource getShaderSource() {
		return this._shaderSource;
	}

	public ShaderMask setBlend(int b) {
		this._blend = b;
		return this;
	}

	public int getBlend() {
		return this._blend;
	}

	protected BaseBatch createBatch() {
		if (_closed) {
			return null;
		}
		if (LSystem.base() == null) {
			return null;
		}
		final GL20 gl = LSystem.base().graphics().gl;
		return new TrilateralBatch(gl, _shaderSource);
	}

	protected BaseBatch getMaskBatch() {
		if (_closed) {
			return null;
		}
		if (_maskBatch == null) {
			_maskBatch = createBatch();
		}
		_maskBatch.setBlendMode(_blend);
		if (_onLoad != null) {
			_onLoad.update();
		}
		return _maskBatch;
	}

	public BaseBatch getOldBatch() {
		return this._oldBatch;
	}

	public void pushBatch(GLEx g) {
		if (_closed) {
			return;
		}
		_oldBatch = g.batch();
		g.pushBatch(getMaskBatch());
	}

	public void popBatch(GLEx g) {
		if (_closed) {
			return;
		}
		g.popBatch(_oldBatch);
	}

	public ShaderMask reset() {
		this._closed = false;
		return this;
	}

	public boolean isClosed() {
		return this._closed;
	}

	@Override
	public void close() {
		if (_closed) {
			return;
		}
		if (_maskBatch != null) {
			_maskBatch.close();
			_maskBatch = null;
		}
		_closed = true;
	}

}

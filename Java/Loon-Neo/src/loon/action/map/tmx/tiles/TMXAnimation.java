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
package loon.action.map.tmx.tiles;

import loon.utils.TArray;
import loon.utils.TimeUtils;

public class TMXAnimation {

	private TMXTile _tile;

	private int _currentFrameIndex;

	private float _elapsedDuration;

	public TMXAnimation(TMXTile tile) {
		this._tile = tile;
		this._elapsedDuration = 0;
		this._currentFrameIndex = 0;
	}

	public void update(long delta) {
		final TArray<TMXAnimationFrame> frames = _tile.getFrames();
		_elapsedDuration += TimeUtils.convert(delta, TimeUtils.getDefaultTimeUnit(), TimeUtils.Unit.MILLIS);
		if (_elapsedDuration >= frames.get(_currentFrameIndex).getDuration()) {
			_currentFrameIndex = (_currentFrameIndex + 1) % frames.size;
			_elapsedDuration = 0;
		}
	}

	public TArray<TMXAnimationFrame> animations() {
		return _tile.getFrames();
	}

	public void next() {
		this._currentFrameIndex++;
		this._currentFrameIndex = this._currentFrameIndex % _tile.getFrames().size;
	}

	public TMXAnimationFrame getCurrentFrame() {
		return _tile.getFrames().get(_currentFrameIndex);
	}
}

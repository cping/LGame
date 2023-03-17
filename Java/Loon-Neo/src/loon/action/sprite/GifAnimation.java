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
package loon.action.sprite;

import loon.BaseIO;
import loon.LTexture;
import loon.geom.Dimension;
import loon.opengl.GLEx;
import loon.utils.ArrayByte;
import loon.utils.GifDecoder;

public class GifAnimation extends Entity {

	private GifDecoder _gifDecoder;

	private Animation _animation;

	public GifAnimation(ArrayByte bytes) {
		loadData(bytes);
	}

	public GifAnimation(String path) {
		loadData(BaseIO.loadArrayByte(path));
	}

	public Animation loadData(ArrayByte bytes) {
		this._animation = new Animation();
		this._gifDecoder = new GifDecoder();
		this._gifDecoder.readStatus(bytes);
		Dimension d = _gifDecoder.getFrameSize();
		this._width = d.getWidth();
		this._height = d.getHeight();
		for (int i = 0; i < _gifDecoder.getFrameCount(); i++) {
			int delay = _gifDecoder.getDelay(i);
			_animation.addFrame(_gifDecoder.getFrame(i).texture(), delay == 0 ? 100 : delay);
		}
		setRepaint(true);
		return _animation;
	}

	public void setRunning(boolean runing) {
		_animation.setRunning(runing);
	}

	@Override
	public void onUpdate(long elapsedTime) {
		_animation.update(elapsedTime);
		setTexture(_animation.getSpriteImage());
	}

	@Override
	public void repaint(GLEx g, float offsetX, float offsetY) {
		g.draw(_animation.getSpriteImage(), drawX(offsetX), drawY(offsetY));
	}

	@Override
	public LTexture getBitmap() {
		return _animation.getSpriteImage();
	}

	public Animation getAnimation() {
		return _animation;
	}

	public GifDecoder getGifDecoder() {
		return _gifDecoder;
	}

	@Override
	public void close() {
		super.close();
		if (_gifDecoder != null) {
			_gifDecoder = null;
		}
	}

}

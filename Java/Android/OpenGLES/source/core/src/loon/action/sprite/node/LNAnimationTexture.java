/**
 * Copyright 2008 - 2012
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
 * @version 0.3.3
 */
package loon.action.sprite.node;

import java.util.ArrayList;

import loon.action.sprite.Animation;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;
import loon.core.graphics.opengl.LTextures;

public class LNAnimationTexture {

	protected float _duration;

	private ArrayList<LTexture> _asList;

	private ArrayList<Float> _timeList;

	private String _name;

	protected float _totalDuration;

	public LNAnimationTexture() {
		this._asList = new ArrayList<LTexture>();
		this._timeList = new ArrayList<Float>();
		this._totalDuration = 0f;
	}

	public LNAnimationTexture(String fileName, int width, int height) {
		this(fileName, fileName, -1, width, height, 3f);
	}

	public LNAnimationTexture(String fileName, int maxFrame, int width, int height) {
		this(fileName, fileName, maxFrame, width, height, 3f);
	}

	public LNAnimationTexture(String fileName, int maxFrame, int width,
			int height, float duration) {
		this(fileName, fileName, maxFrame, width, height, duration);
	}

	public LNAnimationTexture(String aName, String fileName, int maxFrame,
			int width, int height, float duration) {
		this(aName, duration, Animation.getDefaultAnimation(fileName, maxFrame,
				width, height, 0));
	}

	public LNAnimationTexture(String aName, float duration, Animation as) {
		this._asList = new ArrayList<LTexture>(as.getTotalFrames());
		this._timeList = new ArrayList<Float>(as.getTotalFrames());
		this._name = aName;
		this._duration = duration;
		for (int i = 0; i < as.getTotalFrames(); i++) {
			addAnimation(as.getSpriteImage(i), _duration);
		}
	}

	public LNAnimationTexture(String aName, float duration, String... pathList) {
		this._asList = new ArrayList<LTexture>(pathList.length);
		this._timeList = new ArrayList<Float>();
		this._name = aName;
		this._duration = duration;
		for (int i = 0; i < pathList.length; i++) {
			this.addAnimation(pathList[i]);
		}
	}

	public final void addAnimation(String path) {
		this.addAnimation(path, this._duration);
	}

	public final void addAnimation(LTexture tex2d, float time) {
		this._asList.add(tex2d);
		this._timeList.add(time);
		this._totalDuration += time;
	}

	public final void addAnimation(String path, float time) {
		this._asList.add(LTextures.loadTexture(path, Format.LINEAR));
		this._timeList.add(time);
		this._totalDuration += time;
	}

	public final int frameCount() {
		return this._asList.size();
	}

	public final LTexture getFrame(int idx) {
		return this._asList.get(idx);
	}

	public final LTexture getFrameByTime(float time) {
		if (time == 0f) {
			return this._asList.get(0);
		}
		time *= this._totalDuration;
		for (int i = 0; i < this._timeList.size(); i++) {
			float num2 = this._timeList.get(i);
			if (time > num2) {
				time -= num2;
			} else {
				return this._asList.get(i);
			}
		}
		return this._asList.get(this._asList.size() - 1);
	}

	public final float getFrameTime(int idx) {
		return this._timeList.get(idx);
	}

	public final void setAnimationTime(float totalTime) {
		int count = this._timeList.size();
		if (count > 0) {
			float item = totalTime / (count);
			this._timeList.clear();
			for (int i = 0; i < count; i++) {
				this._timeList.add(item);
			}
		}
	}

	public final float getDuration() {
		return this._duration;
	}

	public final String getName() {
		return this._name;
	}

	public void dispose() {
		if (_asList != null) {
			for (LTexture tex2d : _asList) {
				tex2d.dispose();
			}
			_asList.clear();
		}
	}
}

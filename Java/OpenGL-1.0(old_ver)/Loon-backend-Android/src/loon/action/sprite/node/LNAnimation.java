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

import loon.core.LRelease;

public class LNAnimation implements LRelease {

	protected float _duration;

	private ArrayList<LNFrameStruct> _fsList;

	protected String _name;
	
	private ArrayList<Float> _timeList;
	
	protected float _totalDuration;

	public LNAnimation() {
		this._fsList = new ArrayList<LNFrameStruct>();
		this._timeList = new ArrayList<Float>();
		this._totalDuration = 0f;
	}

	public LNAnimation(String aName, float duration) {
		this._fsList = new ArrayList<LNFrameStruct>();
		this._timeList = new ArrayList<Float>();
		this._totalDuration = 0f;
		this._name = aName;
		this._duration = duration;
	}

	public LNAnimation(String aName, float duration, String... lists) {
		this._fsList = new ArrayList<LNFrameStruct>();
		this._timeList = new ArrayList<Float>();
		this._name = aName;
		this._duration = duration;
		for (int i = 0; i < lists.length; i++) {
			this.addFrameStruct(lists[i]);
		}
	}

	public void addFrameStruct(String fs) {
		this.addFrameStruct(fs, this._duration);
	}

	public void addFrameStruct(String fs, float time) {
		LNFrameStruct item = LNDataCache.getFrameStruct(fs);
		this._fsList.add(item);
		this._timeList.add(time);
		this._totalDuration += time;
	}

	public int frameCount() {
		return this._fsList.size();
	}

	public LNFrameStruct getFrame(int idx) {
		return this._fsList.get(idx);
	}

	public LNFrameStruct getFrameByTime(float Time) {
		if (Time == 0f) {
			return this._fsList.get(0);
		}
		Time *= this._totalDuration;
		for (int i = 0; i < this._timeList.size(); i++) {
			float num2 = this._timeList.get(i);
			if (Time > num2) {
				Time -= num2;
			} else {
				return this._fsList.get(i);
			}
		}
		return this._fsList.get(this._fsList.size() - 1);
	}
	
	public float getFrameTime(int idx) {
		return this._timeList.get(idx);
	}

	public void setAnimationTime(float total) {
		int count = this._timeList.size();
		if (count > 0) {
			float item = total / (count);
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

	@Override
	public void dispose() {
		if (_fsList != null) {
			_fsList.clear();
		}
	}
}

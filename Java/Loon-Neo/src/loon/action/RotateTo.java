/**
 * Copyright 2008 - 2011
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
 * @version 0.1
 */
package loon.action;

public class RotateTo extends ActionEvent {

	private float dstAngle;

	private float startAngle;

	private boolean minus;

	private float diffAngle = 1f;

	private float speed = 6f;

	public RotateTo(float dstAngle) {
		this(dstAngle, 6f);
	}

	public RotateTo(float dstAngle, float speed) {
		this(dstAngle, 1f, 6f);
	}

	public RotateTo(float dstAngle, float diff, float speed) {
		this.dstAngle = dstAngle;
		this.diffAngle = diff;
		if (this.dstAngle > 360) {
			this.dstAngle = 360;
		} else if (this.dstAngle < 0) {
			this.dstAngle = 0;
		}
		this.speed = speed;
		if (startAngle >= dstAngle) {
			minus = true;
		}
		if (startAngle == dstAngle) {
			_isCompleted = true;
		}
	}

	public boolean isComplete() {
		return _isCompleted;
	}

	public void onLoad() {
		startAngle = original.getRotation();
		if (startAngle >= dstAngle) {
			minus = true;
		}
		if (startAngle == dstAngle) {
			_isCompleted = true;
		}
	}

	public float getDiffAngle() {
		return this.diffAngle;
	}

	public void setDiffAngle(float diff) {
		this.diffAngle = diff;
	}

	public void update(long elapsedTime) {
		if (minus) {
			startAngle -= diffAngle * speed;
			if (startAngle <= dstAngle) {
				_isCompleted = true;
			}
			if (startAngle <= 0) {
				startAngle = 0;
			}
		} else {
			startAngle += diffAngle * speed;
			if (startAngle >= dstAngle) {
				_isCompleted = true;
			}
			if (startAngle >= 360) {
				startAngle = 360;
			}
		}
		original.setRotation(startAngle);

	}

	@Override
	public ActionEvent cpy() {
		RotateTo r = new RotateTo(dstAngle, speed);
		r.set(this);
		return r;
	}

	@Override
	public ActionEvent reverse() {
		RotateTo r = new RotateTo(-dstAngle, speed);
		r.set(this);
		return r;
	}

	@Override
	public String getName() {
		return "rotate";
	}
}

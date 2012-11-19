package org.loon.framework.android.game.core;

import android.view.MotionEvent;

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
 * @project loonframework
 * @author chenpeng
 * @email：ceponline@yahoo.com.cn
 * @version 0.1
 */
public final class LFlicker extends
		android.view.GestureDetector.SimpleOnGestureListener {

	private LFlickerListener listener;

	private android.view.GestureDetector detector;

	private final int FLICK_TIMEOUT = 200;

	public static final int FLICK_UP = 0;

	public static final int FLICK_RIGHT = 1;

	public static final int FLICK_LEFT = 2;

	public static final int FLICK_DOWN = 3;

	public LFlicker(LFlickerListener listener) {
		this.listener = listener;
		try {
			Runnable runnable = new Runnable() {
				public void run() {
					detector = new android.view.GestureDetector(LSystem
							.getActivity(), LFlicker.this);
				}
			};
			LSystem.runOnUiThread(runnable);
		} catch (Exception e) {
		}
	}

	public void setListener(LFlickerListener listener) {
		this.listener = listener;
	}

	public void onTouchEvent(MotionEvent e) {
		if (detector != null) {
			detector.onTouchEvent(e);
		}
	}

	public boolean onSingleTapConfirmed(MotionEvent e) {
		if (listener == null) {
			return false;
		}
		float y = e.getY();
		float x = e.getX();
		float rawX = e.getRawX();
		float rawY = e.getRawY();

		listener.touchSingleTap(x, y, rawX, rawY);
		return true;
	}

	public boolean onDoubleTap(MotionEvent e) {
		if (listener == null) {
			return false;
		}
		float y = e.getY();
		float x = e.getX();
		float rawX = e.getRawX();
		float rawY = e.getRawY();

		listener.touchDoubleTap(x, y, rawX, rawY);
		return true;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (listener == null) {
			return false;
		}
		long duration = e2.getEventTime() - e1.getDownTime();

		if (duration < FLICK_TIMEOUT) {
			return false;
		}

		float y = e2.getY();
		float x = e2.getX();
		float rawX = e2.getRawX();
		float rawY = e2.getRawY();

		listener.touchScroll(x, y, rawX, rawY);
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (listener == null) {
			return false;
		}
		long duration = e2.getEventTime() - e1.getDownTime();

		if (duration > FLICK_TIMEOUT) {
			return false;
		}

		float y = e1.getY();
		float x = e1.getX();
		float rawX = e1.getRawX();
		float rawY = e1.getRawY();

		float distanceY = e2.getY() - y;
		float distanceX = e2.getX() - x;

		boolean a = (distanceY > distanceX);
		boolean b = (distanceY > -distanceX);

		int direction = (a ? FLICK_LEFT : FLICK_UP)
				| (b ? FLICK_RIGHT : FLICK_UP);

		listener.touchFlick(x, y, rawX, rawY, direction);
		return true;
	}

}

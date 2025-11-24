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
package loon.teavm;

import org.teavm.jso.browser.AnimationFrameCallback;
import org.teavm.jso.browser.Location;
import org.teavm.jso.browser.TimerHandler;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;

import loon.teavm.dom.HTMLDocumentExt;

public class TeaBase implements AnimationFrameCallback, TimerHandler {

	private static final TeaBase _teaWinApp = new TeaBase();

	public static TeaBase get() {
		return _teaWinApp;
	}

	private Window _window;
	private TeaAgentInfo _agentInfo;
	private Runnable _runnable;

	public TeaBase() {
		this._window = Window.current();
		this._agentInfo = TeaWebAgent.computeAgentInfo();
	}

	public Window getWindow() {
		return _window;
	}

	public void setTitle(String title) {
		_window.getDocument().setTitle(title);
	}

	public float getDevicePixelRatio() {
		return (float) _window.getDevicePixelRatio();
	}

	public TeaAgentInfo getAgentInfo() {
		return _agentInfo;
	}

	public HTMLDocumentExt getDocument() {
		return (HTMLDocumentExt) _window.getDocument();
	}

	public void requestAnimationFrame(Runnable runnable) {
		this._runnable = runnable;
		Window.requestAnimationFrame(this);
	}

	public void setTimeout(Runnable runnable, double delay) {
		this._runnable = runnable;
		Window.setTimeout(this, delay);
	}

	@Override
	public void onTimer() {
		final Runnable toRun = _runnable;
		_runnable = null;
		toRun.run();
	}

	@Override
	public void onAnimationFrame(double f) {
		final Runnable toRun = _runnable;
		_runnable = null;
		toRun.run();
	}

	public Location getLocation() {
		Location location = _window.getLocation();
		return location;
	}

	public int getClientWidth() {
		return _window.getInnerWidth();
	}

	public int getClientHeight() {
		return _window.getInnerHeight();
	}

	public void addEventListener(String type, EventListener<Event> listener) {
		_window.addEventListener(type, listener);
	}

}

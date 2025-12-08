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

	private int _requestType = 0;

	private Window _window;

	private TeaAgentInfo _agentInfo;

	private Runnable _runnable;

	public TeaBase() {
		if (Loon.isCrossOriginIframe() || Loon.isIframe()) {
			this._window = Window.current();
			if (this._window == null) {
				this._window = Window.worker();
			}
		} else {
			this._window = Window.current().getTop();
			if (this._window == null) {
				this._window = Window.current();
			}
		}
		this._agentInfo = TeaWebAgent.computeAgentInfo();
	}

	public Window getWindow() {
		return _window;
	}

	public void reload() {
		_window.getLocation().reload();
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

	public int getRequestType() {
		return _requestType;
	}

	public int requestAnimationFrame(Runnable runnable) {
		this._requestType = 0;
		this._runnable = runnable;
		return Window.requestAnimationFrame(this);
	}

	public int setTimeout(Runnable runnable, double delay) {
		this._requestType = 1;
		this._runnable = runnable;
		return Window.setTimeout(this, delay);
	}

	public int setInterval(Runnable runnable, double delay) {
		this._requestType = 2;
		this._runnable = runnable;
		return Window.setInterval(this, delay);
	}

	public void cancelLoop(int id) {
		switch (_requestType) {
		case 0:
			Window.cancelAnimationFrame(id);
			break;
		case 1:
			Window.clearTimeout(id);
			break;
		case 2:
			Window.clearInterval(id);
			break;
		}
	}

	public void cancelInterval(int id) {
		Window.clearInterval(id);
	}

	public void cancelTimeout(int id) {
		Window.clearTimeout(id);
	}

	public void cancelAnimationFrame(int id) {
		Window.cancelAnimationFrame(id);
	}

	public void setRunnable(Runnable r) {
		_runnable = r;
	}

	@Override
	public void onTimer() {
		final Runnable toRun = _runnable;
		if (toRun != null) {
			toRun.run();
		}
	}

	@Override
	public void onAnimationFrame(double f) {
		final Runnable toRun = _runnable;
		if (toRun != null) {
			toRun.run();
		}
	}

	public Location getLocation() {
		Location location = _window.getLocation();
		return location;
	}

	public int getClientWidth() {
		int result = _window.getInnerWidth();
		if (result <= 0) {
			result = _window.getDocument().getBody().getClientWidth();
		}
		if (result <= 0) {
			result = _window.getDocument().getDocumentElement().getClientWidth();
		}
		return result;
	}

	public int getClientHeight() {
		int result = _window.getInnerHeight();
		if (result <= 0) {
			result = _window.getDocument().getBody().getClientHeight();
		}
		if (result <= 0) {
			result = _window.getDocument().getDocumentElement().getClientHeight();
		}
		return result;
	}

	public void addEventListener(String type, EventListener<Event> listener) {
		_window.addEventListener(type, listener);
	}

}

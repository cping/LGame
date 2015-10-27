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
package loon.html5.gwt.widgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;


public class ResizableWidgetCollection implements ResizeHandler, Iterable<ResizableWidget> {

	static class ResizableWidgetInfo {

		private ResizableWidget widget;
		private int curOffsetHeight = 0;
		private int curOffsetWidth = 0;
		private int curClientHeight = 0;
		private int curClientWidth = 0;

		public ResizableWidgetInfo (ResizableWidget widget) {
			this.widget = widget;
			updateSizes();
		}

		public int getClientHeight () {
			return curClientHeight;
		}

		public int getClientWidth () {
			return curClientWidth;
		}

		public int getOffsetHeight () {
			return curOffsetHeight;
		}

		public int getOffsetWidth () {
			return curOffsetWidth;
		}

		public boolean updateSizes () {
			int offsetWidth = widget.getElement().getOffsetWidth();
			int offsetHeight = widget.getElement().getOffsetHeight();
			int clientWidth = widget.getElement().getClientWidth();
			int clientHeight = widget.getElement().getClientHeight();
			if (offsetWidth != curOffsetWidth || offsetHeight != curOffsetHeight || clientWidth != curClientWidth
				|| clientHeight != curClientHeight) {
				this.curOffsetWidth = offsetWidth;
				this.curOffsetHeight = offsetHeight;
				this.curClientWidth = clientWidth;
				this.curClientHeight = clientHeight;
				return true;
			}

			return false;
		}
	}

	private static final int DEFAULT_RESIZE_CHECK_DELAY = 400;

	private static ResizableWidgetCollection staticCollection = null;

	public static ResizableWidgetCollection get () {
		if (staticCollection == null) {
			staticCollection = new ResizableWidgetCollection();
		}
		return staticCollection;
	}

	private Timer resizeCheckTimer = new Timer() {
		@Override
		public void run () {
			if (windowHeight != Window.getClientHeight() || windowWidth != Window.getClientWidth()) {
				windowHeight = Window.getClientHeight();
				windowWidth = Window.getClientWidth();
				schedule(resizeCheckDelay);
				return;
			}

			checkWidgetSize();

			if (resizeCheckingEnabled) {
				schedule(resizeCheckDelay);
			}
		}
	};


	private Map<ResizableWidget, ResizableWidgetInfo> widgets = new HashMap<ResizableWidget, ResizableWidgetInfo>();

	int windowHeight = 0;

	int windowWidth = 0;

	private HandlerRegistration windowHandler;

	int resizeCheckDelay = DEFAULT_RESIZE_CHECK_DELAY;

	boolean resizeCheckingEnabled;

	public ResizableWidgetCollection () {
		this(DEFAULT_RESIZE_CHECK_DELAY);
	}

	public ResizableWidgetCollection (boolean resizeCheckingEnabled) {
		this(DEFAULT_RESIZE_CHECK_DELAY, resizeCheckingEnabled);
	}

	public ResizableWidgetCollection (int resizeCheckDelay) {
		this(resizeCheckDelay, true);
	}

	protected ResizableWidgetCollection (int resizeCheckDelay, boolean resizeCheckingEnabled) {
		setResizeCheckDelay(resizeCheckDelay);
		setResizeCheckingEnabled(resizeCheckingEnabled);
	}

	public void add (ResizableWidget widget) {
		widgets.put(widget, new ResizableWidgetInfo(widget));
	}

	public void checkWidgetSize () {
		for (Map.Entry<ResizableWidget, ResizableWidgetInfo> entry : widgets.entrySet()) {
			ResizableWidget widget = entry.getKey();
			ResizableWidgetInfo info = entry.getValue();
			if (info.updateSizes()) {
				if (info.getOffsetWidth() > 0 && info.getOffsetHeight() > 0 && widget.isAttached()) {
					widget.onResize(info.getOffsetWidth(), info.getOffsetHeight());
				}
			}
		}
	}

	public int getResizeCheckDelay () {
		return resizeCheckDelay;
	}

	public boolean isResizeCheckingEnabled () {
		return resizeCheckingEnabled;
	}

	public Iterator<ResizableWidget> iterator () {
		return widgets.keySet().iterator();
	}

	public void remove (ResizableWidget widget) {
		widgets.remove(widget);
	}

	public void setResizeCheckDelay (int resizeCheckDelay) {
		this.resizeCheckDelay = resizeCheckDelay;
	}

	public void setResizeCheckingEnabled (boolean enabled) {
		if (enabled && !resizeCheckingEnabled) {
			resizeCheckingEnabled = true;
			if (windowHandler == null) {
				windowHandler = Window.addResizeHandler(this);
			}
			resizeCheckTimer.schedule(resizeCheckDelay);
		} else if (!enabled && resizeCheckingEnabled) {
			resizeCheckingEnabled = false;
			if (windowHandler != null) {
				windowHandler.removeHandler();
				windowHandler = null;
			}
			resizeCheckTimer.cancel();
		}
	}

	public void updateWidgetSize (ResizableWidget widget) {
		if (!widget.isAttached()) {
			return;
		}

		ResizableWidgetInfo info = widgets.get(widget);
		if (info != null) {
			info.updateSizes();
		}
	}

	@Override
	public void onResize (ResizeEvent event) {
		checkWidgetSize();
	}

}

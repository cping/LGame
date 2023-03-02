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
package loon.fx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.canvas.Canvas;
import loon.Graphics;

public class JavaFXResizeCanvas extends Canvas {

	private JavaFXGraphics fxgraphics;

	private JavaFXCanvasListener fxlistener;

	public JavaFXResizeCanvas(double w, double h) {
		this(null, null, w, h);
	}

	public JavaFXResizeCanvas(Graphics graphics, double w, double h) {
		this(graphics, null, w, h);
	}

	public JavaFXResizeCanvas(Graphics graphics, JavaFXCanvasListener listener, double w, double h) {
		super(w, h);
		this.setCache(false);
		this.fxgraphics = (JavaFXGraphics) graphics;
		this.setListener(listener);
	}

	public JavaFXResizeCanvas setListener(JavaFXCanvasListener l) {
		this.fxlistener = l;
		if (this.fxlistener != null) {
			widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> value, Number oldWidth, Number newWidth) {
					if (fxlistener != null) {
						fxlistener.onWidthUpdate(newWidth.intValue());
					}
				}
			});
			heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> value, Number oldHeight, Number newHeight) {
					if (fxlistener != null) {
						fxlistener.onHeightUpdate(newHeight.intValue());
					}
				}
			});
			setFocusTraversable(false);
			focusedProperty().addListener(new ChangeListener<Boolean>() {
				public void changed(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
					if (fxlistener != null) {
						fxlistener.onFocus(oldValue.booleanValue(), newValue.booleanValue());
					}
				}
			});
		}
		return this;
	}

	public JavaFXGraphics getFxgraphics() {
		return fxgraphics;
	}

	public JavaFXCanvasListener getFxlistener() {
		return fxlistener;
	}

}
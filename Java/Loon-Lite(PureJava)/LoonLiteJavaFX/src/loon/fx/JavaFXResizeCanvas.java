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

	public JavaFXResizeCanvas(Graphics graphics, double w, double h) {
		super(w, h);
		this.fxgraphics = (JavaFXGraphics) graphics;
		widthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> value, Number oldWidth, Number newWidth) {
				fxgraphics.onSizeChanged(newWidth.intValue(), (int) getHeight());
			}
		});
		heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> value, Number oldHeight, Number newHeight) {
				fxgraphics.onSizeChanged((int) getWidth(), newHeight.intValue());
			}
		});
		setFocusTraversable(false);
		focusedProperty().addListener(new ChangeListener<Boolean>() {
			public void changed(ObservableValue<? extends Boolean> value, Boolean oldValue, Boolean newValue) {
				fxgraphics.onFocused(oldValue.booleanValue(), newValue.booleanValue());
			}
		});
	}

}
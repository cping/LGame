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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;

@SuppressWarnings("deprecation")
public class ProgressBar extends Widget implements ResizableWidget {

	private static final String DEFAULT_TEXT_CLASS_NAME = "gwt-ProgressBar-text";

	private String textClassName = DEFAULT_TEXT_CLASS_NAME;
	private String textFirstHalfClassName = DEFAULT_TEXT_CLASS_NAME + "-firstHalf";
	private String textSecondHalfClassName = DEFAULT_TEXT_CLASS_NAME + "-secondHalf";

	public abstract static class TextFormatter {

		protected abstract String getText(ProgressBar bar, double curProgress);
	}

	private Element barElement;

	private double curProgress;

	private double maxProgress;

	private double minProgress;

	private boolean textVisible = true;

	private Element textElement;

	private TextFormatter textFormatter;

	public ProgressBar() {
		this(0.0, 100.0, 0.0);
	}

	public ProgressBar(double curProgress) {
		this(0.0, 100.0, curProgress);
	}

	public ProgressBar(double minProgress, double maxProgress) {
		this(minProgress, maxProgress, 0.0);
	}

	public ProgressBar(double minProgress, double maxProgress, double curProgress) {
		this(minProgress, maxProgress, curProgress, null);
	}

	public ProgressBar(double minProgress, double maxProgress, double curProgress, TextFormatter textFormatter) {
		this.minProgress = minProgress;
		this.maxProgress = maxProgress;
		this.curProgress = curProgress;
		setTextFormatter(textFormatter);

		setElement(DOM.createDiv());
		DOM.setStyleAttribute(getElement(), "position", "relative");
		setStyleName("gwt-ProgressBar-shell");

		barElement = DOM.createDiv();
		DOM.appendChild(getElement(), barElement);
		DOM.setStyleAttribute(barElement, "height", "100%");
		setBarStyleName("gwt-ProgressBar-bar");

		textElement = DOM.createDiv();
		DOM.appendChild(getElement(), textElement);
		DOM.setStyleAttribute(textElement, "position", "absolute");
		DOM.setStyleAttribute(textElement, "top", "0px");

		setProgress(curProgress);
	}

	public double getMaxProgress() {
		return maxProgress;
	}

	public double getMinProgress() {
		return minProgress;
	}

	public double getPercent() {
		if (maxProgress <= minProgress) {
			return 0.0;
		}
		double percent = (curProgress - minProgress) / (maxProgress - minProgress);
		return Math.max(0.0, Math.min(1.0, percent));
	}

	public double getProgress() {
		return curProgress;
	}

	public TextFormatter getTextFormatter() {
		return textFormatter;
	}

	public boolean isTextVisible() {
		return textVisible;
	}

	public void onResize(int width, int height) {
		if (textVisible) {
			int textWidth = DOM.getElementPropertyInt(textElement, "offsetWidth");
			int left = (width / 2) - (textWidth / 2);
			DOM.setStyleAttribute(textElement, "left", left + "px");
		}
	}

	public void redraw() {
		if (isAttached()) {
			int width = DOM.getElementPropertyInt(getElement(), "clientWidth");
			int height = DOM.getElementPropertyInt(getElement(), "clientHeight");
			onResize(width, height);
		}
	}

	public void setBarStyleName(String barClassName) {
		DOM.setElementProperty(barElement, "className", barClassName);
	}

	public void setMaxProgress(double maxProgress) {
		this.maxProgress = maxProgress;
		curProgress = Math.min(curProgress, maxProgress);
		resetProgress();
	}

	public void setMinProgress(double minProgress) {
		this.minProgress = minProgress;
		curProgress = Math.max(curProgress, minProgress);
		resetProgress();
	}

	public void setProgress(double curProgress) {
		this.curProgress = Math.max(minProgress, Math.min(maxProgress, curProgress));
		int percent = (int) (100 * getPercent());
		DOM.setStyleAttribute(barElement, "width", percent + "%");
		DOM.setElementProperty(textElement, "innerHTML", generateText(curProgress));
		updateTextStyle(percent);
		redraw();
	}

	public void setTextFirstHalfStyleName(String textFirstHalfClassName) {
		this.textFirstHalfClassName = textFirstHalfClassName;
		onTextStyleChange();
	}

	public void setTextFormatter(TextFormatter textFormatter) {
		this.textFormatter = textFormatter;
	}

	public void setTextSecondHalfStyleName(String textSecondHalfClassName) {
		this.textSecondHalfClassName = textSecondHalfClassName;
		onTextStyleChange();
	}

	public void setTextStyleName(String textClassName) {
		this.textClassName = textClassName;
		onTextStyleChange();
	}

	public void setTextVisible(boolean textVisible) {
		this.textVisible = textVisible;
		if (this.textVisible) {
			DOM.setStyleAttribute(textElement, "display", "");
			redraw();
		} else {
			DOM.setStyleAttribute(textElement, "display", "none");
		}
	}

	protected String generateText(double curProgress) {
		if (textFormatter != null) {
			return textFormatter.getText(this, curProgress);
		} else {
			return (int) (100 * getPercent()) + "%";
		}
	}

	protected Element getBarElement() {
		return barElement;
	}

	protected Element getTextElement() {
		return textElement;
	}

	@Override
	protected void onLoad() {
		DOM.setStyleAttribute(getElement(), "position", "relative");
		ResizableWidgetCollection.get().add(this);
		redraw();
	}

	@Override
	protected void onUnload() {
		ResizableWidgetCollection.get().remove(this);
	}

	protected void resetProgress() {
		setProgress(getProgress());
	}

	private void onTextStyleChange() {
		int percent = (int) (100 * getPercent());
		updateTextStyle(percent);
	}

	private void updateTextStyle(int percent) {
		if (percent < 50) {
			DOM.setElementProperty(textElement, "className", textClassName + " " + textFirstHalfClassName);
		} else {
			DOM.setElementProperty(textElement, "className", textClassName + " " + textSecondHalfClassName);
		}
	}
}

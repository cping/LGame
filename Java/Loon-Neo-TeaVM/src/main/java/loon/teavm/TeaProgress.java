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

import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;

import loon.LSetting;
import loon.canvas.LColor;

public class TeaProgress {

	private HTMLImageElement logoImage;
	private int pWidth = 200, pHeight = 60;

	private String bgColor = LColor.black.toCSS();
	private String barColor = LColor.red.toCSS();
	private String barBgColor = "#808080";

	private LSetting config;
	private int centerX = -1, centerY = -1;
	private int logoX = -1, logoY = -1;
	private float currentStep;
	private float maxStep = 100;
	private double startTime;

	protected final int canvasWidth;
	protected final int canvasHeight;

	private Loon loonApp;

	public TeaProgress(Loon loon, LSetting config, int step) {
		this.loonApp = loon;
		this.canvasWidth = config.getShowWidth();
		this.canvasHeight = config.getShowHeight();
		this.maxStep = step;
		this.config = config;
		this.pWidth = canvasWidth - 80;
		this.centerX = (canvasWidth - pWidth) / 2;
		this.centerY = (canvasHeight - pHeight) / 2;
	}

	public void startTime() {
		setStartTime(Loon.startNow());
	}

	public double getStartTime() {
		return startTime;
	}

	public void setStartTime(double time) {
		startTime = time;
	}

	public void update(HTMLCanvasElement g, float progress) {
		progress(g, (int) (Loon.nowTime() - startTime), (currentStep = maxStep * progress));
	}

	public void progress(HTMLCanvasElement g, final int tick, final float currentStep) {
		render(g, tick, currentStep, maxStep);
	}

	public void render(HTMLCanvasElement g, int tick, final float currentStep, float maxStep) {
		if (logoImage == null) {
			final HTMLImageElement img = (HTMLImageElement) HTMLDocument.current().createElement("img");
			img.setSrc(loonApp.getBaseUrl() + "logo.png");
			TeaProgress.this.logoImage = img;
			img.onLoad(new EventListener<Event>() {

				@Override
				public void handleEvent(Event evt) {
					TeaProgress.this.logoImage = img;
				}
			});
		}
		if (centerX == -1 || centerY == -1) {
			this.pWidth = config.getShowWidth() - 80;
			this.centerX = config.getShowWidth() / 2 - pWidth / 2;
			this.centerY = config.getShowHeight() / 2 - pHeight / 2;
		}
		CanvasRenderingContext2D context = TeaCanvasUtils.getContext2d(g);
		TeaCanvasUtils.fillRect(g, bgColor);
		if (logoImage != null && Loon.isComplete(logoImage)) {
			this.logoX = (config.getShowWidth() - logoImage.getWidth()) / 2;
			this.logoY = (config.getShowHeight() - logoImage.getHeight()) / 2;
			context.drawImage(logoImage, logoX, logoY - logoImage.getHeight() - 40);
			context.setFillStyle(barBgColor);
			context.fillRect(centerX, centerY, pWidth, pHeight);
			if (currentStep >= maxStep) {
				context.fillRect(centerX, centerY, pWidth, pHeight);
			} else {
				context.setFillStyle(barColor);
				context.fillRect(centerX, centerY, (int) (pWidth / maxStep * currentStep), pHeight);
			}
		}
	}

	public final boolean isCompleted() {
		return currentStep >= maxStep;
	}

}

/**
 * Copyright 2014
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
 * @version 0.4.2
 */
package loon.core.graphics.component.chart;

import loon.LSystem;
import loon.core.event.Updateable;
import loon.core.graphics.LComponent;
import loon.core.graphics.LContainer;
import loon.core.graphics.device.LColor;
import loon.core.graphics.device.LImage;
import loon.core.graphics.opengl.GLEx;
import loon.core.graphics.opengl.LTexture;
import loon.core.graphics.opengl.LTexture.Format;

/*
 * 
 * ChartValueSerie rr = new ChartValueSerie(LColor.red.getRGB(),1);
 * rr.addPoint(new ChartValue("Total",100f)); 
 * rr.addPoint(new ChartValue("You",1f,LColor.green));
 * 
 * BarChartCanvas canvas = new BarChartCanvas(160, 200);
 * canvas.setYLabelFlag("%");
 * canvas.setGridAA(false);
 * 
 * canvas.addSerie(rr); 
 * canvas.offsetX = -25;
 * 
 * LChart chart=new LChart(canvas, 66, 66);
 * add(chart);
 */
public class LChart extends LContainer {

	private boolean updateChart = false;

	private LTexture chartTexture;

	private ChartBaseCanvas baseCanvas;

	public LChart(ChartBaseCanvas canvas, int x, int y) {
		super(x, y, canvas.getWidth(), canvas.getHeight());
		this.baseCanvas = canvas;
	}

	@Override
	public void createUI(GLEx g, int x, int y, LComponent component,
			LTexture[] buttonImage) {
		if (!isVisible()) {
			return;
		}
		if (baseCanvas != null) {
			Updateable update = new Updateable() {

				@Override
				public void action(Object a) {
					if (chartTexture == null || updateChart) {
						if (chartTexture != null) {
							chartTexture.destroy();
							chartTexture = null;
						}
						LImage image = LImage.createImage(getWidth(),
								getHeight(), true);
						baseCanvas.paint(image.getLGraphics());
						image.setAutoDispose(true);
						image.setFormat(Format.LINEAR);
						chartTexture = image.getTexture();
						updateChart = false;
					}
				}
			};
			LSystem.load(update);
		}
		if (chartTexture != null && !updateChart) {
			g.drawTexture(chartTexture, x, y, LColor.white);
		}
	}

	public void update() {
		this.updateChart = true;
	}

	public boolean isUpdate() {
		return updateChart;
	}

	public ChartBaseCanvas getBaseCanvas() {
		return baseCanvas;
	}

	public void setBaseCanvas(ChartBaseCanvas c) {
		setSize(c.getWidth(), c.getHeight());
		this.baseCanvas = c;
		this.updateChart = true;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (chartTexture != null) {
			chartTexture.destroy();
			chartTexture = null;
		}
	}

	@Override
	public String getUIName() {
		return "Chart";
	}

}

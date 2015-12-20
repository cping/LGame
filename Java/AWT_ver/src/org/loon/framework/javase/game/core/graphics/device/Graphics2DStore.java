package org.loon.framework.javase.game.core.graphics.device;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

/**
 * Copyright 2008 - 2009
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
public class Graphics2DStore {

	private Paint paint;

	private Font font;

	private Stroke stroke;

	private AffineTransform transform;

	private Composite composite;

	private Shape clip;

	private RenderingHints renderingHints;

	private Color color;

	private Color background;

	public void save(Graphics2D g2d) {
		paint = g2d.getPaint();
		font = g2d.getFont();
		stroke = g2d.getStroke();
		transform = g2d.getTransform();
		composite = g2d.getComposite();
		clip = g2d.getClip();
		renderingHints = g2d.getRenderingHints();
		color = g2d.getColor();
		background = g2d.getBackground();
	}

	public void restore(Graphics2D g2d) {
		g2d.setPaint(paint);
		g2d.setFont(font);
		g2d.setStroke(stroke);
		g2d.setTransform(transform);
		g2d.setComposite(composite);
		g2d.setClip(clip);
		g2d.setRenderingHints(renderingHints);
		g2d.setColor(color);
		g2d.setBackground(background);
	}

	public Color getBackground() {
		return background;
	}

	public Shape getClip() {
		return clip;
	}

	public Color getColor() {
		return color;
	}

	public Composite getComposite() {
		return composite;
	}

	public Font getFont() {
		return font;
	}

	public Paint getPaint() {
		return paint;
	}

	public RenderingHints getRenderingHints() {
		return renderingHints;
	}

	public void setRenderingHints(RenderingHints renderingHints) {
		this.renderingHints = renderingHints;
	}

	public Stroke getStroke() {
		return stroke;
	}

	public AffineTransform getTransform() {
		return transform;
	}

}

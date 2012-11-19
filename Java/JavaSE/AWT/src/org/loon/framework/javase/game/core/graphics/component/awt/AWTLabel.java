package org.loon.framework.javase.game.core.graphics.component.awt;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.StringTokenizer;

import org.loon.framework.javase.game.utils.GraphicsUtils;

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
public class AWTLabel extends Canvas {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String[] lines;

	private int numlines, width = 6, height = 6;

	private int line_height, line;

	private int[] line_widths;

	private int min_width, max_width;

	public AWTLabel(String label) {
		this(label, 0);
	}

	public AWTLabel(String label, int width) {
		StringTokenizer t = new StringTokenizer(label, "\n");
		this.numlines = t.countTokens();
		this.lines = new String[numlines];
		this.line_widths = new int[numlines];
		for (int i = 0; i < numlines; i++) {
			lines[i] = t.nextToken();
		}
		this.min_width = width;
	}

	protected void treatment() {
		FontMetrics fm = this.getFontMetrics(this.getFont());
		if (fm == null)
			return;
		line_height = fm.getHeight();
		line = fm.getAscent();
		max_width = 0;
		for (int i = 0; i < numlines; i++) {
			line_widths[i] = fm.stringWidth(lines[i]);
			if (line_widths[i] > max_width) {
				max_width = line_widths[i];
			}
		}
	}

	public void setFont(Font f) {
		super.setFont(f);
		treatment();
		repaint();
	}

	public void addNotify() {
		super.addNotify();
		treatment();
	}

	public Dimension getPreferredSize() {
		return new Dimension(Math.max(min_width, max_width + 2 * width),
				numlines * line_height + 2 * height);
	}

	public Dimension getMinimumSize() {
		return new Dimension(Math.max(min_width, max_width), numlines
				* line_height);
	}

	/**
	 * 绘制文字
	 */
	public void paint(Graphics g) {
		int x, y;
		Dimension d = this.getSize();
		y = line + (d.height - numlines * line_height) / 2;
		GraphicsUtils.setAntialias(g, true);
		for (int i = 0; i < numlines; i++, y += line_height) {
			x = width;
			g.drawString(lines[i], x, y);
		}
		GraphicsUtils.setAntialias(g, false);
	}

}

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
package loon.font;

import loon.canvas.LColor;
import loon.geom.PointF;
import loon.opengl.GLEx;
import loon.utils.TArray;

public class FontBatch {

	private static class FontItem {

		public IFont font;

		public String mes;

		public PointF pos = new PointF();

		public LColor color;
	}

	private final TArray<FontBatch.FontItem> _fonts;

	public FontBatch() {
		this._fonts = new TArray<FontBatch.FontItem>();
	}

	public int size() {
		return _fonts.size;
	}

	public FontBatch clear() {
		_fonts.clear();
		return this;
	}

	public FontBatch addFont(IFont font, String mes, float x, float y, LColor color) {
		FontItem item = new FontItem();
		item.font = font;
		item.mes = mes;
		item.pos.set(x, y);
		item.color = color;
		_fonts.add(item);
		return this;
	}

	public boolean removeFont(IFont font) {
		if (font == null) {
			return false;
		}
		final int size = _fonts.size;
		final TArray<FontItem> items = _fonts;
		for (int i = size - 1; i > -1; i--) {
			FontItem item = items.get(i);
			if (font == item.font || font.equals(item.font)) {
				items.removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeFont(String mes) {
		if (mes == null) {
			return false;
		}
		final int size = _fonts.size;
		final TArray<FontItem> items = _fonts;
		for (int i = size - 1; i > -1; i--) {
			FontItem item = items.get(i);
			if (item.mes == mes || mes.equals(item.mes)) {
				items.removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public boolean removeFont(float x, float y) {
		final int size = _fonts.size;
		final TArray<FontItem> items = _fonts;
		for (int i = size - 1; i > -1; i--) {
			FontItem item = items.get(i);
			if (item.pos.equals(x, y)) {
				items.removeIndex(i);
				return true;
			}
		}
		return false;
	}

	public void draw(GLEx g) {
		final int size = _fonts.size;
		final TArray<FontItem> items = _fonts;
		for (int i = 0; i < size; i++) {
			FontItem item = items.get(i);
			PointF pos = item.pos;
			item.font.drawString(g, item.mes, pos.x, pos.y, item.color);
		}
	}

}

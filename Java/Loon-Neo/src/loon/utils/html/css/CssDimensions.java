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
package loon.utils.html.css;

import loon.utils.MathUtils;
import loon.utils.StringUtils;

public class CssDimensions {

	public final static CssDimensions createDimension(float w, float h) {

		CssDimensions block = new CssDimensions();

		block.content = new Rect(0.0f, 0.0f, w, h);
		block.border = new EdgeSize();
		block.margin = new EdgeSize();
		block.padding = new EdgeSize();

		return block;
	}

	public static class EdgeSize {

		public float left = 0.0f;
		public float right = 0.0f;
		public float top = 0.0f;
		public float bottom = 0.0f;

		public void set(float v) {
			this.left = v;
			this.right = v;
			this.top = v;
			this.bottom = v;
		}
	}

	public static class Rect {

		public float left = 0.0f;
		public float right = 0.0f;
		public float top = 0.0f;
		public float bottom = 0.0f;

		public float x = 0.0f;
		public float y = 0.0f;
		public float width = 0.0f;
		public float height = 0.0f;

		public Rect() {
			this(0f, 0f, 0f, 0f);
		}

		public Rect(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		public static float getValue(float size, float fontSize, String value) {

			float v = 0;
			if (isEm(value)) {
				v = fontSize * getFloat(value);
			} else if (isPer(value)) {
				v = MathUtils.percent(size, getFloat(value));
			} else {
				v = getFloat(value);
			}

			return v;
		}

		public static Rect analyze(Rect bodyRect, float fontSize, float width, float height, String... items) {
			if (bodyRect == null) {
				bodyRect = new Rect();
			}
			if (items.length == 1) {
				bodyRect.top = getValue(height, fontSize, items[0]);
			} else if (items.length == 2) {
				bodyRect.top = getValue(height, fontSize, items[0]);
				bodyRect.right = getValue(width, fontSize, items[1]);
			} else if (items.length == 3) {
				bodyRect.top = getValue(height, fontSize, items[0]);
				bodyRect.right = getValue(width, fontSize, items[1]);
				bodyRect.bottom = getValue(height, fontSize, items[2]);
			} else if (items.length >= 4) {
				bodyRect.top = getValue(height, fontSize, items[0]);
				bodyRect.right = getValue(width, fontSize, items[1]);
				bodyRect.bottom = getValue(height, fontSize, items[2]);
				bodyRect.left = getValue(width, fontSize, items[3]);
			}
			return bodyRect;
		}

		public static Rect em(float value, String top, String right, String bottom, String left) {
			return em(value, getFloat(top), getFloat(right), getFloat(bottom), getFloat(left));
		}

		public static Rect em(float value, float top, float right, float bottom, float left) {
			Rect rect = new Rect();
			rect.top = value * top;
			rect.right = value * right;
			rect.bottom = value * bottom;
			rect.left = value * left;
			return rect;
		}

		public static Rect percent(float value, String top, String right, String bottom, String left) {
			return percent(value, getFloat(top), getFloat(right), getFloat(bottom), getFloat(left));
		}

		public static Rect percent(float value, float top, float right, float bottom, float left) {
			Rect rect = new Rect();
			rect.top = MathUtils.percent(value, top);
			rect.right = MathUtils.percent(value, right);
			rect.bottom = MathUtils.percent(value, bottom);
			rect.left = MathUtils.percent(value, left);
			return rect;
		}

		public static boolean isPx(String value) {
			return value.indexOf("px") != -1;
		}

		public static boolean isEm(String value) {
			return value.indexOf("em") != -1;
		}

		public static boolean isPer(String value) {
			return value.indexOf("%") != -1;
		}

		public static float getFloat(String value) {
			int px = value.indexOf("px");
			int pt = value.indexOf("pt");
			int em = value.indexOf("rem");
			if (em == -1) {
				em = value.indexOf("em");
			}
			int cm = value.indexOf("cm");
			int per = value.indexOf("%");
			if (px != -1) {
				return Float.parseFloat(value.substring(0, px));
			}
			if (pt != -1) {
				return Float.parseFloat(value.substring(0, pt));
			}
			if (em != -1) {
				return Float.parseFloat(value.substring(0, em));
			}
			if (cm != -1) {
				return Float.parseFloat(value.substring(0, cm));
			}
			if (per != -1) {
				return Float.parseFloat(value.substring(0, per));
			}
			if (MathUtils.isNan(value)) {
				return Float.parseFloat(value);
			}
			StringBuilder sbr = new StringBuilder();
			for (int i = 0; i < value.length(); i++) {
				char ch = value.charAt(i);
				if (StringUtils.isDigit(ch)) {
					sbr.append(ch);
				}
			}
			return Float.parseFloat(sbr.toString());
		}

		public Rect doubleWH() {
			this.width *= 2;
			this.height *= 2;
			return this;
		}

		public Rect setLimit(Rect rect) {
			this.top = rect.top;
			this.left = rect.left;
			this.right = rect.right;
			this.bottom = rect.bottom;
			return this;
		}

		public Rect setSize(Rect rect) {
			this.x = rect.x;
			this.y = rect.y;
			this.width = rect.width;
			this.height = rect.height;
			return this;
		}

		public Rect expandedBy(EdgeSize edge) {
			Rect rect = new Rect();

			rect.x = this.x - edge.left;
			rect.y = this.y - edge.top;

			rect.width = this.width + edge.left + edge.right;
			rect.height = this.height + edge.top + edge.bottom;
			return rect;
		}

		public Rect expandByAll(EdgeSize border, EdgeSize margin, EdgeSize padding) {
			Rect rect = new Rect();

			rect.x = this.x - border.left - margin.left - padding.left;
			rect.y = this.y - border.top - margin.top - padding.top;

			rect.width = this.width + border.left + border.right + margin.left + margin.right + padding.left
					+ padding.right;
			rect.height = this.height + border.top + border.bottom + margin.top + margin.bottom + padding.top
					+ padding.bottom;
			return rect;
		}
	}

	public Rect content;

	public EdgeSize padding;
	public EdgeSize border;
	public EdgeSize margin;

	public CssDimensions() {
		content = new Rect();

		padding = new EdgeSize();
		border = new EdgeSize();
		margin = new EdgeSize();
	}

	public Rect paddingBox() {
		return content.expandedBy(padding);
	}

	public Rect borderBox() {
		return content.expandedBy(border);
	}

	public Rect marginBox() {
		return content.expandedBy(margin);
	}

	public Rect extraBox() {
		return content.expandByAll(border, margin, padding);
	}
}

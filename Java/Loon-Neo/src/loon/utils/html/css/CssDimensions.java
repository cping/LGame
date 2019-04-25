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

public class CssDimensions {

	public static class EdgeSize {

		public float left = 0.0f;
		public float right = 0.0f;
		public float top = 0.0f;
		public float bottom = 0.0f;
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
		}

		public Rect(float x, float y, float width, float height) {
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
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

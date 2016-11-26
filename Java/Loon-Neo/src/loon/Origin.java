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
package loon;

public enum Origin {

	FIXED {
		public float ox(float width) {
			return 0;
		}

		public float oy(float height) {
			return 0;
		}
	},

	CENTER {
		public float ox(float width) {
			return width / 2;
		}

		public float oy(float height) {
			return height / 2;
		}
	},

	UL {
		public float ox(float width) {
			return 0;
		}

		public float oy(float height) {
			return 0;
		}
	},

	UR {
		public float ox(float width) {
			return width;
		}

		public float oy(float height) {
			return 0;
		}
	},

	LL {
		public float ox(float width) {
			return 0;
		}

		public float oy(float height) {
			return height;
		}
	},

	LR {
		public float ox(float width) {
			return width;
		}

		public float oy(float height) {
			return height;
		}
	},

	TC {
		public float ox(float width) {
			return width / 2;
		}

		public float oy(float height) {
			return 0;
		}
	},

	BC {
		public float ox(float width) {
			return width / 2;
		}

		public float oy(float height) {
			return height;
		}
	},

	LC {
		public float ox(float width) {
			return 0;
		}

		public float oy(float height) {
			return height / 2;
		}
	},

	RC {
		public float ox(float width) {
			return width;
		}

		public float oy(float height) {
			return height / 2;
		}
	};

	public abstract float ox(float width);

	public abstract float oy(float height);
}
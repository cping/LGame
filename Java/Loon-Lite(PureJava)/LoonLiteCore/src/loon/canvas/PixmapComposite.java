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
package loon.canvas;

import loon.geom.Limit;
import loon.utils.MathUtils;

public class PixmapComposite extends Limit {

	public final static int SRC_IN = 0;

	public final static int SRC_OUT = 1;

	public final static int SRC_OVER = 2;

	public final static int SRC_ATOP = 3;

	public final static int ADD = 4;

	public final static int RED = 5;

	public final static int GREEN = 6;

	public final static int BLUE = 7;

	public final static int COLOR_BURN = 8;
	
	public final static int SOFT_LIGHT = 9;
	
	public final static int DIFFERENCE = 10;
	
	public final static int EXCLUSION = 11;

	public final static int LIGHTEN = 12;
	
	public final static int MULTIPLY = 13;

	private LColor _blendColor = new LColor();

	public int blendToColor(int src, int dst, int alpha) {
		int blendRB = (src & 0xFF00FF) * alpha + (dst & 0xFF00FF) * (256 - alpha) & 0xFF00FF00;
		int blendG = (src & 0xFF00) * alpha + (dst & 0xFF00) * (256 - alpha) & 0xFF0000;
		return ((blendRB | blendG) >>> 8);
	}

	public final int SET_COLOR_BURN(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(dst.r * src.r, dst.g * src.g, dst.b * src.b,
					dst.a + src.a * (1f - dst.a));
			return color.getARGB();
		}
	}

	public final int SET_SRC_OVER(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(dst.r + src.r * (1f - dst.r), dst.g + src.g * (1 - dst.g),
					dst.b + src.b * (1f - dst.b), dst.a + src.a * (1f - dst.a));
			return color.getARGB();
		}
	}

	public final int SET_SRC_ATOP(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(dst.r * src.a + src.r * (1f - dst.r),
					dst.g * src.a + src.g * (1f - dst.g), dst.b * src.a + src.b * (1f - dst.b), src.a);
			return color.getARGB();
		}
	}

	public final int SET_ADD(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(MathUtils.min(1f, src.r + dst.r), MathUtils.min(1f, src.g + dst.g),
					MathUtils.min(1f, src.b + dst.b), MathUtils.min(1f, src.a + dst.a));
			return color.getARGB();
		}
	}

	public final int SET_RED(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(dst.r, src.g, src.b, dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}

	public final int SET_GREEN(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(src.r, dst.g, src.b, dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}

	public final int SET_BLUE(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(src.r, src.g, dst.b, dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}

	public final int SET_SOFT_LIGHT(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			float r = (1f - 2f * dst.r) * src.r * src.r + 2f * dst.r * src.r;
			float g = (1f - 2f * dst.g) * src.g * src.g + 2f * dst.g * src.g;
			float b = (1f - 2f * dst.b) * src.b * src.b + 2f * dst.b * src.b;
			LColor color = _blendColor.setColor(r, g, b, dst.a + src.a * (1f - dst.a));
			return color.getARGB();
		}
	}
	

	public final int SET_DIFFERENCE(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(
	                MathUtils.abs(dst.r - src.r),
	                MathUtils.abs(dst.g - src.g),
	                MathUtils. abs(dst.b - src.b),
	                dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}
	
	public final int SET_EXCLUSION(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(
	                dst.r + src.r - 2 * dst.r * src.r,
	                dst.g + src.g - 2 * dst.g * src.g,
	                dst.b + src.b - 2 * dst.b * src.b,
	                dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}
	
	public final int SET_LIGHTEN(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor( MathUtils.max(dst.r, src.r),
					MathUtils.max(dst.g, src.g),
					MathUtils. max(dst.b, src.b),
	                dst.a + src.a * (1f - dst.a));
			return color.getARGB();
		}
	}
	
	public final int SET_MULTIPLY(LColor src, LColor dst, int transparent) {
		if (dst.getARGB() == transparent) {
			return transparent;
		} else {
			LColor color = _blendColor.setColor(
	                dst.r * src.r,
	                dst.g * src.g,
	                dst.b * src.b,
	                dst.a + src.a * (1 - dst.a));
			return color.getARGB();
		}
	}
}

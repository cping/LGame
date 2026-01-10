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
package loon.utils;

import loon.geom.RectI;
import loon.geom.XYZW;

public class Resolution implements Comparable<Resolution> {

	public static Resolution Atari2600() {
		return new Resolution(160, 192);
	}

	public static Resolution GameBoyAdvance() {
		return new Resolution(240, 160);
	}

	public static Resolution GameBoy() {
		return new Resolution(160, 144);
	}

	public static Resolution XboxSeriesX() {
		return new Resolution(3840, 2160);
	}

	public static Resolution XboxSeriesS() {
		return new Resolution(2560, 1440);
	}

	public static Resolution XboxOne() {
		return new Resolution(1920, 1080);
	}

	public static Resolution PS4() {
		return new Resolution(1920, 1080);
	}

	public static Resolution PS5() {
		return new Resolution(3840, 2160);
	}

	public static Resolution Switch() {
		return new Resolution(1920, 1080);
	}

	public static Resolution SwitchOLED() {
		return new Resolution(1280, 720);
	}

	public static Resolution SteamDeck() {
		return new Resolution(1280, 800);
	}

	public static Resolution NDS() {
		return new Resolution(256, 192);
	}

	public static Resolution NES() {
		return new Resolution(256, 224);
	}

	public static Resolution SNES() {
		return new Resolution(256, 244);
	}

	public static Resolution PSP3000() {
		return new Resolution(480, 272);
	}

	public static Resolution HVGA() {
		return new Resolution(480, 320);
	}

	public static Resolution WVGA() {
		return new Resolution(800, 480);
	}

	public static Resolution DVGA() {
		return new Resolution(960, 640);
	}

	public static Resolution SVGA() {
		return new Resolution(800, 600);
	}

	public static Resolution QVGA() {
		return new Resolution(1280, 960);
	}

	public static Resolution XGA() {
		return new Resolution(1280, 768);
	}

	public static Resolution SXGA() {
		return new Resolution(1280, 1024);
	}

	/**
	 * 转换dpi为缩放比,一些已知缩放比例按照windows规则做了修正
	 */
	public final static float convertDPIScale(int dpiv) {
		switch (dpiv) {
		case 96:
			return 1f;
		case 108:
			return 1.13f;
		case 120:
			return 1.25f;
		case 132:
			return 1.38f;
		case 144:
			return 1.5f;
		case 156:
			return 1.63f;
		case 180:
			return 1.88f;
		case 192:
			return 2f;
		case 204:
			return 2.13f;
		case 228:
			return 2.38f;
		case 240:
			return 2.5f;
		case 252:
			return 2.63f;
		case 276:
			return 2.88f;
		case 288:
			return 3f;
		case 300:
			return 3.13f;
		case 324:
			return 3.38f;
		case 348:
			return 3.63f;
		case 372:
			return 3.88f;
		case 384:
			return 4f;
		case 396:
			return 4.13f;
		case 420:
			return 4.38f;
		case 444:
			return 4.63f;
		case 468:
			return 4.88f;
		case 480:
			return 5f;
		}
		return (float) dpiv / 96f;
	}

	private final static DPI compareDPI(Resolution source, Resolution target) {
		if (source == null || target == null) {
			return DPI.MDPI;
		}
		final float x = (float) target.getWidth() / (float) source.getWidth();
		final float y = (float) target.getHeight() / (float) source.getHeight();
		final float factor = MathUtils.min(x, y);
		final DPI dpiv;
		if (factor < 1f) {
			dpiv = DPI.LDPI;
		} else if (factor < 1.5f) {
			dpiv = DPI.MDPI;
		} else if (factor < 2f) {
			dpiv = DPI.HDPI;
		} else if (factor < 3f) {
			dpiv = DPI.XHDPI;
		} else if (factor < 4f) {
			dpiv = DPI.XXHDPI;
		} else {
			dpiv = DPI.XXXHDPI;
		}
		return dpiv;
	}

	public final float getDPIFactor(DPI v) {
		switch (v) {
		case LDPI:
			return 0.75f;
		case MDPI:
			return 1f;
		case HDPI:
			return 1.5f;
		case XHDPI:
			return 2f;
		case XXHDPI:
			return 3f;
		case XXXHDPI:
			return 4f;
		}
		return 1f;
	}

	private static final Resolution[] resolutions4x3 = new Resolution[] { new Resolution(480, 320),
			new Resolution(640, 480), new Resolution(800, 600), new Resolution(1024, 768), new Resolution(1152, 864),
			new Resolution(1280, 960) };

	private static final Resolution[] resolutions16x9 = new Resolution[] { new Resolution(640, 360),
			new Resolution(854, 480), new Resolution(1176, 664), new Resolution(1280, 720), new Resolution(1360, 768),
			new Resolution(1366, 768), new Resolution(1600, 900), new Resolution(1768, 992), new Resolution(1920, 1080),
			new Resolution(2560, 1440), new Resolution(3840, 2160) };

	private static final Resolution[] resolutions16x10 = new Resolution[] { new Resolution(1280, 800),
			new Resolution(1440, 900), new Resolution(1600, 1024), new Resolution(1680, 750) };

	private final RectI rectangle;

	public Resolution(int w, int h) {
		rectangle = new RectI(w, h);
	}

	public Resolution(XYZW rect) {
		this((int) rect.getZ(), (int) rect.getW());
	}

	public String matchMode() {
		for (int i = 0; i < resolutions4x3.length; i++) {
			Resolution res = resolutions4x3[i];
			if (res.rectangle.width == rectangle.width && res.rectangle.height == rectangle.height) {
				return "Mode [4 : 3] " + toString();
			}
		}
		for (int i = 0; i < resolutions16x9.length; i++) {
			Resolution res = resolutions16x9[i];
			if (res.rectangle.width == rectangle.width && res.rectangle.height == rectangle.height) {
				return "Mode [16 : 9] " + toString();
			}
		}
		for (int i = 0; i < resolutions16x10.length; i++) {
			Resolution res = resolutions16x10[i];
			if (res.rectangle.width == rectangle.width && res.rectangle.height == rectangle.height) {
				return "Mode [16 : 10] " + toString();
			}
		}
		return "Mode [unknown] " + toString();
	}

	public int getWidth() {
		return rectangle.getWidth();
	}

	public int getHeight() {
		return rectangle.getHeight();
	}

	public DPI compareDPI(Resolution target) {
		return compareDPI(this, target);
	}

	public RectI getRect() {
		return rectangle;
	}

	public int getArea() {
		return rectangle.width * rectangle.height;
	}

	public static Resolution[] get4x3ResolutionsList() {
		return resolutions4x3;
	}

	public static Resolution[] get16x9ResolutionsList() {
		return resolutions16x9;
	}

	public static Resolution[] get16x10ResolutionsList() {
		return resolutions16x10;
	}

	public static String[] get4x3ResolutionsStrings() {
		String[] list = new String[get4x3ResolutionsList().length];
		for (int i = 0; i < list.length; i++) {
			list[i] = "4:3 [" + get4x3ResolutionsList()[i].toString() + "]";
		}
		return list;
	}

	public static String[] get16x9ResolutionsStrings() {
		String[] list = new String[get16x9ResolutionsList().length];
		for (int i = 0; i < list.length; i++) {
			list[i] = "16:9 [" + get16x9ResolutionsList()[i].toString() + "]";
		}
		return list;
	}

	public static String[] get16x10ResolutionsStrings() {
		String[] list = new String[get16x10ResolutionsList().length];
		for (int i = 0; i < list.length; i++) {
			list[i] = "16:10 [" + get16x10ResolutionsList()[i].toString() + "]";
		}
		return list;
	}

	@Override
	public int compareTo(Resolution other) {
		int area = this.getArea();
		return area - other.getArea();
	}

	@Override
	public String toString() {
		return rectangle.width + "x" + rectangle.height;
	}

}

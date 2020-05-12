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

public class Resolution implements Comparable<Resolution> {

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

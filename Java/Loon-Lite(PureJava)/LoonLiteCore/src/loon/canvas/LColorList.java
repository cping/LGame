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

import loon.LSystem;
import loon.utils.ListMap;
import loon.utils.StringUtils;

/**
 * 符合html(w3c)标准的字体英文名称与颜色关系列表
 */
public class LColorList {

	private static LColorList instance;

	public final static void freeStatic() {
		instance = null;
	}

	public final static LColorList get() {
		if (instance == null || instance.dirty) {
			synchronized (LColorList.class) {
				if (instance == null || instance.dirty) {
					instance = new LColorList();
				}
			}
		}
		return instance;
	}

	private final ListMap<String, LColor> colorList;

	private boolean dirty;

	LColorList() {
		this.colorList = new ListMap<>();
		dirty = true;
	}

	protected void pushColor(String name, LColor color) {
		colorList.put(name, color);
	}

	public boolean putColor(String name, LColor color) {
		if (StringUtils.isEmpty(name) || (color == null)) {
			return false;
		}
		if (dirty) {
			init();
		}
		pushColor(name, color);
		return true;
	}

	public LColor find(String name) {
		if (StringUtils.isEmpty(name)) {
			return LColor.white.cpy();
		}
		if (dirty) {
			init();
		}
		LColor color = colorList.get(name.trim().toLowerCase());
		if (color != null) {
			return color.cpy();
		}
		return LColor.white.cpy();
	}

	public String find(LColor color) {
		if (color == null) {
			return LSystem.UNKNOWN;
		}
		return find(color.getRGB());
	}

	public String find(int pixel) {
		if (dirty) {
			init();
		}
		for (int i = 0; i < colorList.size; i++) {
			LColor c = colorList.getValueAt(i);
			if (c != null) {
				if ((c.getRGB() == pixel) || (c.getARGB() == pixel)) {
					return colorList.getKeyAt(i);
				}
			}
		}
		return LSystem.UNKNOWN;
	}

	public void init() {

		if (dirty) {
			LColor transparent = new LColor(0, 0, 0, 0);
			pushColor("transparent", transparent);

			LColor aliceblue = new LColor(240, 248, 255);
			pushColor("aliceblue", aliceblue);

			LColor antiquewhite = new LColor(250, 235, 215);
			pushColor("antiquewhite", antiquewhite);

			LColor aqua = new LColor(0, 255, 255);
			pushColor("aqua", aqua);

			LColor aquamarine = new LColor(127, 255, 212);
			pushColor("aquamarine", aquamarine);

			LColor azure = new LColor(240, 255, 255);
			pushColor("azure", azure);

			LColor beige = new LColor(245, 245, 220);
			pushColor("beige", beige);

			LColor bisque = new LColor(255, 228, 196);
			pushColor("bisque", bisque);

			LColor black = new LColor(0, 0, 0);
			pushColor("black", black);

			LColor blanchedalmond = new LColor(255, 235, 205);
			pushColor("blanchedalmond", blanchedalmond);

			LColor blue = new LColor(0, 0, 255);
			pushColor("blue", blue);

			LColor blueviolet = new LColor(138, 43, 226);
			pushColor("blueviolet", blueviolet);

			LColor brown = new LColor(165, 42, 42);
			pushColor("brown", brown);

			LColor burlywood = new LColor(222, 184, 135);
			pushColor("burlywood", burlywood);

			LColor cadetblue = new LColor(95, 158, 160);
			pushColor("cadetblue", cadetblue);

			LColor chartreuse = new LColor(127, 255, 0);
			pushColor("chartreuse", chartreuse);

			LColor chocolate = new LColor(210, 105, 30);
			pushColor("chocolate", chocolate);

			LColor coral = new LColor(255, 127, 80);
			pushColor("coral", coral);

			LColor cornflowerblue = new LColor(100, 149, 237);
			pushColor("cornflowerblue", cornflowerblue);

			LColor cornsilk = new LColor(255, 248, 220);
			pushColor("cornsilk", cornsilk);

			LColor crimson = new LColor(220, 20, 60);
			pushColor("crimson", crimson);

			LColor cyan = new LColor(0, 255, 255);
			pushColor("cyan", cyan);

			LColor darkblue = new LColor(0, 0, 139);
			pushColor("darkblue", darkblue);

			LColor darkcyan = new LColor(0, 139, 139);
			pushColor("darkcyan", darkcyan);

			LColor darkgoldenrod = new LColor(184, 134, 11);
			pushColor("darkgoldenrod", darkgoldenrod);

			LColor darkgray = new LColor(169, 169, 169);
			pushColor("darkgray", darkgray);

			LColor darkgreen = new LColor(0, 100, 0);
			pushColor("darkgreen", darkgreen);

			LColor darkgrey = new LColor(169, 169, 169);
			pushColor("darkgrey", darkgrey);

			LColor darkkhaki = new LColor(189, 183, 107);
			pushColor("darkkhaki", darkkhaki);

			LColor darkmagenta = new LColor(139, 0, 139);
			pushColor("darkmagenta", darkmagenta);

			LColor darkolivegreen = new LColor(85, 107, 47);
			pushColor("darkolivegreen", darkolivegreen);

			LColor darkorange = new LColor(255, 140, 0);
			pushColor("darkorange", darkorange);

			LColor darkorchid = new LColor(153, 50, 204);
			pushColor("darkorchid", darkorchid);

			LColor darkred = new LColor(139, 0, 0);
			pushColor("darkred", darkred);

			LColor darksalmon = new LColor(233, 150, 122);
			pushColor("darksalmon", darksalmon);

			LColor darkseagreen = new LColor(143, 188, 143);
			pushColor("darkseagreen", darkseagreen);

			LColor darkslateblue = new LColor(72, 61, 139);
			pushColor("darkslateblue", darkslateblue);

			LColor darkslategray = new LColor(47, 79, 79);
			pushColor("darkslategray", darkslategray);

			LColor darkslategrey = new LColor(47, 79, 79);
			pushColor("darkslategrey", darkslategrey);

			LColor darkturquoise = new LColor(0, 206, 209);
			pushColor("darkturquoise", darkturquoise);

			LColor darkviolet = new LColor(148, 0, 211);
			pushColor("darkviolet", darkviolet);

			LColor deeppink = new LColor(255, 20, 147);
			pushColor("deeppink", deeppink);

			LColor deepskyblue = new LColor(0, 191, 255);
			pushColor("deepskyblue", deepskyblue);

			LColor dimgray = new LColor(105, 105, 105);
			pushColor("dimgray", dimgray);
			pushColor("dimgrey", dimgray);

			LColor dodgerblue = new LColor(30, 144, 255);
			pushColor("dodgerblue", dodgerblue);

			LColor firebrick = new LColor(178, 34, 34);
			pushColor("firebrick", firebrick);

			LColor floralwhite = new LColor(255, 250, 240);
			pushColor("floralwhite", floralwhite);

			LColor forestgreen = new LColor(34, 139, 34);
			pushColor("forestgreen", forestgreen);

			LColor fuchsia = new LColor(255, 0, 255);
			pushColor("fuchsia", fuchsia);

			LColor gainsboro = new LColor(220, 220, 220);
			pushColor("gainsboro", gainsboro);

			LColor ghostwhite = new LColor(248, 248, 255);
			pushColor("ghostwhite", ghostwhite);

			LColor gold = new LColor(255, 215, 0);
			pushColor("gold", gold);

			LColor goldenrod = new LColor(218, 165, 32);
			pushColor("goldenrod", goldenrod);

			LColor gray = new LColor(128, 128, 128);
			pushColor("gray", gray);

			LColor green = new LColor(0, 128, 0);
			pushColor("green", green);

			LColor greenyellow = new LColor(173, 255, 47);
			pushColor("greenyellow", greenyellow);

			LColor grey = new LColor(128, 128, 128);
			pushColor("grey", grey);

			LColor honeydew = new LColor(240, 255, 240);
			pushColor("honeydew", honeydew);

			LColor hotpink = new LColor(255, 105, 180);
			pushColor("hotpink", hotpink);

			LColor indianred = new LColor(205, 92, 92);
			pushColor("indianred", indianred);

			LColor indigo = new LColor(75, 0, 130);
			pushColor("indigo", indigo);

			LColor ivory = new LColor(255, 255, 240);
			pushColor("ivory", ivory);

			LColor khaki = new LColor(240, 230, 140);
			pushColor("khaki", khaki);

			LColor lavender = new LColor(230, 230, 250);
			pushColor("lavender", lavender);

			LColor lavenderblush = new LColor(255, 240, 245);
			pushColor("lavenderblush", lavenderblush);

			LColor lawngreen = new LColor(124, 252, 0);
			pushColor("lawngreen", lawngreen);

			LColor lemonchiffon = new LColor(255, 250, 205);
			pushColor("lemonchiffon", lemonchiffon);

			LColor lightblue = new LColor(173, 216, 230);
			pushColor("lightblue", lightblue);

			LColor lightcoral = new LColor(240, 128, 128);
			pushColor("lightcoral", lightcoral);

			LColor lightcyan = new LColor(224, 255, 255);
			pushColor("lightcyan", lightcyan);

			LColor lightgoldenrodyellow = new LColor(250, 250, 210);
			pushColor("lightgoldenrodyellow", lightgoldenrodyellow);

			LColor lightgray = new LColor(211, 211, 211);
			pushColor("lightgray", lightgray);

			LColor lightgreen = new LColor(144, 238, 144);
			pushColor("lightgreen", lightgreen);

			LColor lightgrey = new LColor(211, 211, 211);
			pushColor("lightgrey", lightgrey);

			LColor lightpink = new LColor(255, 182, 193);
			pushColor("lightpink", lightpink);

			LColor lightsalmon = new LColor(255, 160, 122);
			pushColor("lightsalmon", lightsalmon);

			LColor lightseagreen = new LColor(32, 178, 170);
			pushColor("lightseagreen", lightseagreen);

			LColor lightskyblue = new LColor(135, 206, 250);
			pushColor("lightskyblue", lightskyblue);

			LColor lightslategray = new LColor(119, 136, 153);
			pushColor("lightslategray", lightslategray);

			LColor lightslategrey = new LColor(119, 136, 153);
			pushColor("lightslategrey", lightslategrey);

			LColor lightsteelblue = new LColor(176, 196, 222);
			pushColor("lightsteelblue", lightsteelblue);

			LColor lightyellow = new LColor(255, 255, 224);
			pushColor("lightyellow", lightyellow);

			LColor lime = new LColor(0, 255, 0);
			pushColor("lime", lime);

			LColor limegreen = new LColor(50, 205, 50);
			pushColor("limegreen", limegreen);

			LColor linen = new LColor(250, 240, 230);
			pushColor("linen", linen);

			LColor magenta = new LColor(255, 0, 255);
			pushColor("magenta", magenta);

			LColor maroon = new LColor(128, 0, 0);
			pushColor("maroon", maroon);

			LColor mediumaquamarine = new LColor(102, 205, 170);
			pushColor("mediumaquamarine", mediumaquamarine);

			LColor mediumblue = new LColor(0, 0, 205);
			pushColor("mediumblue", mediumblue);

			LColor mediumorchid = new LColor(186, 85, 211);
			pushColor("mediumorchid", mediumorchid);

			LColor mediumpurple = new LColor(147, 112, 219);
			pushColor("mediumpurple", mediumpurple);

			LColor mediumseagreen = new LColor(60, 179, 113);
			pushColor("mediumseagreen", mediumseagreen);

			LColor mediumslateblue = new LColor(123, 104, 238);
			pushColor("mediumslateblue", mediumslateblue);

			LColor mediumspringgreen = new LColor(0, 250, 154);
			pushColor("mediumspringgreen", mediumspringgreen);

			LColor mediumturquoise = new LColor(72, 209, 204);
			pushColor("mediumturquoise", mediumturquoise);

			LColor mediumvioletred = new LColor(199, 21, 133);
			pushColor("mediumvioletred", mediumvioletred);

			LColor midnightblue = new LColor(25, 25, 112);
			pushColor("midnightblue", midnightblue);

			LColor mintcream = new LColor(245, 255, 250);
			pushColor("mintcream", mintcream);

			LColor mistyrose = new LColor(255, 228, 225);
			pushColor("mistyrose", mistyrose);

			LColor moccasin = new LColor(255, 228, 181);
			pushColor("moccasin", moccasin);

			LColor navajowhite = new LColor(255, 222, 173);
			pushColor("navajowhite", navajowhite);

			LColor navy = new LColor(0, 0, 128);
			pushColor("navy", navy);

			LColor oldlace = new LColor(253, 245, 230);
			pushColor("oldlace", oldlace);

			LColor olive = new LColor(128, 128, 0);
			pushColor("olive", olive);

			LColor olivedrab = new LColor(107, 142, 35);
			pushColor("olivedrab", olivedrab);

			LColor orange = new LColor(255, 165, 0);
			pushColor("orange", orange);

			LColor orangered = new LColor(255, 69, 0);
			pushColor("orangered", orangered);

			LColor orchid = new LColor(218, 112, 214);
			pushColor("orchid", orchid);

			LColor palegoldenrod = new LColor(238, 232, 170);
			pushColor("palegoldenrod", palegoldenrod);

			LColor palegreen = new LColor(152, 251, 152);
			pushColor("palegreen", palegreen);

			LColor paleturquoise = new LColor(175, 238, 238);
			pushColor("paleturquoise", paleturquoise);

			LColor palevioletred = new LColor(219, 112, 147);
			pushColor("palevioletred", palevioletred);

			LColor papayawhip = new LColor(255, 239, 213);
			pushColor("papayawhip", papayawhip);

			LColor peachpuff = new LColor(255, 218, 185);
			pushColor("peachpuff", peachpuff);

			LColor peru = new LColor(205, 133, 63);
			pushColor("peru", peru);

			LColor pink = new LColor(255, 192, 203);
			pushColor("pink", pink);

			LColor plum = new LColor(221, 160, 221);
			pushColor("plum", plum);

			LColor powderblue = new LColor(176, 224, 230);
			pushColor("powderblue", powderblue);

			LColor purple = new LColor(128, 0, 128);
			pushColor("purple", purple);

			LColor rebeccapurple = new LColor(102, 51, 153);
			pushColor("rebeccapurple", rebeccapurple);

			LColor red = new LColor(255, 0, 0);
			pushColor("red", red);

			LColor rosybrown = new LColor(188, 143, 143);
			pushColor("rosybrown", rosybrown);

			LColor royalblue = new LColor(65, 105, 225);
			pushColor("royalblue", royalblue);

			LColor saddlebrown = new LColor(139, 69, 19);
			pushColor("saddlebrown", saddlebrown);

			LColor salmon = new LColor(250, 128, 114);
			pushColor("salmon", salmon);

			LColor sandybrown = new LColor(244, 164, 96);
			pushColor("sandybrown", sandybrown);

			LColor seagreen = new LColor(46, 139, 87);
			pushColor("seagreen", seagreen);

			LColor seashell = new LColor(255, 245, 238);
			pushColor("seashell", seashell);

			LColor sienna = new LColor(160, 82, 45);
			pushColor("sienna", sienna);

			LColor silver = new LColor(192, 192, 192);
			pushColor("silver", silver);

			LColor skyblue = new LColor(135, 206, 235);
			pushColor("skyblue", skyblue);

			LColor slateblue = new LColor(106, 90, 205);
			pushColor("slateblue", slateblue);

			LColor slategray = new LColor(112, 128, 144);
			pushColor("slategray", slategray);
			pushColor("slategrey", slategray);

			LColor snow = new LColor(255, 250, 250);
			pushColor("snow", snow);

			LColor springgreen = new LColor(0, 255, 127);
			pushColor("springgreen", springgreen);

			LColor steelblue = new LColor(70, 130, 180);
			pushColor("steelblue", steelblue);

			LColor tan = new LColor(210, 180, 140);
			pushColor("tan", tan);

			LColor teal = new LColor(0, 128, 128);
			pushColor("teal", teal);

			LColor thistle = new LColor(216, 191, 216);
			pushColor("thistle", thistle);

			LColor tomato = new LColor(255, 99, 71);
			pushColor("tomato", tomato);

			LColor turquoise = new LColor(64, 224, 208);
			pushColor("turquoise", turquoise);

			LColor violet = new LColor(238, 130, 238);
			pushColor("violet", violet);

			LColor wheat = new LColor(245, 222, 179);
			pushColor("wheat", wheat);

			LColor white = new LColor(255, 255, 255);
			pushColor("white", white);

			LColor whitesmoke = new LColor(245, 245, 245);
			pushColor("whitesmoke", whitesmoke);

			LColor yellow = new LColor(255, 255, 0);
			pushColor("yellow", yellow);

			LColor yellowgreen = new LColor(154, 205, 50);
			pushColor("yellowgreen", yellowgreen);

			dirty = false;
		}

	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		if (dirty) {
			if (colorList != null) {
				colorList.clear();
			}
		}
		this.dirty = dirty;
	}

}

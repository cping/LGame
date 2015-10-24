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
package loon.robovm;

import java.util.HashMap;
import java.util.Map;

import loon.font.Font;
import loon.font.Font.Style;

import org.robovm.apple.coregraphics.CGAffineTransform;
import org.robovm.apple.coretext.CTFont;

public class RoboVMFont {

	private static final Font DEFAULT_FONT = new Font("Helvetica",
			Font.Style.PLAIN, 12);

	public static void registerVariant(String name, Style style,
			String variantName) {
		Map<String, String> styleVariants = _variants.get(style);
		if (styleVariants == null) {
			_variants.put(style, styleVariants = new HashMap<String, String>());
		}
		styleVariants.put(name, variantName);
	}

	final static String filterFontName(String name) {
		String familyName = name;
		if (familyName != null) {
			if (familyName.equalsIgnoreCase("Serif")
					|| familyName.equalsIgnoreCase("TimesRoman")) {
				familyName = "Times New Roman";
			} else if (familyName.equalsIgnoreCase("SansSerif")
					|| familyName.equalsIgnoreCase("Helvetica")) {
				familyName = "Helvetica";
			} else if (familyName.equalsIgnoreCase("Monospaced")
					|| familyName.equalsIgnoreCase("Courier")
					|| familyName.equalsIgnoreCase("Dialog")) {
				familyName = "Arial";
			}
		}
		return familyName;
	}

	static CTFont resolveFont(Font font) {
		CTFont ctFont = fonts.get(font == null ? DEFAULT_FONT : font);
		if (ctFont == null) {
			String iosName = getVariant(filterFontName(font.name), font.style);
			fonts.put(
					font,
					ctFont = CTFont.create(iosName, font.size,
							CGAffineTransform.Identity()));
		}
		return ctFont;
	}

	private static String getVariant(String name, Style style) {
		Map<String, String> styleVariants = _variants.get(style);
		String variant = (styleVariants == null) ? null : styleVariants
				.get(name);
		if (variant != null)
			return variant;
		else if (style == Style.BOLD_ITALIC) {
			return getVariant(name, Style.BOLD);
		} else
			return name;
	}

	private static Map<Font, CTFont> fonts = new HashMap<>();

	private static Map<Style, Map<String, String>> _variants = new HashMap<>();
	static {
		registerVariant("American Typewriter", Style.PLAIN,
				"AmericanTypewriter");
		registerVariant("American Typewriter", Style.BOLD,
				"AmericanTypewriter-Bold");
		registerVariant("Arial", Style.PLAIN, "ArialMT");
		registerVariant("Arial", Style.ITALIC, "Arial-ItalicMT");
		registerVariant("Arial", Style.BOLD, "Arial-BoldMT");
		registerVariant("Arial", Style.BOLD_ITALIC, "Arial-BoldItalicMT");
		registerVariant("Arial Hebrew", Style.PLAIN, "ArialHebrew");
		registerVariant("Arial Hebrew", Style.BOLD, "ArialHebrew-Bold");
		registerVariant("Baskerville", Style.BOLD, "Baskerville-Bold");
		registerVariant("Baskerville", Style.ITALIC, "Baskerville-Italic");
		registerVariant("Baskerville", Style.BOLD_ITALIC,
				"Baskerville-BoldItalic");
		registerVariant("Chalkboard SE", Style.PLAIN, "ChalkboardSE-Regular");
		registerVariant("Chalkboard SE", Style.BOLD, "ChalkboardSE-Bold");
		registerVariant("Cochin", Style.BOLD, "Cochin-Bold");
		registerVariant("Cochin", Style.ITALIC, "Cochin-Italic");
		registerVariant("Cochin", Style.BOLD_ITALIC, "Cochin-BoldItalic");
		registerVariant("Courier", Style.BOLD, "Courier-Bold");
		registerVariant("Courier", Style.ITALIC, "Courier-Oblique");
		registerVariant("Courier", Style.BOLD_ITALIC, "Courier-BoldOblique");
		registerVariant("Courier New", Style.PLAIN, "CourierNewPSMT");
		registerVariant("Courier New", Style.BOLD, "CourierNewPS-BoldMT");
		registerVariant("Courier New", Style.ITALIC, "CourierNewPS-ItalicMT");
		registerVariant("Courier New", Style.BOLD_ITALIC,
				"CourierNewPS-BoldItalicMT");
		registerVariant("Georgia", Style.ITALIC, "Georgia-Italic");
		registerVariant("Georgia", Style.BOLD, "Georgia-Bold");
		registerVariant("Georgia", Style.BOLD_ITALIC, "Georgia-BoldItalic");
		registerVariant("Helvetica", Style.BOLD, "Helvetica-Bold");
		registerVariant("Helvetica", Style.ITALIC, "Helvetica-Oblique");
		registerVariant("Helvetica", Style.BOLD_ITALIC,
				"Helvetica-Bold-Oblique");
		registerVariant("Helvetica Neue", Style.PLAIN, "HelveticaNeue");
		registerVariant("Helvetica Neue", Style.BOLD, "HelveticaNeue-Bold");
		registerVariant("Helvetica Neue", Style.ITALIC, "HelveticaNeue-Italic");
		registerVariant("Helvetica Neue", Style.BOLD_ITALIC,
				"HelveticaNeue-BoldItalic");
		registerVariant("Palatino", Style.PLAIN, "Palatino-Romain");
		registerVariant("Palatino", Style.ITALIC, "Palatino-Italic");
		registerVariant("Palatino", Style.BOLD, "Palatino-Bold");
		registerVariant("Palatino", Style.BOLD_ITALIC, "Palatino-BoldItalic");
		registerVariant("Times New Roman", Style.PLAIN, "TimesNewRomanPSMT");
		registerVariant("Times New Roman", Style.ITALIC,
				"TimesNewRomanPS-ItalicMT");
		registerVariant("Times New Roman", Style.BOLD, "TimesNewRomanPS-BoldMT");
		registerVariant("Times New Roman", Style.BOLD_ITALIC,
				"TimesNewRomanPS-BoldItalicMT");
		registerVariant("Trebuchet MS", Style.PLAIN, "TrebuchetMS");
		registerVariant("Trebuchet MS", Style.ITALIC, "TrebuchetMS-Italic");
		registerVariant("Trebuchet MS", Style.BOLD, "TrebuchetMS-Bold");
		registerVariant("Trebuchet MS", Style.BOLD_ITALIC,
				"Trebuchet-BoldItalic");
		registerVariant("Verdana", Style.ITALIC, "Verdana-Italic");
		registerVariant("Verdana", Style.BOLD, "Verdana-Bold");
		registerVariant("Verdana", Style.BOLD_ITALIC, "Verdana-BoldItalic");
		registerVariant("Times", Style.PLAIN, "TimesNewRomanPSMT");
		registerVariant("Times", Style.ITALIC, "TimesNewRomanPS-ItalicMT");
		registerVariant("Times", Style.BOLD, "TimesNewRomanPS-BoldMT");
		registerVariant("Times", Style.BOLD_ITALIC,
				"TimesNewRomanPS-BoldItalicMT");
	}

}

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

import java.util.Comparator;

import loon.BaseIO;
import loon.canvas.LColor;
import loon.utils.CharParser;
import loon.utils.StringUtils;
import loon.utils.TArray;

public class CssParser extends CharParser {

	public final static CssStyleSheet parse(String path) {
		return new CssParser().parseText(BaseIO.loadText(path));
	}

	public final static CssStyleSheet loadText(String context) {
		return new CssParser().parseText(context);
	}
	
	public CssStyleSheet parseText(String c) {
		context = c;
		poistion = 0;
		CssStyleSheet sheet = new CssStyleSheet();
		sheet.rules = parseRules();
		return sheet;
	}

	protected CssRule parseRule() {
		CssRule rule = new CssRule();
		TArray<CssSelectorObject> selectors = parseSelectors();
		TArray<CssDeclaration> declarations = parseDeclarations();
		rule.selectors = selectors;
		rule.declarations = declarations;
		return rule;
	}

	protected TArray<CssRule> parseRules() {
		TArray<CssRule> rules = new TArray<CssRule>();
		while (!eof()) {
			CssRule rule = parseRule();
			rules.add(rule);
		}
		return rules;
	}

	protected CssSelector parseSelectorData() {
		CssSelector SimpleSelector = new CssSelector(null, null, new TArray<String>());

		for (;;) {
			char ch = nextChar();
			switch (ch) {
			case '#':
				consumeChar();
				SimpleSelector.id = parseName();
				break;
			case '.':
				consumeChar();
				SimpleSelector.classNames.add(parseName());
				break;
			case '*':
				consumeChar();
				SimpleSelector.classNames.add("*");
			default:
				if (!(StringUtils.isAsciiLetterDiait(nextChar()))) {
					return SimpleSelector;
				}
				String tagName = parseName();
				SimpleSelector.tagName = tagName;
				break;
			}
		}
	}

	protected TArray<CssSelectorObject> parseSelectors() {

		consumeWhitespace();

		TArray<CssSelectorObject> selectors = new TArray<CssSelectorObject>();

		for (;;) {

			CssSelector obj = parseSelectorData();

			CssSelectorObject selector = new CssSelectorObject();

			selector.setSelector(obj);

			selectors.add(selector);

			char nextChar = nextChar();

			switch (nextChar) {

			case ',':
				consumeChar();
				break;
			case '{':
				consumeWhitespace();
				return sort(selectors);
			default:
				return sort(selectors);
			}
		}
	}

	protected TArray<CssSelectorObject> sort(TArray<CssSelectorObject> selectors) {

		selectors.sort(new Comparator<CssSelectorObject>() {

			@Override
			public int compare(CssSelectorObject selector1, CssSelectorObject selector2) {
				if (selector1 == null || selector2 == null) {
					return 0;
				}
				return (selector1.getSelectorTempString().compareTo(selector2.getSelectorTempString()));
			}
		});

		selectors.reverse();

		return selectors;
	}

	protected String parseName() {
		StringBuilder sb = new StringBuilder();
		while (!eof() && StringUtils.isAsciiLetterDiait(nextChar())) {
			char consumedChar = consumeChar();
			sb.append(consumedChar);
		}
		return sb.toString();
	}

	protected CssColor parseColor() {
		if (!(consumeChar() == '#')) {
			return null;
		}
		String colorString = context.substring(poistion - 1, poistion += 6);
		LColor color = new LColor(colorString);
		return new CssColor(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	protected float parseFloat() {
		StringBuilder sb = new StringBuilder();
		while (!eof() && StringUtils.isDigitCharacter(nextChar())) {
			sb.append(consumeChar());
		}
		return Float.parseFloat(sb.toString());
	}

	protected CssUnit parseUnit() {
		String unitStr = parseName().toLowerCase();
		if (unitStr.equals("px")) {
			return CssUnit.PX();
		} else {
			return null;
		}
	}

	protected CssLength parseLength() {
		return new CssLength(parseFloat(), parseUnit());
	}

	protected CssValue parseValue() {
		CssValue value = null;
		if (StringUtils.isDigit(nextChar())) {
			value = parseLength();
		} else if (nextChar() == '#') {
			value = parseColor();
		} else {
			value = new CssKeyword(parseName());
		}
		return value;
	}

	protected CssDeclaration parseDeclaration() {

		consumeWhitespace();
		String propertyName = parseName();

		consumeWhitespace();
		if (!(consumeChar() == ':')) {
			return null;
		}

		consumeWhitespace();
		CssValue propertyValue = parseValue();

		if (!(consumeChar() == ';')) {
			return null;
		}

		return new CssDeclaration(propertyName, propertyValue);
	}

	protected TArray<CssDeclaration> parseDeclarations() {

		consumeWhitespace();

		TArray<CssDeclaration> declarations = new TArray<CssDeclaration>();
		char consumedChar = consumeChar();

		if (!(consumedChar == '{')) {
			return null;
		}

		for (;;) {
			consumeWhitespace();

			boolean endOfDeclaration = (nextChar() == '}');

			if (eof() || endOfDeclaration) {
				consumeChar();
				return declarations;
			}

			CssDeclaration declaration = parseDeclaration();
			if (declaration != null) {
				declarations.add(declaration);
			}
		}
	}

}

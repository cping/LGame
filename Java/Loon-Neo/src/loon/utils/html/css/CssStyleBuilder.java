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

import loon.utils.ObjectMap;
import loon.utils.TArray;
import loon.utils.html.HtmlElement;

public class CssStyleBuilder {

	private ObjectMap<String, CssValue> selectorValues(HtmlElement elementData, CssStyleSheet styleSheet) {

		ObjectMap<String, CssValue> values = new ObjectMap<String, CssValue>();

		TArray<CssMatchedRule> rules = matchRules(elementData, styleSheet);

		for (CssMatchedRule matchedRule : rules) {
			for (CssDeclaration declaration : matchedRule.rule.declarations) {

				values.put(declaration.name, declaration.value);
			}
		}
		return values;
	}

	private ObjectMap<String, CssValue> generalSelectorValues(CssStyleSheet stylesheet) {

		ObjectMap<String, CssValue> values = new ObjectMap<String, CssValue>();

		for (CssRule rule : stylesheet.rules) {
			for (CssSelectorObject selector : rule.selectors) {
				
				if (selector != null && selector.selector != null && selector.selector.classNames != null
						&& selector.selector.classNames.contains("*")) {
					
					for (CssDeclaration declaration : rule.declarations) {
					
						values.put(declaration.name, declaration.value);
					}
				}
			}
		}

		return values;
	}

	private TArray<CssMatchedRule> matchRules(HtmlElement elementData, CssStyleSheet styleSheet) {

		TArray<CssMatchedRule> matchedRules = new TArray<CssMatchedRule>();
		TArray<CssRule> rules = styleSheet.rules;

		for (CssRule rule : rules) {
			CssMatchedRule matchedRule = matchRule(elementData, rule);
			if (null != matchedRule){
				matchedRules.add(matchedRule);
			}
		}
		return matchedRules;
	}

	private CssMatchedRule matchRule(HtmlElement elementData, CssRule rule) {

		for (CssSelectorObject s : rule.selectors) {

			if (s != null) {

				boolean matchedSelector = matchBaseSelector(elementData, s.selector);

				if (matchedSelector) {
					return new CssMatchedRule(s.getSelectorTemp(), rule);
				}
			}
		}
		return null;
	}

	private boolean matchBaseSelector(HtmlElement elementData, CssSelector selector) {

		boolean found = false;

		if (selector.tagName != null) {
			if (selector.tagName.equals(elementData.getName())) {
				found = true;
			}
		}

		String[] clazz = elementData.getClasses();
		for (int i = 0; i < clazz.length; i++) {

			if (selector.classNames.contains("*") || selector.classNames.contains(clazz[i])) {

				found = true;
				break;
			}
		}

		if (elementData.getId() != null) {
			if (selector.id != null && selector.id.equals(elementData.getId())) {

				found = true;
			}
		}

		return found;
	}

	protected boolean matches(HtmlElement elementData, CssSelectorObject selector) {
		if (isSelector(selector)) {
			return matchBaseSelector(elementData, selector.selector);
		} else {
			return false;
		}
	}

	private boolean isSelector(CssSelectorObject selector) {
		return selector.selector != null;
	}

	public CssStyleNode build(HtmlElement root, CssStyleSheet styleSheet) {

		CssStyleNode styledNode = new CssStyleNode();

		styledNode.node = root;

		if (root.isOnlyText()) {
		
			styledNode.values = generalSelectorValues(styleSheet);

		} else {

			styledNode.values = selectorValues(root, styleSheet);
		}

		if (root.childCount() > 0) {
			TArray<CssStyleNode> childrenStyle = new TArray<CssStyleNode>();

			for (HtmlElement childNode : root.childs()) {
				childrenStyle.add(build(childNode, styleSheet));
			}

			styledNode.children = childrenStyle;
		}

		return styledNode;

	}
}

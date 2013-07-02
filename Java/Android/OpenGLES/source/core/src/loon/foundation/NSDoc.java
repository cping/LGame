/**
 * Copyright 2013 The Loon Authors
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
 */
package loon.foundation;

import java.io.InputStream;
import java.util.ArrayList;
import loon.utils.xml.XMLDocument;
import loon.utils.xml.XMLElement;
import loon.utils.xml.XMLParser;

public class NSDoc {


	public static NSObject parse(String res) throws Exception {
		return parse(res);
	}

	public static NSObject parse(InputStream is) throws Exception {
		XMLDocument doc = XMLParser.parse(is);
		return parseObject(doc.getRoot().getFirstChild().getFirstChild());
	}

	private static NSObject parseObject(XMLElement n) throws Exception {
		String type = n.getName();
		if (type.equalsIgnoreCase("dict")) {
			NSDictionary dict = new NSDictionary();
			ArrayList<XMLElement> children = n.list();
			for (int i = 0; i < children.size(); i += 2) {
				XMLElement key = children.get(i + 0);
				XMLElement val = children.get(i + 1);
				dict.put(key.getContents(), parseObject(val));
			}
			return dict;
		} else if (type.equalsIgnoreCase("array")) {
			ArrayList<XMLElement> children = n.list();
			NSArray array = new NSArray(children.size());
			for (int i = 0; i < children.size(); i++) {
				array.setValue(i, parseObject(children.get(i)));
			}
			return array;
		} else if (type.equalsIgnoreCase("true")
				|| type.equalsIgnoreCase("yes")) {
			return new NSNumber(true);
		} else if (type.equalsIgnoreCase("false")
				|| type.equalsIgnoreCase("no")) {
			return new NSNumber(false);
		} else if (type.equalsIgnoreCase("integer")) {
			return new NSNumber(n.getContents());
		} else if (type.equalsIgnoreCase("real")) {
			return new NSNumber(n.getContents());
		} else if (type.equalsIgnoreCase("string")) {
			return new NSString(n.getContents());
		} else if (type.equalsIgnoreCase("data")) {
			return new NSData(n.getContents());
		} else if (type.equalsIgnoreCase("range")) {
			ArrayList<XMLElement> children = n.list();
			if (children.size() == 2) {
				XMLElement key = children.get(0);
				XMLElement val = children.get(1);
				return new NSRange(Integer.parseInt(key.getContents()),
						Integer.parseInt(val.getContents()));
			}
		}
		return null;
	}
}

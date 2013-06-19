namespace Loon.Foundation {
	
    using Loon.Utils.Xml;
    using Loon.Utils;
    using System;
    using System.Collections.Generic;
	
	public class NSDoc {
	
		public static NSObject Parse(String res) {
			return Parse(res);
		}
	
		public static NSObject Parse(System.IO.Stream s) {
			XMLDocument doc = XMLParser.Parse(s);
			return ParseObject(doc.GetRoot().GetFirstChild().GetFirstChild());
		}

        private static NSObject ParseObject(XMLElement n)
        {
			String type = n.GetName();
			if (type.Equals("dict",StringComparison.InvariantCultureIgnoreCase)) {
				NSDictionary dict = new NSDictionary();
				List<XMLElement> children = n.List();
				for (int i = 0; i < children.Count; i += 2) {
                    XMLElement key = (XMLElement)children[i + 0];
					XMLElement val = (XMLElement) children[i + 1];
					dict.Put(key.GetContents(), ParseObject(val));
				}
				return dict;
			} else if (type.Equals("array",StringComparison.InvariantCultureIgnoreCase)) {
				List<XMLElement> children = n.List();
				NSArray array = new NSArray(children.Count);
				for (int i = 0; i < children.Count; i++) {
					array.SetValue(i, ParseObject((XMLElement) children[i]));
				}
				return array;
			} else if (type.Equals("true",StringComparison.InvariantCultureIgnoreCase)
					|| type.Equals("yes",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSNumber(true);
			} else if (type.Equals("false",StringComparison.InvariantCultureIgnoreCase)
					|| type.Equals("no",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSNumber(false);
			} else if (type.Equals("integer",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSNumber(n.GetContents());
			} else if (type.Equals("real",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSNumber(n.GetContents());
			} else if (type.Equals("string",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSString(n.GetContents());
			} else if (type.Equals("data",StringComparison.InvariantCultureIgnoreCase)) {
				return new NSData(n.GetContents());
			} else if (type.Equals("range",StringComparison.InvariantCultureIgnoreCase)) {
				List<XMLElement> children = n.List();
				if (children.Count == 2) {
					XMLElement key = (XMLElement) children[0];
					XMLElement val = (XMLElement) children[1];
					return new NSRange(Int32.Parse(key.GetContents()),
							Int32.Parse(val.GetContents()));
				}
			}
			return null;
		}
	}
}

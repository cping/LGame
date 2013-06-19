using Loon.Utils;
using System;
using Loon.Java;
using System.Text;
namespace Loon.Foundation {

	public class NSString : NSObject {
	
		protected internal string content;
	
		internal NSString() {
			this.content = null;
			this.content = "";
		}
	
		internal NSString(string s) {
			this.content = null;
			this.content = s;
		}
	
		internal NSString(string str0, params object[] args) {
			this.content = string.Format(str0, args);
		}
	
		internal NSString(byte[] bytes, System.Text.Encoding encoding) {
			content = StringUtils.GetString(bytes,encoding);
		}
	
		public static NSString WithString(string str0) {
			return new NSString(str0);
		}
	
		public static NSString String() {
			return new NSString();
		}
	
		public static string StringWithFormat(string format, params object[] args) {
            return string.Format(format, args);
		}
	
		public string StringByAppendingFormat(string str0, string format,
				params object[] args) {
                    return str0 + string.Format(format, args);
		}
	
		public string StringByAppendingString(string str0) {
			return this.content + str0;
		}
	
		public string SubStringFromIndex(int start) {
			return this.content.Substring(start);
		}
	
		public string SubStringToIndex(int end) {
			int start = 0;
			return this.content.Substring(start,(end)-(start));
		}
	
		public int Length() {
			return this.content.Length;
		}
	
		public string GetString() {
			return this.content;
		}
	
		public void SetString(string str0) {
			this.content = str0;
		}
	
		public bool IsEqualToString(NSString str0) {
			return this.content.Equals(str0.GetString());
		}
	
		public char CharacterAtIndex(int index) {
			return this.content[index];
		}
	
		public string SubstringWithRange(NSRange range) {
			return this.content.Substring(range.start,(range.end)-(range.start));
		}
	
		public NSArray ComponentsSeparatedByString(string str0) {
			string[] c = StringUtils.Split(this.content, str0);
			int size = c.Length;
			NSString[] strings = new NSString[size];
			for (int i = 0; i < size; i++) {
				strings[i] = new NSString(c[i]);
			}
			NSArray array = Foundation.NSArray.ArrayWithObjects(strings);
			return array;
		}
	
		public string StringByReplacingCharactersInRange(NSRange range,
				string str0) {
			int length = this.Length();
			string stringBegin = this.content.Substring(0,(range.start)-(0));
			string stringEnd = this.content.Substring(range.end + 1,(length)-(range.end + 1));
			return stringBegin + str0 + stringEnd;
		}
	
		public string PathExtension() {
			return FileUtils.GetExtension(content);
		}
	
		public string LastPathComponent() {
			int start = -1, end = -1;
			for (int i = content.Length - 1; i >= 0; i--) {
				if (end == -1) {
					if (content[i] != '/') {
						end = i;
					}
				} else {
					if (content[i] == '/') {
						start = i;
						break;
					}
				}
			}
			if (end == -1) {
				return "/";
			}
			return content.Substring(start + 1,(end + 1)-(start + 1));
		}
	
		public NSArray PathComponents() {
			NSMutableArray pathArray = Foundation.NSMutableArray.Array();
			string str0;
			int start = 0, end = 0;
			for (int i = 0; i < content.Length; i++) {
				if (content[i] == '/' || i == Length() - 1) {
					end = i;
					str0 = content.Substring(start,(end + 1)-(start));
					if (str0.Equals("/")) {
						if (start == 0 || end == content.Length - 1) {
							pathArray.AddObject(new NSString("/"));
						}
					} else {
						pathArray.AddObject(new NSString(str0));
					}
					start = i + 1;
				}
			}
			return pathArray;
		}
	
		public string StringByAppendingPathComponent(string str0) {
			string receive = this.GetString();
			if (receive.Equals("")) {
				return str0;
			} else {
				int index = this.content.Length;
				int c = this.content.LastIndexOf('/');
				if (c == index - 1) {
					receive = this.content + str0;
				} else {
					receive = this.content + '/' + str0;
				}
				return receive;
			}
		}
	
		public int IntValue() {
			return Int32.Parse(this.content);
		}
	
		public Single FloatValue() {
			return Single.Parse(this.content,JavaRuntime.NumberFormat);
		}
	
		public Double DoubleValue() {
            return ((Double)Double.Parse(this.content, JavaRuntime.NumberFormat));
		}
	
		public bool BooleanValue() {
			return Int32.Parse(this.content) != 0;
		}
	
		public override string ToString() {
			return this.content;
		}
	
		public override bool Equals(object o) {
			if (o  is  NSString) {
				return ((NSString) o).content.Equals(content);
			}
			return content.Equals(o);
		}
	
		public override int GetHashCode() {
			return content.GetHashCode();
		}
	
		protected internal override void AddSequence(StringBuilder sbr, string indent) {
			sbr.Append(indent);
			sbr.Append("<string>");
			sbr.Append(content);
			sbr.Append("</string>");
		}
	
	}
}

namespace Loon.Utils.Xml {

    using System.Collections.Generic;
	using Loon.Core;
    using Loon.Java.Collections;
   
	public class XMLElement : LRelease {
	
		private string name;
	
		private IDictionary<string, XMLAttribute> attributes;
	
		private IList<object> contents;
	
		private XMLElement parent;
	
		internal XMLElement(string n) {
            this.attributes = new Dictionary<string, XMLAttribute>();
            this.contents = new List<object>();
			this.name = n;
		}
	
		public XMLAttribute GetAttribute(string n) {
			if (!this.attributes.ContainsKey(n))
				throw new System.Exception("Unknown attribute name '" + n
						+ "' in element '" + this.name + "' !");
            return attributes[n];
		}
	
		public string GetAttribute(string n, string v) {
            if (!this.attributes.ContainsKey(n))
            {
				return v;
			}
            return attributes[n].GetValue();
		}
	
		public int GetIntAttribute(string n, int v) {
            if (!this.attributes.ContainsKey(n))
            {
				return v;
			}
            return attributes[n].GetIntValue();
		}
	
		public float GetFloatAttribute(string n, float v) {
			if (!this.attributes.ContainsKey(n)) {
				return v;
			}
            return attributes[n].GetFloatValue();
		}
	
		public double GetDoubleAttribute(string n, double v) {
			if (!this.attributes.ContainsKey(n)) {
				return v;
			}
            return attributes[n].GetDoubleValue(); ;
		}
	
		public bool GetBoolAttribute(string n, bool v) {
			if (!this.attributes.ContainsKey(n)) {
				return v;
			}
            return attributes[n].GetBoolValue();
		}

        public IDictionary<string, XMLAttribute> GetAttributes()
        {
			return this.attributes;
		}
	
		public bool HasAttribute(string n) {
			return this.attributes.ContainsKey(n);
		}
	
		public IIterator Elements() {
			return new IteratorAdapter(this.contents.GetEnumerator());
		}
	
		public XMLElement GetFirstChild() {
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if (!(o  is  XMLElement)) {
					continue;
				}
				return (XMLElement) o;
			}
			return null;
		}
	
		public XMLElement GetChildrenByName(string n) {
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if ((!(o  is  XMLElement))
						|| (!((XMLElement) o).GetName().Equals(n))) {
					continue;
				}
				return (XMLElement) o;
			}
			return null;
		}
	
		public List<XMLElement> Find(string n) {
			List<XMLElement> v = new List<XMLElement>();
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if ((!(o  is  XMLElement))) {
					continue;
				}
				XMLElement ele = (XMLElement) o;
				if (!ele.Equals(ele.GetName())) {
					IIterator it = ele.Elements(n);
					for (; it.HasNext();) {
						XMLElement child = (XMLElement) it.Next();
						child.parent = ele;
                        CollectionUtils.Add(v, child);
					}
					continue;
				} else if (ele.Equals(ele.GetName())) {
                    CollectionUtils.Add(v, (XMLElement)o);
					continue;
				}
			}
			return v;
		}
	
		public List<XMLElement> List(string n) {
			List<XMLElement> v = new List<XMLElement>();
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if ((!(o  is  XMLElement))
						|| (!((XMLElement) o).GetName().Equals(n))) {
					continue;
				}
                CollectionUtils.Add(v, (XMLElement)o);
			}
			return v;
		}

        public IIterator Elements(string n)
        {
			List<object> v = new List<object>();
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if ((!(o  is  XMLElement))
						|| (!((XMLElement) o).GetName().Equals(n))) {
					continue;
				}
                CollectionUtils.Add(v, o);
			}
			return new IteratorAdapter(v.GetEnumerator());
		}
	
		public void AddAllTo(List<XMLElement> list) {
			for (IIterator e = Elements(); e.HasNext();) {
				object o = e.Next();
				if ((!(o  is  XMLElement))
						|| (!((XMLElement) o).GetName().Equals(name))) {
					continue;
				}
                CollectionUtils.Add(list, (XMLElement)o);
			}
		}
	
		public string GetName() {
			return this.name;
		}
	
		public XMLElement GetParent() {
			return this.parent;
		}
	
		public string GetContents() {
			System.Text.StringBuilder sbr = new System.Text.StringBuilder(1024);
			for (IIterator e = Elements(); e.HasNext();) {
				sbr.Append(e.Next().ToString());
			}
			return sbr.ToString();
		}
	
		public override string ToString() {
			ICollection<string> set = this.attributes.Keys;
			string str1 = "<" + this.name;
			for (IIterator it = new IteratorAdapter(set.GetEnumerator()); it.HasNext();) {
				string str2 = (string) it.Next();
				str1 = str1 + " " + str2 + " = \"" + GetAttribute(str2).GetValue()
						+ "\"";
			}
			str1 = str1 + ">";
			str1 = str1 + GetContents();
			str1 = str1 + "</" + this.name + ">";
			return str1;
		}
	
		internal XMLAttribute AddAttribute(string n, string value_ren) {
			XMLAttribute attribute = new XMLAttribute(n, value_ren);
            attributes.Add(n,attribute);
			return attribute;
		}
	
		internal void AddContents(object o) {
            contents.Add(o);
		}
	
		public virtual void Dispose() {
			if (attributes != null) {
				attributes.Clear();
				attributes = null;
			}
			if (contents != null) {
                contents.Clear();
				contents = null;
			}
		}
	
	}
}

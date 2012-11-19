namespace Loon.Utils.Xml {
	
	public class XMLDocument {
	
		private string header;
	
		private XMLElement root;

        public XMLDocument(XMLElement e)
            : this("<?xml version=\"1.0\" standalone=\"yes\" ?>\n", e)
        {
			
		}
	
		public XMLElement GetRoot() {
			return this.root;
		}
	
		public string GetHeader() {
			return this.header;
		}
	
		public override string ToString() {
			return this.header + this.root.ToString();
		}
	
		public XMLDocument(string h, XMLElement r) {
			this.header = h;
			this.root = r;
		}
	
	}
}
